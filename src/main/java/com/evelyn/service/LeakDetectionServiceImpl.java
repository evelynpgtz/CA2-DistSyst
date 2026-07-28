package com.evelyn.service;

import com.evelyn.proto.leakdetection.LeakAlert;
import com.evelyn.proto.leakdetection.LeakUpdate;
import com.evelyn.proto.leakdetection.LeakRequest;
import com.evelyn.proto.leakdetection.LeakResponse;
import com.evelyn.proto.leakdetection.LeakDetectionServiceGrpc;
import io.grpc.stub.StreamObserver;

/* This class implements the Leak Detection gRPC service.
 * It receives leak updates and sends alerts to the client. */
public class LeakDetectionServiceImpl extends LeakDetectionServiceGrpc.LeakDetectionServiceImplBase {


        /* Receives leak updates from the client and sends alerts back. */
        @Override
        public StreamObserver<LeakUpdate> liveLeakMonitoring(
                StreamObserver<LeakAlert> responseObserver) {

            return new StreamObserver<LeakUpdate>() {

                @Override
                public void onNext(LeakUpdate update) {

                /* Create an alert using the received leak information. */
                LeakAlert alert = LeakAlert.newBuilder()
                        .setMessage("Leak detected at " + update.getLocation())
                        .setRecommendation("Check the area immediately.")
                        .build();

                /* Send the alert back to the client. */
                responseObserver.onNext(alert);

            }

                @Override
                public void onError(Throwable throwable) {

                    /* Display the error message in the server console. */
                    System.out.println("Error: " + throwable.getMessage());

                }

                @Override
                public void onCompleted() {

                    /* Complete the communication with the client. */
                    responseObserver.onCompleted();

                }

            };

        }

        /* Reports a leak to the server. */
        @Override
        public void reportLeak(LeakRequest request,
                            StreamObserver<LeakResponse> responseObserver) {

            /* Create the response message. */
            LeakResponse response = LeakResponse.newBuilder()
                    .setLeakId("LEAK-001")
                    .setStatus("Leak reported successfully.")
                    .build();

            /* Display the received information. */
            System.out.println("Leak reported:");
            System.out.println("Location: " + request.getLocation());
            System.out.println("Severity: " + request.getSeverity());

            /* Send the response to the client. */
            responseObserver.onNext(response);

            /* Complete the request. */
            responseObserver.onCompleted();

        }
}