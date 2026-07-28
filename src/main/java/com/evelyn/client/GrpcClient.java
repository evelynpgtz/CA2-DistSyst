package com.evelyn.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import com.evelyn.proto.waterquality.WaterQualityServiceGrpc;
import com.evelyn.proto.waterconsumption.WaterConsumptionServiceGrpc;
import com.evelyn.proto.leakdetection.LeakDetectionServiceGrpc;

import com.evelyn.proto.waterquality.QualityRequest;
import com.evelyn.proto.waterquality.QualityResponse;
import com.evelyn.proto.waterquality.QualityData;
import com.evelyn.proto.waterquality.UpdateResponse;

import com.evelyn.proto.waterconsumption.ConsumptionRequest;
import com.evelyn.proto.waterconsumption.ConsumptionResponse;
import com.evelyn.proto.waterconsumption.ConsumptionRecord;
import com.evelyn.proto.waterconsumption.ConsumptionSummary;

import com.evelyn.proto.leakdetection.LeakRequest;
import com.evelyn.proto.leakdetection.LeakResponse;

import javax.jmdns.ServiceInfo;

/* This class creates the gRPC client connection. */
public class GrpcClient {

    /* Communication channel with the gRPC server. */
    private ManagedChannel channel;

    /* Stub for the Water Quality service. */
    private WaterQualityServiceGrpc.WaterQualityServiceBlockingStub waterQualityStub;

    /* Async stub for the Water Quality service. */
    private WaterQualityServiceGrpc.WaterQualityServiceStub waterQualityAsyncStub;

    /* Stub for the Water Consumption service. */
    private WaterConsumptionServiceGrpc.WaterConsumptionServiceBlockingStub waterConsumptionStub;

    /* Async stub for the Water Consumption service. */
    private WaterConsumptionServiceGrpc.WaterConsumptionServiceStub waterConsumptionAsyncStub;

    /* Stub for the Leak Detection service. */
    private LeakDetectionServiceGrpc.LeakDetectionServiceBlockingStub leakDetectionStub;

    /* Async stub for the Leak Detection service. */
    private LeakDetectionServiceGrpc.LeakDetectionServiceStub leakDetectionAsyncStub;

    /* Creates the client connection. */
    public GrpcClient(String host, int port) {

        /* Build the communication channel. */
        channel = ManagedChannelBuilder
                .forAddress(host, port)
                .usePlaintext()
                .build();

        /* Create the Water Quality stub. */
        waterQualityStub = WaterQualityServiceGrpc.newBlockingStub(channel);
        waterQualityAsyncStub = WaterQualityServiceGrpc.newStub(channel);

        /* Create the Water Consumption stub. */
        waterConsumptionStub = WaterConsumptionServiceGrpc.newBlockingStub(channel);
        waterConsumptionAsyncStub = WaterConsumptionServiceGrpc.newStub(channel);

        /* Create the Leak Detection stub. */
        leakDetectionStub = LeakDetectionServiceGrpc.newBlockingStub(channel);
        leakDetectionAsyncStub = LeakDetectionServiceGrpc.newStub(channel);

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

    /* Updates the water quality information. */
    public UpdateResponse updateQuality(QualityData qualityData) {

        /* Send the request to the server. */
        return waterQualityStub.updateQuality(qualityData);

    }

    /* Starts the water quality streaming service. */
    public void streamQualityUpdates(
            QualityRequest request,
            StreamObserver<QualityResponse> responseObserver) {

        /* Send the streaming request to the server. */
        waterQualityAsyncStub.streamQualityUpdates(request, responseObserver);

    }

    /* Gets the current water consumption information. */
    public ConsumptionResponse getConsumption(String householdId) {

        /* Create the request message. */
        ConsumptionRequest request = ConsumptionRequest.newBuilder()
                .setHouseholdId(householdId)
                .build();

        /* Send the request to the server. */
        return waterConsumptionStub.getConsumption(request);

    }

    /* Starts the client streaming for water consumption records. */
    public StreamObserver<ConsumptionRecord> uploadConsumptionRecords(
            StreamObserver<ConsumptionSummary> responseObserver) {

        /* Send the client streaming request to the server. */
        return waterConsumptionAsyncStub.uploadConsumptionRecords(responseObserver);

    }

    /* Reports a leak to the server. */
    public LeakResponse reportLeak(String location, int severity) {

        /* Create the request message. */
        LeakRequest request = LeakRequest.newBuilder()
                .setLocation(location)
                .setSeverity(severity)
                .build();

        /* Send the request to the server. */
        return leakDetectionStub.reportLeak(request);

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
        waterQualityAsyncStub = WaterQualityServiceGrpc.newStub(channel);

        /* Create the Water Consumption stub. */
        waterConsumptionStub = WaterConsumptionServiceGrpc.newBlockingStub(channel);
        waterConsumptionAsyncStub = WaterConsumptionServiceGrpc.newStub(channel);

        /* Create the Leak Detection stub. */
        leakDetectionStub = LeakDetectionServiceGrpc.newBlockingStub(channel);
        leakDetectionAsyncStub = LeakDetectionServiceGrpc.newStub(channel);

    }

    /* Closes the communication channel. */
    public void shutdown() {

        if (channel != null) {
            channel.shutdown();
        }

    }

}