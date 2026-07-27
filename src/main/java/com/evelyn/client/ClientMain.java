package com.evelyn.client;

import com.evelyn.proto.waterquality.QualityResponse;

/* This class starts the gRPC client. */
public class ClientMain {

    /* Starts the client application. */
    public static void main(String[] args) throws Exception {

        /* Create the gRPC client. */
        GrpcClient client = new GrpcClient("localhost", 50051);

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