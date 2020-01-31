package networking.protobuf;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.16.1)",
    comments = "Source: Changes.proto")
public final class GossipServiceGrpc {

  private GossipServiceGrpc() {}

  public static final String SERVICE_NAME = "networking.protobuf.GossipService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<networking.protobuf.ChangeRequest,
      networking.protobuf.ChangeAck> getProcessMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "process",
      requestType = networking.protobuf.ChangeRequest.class,
      responseType = networking.protobuf.ChangeAck.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<networking.protobuf.ChangeRequest,
      networking.protobuf.ChangeAck> getProcessMethod() {
    io.grpc.MethodDescriptor<networking.protobuf.ChangeRequest, networking.protobuf.ChangeAck> getProcessMethod;
    if ((getProcessMethod = GossipServiceGrpc.getProcessMethod) == null) {
      synchronized (GossipServiceGrpc.class) {
        if ((getProcessMethod = GossipServiceGrpc.getProcessMethod) == null) {
          GossipServiceGrpc.getProcessMethod = getProcessMethod = 
              io.grpc.MethodDescriptor.<networking.protobuf.ChangeRequest, networking.protobuf.ChangeAck>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "networking.protobuf.GossipService", "process"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  networking.protobuf.ChangeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  networking.protobuf.ChangeAck.getDefaultInstance()))
                  .setSchemaDescriptor(new GossipServiceMethodDescriptorSupplier("process"))
                  .build();
          }
        }
     }
     return getProcessMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GossipServiceStub newStub(io.grpc.Channel channel) {
    return new GossipServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GossipServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new GossipServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GossipServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new GossipServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class GossipServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void process(networking.protobuf.ChangeRequest request,
        io.grpc.stub.StreamObserver<networking.protobuf.ChangeAck> responseObserver) {
      asyncUnimplementedUnaryCall(getProcessMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getProcessMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                networking.protobuf.ChangeRequest,
                networking.protobuf.ChangeAck>(
                  this, METHODID_PROCESS)))
          .build();
    }
  }

  /**
   */
  public static final class GossipServiceStub extends io.grpc.stub.AbstractStub<GossipServiceStub> {
    private GossipServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GossipServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GossipServiceStub(channel, callOptions);
    }

    /**
     */
    public void process(networking.protobuf.ChangeRequest request,
        io.grpc.stub.StreamObserver<networking.protobuf.ChangeAck> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getProcessMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class GossipServiceBlockingStub extends io.grpc.stub.AbstractStub<GossipServiceBlockingStub> {
    private GossipServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GossipServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GossipServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public networking.protobuf.ChangeAck process(networking.protobuf.ChangeRequest request) {
      return blockingUnaryCall(
          getChannel(), getProcessMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class GossipServiceFutureStub extends io.grpc.stub.AbstractStub<GossipServiceFutureStub> {
    private GossipServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private GossipServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GossipServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new GossipServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<networking.protobuf.ChangeAck> process(
        networking.protobuf.ChangeRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getProcessMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_PROCESS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final GossipServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(GossipServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PROCESS:
          serviceImpl.process((networking.protobuf.ChangeRequest) request,
              (io.grpc.stub.StreamObserver<networking.protobuf.ChangeAck>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class GossipServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GossipServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return networking.protobuf.Changes.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GossipService");
    }
  }

  private static final class GossipServiceFileDescriptorSupplier
      extends GossipServiceBaseDescriptorSupplier {
    GossipServiceFileDescriptorSupplier() {}
  }

  private static final class GossipServiceMethodDescriptorSupplier
      extends GossipServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GossipServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (GossipServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GossipServiceFileDescriptorSupplier())
              .addMethod(getProcessMethod())
              .build();
        }
      }
    }
    return result;
  }
}
