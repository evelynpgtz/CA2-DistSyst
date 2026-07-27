package com.evelyn.client;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

/* This class discovers services using JmDNS. */
public class ServiceDiscovery {

    /* JmDNS instance used to discover services. */
    private JmDNS jmdns;

    /* Creates the JmDNS instance. */
    public ServiceDiscovery() throws IOException {

        jmdns = JmDNS.create(InetAddress.getLocalHost());

    }

    /* Finds a service using JmDNS. */
    public ServiceInfo discoverService(String serviceType) throws IOException {

        /* Search for the requested service. */
        ServiceInfo serviceInfo = jmdns.getServiceInfo(serviceType, "", 5000);

        /* Check if the service was found. */
        if (serviceInfo != null) {

            System.out.println("Service found: " + serviceInfo.getName());

        } else {

            System.out.println("Service not found.");

        }

        return serviceInfo;

    }

}