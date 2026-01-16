package com.itis.oris.gunmayhem.client;

import com.itis.oris.gunmayhem.common.model.GameState;

public class ClientGameState {

    private volatile GameState state;
    private volatile boolean initialized = false;

    public void update(GameState s) {
        this.state = s;
    }

    public GameState get() {
        return state;
    }

    public void setInitialized(boolean v) {
        this.initialized = v;
    }

    public boolean isReady() {
        return initialized && state != null;
    }
}
