package com.evelyn.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jmdns.ServiceInfo;

import com.evelyn.client.GrpcClient;
import com.evelyn.client.ServiceDiscovery;
import com.evelyn.proto.waterconsumption.ConsumptionRecord;
import com.evelyn.proto.waterconsumption.ConsumptionResponse;
import com.evelyn.proto.waterconsumption.ConsumptionSummary;

import io.grpc.stub.StreamObserver;

/* This panel displays the Water Consumption service. */
public class WaterConsumptionPanel extends JPanel {

    /* Input fields. */
    private JTextField householdIdField;
    private JTextField totalLitresField;
    private JTextField lastUpdatedField;

    /* Buttons. */
    private JButton getConsumptionButton;
    private JButton uploadRecordsButton;

    /* Output area. */
    private JTextArea outputArea;

    /* Creates the Water Consumption panel. */
    public WaterConsumptionPanel() {

        /* Use BorderLayout for the panel. */
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        householdIdField = new JTextField("House-001", 15);
        totalLitresField = new JTextField(10);
        lastUpdatedField = new JTextField(15);

        getConsumptionButton = new JButton("Get Consumption");
        uploadRecordsButton = new JButton("Upload Records");

        outputArea = new JTextArea(12, 45);
        outputArea.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Household ID:"), gbc);

        gbc.gridx = 1;
        formPanel.add(householdIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Total Litres:"), gbc);

        gbc.gridx = 1;
        formPanel.add(totalLitresField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Last Updated:"), gbc);

        gbc.gridx = 1;
        formPanel.add(lastUpdatedField, gbc);

        /* Add some space before the buttons. */
        gbc.insets = new Insets(15, 5, 10, 5);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(getConsumptionButton, gbc);

        gbc.gridx = 1;
        formPanel.add(uploadRecordsButton, gbc);

        /* Create the results area. */
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        /* Add the components to the panel. */
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        /* Add the Get Consumption button action. */
        getConsumptionButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                loadConsumption();

            }

        });

        /* Add the Upload Records button action. */
        uploadRecordsButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                uploadConsumptionRecords();

            }

        });

    }

    /* Loads the current water consumption information. */
    private void loadConsumption() {

        try {

            /* Discover the Water Consumption service. */
            ServiceDiscovery discovery = new ServiceDiscovery();

            ServiceInfo serviceInfo =
                    discovery.discoverService("_waterconsumption._tcp.local.");

            if (serviceInfo == null) {

                outputArea.setText("Water Consumption Service not found.");
                return;

            }

            /* Create the gRPC client. */
            GrpcClient client = new GrpcClient(serviceInfo);

            /* Request the current consumption information. */
            ConsumptionResponse response =
                    client.getConsumption(householdIdField.getText());

            /* Display the received information. */
            totalLitresField.setText(String.valueOf(response.getTotalLitres()));
            lastUpdatedField.setText(response.getLastUpdated());

            outputArea.setText("Water consumption loaded successfully.");

            /* Close the client connection. */
            client.shutdown();

        } catch (Exception e) {

            outputArea.setText("Error: " + e.getMessage());

        }

    }

    /* Uploads water consumption records to the server. */
    private void uploadConsumptionRecords() {

        try {

            /* Discover the Water Consumption service. */
            ServiceDiscovery discovery = new ServiceDiscovery();

            ServiceInfo serviceInfo =
                    discovery.discoverService("_waterconsumption._tcp.local.");

            if (serviceInfo == null) {

                outputArea.setText("Water Consumption Service not found.");
                return;

            }

            /* Create the gRPC client. */
            GrpcClient client = new GrpcClient(serviceInfo);

            /* Create the response observer. */
            StreamObserver<ConsumptionSummary> responseObserver =
                    new StreamObserver<ConsumptionSummary>() {

                @Override
                public void onNext(ConsumptionSummary response) {

                    outputArea.setText("");

                    outputArea.append("Upload completed.\n");
                    outputArea.append("Records received: "
                            + response.getRecordsReceived() + "\n");
                    outputArea.append("Total consumption: "
                            + response.getTotalConsumption() + " litres\n");

                }

                @Override
                public void onError(Throwable throwable) {

                    outputArea.setText(
                            "Streaming error: " + throwable.getMessage());

                }

                @Override
                public void onCompleted() {

                    client.shutdown();

                }

            };

            /* Create the request stream. */
            StreamObserver<ConsumptionRecord> requestObserver =
                    client.uploadConsumptionRecords(responseObserver);

            /* Send the first record. */
            requestObserver.onNext(
                    ConsumptionRecord.newBuilder()
                            .setHouseholdId(householdIdField.getText())
                            .setLitres(120.5)
                            .setTimestamp("2026-07-28 08:00")
                            .build());

            /* Send the second record. */
            requestObserver.onNext(
                    ConsumptionRecord.newBuilder()
                            .setHouseholdId(householdIdField.getText())
                            .setLitres(145.8)
                            .setTimestamp("2026-07-28 12:00")
                            .build());

            /* Send the third record. */
            requestObserver.onNext(
                    ConsumptionRecord.newBuilder()
                            .setHouseholdId(householdIdField.getText())
                            .setLitres(98.2)
                            .setTimestamp("2026-07-28 18:00")
                            .build());

            /* Complete the stream. */
            requestObserver.onCompleted();

        } catch (Exception e) {

            outputArea.setText("Error: " + e.getMessage());

        }

    }
}