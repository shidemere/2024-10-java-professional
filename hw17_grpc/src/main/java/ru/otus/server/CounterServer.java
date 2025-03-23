package ru.otus.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CounterServer {

    private static final int PORT = 8093;

    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Starting counter server");

        Server server =
                ServerBuilder.forPort(PORT).addService(new CounterServerImpl()).build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Received shutdown request");
            server.shutdown();
            log.info("Server stopped");
        }));

        log.info("Server is waiting for client, port:{}", PORT);
        server.awaitTermination();
    }
}
