package com.itis.oris.gunmayhem.ui.connect;

import com.itis.oris.gunmayhem.client.GameClient;
import com.itis.oris.gunmayhem.ui.GameFrame;

import javax.swing.*;
import java.awt.*;

public class ConnectFrame extends JFrame {

    private JTextField ipField;
    private JTextField portField;
    private JButton connectButton;

    public ConnectFrame() {
        super("Connect to server");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        initComponents();
        layoutComponents();
        initListeners();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ================== UI ==================

    private void initComponents() {
        ipField = new JTextField("127.0.0.1", 15);
        portField = new JTextField("1234", 6);

        connectButton = new JButton("Connect");
    }

    private void layoutComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // IP
        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Server IP:"), c);

        c.gridx = 1;
        panel.add(ipField, c);

        // PORT
        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Port:"), c);

        c.gridx = 1;
        panel.add(portField, c);

        // BUTTON
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        panel.add(connectButton, c);

        add(panel);
    }

    // ================== LOGIC ==================

    private void initListeners() {
        connectButton.addActionListener(e -> connect());
        getRootPane().setDefaultButton(connectButton);
    }

    private void connect() {
        String ip = ipField.getText().trim();
        String portText = portField.getText().trim();

        int port;
        try {
            port = Integer.parseInt(portText);
        } catch (NumberFormatException ex) {
            showError("Port must be a number");
            return;
        }

        GameClient client = new GameClient();

        // подключение выносим в отдельный поток,
        // чтобы не блокировать Swing
        new Thread(() -> {
            client.connect(ip, port);

            SwingUtilities.invokeLater(() -> {
                new GameFrame(client);
                dispose();
            });
        }, "client-connect-thread").start();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Connection error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
