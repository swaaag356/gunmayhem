package com.itis.oris.gunmayhem.client;

import com.itis.oris.gunmayhem.ui.connect.ConnectFrame;

import javax.swing.*;

public class MainClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ConnectFrame::new);
    }
}

