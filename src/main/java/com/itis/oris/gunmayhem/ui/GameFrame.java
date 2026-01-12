package com.itis.oris.gunmayhem.ui;

import com.itis.oris.gunmayhem.client.GameClient;
import com.itis.oris.gunmayhem.ui.animation.GamePanel;

import javax.swing.*;

public class GameFrame extends JFrame {

    public GameFrame(GameClient client) {
        setTitle("Gun Mayhem");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel(client);
        add(panel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
