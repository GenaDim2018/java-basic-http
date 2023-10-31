package ru.fisunov.http.server;

public class MainApplication {
    public static final int PORT = 8189;

    // Домашнее задание:
    // - Добавить sqlite БД для хранения Item
    // - * Подключите Gson/Jackson для сериализации объектов
    // - * Добавить создание объектов через POST (GET /items - возвращает все объекты, POST /items + объект в теле - создает новый объект)

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
