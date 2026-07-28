package com.evelyn.gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/* This class creates the main application window. */
public class MainWindow extends JFrame {

    /* Creates the main window. */
    public MainWindow() {

        /* Set the window title. */
        setTitle("Smart Water Management System");

        /* Set the window size. */
        setSize(900, 600);

        /* Close the application when the window closes. */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /* Show the window in the center of the screen. */
        setLocationRelativeTo(null);

        /* Create the tab panel. */
        JTabbedPane tabbedPane = new JTabbedPane();

        /* Add the project tabs. */
        tabbedPane.addTab("Water Quality", new WaterQualityPanel());
        tabbedPane.addTab("Water Consumption", new WaterConsumptionPanel());
        tabbedPane.addTab("Leak Detection", new LeakDetectionPanel());

        /* Add the tabs to the window. */
        add(tabbedPane);

    }

}