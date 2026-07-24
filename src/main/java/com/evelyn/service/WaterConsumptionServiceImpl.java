package com.evelyn.service;

import com.evelyn.proto.waterconsumption.ConsumptionRecord;
import com.evelyn.proto.waterconsumption.ConsumptionSummary;
import com.evelyn.proto.waterconsumption.WaterConsumptionServiceGrpc;
import io.grpc.stub.StreamObserver;

/* This class implements the Water Consumption gRPC service.
 * It receives multiple consumption records from the client. */
public class WaterConsumptionServiceImpl extends WaterConsumptionServiceGrpc.WaterConsumptionServiceImplBase {

    /* Receives multiple consumption records from the client.
     * When the client finishes, the server sends a summary. */
    @Override
    public StreamObserver<ConsumptionRecord> uploadConsumptionRecords(
            StreamObserver<ConsumptionSummary> responseObserver) {

        return new StreamObserver<ConsumptionRecord>() {

            private int recordsReceived = 0;
            private double totalConsumption = 0;

            @Override
            public void onNext(ConsumptionRecord record) {

                /* Count each record received from the client. */
                recordsReceived++;

                /* Add the litres value to the total consumption. */
                totalConsumption += record.getLitres();

                /* Display the received record in the server console. */
                System.out.println("Record received: "
                        + record.getHouseholdId()
                        + " - "
                        + record.getLitres()
                        + " litres");

            }

            @Override
            public void onError(Throwable throwable) {

                /* Display the error message in the server console. */
                System.out.println("Error: " + throwable.getMessage());

            }

            @Override
            public void onCompleted() {

                /* Build the summary with the received information. */
                ConsumptionSummary summary = ConsumptionSummary.newBuilder()
                        .setRecordsReceived(recordsReceived)
                        .setTotalConsumption(totalConsumption)
                        .build();

                /* Send the summary back to the client. */
                responseObserver.onNext(summary);

                /* Complete the communication. */
                responseObserver.onCompleted();

            }

        };

    }

}