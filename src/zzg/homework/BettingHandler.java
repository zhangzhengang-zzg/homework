package zzg.homework;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class BettingHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(BettingHandler.class.getName());
    //session
    private final ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();
    //betoffers
    private final ConcurrentHashMap<Integer, Betoffer> betoffers = new ConcurrentHashMap<>();

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String path = httpExchange.getRequestURI().getPath();
        //uri解析
        String[] parts = path.split("/");
        if (parts.length != 3) {
            httpExchange.sendResponseHeaders(422, -1L);
        }
        String part = parts[parts.length - 1];
        //异常
        int id = Integer.parseInt(parts[1]);
        switch (part) {
            case "session":
                sessionHandler(httpExchange, id);
                break;
            case "stake":
                stakeHandler(httpExchange, id);
                break;
            case "highstakes":
                highstakesHandler(httpExchange, id);
                break;
            default:
                defaultHandler(httpExchange);
                break;
        }
    }

    private void defaultHandler(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(404, -1L);
    }

    /**
     * 处理highstakes请求
     *
     * @param httpExchange
     * @param betofferId
     */
    private void highstakesHandler(HttpExchange httpExchange, int betofferId) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(405, -1L);
            return;
        }
        Betoffer betoffer = betoffers.get(betofferId);
        if (betoffer == null) {
            httpExchange.sendResponseHeaders(403, -1L);
            return;
        }
        List<Stake> highstakes = betoffer.getHighstakes();
        String response = highstakes.toString();
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }

    /**
     * 处理stake请求
     *
     * @param httpExchange
     * @param betofferId
     */
    private void stakeHandler(HttpExchange httpExchange, int betofferId) throws IOException {
        if (!httpExchange.getRequestMethod().equals("POST")) {
            httpExchange.sendResponseHeaders(405, -1L);
            return;
        }
        //验证session
        String sessionKey = httpExchange.getRequestURI().getQuery().split("=")[1];
        AtomicInteger customerId = new AtomicInteger();
        Session session = sessions.search(1, (k, v) -> {
            if (v.getKey().equals(sessionKey)) {
                customerId.set(k);
                return v;
            }
            return null;
        });
        if (session == null || session.invalid()) {
            httpExchange.sendResponseHeaders(401, -1L);
            return;
        }
        //读取requestbody
        StringBuilder bodyStr;
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8))) {
            bodyStr = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                bodyStr.append(line);
            }
        }
        String requestBodyStr = bodyStr.toString();
        //异常
        int stake = Integer.parseInt(requestBodyStr);
        Betoffer betoffer = betoffers.computeIfAbsent(betofferId, v -> new Betoffer());
        int totalStake = betoffer.offer(customerId.get(), stake);
        String response = Integer.toString(totalStake);
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.flush();
        os.close();
    }

    /**
     * 处理session请求
     *
     * @param httpExchange
     * @param customerId
     * @throws IOException
     */
    private void sessionHandler(HttpExchange httpExchange, int customerId) throws IOException {
        if (!httpExchange.getRequestMethod().equals("GET")) {
            httpExchange.sendResponseHeaders(405, -1L);
            return;
        }
        Session session = sessions.compute(customerId, (k, v) -> {
            if (v == null || v.invalid()) {
                return new Session(generateSessionKey());
            }
            return v;
        });
        String key = session.getKey();
        httpExchange.sendResponseHeaders(200, key.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(key.getBytes());
        os.flush();
        os.close();

    }

    /**
     * 生成session key
     *
     * @return
     */
    private String generateSessionKey() {
        return UUID.randomUUID().toString();
    }


}
