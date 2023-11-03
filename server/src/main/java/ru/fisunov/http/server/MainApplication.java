package ru.fisunov.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication {
    public static final int PORT = 8189;
    private static final Logger logger = LogManager.getLogger(MainApplication.class.getName());
    // + К домашнему заданию:
    // Добавить логирование!!!

    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Map<String, MyWebApplication> router = new HashMap<>();
            router.put("/calculator", new CalculatorWebApplication());
            router.put("/greetings", new GreetingsWebApplication());
            logger.info("Сервер запущен, порт: " + PORT);
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            while (true) {
                Socket socket = serverSocket.accept();
                executorService.submit(() -> {
                    logger.info("Клиент подключился");
                    try {
                        byte[] buffer = new byte[2048];
                        int n = socket.getInputStream().read(buffer);
                        String rawRequest = new String(buffer, 0, n);
                        Request request = new Request(rawRequest);
                        logger.info("Получен запрос:");
                        request.show();
                        boolean executed = false;
                        for (Map.Entry<String, MyWebApplication> e : router.entrySet()) {
                            if (request.getUri().startsWith(e.getKey())) {
                                e.getValue().execute(request, socket.getOutputStream());
                                executed = true;
                                socket.close();
                            }
                        }
                        if (!executed) {
                            socket.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));
                            socket.close();
                        }
                    } catch (IOException e) {
                        logger.error("Ошибка ввода/вывода", e);
                    }
                });
            }
        }
    }
}
