package hr.fer.tel.rassus.client;

import hr.fer.tel.rassus.client.Message;
import hr.fer.tel.rassus.client.UppercaseGrpc;
import io.grpc.stub.StreamObserver;
import java.util.logging.Logger;

/**
 * The type Uppercase service.
 */
public class UppercaseService extends UppercaseGrpc.UppercaseImplBase {
  private static final Logger logger = Logger.getLogger(UppercaseService.class.getName());


  @Override
  public void requestUppercase(
          Message request, StreamObserver<Message> responseObserver
  ) {
    logger.info("Got a new message: " + request.getPayload());


    // Create response
    Message response = Message.newBuilder().setPayload(request.getPayload().toUpperCase()).build();
    // Send response
    responseObserver.onNext(
        response
    );

    logger.info("Responding with: " + response.getPayload());
    // Send a notification of successful stream completion.
    responseObserver.onCompleted();
  }
}
