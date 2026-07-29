package com.evelyn.service;

import com.evelyn.model.WaterQuality;
import com.evelyn.proto.waterquality.QualityRequest;
import com.evelyn.proto.waterquality.QualityResponse;
import com.evelyn.proto.waterquality.WaterQualityServiceGrpc;
import com.evelyn.proto.waterquality.QualityData;
import com.evelyn.proto.waterquality.UpdateResponse;
import java.util.concurrent.TimeUnit;

import io.grpc.stub.StreamObserver;


/* This class implements the Water Quality gRPC service.
 * It receives client requests and sends back water quality information. */
public class WaterQualityServiceImpl extends WaterQualityServiceGrpc.WaterQualityServiceImplBase {

        /* Returns the current water quality information.
         * This method is called when the client requests the latest data. */
        @Override
        public void getCurrentQuality(QualityRequest request,
                                        StreamObserver<QualityResponse> responseObserver) {
                
                /* Check if the sensor exists. */
                if (!request.getSensorId().equals("Sensor-001")) {

                        responseObserver.onError(
                                io.grpc.Status.NOT_FOUND
                                        .withDescription("Sensor not found.")
                                        .asRuntimeException());

                        return;

                }

                /* Create a sample water quality object. */
                WaterQuality waterQuality = new WaterQuality(
                        "Sensor-001",
                        7.2,
                        1.3,
                        18.5,
                        true
                );

                /* Simulate a slow server response.
                try {

                TimeUnit.SECONDS.sleep(5);

                } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

                }
                */

                /* Build the gRPC response using the model data. */
                QualityResponse response = QualityResponse.newBuilder()
                        .setPhLevel(waterQuality.getPhLevel())
                        .setTurbidity(waterQuality.getTurbidity())
                        .setTemperature(waterQuality.getTemperature())
                        .setSafe(waterQuality.isSafe())
                        .build();

                /* Send the response to the client. */
                responseObserver.onNext(response);

                /* Complete the request. */
                responseObserver.onCompleted();
                
        }

        /* Updates the water quality information. */
        @Override
        public void updateQuality(QualityData request,
                                StreamObserver<UpdateResponse> responseObserver) {

        /* Display the received data. */
        System.out.println("Water quality updated:");
        System.out.println("Sensor: " + request.getSensorId());
        System.out.println("pH: " + request.getPhLevel());
        System.out.println("Temperature: " + request.getTemperature());
        System.out.println("Turbidity: " + request.getTurbidity());
        System.out.println("Safe: " + request.getSafe());

        /* Create the response message. */
        UpdateResponse response = UpdateResponse.newBuilder()
                .setMessage("Water quality updated successfully.")
                .build();

        /* Send the response. */
        responseObserver.onNext(response);

        /* Complete the request. */
        responseObserver.onCompleted();

        }

        /* Streams water quality updates to the client. */
        @Override
        public void streamQualityUpdates(QualityRequest request,
                                        StreamObserver<QualityResponse> responseObserver) {

        /* Send five sample updates. */
        for (int i = 0; i < 5; i++) {

                /* Check if the client cancelled the request. */
                if (io.grpc.Context.current().isCancelled()) {

                        System.out.println("Client cancelled the stream.");

                        return;
                }

                /* Create a sample response. */
                QualityResponse response = QualityResponse.newBuilder()
                        .setPhLevel(7.2 + (i * 0.1))
                        .setTurbidity(1.3 + (i * 0.1))
                        .setTemperature(18.5 + i)
                        .setSafe(true)
                        .build();

                /* Send the response to the client. */
                responseObserver.onNext(response);

                /* Wait before sending the next update. */
                try {
                TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
                }

        }

        /* Complete the stream. */
        responseObserver.onCompleted();

        }

}