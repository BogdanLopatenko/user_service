package com.user_service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.UUID;

public abstract class AbstractGrpcIntegrationTest extends AbstractIntegrationTest{

    private static final String GRPC_NAME =
            "grpc-test-" + UUID.randomUUID();

    @DynamicPropertySource
    static void grpcProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "grpc.server.in-process-name",
                () -> GRPC_NAME
        );

        registry.add(
                "grpc.client.inProcess.address",
                () -> "in-process:" + GRPC_NAME
        );
    }
}
