package ru.fisunov.http.server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;
import java.util.Properties;

public class ItemsWebApplication implements MyWebApplication {


    private String name;
    private List<Item> items;

    public ItemsWebApplication() {
        this.name = "Items Web Application";
        this.items = getItemsFromDB();
    }

    public static List<Item> getItemsFromDB() {
        List<Item> items = new ArrayList<>();
        String url = "jdbc:postgresql://localhost:5432/items";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "3654");
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, props);
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT id as id, title as title FROM items");
            while (rs.next()) {
                items.add(new Item(rs.getLong("id"), rs.getString("title")));
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (SQLException|ClassNotFoundException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public void execute(Request request, OutputStream output) throws IOException {
        StringBuilder builder = new StringBuilder("[ ");
        for (int i = 0; i < items.size(); i++) {
            builder.append("{ \"id\": ").append(items.get(i).getId()).append(", \"title\": \"").append(items.get(i).getTitle()).append("\" }");
            if (i < items.size() - 1) {
                builder.append(", ");
            }
        }
        builder.append(" ]");

        output.write(("" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" +
                builder
        ).getBytes(StandardCharsets.UTF_8));
    }
}
