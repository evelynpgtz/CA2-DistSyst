package com.evelyn.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jmdns.ServiceInfo;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.evelyn.client.GrpcClient;
import com.evelyn.client.ServiceDiscovery;
import com.evelyn.proto.leakdetection.LeakResponse;
import com.evelyn.proto.leakdetection.LeakUpdate;
import com.evelyn.proto.leakdetection.LeakAlert;

import io.grpc.stub.StreamObserver;

/* This panel displays the Leak Detection service. */
public class LeakDetectionPanel extends JPanel {

    /* Input fields. */
    private JTextField locationField;
    private JTextField severityField;

    /* Buttons. */
    private JButton reportLeakButton;
    private JButton liveMonitoringButton;

    /* Output area. */
    private JTextArea outputArea;

    /* Creates the Leak Detection panel. */
    public LeakDetectionPanel() {

        /* Use BorderLayout for the panel. */
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        locationField = new JTextField("Main Street", 15);
        severityField = new JTextField("3", 10);

        reportLeakButton = new JButton("Report Leak");
        liveMonitoringButton = new JButton("Live Monitoring");

        outputArea = new JTextArea(12, 45);
        outputArea.setEditable(false);

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Location:"), gbc);

        gbc.gridx = 1;
        formPanel.add(locationField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Severity:"), gbc);

        gbc.gridx = 1;
        formPanel.add(severityField, gbc);

        /* Add some space before the buttons. */
        gbc.insets = new Insets(15, 5, 10, 5);

        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(reportLeakButton, gbc);

        gbc.gridx = 1;
        formPanel.add(liveMonitoringButton, gbc);

        /* Create the results area. */
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));

        /* Add the components to the panel. */
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        /* Add the Report Leak button action. */
        reportLeakButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                reportLeak();

            }

        });

        /* Add the Live Monitoring button action. */
        liveMonitoringButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                startLiveMonitoring();

            }

        });

    }

    /* Reports a leak to the server. */
    private void reportLeak() {

        try {

            /* Discover the Leak Detection service. */
            ServiceDiscovery discovery = new ServiceDiscovery();

            ServiceInfo serviceInfo =
                    discovery.discoverService("_leakdetection._tcp.local.");

            if (serviceInfo == null) {

                outputArea.setText("Leak Detection Service not found.");
                return;

            }

            /* Create the gRPC client. */
            GrpcClient client = new GrpcClient(serviceInfo);

            /* Send the leak report. */
            LeakResponse response = client.reportLeak(
                    locationField.getText(),
                    Integer.parseInt(severityField.getText()));

            /* Display the server response. */
            outputArea.setText("");
            outputArea.append("Leak ID: " + response.getLeakId() + "\n");
            outputArea.append("Status: " + response.getStatus());

            /* Close the client connection. */
            client.shutdown();

        } catch (Exception e) {

            outputArea.setText("Error: " + e.getMessage());

        }

    }

    /* Starts the live monitoring stream. */
    private void startLiveMonitoring() {

        try {

            /* Discover the Leak Detection service. */
            ServiceDiscovery discovery = new ServiceDiscovery();

            ServiceInfo serviceInfo =
                    discovery.discoverService("_leakdetection._tcp.local.");

            if (serviceInfo == null) {

                outputArea.setText("Leak Detection Service not found.");
                return;

            }

            /* Create the gRPC client. */
            GrpcClient client = new GrpcClient(serviceInfo);

            outputArea.setText("Monitoring started...\n");

            /* Create the response observer. */
            StreamObserver<LeakAlert> responseObserver =
                    new StreamObserver<LeakAlert>() {

                @Override
                public void onNext(LeakAlert alert) {

                    outputArea.append("\nAlert received\n");
                    outputArea.append("Message: "
                            + alert.getMessage() + "\n");
                    outputArea.append("Recommendation: "
                            + alert.getRecommendation() + "\n");

                }

                @Override
                public void onError(Throwable throwable) {

                    outputArea.append("\nStreaming error: "
                            + throwable.getMessage());

                }

                @Override
                public void onCompleted() {

                    outputArea.append("\nMonitoring completed.");

                    client.shutdown();

                }

            };

            /* Create the request stream. */
            StreamObserver<LeakUpdate> requestObserver =
                    client.startLeakMonitoring(responseObserver);

            /* Send the first update. */
            requestObserver.onNext(
                    LeakUpdate.newBuilder()
                            .setLocation(locationField.getText())
                            .setSeverity(Integer.parseInt(severityField.getText()))
                            .build());

            /* Send the second update. */
            requestObserver.onNext(
                    LeakUpdate.newBuilder()
                            .setLocation("North Zone")
                            .setSeverity(2)
                            .build());

            /* Send the third update. */
            requestObserver.onNext(
                    LeakUpdate.newBuilder()
                            .setLocation("South Zone")
                            .setSeverity(4)
                            .build());

            /* Complete the stream. */
            requestObserver.onCompleted();

        } catch (Exception e) {

            outputArea.setText("Error: " + e.getMessage());

        }

    }

}