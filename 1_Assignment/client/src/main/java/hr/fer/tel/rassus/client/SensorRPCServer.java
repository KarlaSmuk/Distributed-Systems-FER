package hr.fer.tel.rassus.client;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SensorRPCServer {
    private static final Logger logger = Logger.getLogger(SensorRPCServer.class.getName());

    private Server server;
    private final SensorRPCClient sensorRPCClient;

    public SensorRPCServer(SensorRPCClient sensorRPCClient) {
        this.sensorRPCClient = sensorRPCClient;
    }

    public int getPort() {
        return server.getPort();
    }

    public void start() throws IOException {
        // Register the service
        this.server = ServerBuilder.forPort(0) // 0 to find free port
                .addService(new SensorService(this.sensorRPCClient))
                .build()
                .start();

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

    public void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}