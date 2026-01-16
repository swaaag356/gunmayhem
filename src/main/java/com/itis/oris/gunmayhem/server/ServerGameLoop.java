package com.itis.oris.gunmayhem.server;

import com.itis.oris.gunmayhem.common.model.*;
import com.itis.oris.gunmayhem.common.model.enums.PlayerState;
import com.itis.oris.gunmayhem.common.protocol.*;
import com.itis.oris.gunmayhem.common.protocol.enums.MessageType;
import com.itis.oris.gunmayhem.common.protocol.payloads.CastPayload;
import com.itis.oris.gunmayhem.common.protocol.payloads.InputPayload;
import com.itis.oris.gunmayhem.common.protocol.payloads.StatePayload;
import com.itis.oris.gunmayhem.server.storage.PlayerStats;
import com.itis.oris.gunmayhem.server.storage.PlayerStatsStorage;
import com.itis.oris.gunmayhem.server.storage.StatsStorageModel;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

public class ServerGameLoop implements Runnable {

    private static final int TICK_RATE = 60;

    private static final int PLAYER_W = 64;
    private static final int PLAYER_H = 64;

    private static final double KNOCKBACK_X = 6.0;
    private static final double KNOCKBACK_Y = -6.0;

    private static final double GRAVITY = 0.5;

    private static final double MOVE_SPEED = 3.0;
    private static final double JUMP_SPEED = -11.0;

    private static final int VOID_Y = 550;

    private final AtomicLong magicIdGenerator = new AtomicLong();

    private final GameState gameState;
    private final PrintWriter out1;
    private final PrintWriter out2;

    private final BlockingQueue<Map.Entry<Integer, GameMessage>> inputQueue = new LinkedBlockingQueue<>();

    public ServerGameLoop(GameState gameState, PrintWriter out1, PrintWriter out2) {
        this.gameState = gameState;
        this.out1 = out1;
        this.out2 = out2;
    }

    public void pushInput(int playerId, GameMessage msg) {
        inputQueue.add(Map.entry(playerId, msg));
    }


