package org.example;

public class DatabaseFactory {

    public static Database createDatabase(String type) {
        switch (type) {
            case "MYSQL":
                return new MySQLDatabase();
            case "POSTGRES":
                return new PostgresDatabase();
            default:
                throw new IllegalArgumentException("Loại CSDL không hỗ trợ");
        }
    }
}

