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
import com.evelyn.proto.waterquality.QualityData;
import com.evelyn.proto.waterquality.UpdateResponse;
import io.grpc.stub.StreamObserver;
import com.evelyn.proto.waterquality.QualityRequest;

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
        private JButton stopStreamButton;

        /* Output area. */
        private JTextArea outputArea;

        /* gRPC client used by the panel. */
        private GrpcClient client;

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

            getQualityButton = new JButton("Get Quality");
            updateQualityButton = new JButton("Update Quality");
            streamButton = new JButton("Stream Updates");
            stopStreamButton = new JButton("Stop Stream");

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

            gbc.gridx = 3;
            formPanel.add(stopStreamButton, gbc);

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

            /* Add the Update Quality button action. */
            updateQualityButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    updateWaterQuality();

                }

            });

            /* Add the Stream Updates button action. */
            streamButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    streamWaterQuality();

                }

            });

            /* Add the Stop Stream button action. */
            stopStreamButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    if (client != null) {

                        client.cancelStream();

                        outputArea.append("Stream cancelled.\n");

                    }

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
                client = new GrpcClient(serviceInfo);

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

        /* Updates the current water quality information. */
        private void updateWaterQuality() {

            try {

                /* Discover the Water Quality service. */
                ServiceDiscovery discovery = new ServiceDiscovery();

                ServiceInfo serviceInfo =
                        discovery.discoverService("_waterquality._tcp.local.");

                if (serviceInfo == null) {

                    outputArea.setText("Water Quality Service not found.");
                    return;

                }

                /* Create the gRPC client. */
                client = new GrpcClient(serviceInfo);

                /* Create the request message. */
                QualityData request = QualityData.newBuilder()
                        .setSensorId(sensorIdField.getText())
                        .setPhLevel(Double.parseDouble(phField.getText()))
                        .setTemperature(Double.parseDouble(temperatureField.getText()))
                        .setTurbidity(Double.parseDouble(turbidityField.getText()))
                        .setSafe(safeCheckBox.isSelected())
                        .build();

                /* Send the update request. */
                UpdateResponse response = client.updateQuality(request);

                /* Display the server response. */
                outputArea.setText(response.getMessage());

                /* Close the client connection. */
                client.shutdown();

            } catch (Exception e) {

                outputArea.setText("Error: " + e.getMessage());

            }

        }

        /* Starts the water quality streaming service. */
        private void streamWaterQuality() {

            try {

                /* Discover the Water Quality service. */
                ServiceDiscovery discovery = new ServiceDiscovery();

                ServiceInfo serviceInfo =
                        discovery.discoverService("_waterquality._tcp.local.");

                if (serviceInfo == null) {

                    outputArea.setText("Water Quality Service not found.");
                    return;

                }

                /* Create the gRPC client. */
                client = new GrpcClient(serviceInfo);

                /* Create the request message. */
                QualityRequest request = QualityRequest.newBuilder()
                        .setSensorId(sensorIdField.getText())
                        .build();

                /* Start receiving stream updates. */
                client.streamQualityUpdates(request, new StreamObserver<QualityResponse>() {

                    @Override
                    public void onNext(QualityResponse response) {

                        phField.setText(String.valueOf(response.getPhLevel()));
                        temperatureField.setText(String.valueOf(response.getTemperature()));
                        turbidityField.setText(String.valueOf(response.getTurbidity()));
                        safeCheckBox.setSelected(response.getSafe());

                        outputArea.append("Streaming update received.\n");

                    }

                    @Override
                    public void onError(Throwable throwable) {

                        outputArea.append("Streaming error.\n");

                    }

                    @Override
                    public void onCompleted() {

                        outputArea.append("Streaming finished.\n");

                        client.shutdown();

                    }

                });

            } catch (Exception e) {

                outputArea.setText("Error: " + e.getMessage());

            }

        }

}