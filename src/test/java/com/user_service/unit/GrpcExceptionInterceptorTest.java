package com.user_service.unit;

import com.user_service.grpc.GrpcExceptionMapper;
import com.user_service.grpc.interceptor.GrpcExceptionInterceptor;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GrpcExceptionInterceptorTest {

    @InjectMocks
    private GrpcExceptionInterceptor interceptor;

    @Mock
    private GrpcExceptionMapper grpcExceptionMapper;

    @Mock
    private ServerCall<Object, Object> serverCall;

    @Mock
    private ServerCallHandler<Object, Object> serverCallHandler;

    @Mock
    private ServerCall.Listener<Object> listener;

    private Metadata metadata;

    @BeforeEach
    void setUp() {
        metadata = new Metadata();
    }

    @Test
    void shouldReturnListenerWhenCallStartsSuccessfully() {

        when(serverCallHandler.startCall(serverCall, metadata))
                .thenReturn(listener);

        ServerCall.Listener<Object> result = interceptor.interceptCall(
                serverCall,
                metadata,
                serverCallHandler
        );

        assertNotNull(result);

        verify(serverCallHandler)
                .startCall(serverCall, metadata);

        verify(serverCall, never())
                .close(any(), any());
    }

    @Test
    void shouldCloseCallWithInternalStatusWhenExceptionOccursInStartCall() {

        RuntimeException exception =
                new RuntimeException("Unexpected error");

        when(serverCallHandler.startCall(serverCall, metadata))
                .thenThrow(exception);

        when(grpcExceptionMapper.getStatusCode(exception))
                .thenReturn(Status.Code.INTERNAL);

        interceptor.interceptCall(
                serverCall,
                metadata,
                serverCallHandler
        );

        verify(serverCall).close(
                argThat(status ->
                        status.getCode() == Status.Code.INTERNAL
                                && status.getDescription()
                                .equals("Unexpected error")
                ),
                any(Metadata.class)
        );
    }

    @Test
    void shouldCloseCallWithMappedStatusWhenExceptionOccursInOnHalfClose() {

        RuntimeException exception =
                new RuntimeException("Half close error");

        when(serverCallHandler.startCall(serverCall, metadata))
                .thenReturn(listener);

        when(grpcExceptionMapper.getStatusCode(exception))
                .thenReturn(Status.Code.INVALID_ARGUMENT);

        ServerCall.Listener<Object> result = interceptor.interceptCall(
                serverCall,
                metadata,
                serverCallHandler
        );

        doThrow(exception)
                .when(listener)
                .onHalfClose();

        result.onHalfClose();

        verify(serverCall).close(
                argThat(status ->
                        status.getCode() == Status.Code.INVALID_ARGUMENT
                                && status.getDescription()
                                .equals("Half close error")
                ),
                any(Metadata.class)
        );
    }

    @Test
    void shouldCloseCallWithOriginalStatusWhenStatusRuntimeExceptionIsThrown() {

        StatusRuntimeException exception =
                Status.NOT_FOUND
                        .withDescription("User not found")
                        .asRuntimeException();

        when(serverCallHandler.startCall(serverCall, metadata))
                .thenReturn(listener);

        ServerCall.Listener<Object> result = interceptor.interceptCall(
                serverCall,
                metadata,
                serverCallHandler
        );

        doThrow(exception)
                .when(listener)
                .onHalfClose();

        result.onHalfClose();

        verify(serverCall).close(
                argThat(status ->
                        status.getCode() == Status.Code.NOT_FOUND
                                && status.getDescription()
                                .equals("User not found")
                ),
                any()
        );

        verify(grpcExceptionMapper, never())
                .getStatusCode(any());
    }
}
