package org.lessons.java.dbNations;

//IMPORT
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        //parametri di connessione
        String url = "jdbc:mysql://localhost:3306/db-nations";
        String user = "root";
        String password = "root";

        //apro la connessione
        try(Connection c = DriverManager.getConnection(url, user, password)) {

            System.out.println(c.getCatalog());
            System.out.println("Collegamento riuscito");

        }catch (SQLException e) {  //in caso il collegamento non abbia funzionato
            System.out.println("Errore durante il collegamento al DB");
            e.printStackTrace();
        }

    }

}
