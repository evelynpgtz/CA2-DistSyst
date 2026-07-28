package com.evelyn.client;

import javax.jmdns.ServiceInfo;

import com.evelyn.proto.waterquality.QualityResponse;

/* This class starts the gRPC client. */
public class ClientMain {

    /* Starts the client application. */
    public static void main(String[] args) throws Exception {

        /* Discover the service using JmDNS. */
        ServiceDiscovery discovery = new ServiceDiscovery();

        ServiceInfo serviceInfo =
                discovery.discoverService("_waterquality._tcp.local.");

        /* Check if the service was found. */
        if (serviceInfo == null) {

            System.out.println("Water Quality Service not found.");
            return;

        }

        /* Create the gRPC client. */
        GrpcClient client = new GrpcClient(
                serviceInfo.getHostAddresses()[0],
                serviceInfo.getPort());

        /* Request the current water quality. */
        QualityResponse response =
                client.getCurrentQuality("Sensor-001");

        /* Display the received data. */
        System.out.println("Water Quality Information");
        System.out.println("-------------------------");
        System.out.println("pH: " + response.getPhLevel());
        System.out.println("Temperature: " + response.getTemperature());
        System.out.println("Turbidity: " + response.getTurbidity());
        System.out.println("Safe: " + response.getSafe());

        /* Close the client connection. */
        client.shutdown();

    }

}