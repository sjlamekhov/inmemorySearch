// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Changes.proto

package networking.protobuf;

public final class Changes {
  private Changes() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_networking_protobuf_ChangeRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_networking_protobuf_ChangeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_networking_protobuf_ChangeAck_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_networking_protobuf_ChangeAck_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rChanges.proto\022\023networking.protobuf\"3\n\r" +
      "ChangeRequest\022\021\n\ttimestamp\030\001 \001(\t\022\017\n\007mess" +
      "age\030\002 \001(\t\"\034\n\tChangeAck\022\017\n\007message\030\001 \001(\t2" +
      "^\n\rGossipService\022M\n\007process\022\".networking" +
      ".protobuf.ChangeRequest\032\036.networking.pro" +
      "tobuf.ChangeAckB\005P\001\210\001\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_networking_protobuf_ChangeRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_networking_protobuf_ChangeRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_networking_protobuf_ChangeRequest_descriptor,
        new java.lang.String[] { "Timestamp", "Message", });
    internal_static_networking_protobuf_ChangeAck_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_networking_protobuf_ChangeAck_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_networking_protobuf_ChangeAck_descriptor,
        new java.lang.String[] { "Message", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