    @Override
    public void run() {
        long sleepTime = 16;

        while (!gameState.isGameOver()) {
            processInput();
            updatePhysics();
            updateMagic();
            checkVoid();
            sendState();

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
    }

    // ================== INPUT ==================

    private void processInput() {

        Map.Entry<Integer, GameMessage> entry;

        while ((entry = inputQueue.poll()) != null) {

            int playerId = entry.getKey();
            GameMessage msg = entry.getValue();

            switch (msg.getType()) {
                case INPUT -> handleInput(playerId, msg);
                case CAST -> handleCast(playerId, msg);
            }
        }
    }


    private void handleInput(int playerId, GameMessage msg) {

        InputPayload payload = ProtocolUtils.fromJson(msg.getPayload(), InputPayload.class);

        Player player = getPlayer(playerId);

        switch (payload.getCommand()) {

            case LEFT -> {
                player.setFacingRight(false);
                if (player.getVelocity().getX() >= 0) {
                    player.setState(PlayerState.RUN);
                }
                player.getVelocity().setX(-MOVE_SPEED);
            }

            case RIGHT -> {
                player.setFacingRight(true);
                if (player.getVelocity().getX() <= 0) {
                    player.setState(PlayerState.RUN);
                }
                player.getVelocity().setX(MOVE_SPEED);
            }

            case STOP -> {
                player.getVelocity().setX(0);
                if (player.isOnGround()) {
                    player.setState(PlayerState.IDLE);
                }
            }

            case JUMP -> {
                if (player.isOnGround()) {
                    player.getVelocity().setY(JUMP_SPEED);
                    player.setOnGround(false);
                    player.setState(PlayerState.JUMP);
                }
            }
        }
    }


    private void handleCast(int playerId, GameMessage msg) {

        CastPayload payload = ProtocolUtils.fromJson(msg.getPayload(), CastPayload.class);

        Player player = getPlayer(playerId);

        double speed = player.isFacingRight() ? 8 : -8;

        MagicBall ball = MagicBall.builder()
                .id(magicIdGenerator.incrementAndGet())
                .ownerId(player.getId())
                .type(payload.getMagicType())
                .position(
                        new Vector2D(player.getPosition().getX() + 32,
                                player.getPosition().getY() + 16))
                .velocity(
                        new Vector2D(speed,
                                0))
                .build();

        gameState.getMagicBalls().add(ball);
        player.setState(PlayerState.ATTACK);
    }


    // ================== ФИЗИКА ==================

    private void updatePhysics() {
        updatePlayer(gameState.getRedPlayer());
        updatePlayer(gameState.getBluePlayer());
    }

    private void updatePlayer(Player player) {

        // 1. сохраняем прошлую позицию
        double prevY = player.getPosition().getY();

        // 2. гравитация
        player.getVelocity().setY(
                player.getVelocity().getY() + GRAVITY
        );

        // 3. движение
        player.getPosition().add(player.getVelocity());

        boolean landed = false;

        for (Platform p : gameState.getPlatforms()) {

            // границы
            double playerBottomPrev = prevY + PLAYER_H;
            double playerBottomNow  = player.getPosition().getY() + PLAYER_H;

            double platformTop = p.getY();

            boolean falling = player.getVelocity().getY() >= 0;

            boolean crossedPlatformTop =
                    playerBottomPrev <= platformTop &&
                            playerBottomNow  >= platformTop;

            boolean intersectsX =
                    player.getPosition().getX() + PLAYER_W > p.getX() &&
                            player.getPosition().getX() < p.getX() + p.getWidth();

            if (falling && crossedPlatformTop && intersectsX) {

                // приземление
                player.getPosition().setY(platformTop - PLAYER_H);
                player.getVelocity().setY(0);
                player.setOnGround(true);
                landed = true;

                if (player.getVelocity().getX() == 0) {
                    player.setState(PlayerState.IDLE);
                } else {
                    player.setState(PlayerState.RUN);
                }
                break;
            }
        }

        if (!landed) {
            player.setOnGround(false);
        }
    }



    private void updateMagic() {

        Iterator<MagicBall> it = gameState.getMagicBalls().iterator();

        while (it.hasNext()) {

            MagicBall ball = it.next();

            // движение магии
            ball.getPosition().add(ball.getVelocity());

            // проверка попадания
            Player target =
                    ball.getOwnerId() == gameState.getRedPlayer().getId()
                            ? gameState.getBluePlayer()
                            : gameState.getRedPlayer();

            if (intersects(target, ball)) {

                // направление отталкивания
                double dir = ball.getVelocity().getX() > 0 ? 1 : -1;

                target.getVelocity().setX(dir * KNOCKBACK_X);
                target.getVelocity().setY(KNOCKBACK_Y);
                target.setOnGround(false);

                it.remove();
                continue;
            }

            // вылет за экран
            if (ball.getPosition().getX() < -50 ||
                    ball.getPosition().getX() > 850) {

                it.remove();
            }
        }
    }

    private boolean intersects(Player p, MagicBall b) {

        double px = p.getPosition().getX();
        double py = p.getPosition().getY();

        double bx = b.getPosition().getX();
        double by = b.getPosition().getY();

        return px < bx + 16 &&
                px + PLAYER_W > bx &&
                py < by + 16 &&
                py + PLAYER_H > by;
    }


    // ================== БЕЗДНА ==================

    private void checkVoid() {
        System.out.println("[VOID] checkVoid()");
        checkPlayerVoid(gameState.getRedPlayer());
        checkPlayerVoid(gameState.getBluePlayer());
    }

    private void checkPlayerVoid(Player player) {

        if (player.getPosition().getY() > VOID_Y) {

            player.setLives(player.getLives() - 1);


            respawn(player);

            if (player.getLives() <= 0) {
                int looserId = player.getId();
                int winnerId = looserId == 1 ? 2 : 1;

                gameState.setGameOver(true);
                gameState.setLooserId(looserId);

                saveStats(looserId, winnerId);
            }

        }
    }

    private void respawn(Player player) {

        Vector2D spawn =
                player.getId() == 1
                        ? new Vector2D(120, 300)
                        : new Vector2D(580, 300);

        player.getPosition().set(spawn.getX(), spawn.getY());
        player.getVelocity().set(0, 0);
        player.setOnGround(false);
        player.setState(PlayerState.IDLE);
    }

    private void saveStats(int looserId, int winnerId) {
        StatsStorageModel model = PlayerStatsStorage.load();

        model.getStats().putIfAbsent(1, new PlayerStats(0, 0));
        model.getStats().putIfAbsent(2, new PlayerStats(0, 0));

        PlayerStats looserStats = model.getStats().get(looserId);
        PlayerStats winnerStats = model.getStats().get(winnerId);

        looserStats.setLosses(looserStats.getLosses() + 1);
        winnerStats.setWins(winnerStats.getWins() + 1);

        PlayerStatsStorage.save(model);
    }


    // ================== STATE ==================

    private void sendState() {
        String json = ProtocolUtils.toJson(new StatePayload(gameState));

        out1.println(ProtocolUtils.encode(new GameMessage(MessageType.STATE, json)));
        out2.println(ProtocolUtils.encode(new GameMessage(MessageType.STATE, json)));
    }

    // ================== UTIL ==================

    private Player getPlayer(int id) {
        return id == gameState.getRedPlayer().getId() ? gameState.getRedPlayer() : gameState.getBluePlayer();
    }
}

