package com.evelyn.service;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/* This class registers gRPC services using JmDNS. */
public class ServiceRegistrar {

    /* JmDNS instance used to register services. */
    private JmDNS jmdns;

    /* Creates the JmDNS instance. */
    public ServiceRegistrar() throws IOException {

        /* Create the JmDNS object using the local address. */
        jmdns = JmDNS.create(InetAddress.getLocalHost());

    }

    /* Registers a service in JmDNS. */
    public void registerService(String serviceType,
                                String serviceName,
                                int port,
                                String description) throws IOException {

        /* Create the service information. */
        ServiceInfo serviceInfo = ServiceInfo.create(
                serviceType,
                serviceName,
                port,
                description);

        /* Register the service in JmDNS. */
        jmdns.registerService(serviceInfo);

        System.out.println(serviceName + " registered successfully.");

    }

    /* Closes the JmDNS instance. */
    public void close() throws IOException {

        if (jmdns != null) {
            jmdns.close();
        }

    }

}