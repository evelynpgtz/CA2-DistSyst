package com.evelyn.server;

import com.evelyn.service.LeakDetectionServiceImpl;
import com.evelyn.service.WaterConsumptionServiceImpl;
import com.evelyn.service.WaterQualityServiceImpl;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/* This class creates and starts the gRPC server.
 * It hosts all the project services. */
public class GrpcServer {

    /* Port used by the gRPC server. */
    private static final int PORT = 50051;

    /* gRPC server instance. */
    private Server server;

    /* Creates the gRPC server and registers all services. */
    public GrpcServer() {

        server = ServerBuilder.forPort(PORT)

                /* Register the Water Quality service. */
                .addService(new WaterQualityServiceImpl())

                /* Register the Water Consumption service. */
                .addService(new WaterConsumptionServiceImpl())

                /* Register the Leak Detection service. */
                .addService(new LeakDetectionServiceImpl())

                .build();
    }

    /* Starts the gRPC server and waits for client requests. */
    public void start() throws Exception {

        server.start();

        System.out.println("gRPC Server started on port " + PORT);

        server.awaitTermination();

    }

}