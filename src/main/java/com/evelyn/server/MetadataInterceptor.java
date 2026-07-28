package com.evelyn.server;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

/* This interceptor reads client metadata. */
public class MetadataInterceptor implements ServerInterceptor {
    
        /* Valid API key for client authentication. */
        private static final String API_KEY = "smart-water-key";    

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

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

        String receivedKey = headers.get(apiKey);

        if (!API_KEY.equals(receivedKey)) {

                call.close(
                        Status.UNAUTHENTICATED
                                .withDescription("Invalid API Key"),
                        new Metadata());

                return new ServerCall.Listener<ReqT>() {};
        }


        System.out.println("========== Client Metadata ==========");
        System.out.println("Client: " + headers.get(clientKey));
        System.out.println("User: " + headers.get(userKey));
        System.out.println("=====================================");

        return next.startCall(call, headers);
    }
}