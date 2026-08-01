package com.evelyn.client;

import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.stub.MetadataUtils;
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

import com.evelyn.proto.leakdetection.LeakUpdate;
import com.evelyn.proto.leakdetection.LeakAlert;

import javax.jmdns.ServiceInfo;
import java.util.concurrent.TimeUnit;

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

        /* Create the metadata. */
        Metadata metadata = new Metadata();

        Metadata.Key<String> clientKey =
                Metadata.Key.of(
                        "client-name",
                        Metadata.ASCII_STRING_MARSHALLER);

        Metadata.Key<String> userKey =
                Metadata.Key.of(
                        "user-name",
                        Metadata.ASCII_STRING_MARSHALLER);

        Metadata.Key<String> apiKey =
                Metadata.Key.of(
                        "api-key",
                        Metadata.ASCII_STRING_MARSHALLER);

        metadata.put(clientKey, "SmartWaterGUI");
        metadata.put(userKey, "CommunityOperator");
        metadata.put(apiKey, "smart-water-key");

        /* Create the metadata interceptor. */
        ClientInterceptor metadataInterceptor =
        MetadataUtils.newAttachHeadersInterceptor(metadata);

        /* Create the Water Quality stubs. */
        waterQualityStub =
        WaterQualityServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        waterQualityAsyncStub =
        WaterQualityServiceGrpc
                .newStub(channel)
                .withInterceptors(metadataInterceptor);

        /* Create the Water Consumption stubs. */
        waterConsumptionStub =
        WaterConsumptionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        waterConsumptionAsyncStub =
        WaterConsumptionServiceGrpc
                .newStub(channel)
                .withInterceptors(metadataInterceptor);

        /* Create the Leak Detection stubs. */
        leakDetectionStub =
        LeakDetectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        leakDetectionAsyncStub =
        LeakDetectionServiceGrpc
                .newStub(channel)
                .withInterceptors(metadataInterceptor);

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

    /* Starts the bidirectional streaming for leak monitoring. */
    public StreamObserver<LeakUpdate> startLeakMonitoring(
            StreamObserver<LeakAlert> responseObserver) {

        /* Start the bidirectional streaming connection. */
        return leakDetectionAsyncStub.liveLeakMonitoring(responseObserver);

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

        /* Create the metadata. */
        Metadata metadata = new Metadata();

        Metadata.Key<String> clientKey =
                Metadata.Key.of(
                        "client-name",
                        Metadata.ASCII_STRING_MARSHALLER);

        Metadata.Key<String> userKey =
                Metadata.Key.of(
                        "user-name",
                        Metadata.ASCII_STRING_MARSHALLER);
        
        Metadata.Key<String> apiKey =
                Metadata.Key.of(
                        "api-key",
                        Metadata.ASCII_STRING_MARSHALLER);
        

        metadata.put(clientKey, "SmartWaterGUI");
        metadata.put(userKey, "CommunityOperator");
        metadata.put(apiKey, "smart-water-key");
        
        /* Create the metadata interceptor. */
        ClientInterceptor metadataInterceptor =
        MetadataUtils.newAttachHeadersInterceptor(metadata);

        /* Create the Water Quality stubs. */
        waterQualityStub =
        WaterQualityServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        waterQualityAsyncStub =
                WaterQualityServiceGrpc
                        .newStub(channel)
                        .withInterceptors(metadataInterceptor);

        /* Create the Water Consumption stubs. */
        waterConsumptionStub =
        WaterConsumptionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        waterConsumptionAsyncStub =
        WaterConsumptionServiceGrpc
                .newStub(channel)
                .withInterceptors(metadataInterceptor);

        /* Create the Leak Detection stubs. */
        leakDetectionStub =
        LeakDetectionServiceGrpc
                .newBlockingStub(channel)
                .withInterceptors(metadataInterceptor)
                .withDeadlineAfter(3, TimeUnit.SECONDS);

        leakDetectionAsyncStub =
        LeakDetectionServiceGrpc
                .newStub(channel)
                .withInterceptors(metadataInterceptor);

    }

        /* Closes the communication channel. */
        public void shutdown() {

                if (channel != null) {
                channel.shutdown();
                }

        }

        /* Cancels the current streaming request. */
                public void cancelStream() {

                if (channel != null) {

                        channel.shutdownNow();

                }

        }

}