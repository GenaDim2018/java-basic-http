package ru.fisunov.http.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер поднят, порт: " + port);

            Map<String, MyWebApplication> router = new HashMap<>();
            router.put("/calculator", new CalculatorWebApplication());
            router.put("/greetings", new GreetingsWebApplication());
            router.put("/items", new ItemsWebApplication());
            System.out.println("Построен маппинг для точек назначения:");
            router.entrySet().forEach(e -> System.out.println(e.getKey() + ": " + e.getValue().getClass().getSimpleName()));

            System.out.println("Сервер готов к работе");

            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("Клиент подключился");
                    Request request = new Request(socket.getInputStream());
                    request.show();
                    boolean executed = false;
                    for (Map.Entry<String, MyWebApplication> e : router.entrySet()) {
                        if (request.getUri().startsWith(e.getKey())) {
                            e.getValue().execute(request, socket.getOutputStream());
                            executed = true;
                            break;
                        }
                    }
                    if (!executed) {
                        socket.getOutputStream().write((
                                "HTTP/1.1 404 Not Found\r\n" +
                                        "Content-Type: text/html;charset=utf-8\r\n" +
                                        "Content-Length: 431\r\n" +
                                        "\r\n" +
                                        "<!doctype html>\n" +
                                "<html lang=\"en\">\n" +
                                "\n" +
                                "<head>\n" +
                                "\t<title>HTTP Status 404 – Not Found</title>\n" +
                                "\t<style type=\"text/css\">\n" +
                                "\t\tbody {\n" +
                                "\t\t\tfont-family: Tahoma, Arial, sans-serif;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\th1,\n" +
                                "\t\th2,\n" +
                                "\t\th3,\n" +
                                "\t\tb {\n" +
                                "\t\t\tcolor: white;\n" +
                                "\t\t\tbackground-color: #525D76;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\th1 {\n" +
                                "\t\t\tfont-size: 22px;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\th2 {\n" +
                                "\t\t\tfont-size: 16px;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\th3 {\n" +
                                "\t\t\tfont-size: 14px;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\tp {\n" +
                                "\t\t\tfont-size: 12px;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\ta {\n" +
                                "\t\t\tcolor: black;\n" +
                                "\t\t}\n" +
                                "\n" +
                                "\t\t.line {\n" +
                                "\t\t\theight: 1px;\n" +
                                "\t\t\tbackground-color: #525D76;\n" +
                                "\t\t\tborder: none;\n" +
                                "\t\t}\n" +
                                "\t</style>\n" +
                                "</head>\n" +
                                "\n" +
                                "<body>\n" +
                                "\t<h1>HTTP Status 404 – Not Found</h1>\n" +
                                "</body>\n" +
                                "\n" +
                                "</html>").getBytes(StandardCharsets.UTF_8));
//                        socket.getOutputStream().write(("HTTP/1.1 404 Not Found\r\nContent-Type: text/html\r\n\r\n<html><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
