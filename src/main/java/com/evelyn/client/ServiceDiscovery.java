package com.evelyn.client;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/* This class discovers services using JmDNS. */
public class ServiceDiscovery {

    /* JmDNS instance used to discover services. */
    private JmDNS jmdns;

    /* Stores the discovered service. */
    private ServiceInfo serviceInfo;

    /* Creates the JmDNS instance. */
    public ServiceDiscovery() throws IOException {

        jmdns = JmDNS.create(InetAddress.getLocalHost());

    }

    /* Finds a service using JmDNS. */
    public ServiceInfo discoverService(String serviceType) throws IOException {

        /* Listen for the requested service. */
        jmdns.addServiceListener(serviceType, new ServiceListener() {

            @Override
            public void serviceAdded(ServiceEvent event) {

                System.out.println("Service added: " + event.getName());

                jmdns.requestServiceInfo(
                        event.getType(),
                        event.getName(),
                        true);

            }

            @Override
            public void serviceRemoved(ServiceEvent event) {

                System.out.println("Service removed: " + event.getName());

            }

            @Override
            public void serviceResolved(ServiceEvent event) {

                System.out.println("Service resolved: " + event.getInfo());

                serviceInfo = event.getInfo();

            }

        });

        /* Wait for the service to be resolved. */
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        /* Check if the service was found. */
        if (serviceInfo != null) {

            System.out.println("Service found: " + serviceInfo.getName());

        } else {

            System.out.println("Service not found.");

        }

        return serviceInfo;

    }

}