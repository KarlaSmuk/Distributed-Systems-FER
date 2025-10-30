package hr.fer.tel.rassus.client;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SensorRPCServer {
    private static final Logger logger = Logger.getLogger(SensorRPCServer.class.getName());

    private Server server;
    private final int port;

    public SensorRPCServer(int port) {
        this.port = port;
    }

    /**
     * Start the server.
     *
     * @throws IOException the io exception
     */
    public void start() throws IOException {
        // Register the service
        server = ServerBuilder.forPort(port)
                .addService(new SensorService())
                .build()
                .start();
        logger.info("Server started on " + port);

        //  Clean shutdown of server in case of JVM shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            try {
                SensorRPCServer.this.stop();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
            System.err.println("Server shut down");
        }));
    }

    /**
     * Stops the server.
     *
     * @throws InterruptedException the interrupted exception
     */
    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread
     *
     * @throws InterruptedException the interrupted exception
     */
    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final SensorRPCServer server = new SensorRPCServer(3000);
        server.start();
        server.blockUntilShutdown();
    }
}