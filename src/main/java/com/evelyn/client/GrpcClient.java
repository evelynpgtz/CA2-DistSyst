package com.evelyn.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import com.evelyn.proto.waterquality.WaterQualityServiceGrpc;
import com.evelyn.proto.waterconsumption.WaterConsumptionServiceGrpc;
import com.evelyn.proto.leakdetection.LeakDetectionServiceGrpc;

import com.evelyn.proto.waterquality.QualityRequest;
import com.evelyn.proto.waterquality.QualityResponse;

import javax.jmdns.ServiceInfo;

/* This class creates the gRPC client connection. */
public class GrpcClient {

    /* Communication channel with the gRPC server. */
    private ManagedChannel channel;

    /* Stub for the Water Quality service. */
    private WaterQualityServiceGrpc.WaterQualityServiceBlockingStub waterQualityStub;

    /* Stub for the Water Consumption service. */
    private WaterConsumptionServiceGrpc.WaterConsumptionServiceBlockingStub waterConsumptionStub;

    /* Stub for the Leak Detection service. */
    private LeakDetectionServiceGrpc.LeakDetectionServiceBlockingStub leakDetectionStub;

    /* Creates the client connection. */
    public GrpcClient(String host, int port) {

        /* Build the communication channel. */
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();
        
        /* Create the Water Quality stub. */
        waterQualityStub = WaterQualityServiceGrpc.newBlockingStub(channel);

        /* Create the Water Consumption stub. */
        waterConsumptionStub = WaterConsumptionServiceGrpc.newBlockingStub(channel);

        /* Create the Leak Detection stub. */
        leakDetectionStub = LeakDetectionServiceGrpc.newBlockingStub(channel);        
    }

    /* Gets the current water quality information. */
    public QualityResponse getCurrentQuality(String sensorId) {

        /* Create the request message. */
        QualityRequest request = QualityRequest.newBuilder()
                .setSensorId(sensorId)
                .build();

        /* Send the request to the server. */
        return waterQualityStub.getCurrentQuality(request);

    }

    /* Creates the client connection using ServiceInfo. */
    public GrpcClient(ServiceInfo serviceInfo) {

        /* Build the communication channel. */
        channel = ManagedChannelBuilder
                .forAddress(
                        serviceInfo.getHostAddresses()[0],
                        serviceInfo.getPort())
                .usePlaintext()
                .build();

        /* Create the Water Quality stub. */
        waterQualityStub = WaterQualityServiceGrpc.newBlockingStub(channel);

        /* Create the Water Consumption stub. */
        waterConsumptionStub = WaterConsumptionServiceGrpc.newBlockingStub(channel);

        /* Create the Leak Detection stub. */
        leakDetectionStub = LeakDetectionServiceGrpc.newBlockingStub(channel);

    }

    /* Closes the communication channel. */
    public void shutdown() {

        if (channel != null) {
            channel.shutdown();
        }

    }

}