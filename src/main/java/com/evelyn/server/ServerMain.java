package com.evelyn.server;

import com.evelyn.service.ServiceRegistrar;

/* This class starts the gRPC server application. */
public class ServerMain {

    /* Starts the server application. */
    public static void main(String[] args) throws Exception {

        /* Create the gRPC server object. */
        GrpcServer grpcServer = new GrpcServer();

        /* Register services using JmDNS. */
        ServiceRegistrar registrar = new ServiceRegistrar();

        registrar.registerService(
                "_waterquality._tcp.local.",
                "Water Quality Service",
                50051,
                "Water Quality Monitoring");

        registrar.registerService(
                "_waterconsumption._tcp.local.",
                "Water Consumption Service",
                50051,
                "Water Consumption Monitoring");

        registrar.registerService(
                "_leakdetection._tcp.local.",
                "Leak Detection Service",
                50051,
                "Leak Detection Monitoring");

        /* Start the gRPC server. */
        grpcServer.start();

    }

}