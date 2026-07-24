package com.evelyn.service;

import com.evelyn.model.WaterQuality;
import com.evelyn.proto.waterquality.QualityRequest;
import com.evelyn.proto.waterquality.QualityResponse;
import com.evelyn.proto.waterquality.WaterQualityServiceGrpc;
import io.grpc.stub.StreamObserver;

/* This class implements the Water Quality gRPC service.
 * It receives client requests and sends back water quality information. */
public class WaterQualityServiceImpl extends WaterQualityServiceGrpc.WaterQualityServiceImplBase {

    /* Returns the current water quality information.
     * This method is called when the client requests the latest data. */
    @Override
    public void getCurrentQuality(QualityRequest request,
                                  StreamObserver<QualityResponse> responseObserver) {

        // Create a sample water quality object.
        WaterQuality waterQuality = new WaterQuality(
                "Sensor-001",
                7.2,
                1.3,
                18.5,
                true
        );

        // Build the gRPC response using the model data.
        QualityResponse response = QualityResponse.newBuilder()
                .setSensorId(waterQuality.getSensorId())
                .setPhLevel(waterQuality.getPhLevel())
                .setTurbidity(waterQuality.getTurbidity())
                .setTemperature(waterQuality.getTemperature())
                .setSafe(waterQuality.isSafe())
                .build();

        // Send the response to the client.
        responseObserver.onNext(response);

        // Complete the request.
        responseObserver.onCompleted();
    }

}