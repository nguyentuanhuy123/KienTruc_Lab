package org.example;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
public class FactoryMethodTest {
    public static void main(String[] args) {
        Database db = DatabaseFactory.createDatabase("MYSQL");
        db.connect();
    }
}
