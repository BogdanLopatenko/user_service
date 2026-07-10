package com.user_service.grpc.interceptor;

import com.user_service.constant.ExceptionConstant;
import com.user_service.grpc.GrpcExceptionMapper;
import io.grpc.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.stereotype.Component;


@Component
@GrpcGlobalServerInterceptor
@RequiredArgsConstructor
public class GrpcExceptionInterceptor implements ServerInterceptor {

    private final GrpcExceptionMapper grpcExceptionMapper;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next
    ) {

        ServerCall.Listener<ReqT> delegate;

        try {
            delegate = next.startCall(call, headers);
        } catch (Exception e) {
            handleException(call, e);
            return new ServerCall.Listener<>() {
            };
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (Throwable e) {
                    handleException(call, e);
                }
            }
        };
    }

    private void handleException(ServerCall<?, ?> call, Throwable t) {

        if (t instanceof StatusRuntimeException ex) {
            call.close(ex.getStatus(), ex.getTrailers());
            return;
        }

        Status.Code statusCode = grpcExceptionMapper.getStatusCode(t);

        String description = t.getMessage() != null
                ? t.getMessage()
                : ExceptionConstant.UNEXPECTED_INTERNAL_ERROR;

        Status status = Status
                .fromCode(statusCode)
                .withDescription(description);

        Metadata metadata = new Metadata();

        call.close(status, metadata);
    }
}
