package com.evelyn.gui;

import javax.swing.SwingUtilities;

/* This class starts the GUI application. */
public class GuiMain {

    /* Starts the graphical user interface. */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            MainWindow window = new MainWindow();
            window.setVisible(true);

        });

    }

}