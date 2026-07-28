package com.evelyn.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.evelyn.client.GrpcClient;
import com.evelyn.client.ServiceDiscovery;

import com.evelyn.proto.waterquality.QualityResponse;

import javax.jmdns.ServiceInfo;

/* This panel displays the Water Quality service. */
public class WaterQualityPanel extends JPanel {

        /* Input fields. */
        private JTextField sensorIdField;
        private JTextField phField;
        private JTextField temperatureField;
        private JTextField turbidityField;

        /* Check box for water safety. */
        private JCheckBox safeCheckBox;

        /* Buttons. */
        private JButton getQualityButton;
        private JButton updateQualityButton;
        private JButton streamButton;

        /* Output area. */
        private JTextArea outputArea;

        /* Creates the Water Quality panel. */
        public WaterQualityPanel() {

            /* Use BorderLayout for the panel. */
            setLayout(new BorderLayout());

            JPanel formPanel = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;

            sensorIdField = new JTextField("Sensor-001", 15);
            phField = new JTextField(10);
            temperatureField = new JTextField(10);
            turbidityField = new JTextField(10);
            safeCheckBox = new JCheckBox();
            safeCheckBox.setEnabled(false);

            getQualityButton = new JButton("Get Quality");
            updateQualityButton = new JButton("Update Quality");
            streamButton = new JButton("Stream Updates");

            outputArea = new JTextArea(12, 45);
            outputArea.setEditable(false);

            gbc.gridx = 0;
            gbc.gridy = 0;
            formPanel.add(new JLabel("Sensor ID:"), gbc);

            gbc.gridx = 1;
            formPanel.add(sensorIdField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("pH:"), gbc);

            gbc.gridx = 1;
            formPanel.add(phField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Temperature:"), gbc);

            gbc.gridx = 1;
            formPanel.add(temperatureField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Turbidity:"), gbc);

            gbc.gridx = 1;
            formPanel.add(turbidityField, gbc);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(new JLabel("Safe:"), gbc);

            gbc.gridx = 1;
            formPanel.add(safeCheckBox, gbc);

            /* Add some space before the buttons. */
            gbc.insets = new Insets(15, 5, 10, 5);

            gbc.gridx = 0;
            gbc.gridy++;
            formPanel.add(getQualityButton, gbc);

            gbc.gridx = 1;
            formPanel.add(updateQualityButton, gbc);

            gbc.gridx = 2;
            formPanel.add(streamButton, gbc);

            /* Create the results area. */
            JScrollPane scrollPane = new JScrollPane(outputArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

            /* Add the components to the panel. */
            add(formPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            /* Add the Get Quality button action. */
            getQualityButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    loadWaterQuality();

                }

            });
        }

        /* Loads the current water quality information. */
        private void loadWaterQuality() {

            try {

                /* Discover the Water Quality service. */
                ServiceDiscovery discovery = new ServiceDiscovery();

                ServiceInfo serviceInfo = discovery.discoverService("_waterquality._tcp.local.");

                if (serviceInfo == null) {

                    outputArea.setText("Water Quality Service not found.");
                    return;

                }

                /* Create the gRPC client. */
                GrpcClient client = new GrpcClient(serviceInfo);

                /* Request the current water quality. */
                QualityResponse response =
                        client.getCurrentQuality(sensorIdField.getText());

                /* Display the received information. */
                phField.setText(String.valueOf(response.getPhLevel()));
                temperatureField.setText(String.valueOf(response.getTemperature()));
                turbidityField.setText(String.valueOf(response.getTurbidity()));
                safeCheckBox.setSelected(response.getSafe());

                outputArea.setText("Water quality loaded successfully.");

                /* Close the client connection. */
                client.shutdown();

            } catch (Exception e) {

                outputArea.setText("Error: " + e.getMessage());

            }

        }

}