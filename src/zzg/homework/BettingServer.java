package zzg.homework;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class BettingServer {
    //    public static final int SESSION_DURATION = 10 * 60 * 1000;
    public static final int SESSION_DURATION = 60 * 1000;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/index", new IndexHandler());
        server.createContext("/", new BettingHandler());
        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
    }
}
