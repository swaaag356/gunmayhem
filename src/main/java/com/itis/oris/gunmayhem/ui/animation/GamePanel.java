package com.itis.oris.gunmayhem.ui.animation;

import com.itis.oris.gunmayhem.client.ClientGameState;
import com.itis.oris.gunmayhem.client.GameClient;
import com.itis.oris.gunmayhem.common.model.*;
import com.itis.oris.gunmayhem.common.model.enums.MageType;
import com.itis.oris.gunmayhem.common.model.enums.MagicType;
import com.itis.oris.gunmayhem.common.protocol.enums.InputCommand;
import com.itis.oris.gunmayhem.ui.animation.util.SpriteUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int VOID_Y = 520;

    private final GameClient gameClient;
    private final ClientGameState clientGameState;

    private final PlayerView redView = new PlayerView(MageType.RED);
    private final PlayerView blueView = new PlayerView(MageType.BLUE);
    private final Map<Long, MagicView> magicViews = new HashMap<>();

    private final BufferedImage background;
    private final Timer gameTimer;

    public GamePanel(GameClient gameClient) {
        System.out.println("[PANEL] constructor start");

        this.gameClient = gameClient;
        this.clientGameState = gameClient.getGameState();

        System.out.println("[PANEL] clientGameState = " + clientGameState);

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocusInWindow();

        background = SpriteUtils.loadImage("/sprites/background/bg_world.png");
        System.out.println("[PANEL] background loaded = " + (background != null));

        addKeyListener(new KeyHandler());

        gameTimer = new Timer(16, e -> {
            System.out.println("[TIMER] tick");
            updateAnimations();
            repaint();
        });
        gameTimer.start();

        System.out.println("[PANEL] constructor end");
    }

    // ================== UPDATE ==================

    private void updateAnimations() {
        System.out.println("[UPDATE] updateAnimations");

        redView.update();
        blueView.update();

        for (MagicView view : magicViews.values()) {
            view.update();
        }
    }

    // ================== RENDER ==================

    @Override
    protected void paintComponent(Graphics g) {
        System.out.println("[RENDER] paintComponent ENTER");

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );

        System.out.println("[RENDER] drawBackground");
        drawBackground(g2);

        drawPlatforms(g2, clientGameState.get());

        boolean ready = clientGameState.isReady();
        GameState state = clientGameState.get();

        System.out.println(
                "[RENDER] isReady=" + ready +
                        " state=" + state
        );

        if (!ready) {
            System.out.println("[RENDER] EXIT: client not ready");
            return;
        }

        System.out.println("[RENDER] drawVoid");
        drawVoid(g2);

        System.out.println("[RENDER] draw RED player");
        drawPlayer(g2, state.getRedPlayer(), redView);

        System.out.println("[RENDER] draw BLUE player");
        drawPlayer(g2, state.getBluePlayer(), blueView);

        System.out.println("[RENDER] drawMagic");
        drawMagic(g2, state);

        System.out.println("[RENDER] paintComponent EXIT");
    }

    private void drawBackground(Graphics2D g) {
        g.drawImage(background, 0, 0, WIDTH, HEIGHT, null);
    }

    private void drawPlatforms(Graphics2D g, GameState state) {

        if (state.getPlatforms() == null) {
            return;
        }

        g.setColor(new Color(24, 52, 80)); // коричневый

        for (Platform p : state.getPlatforms()) {

            int x = (int) p.getX();
            int y = (int) p.getY();
            int w = (int) p.getWidth();
            int h = (int) p.getHeight();

            g.fillRect(x, y, w, h);
        }
    }


    private void drawVoid(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, VOID_Y, WIDTH, HEIGHT - VOID_Y);
    }

    private void drawPlayer(Graphics2D g, Player player, PlayerView view) {
        System.out.println(
                "[DRAW PLAYER] id=" + player.getId() +
                        " pos=(" + player.getPosition().getX() + "," + player.getPosition().getY() + ")" +
                        " state=" + player.getState()
        );

        view.setState(player.getState());

        int x = (int) player.getPosition().getX();
        int y = (int) player.getPosition().getY();

        view.draw(g, x, y);
    }

    private void drawMagic(Graphics2D g, GameState state) {
        System.out.println("[DRAW MAGIC] balls=" + state.getMagicBalls().size());

        for (MagicBall ball : state.getMagicBalls()) {
            long id = ball.getId();
            if (!magicViews.containsKey(id)) {
                System.out.println("[MAGIC] create view for ball " + id);
                magicViews.put(id, new MagicView(ball.getType()));
            }
        }

        for (MagicBall ball : state.getMagicBalls()) {
            MagicView view = magicViews.get(ball.getId());

            int x = (int) ball.getPosition().getX();
            int y = (int) ball.getPosition().getY();

            view.draw(g, x, y);
        }

        magicViews.keySet().removeIf(id ->
                state.getMagicBalls().stream().noneMatch(b -> b.getId() == id)
        );
    }

    // ================== INPUT ==================

    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            System.out.println("[INPUT] keyPressed " + e.getKeyCode());

            switch (e.getKeyCode()) {
                case KeyEvent.VK_A -> gameClient.sendInput(InputCommand.LEFT);
                case KeyEvent.VK_D -> gameClient.sendInput(InputCommand.RIGHT);
                case KeyEvent.VK_W -> gameClient.sendInput(InputCommand.JUMP);
                case KeyEvent.VK_SPACE -> gameClient.sendCast(gameClient.getMagicType());
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            System.out.println("[INPUT] keyReleased " + e.getKeyCode());

            if (e.getKeyCode() == KeyEvent.VK_A || e.getKeyCode() == KeyEvent.VK_D) {
                gameClient.sendInput(InputCommand.STOP);
            }
        }
    }
}
