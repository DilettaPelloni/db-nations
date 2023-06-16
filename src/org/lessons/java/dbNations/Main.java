package org.lessons.java.dbNations;

//IMPORT
import com.bethecoder.ascii_table.ASCIITable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        //parametri di connessione
        String url = "jdbc:mysql://localhost:3306/db-nations";
        String user = "root";
        String password = "root";

        //apro lo scanner
        Scanner scan = new Scanner(System.in);

        //apro la connessione
        try(Connection c = DriverManager.getConnection(url, user, password)) {

            //chiedo all'utente la stringa per il filtro
            System.out.println("Ricerca: ");
            String filter = scan.nextLine();

            //definisco la query
            String query = """
                    SELECT `countries`.`name` AS `country_name`, `countries`.`country_id`, `regions`.`name` AS `region_name`, `continents`.`name` AS `continent_name`
                    FROM `countries`
                    JOIN `regions` ON `countries`.`region_id` = `regions`.`region_id`
                    JOIN `continents` ON `regions`.`continent_id` = `continents`.`continent_id`
                    WHERE `countries`.`name` LIKE ?
                    ORDER BY `country_name`;
            """;

            //creo lo statement passando la mia query
            try(PreparedStatement ps = c.prepareStatement(query)) {

                //setto il valore ? della query
                ps.setString(1, "%" + filter + "%");

                //eseguo la query e uso il ResultSet
                try(ResultSet rs = ps.executeQuery()) {

                    //----- INTESTAZIONE TABELLA -----
                    int colNum = rs.getMetaData().getColumnCount(); //numero di colonne della tabella risultato
                    String[] tableHeader = new String[colNum]; //array che conterrà le etichette delle colonne
                    for (int i = 0; i < colNum; i++) { //tante volte quante sono le colonne
                        tableHeader[i] = rs.getMetaData().getColumnLabel(i + 1); //inserisco nell'array l'intestazione della colonna
                    }

                    //----- RIGHE TABELLA -----
                    List<String[]> tableRowsList = new ArrayList<>(); //lista che conterrà un array di stringhe per ogni riga (poi la converto)

                    while (rs.next()) { //per ogni riga del risultato
                        //raccolgo i dati della riga
                        String countryName = rs.getString("country_name");
                        int countryId = rs.getInt("country_id");
                        String regionName = rs.getString("region_name");
                        String continentName = rs.getString("continent_name");

                        //creo un array con i dati della riga
                        String[] row = {countryName, String.valueOf(countryId), regionName, continentName};

                        //aggiungo row alla lista
                        tableRowsList.add(row);
                    }

                    //----- STAMPO TABELLA -----
                    String[][] tableRows = tableRowsList.toArray(new String[0][]);
                    String table = ASCIITable.getInstance().getTable(tableHeader, tableRows);
                    System.out.println(table);

                }
            }

        }catch (SQLException e) {  //in caso il collegamento non abbia funzionato
            System.out.println("Errore durante il collegamento al DB");
            e.printStackTrace();
        }

        //chiudo lo scanner
        scan.close();

    }

}
