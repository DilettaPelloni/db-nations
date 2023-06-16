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

            //------------------------- PRIMA QUERY: TABELLA FILTRATA PER NOME NAZIONE -------------------------

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

                ps.setString(1, "%" + filter + "%"); //setto il valore ? della query

                try(ResultSet rs = ps.executeQuery()) { //eseguo la query e uso il ResultSet

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

            //------------------------- SECONDA QUERY: RICERCA INFO PER ID -------------------------

            //chiedo all'utente l'id e verifico che mi dia un numero
            Integer choosenId = null;
            do {
                System.out.println("Scegli l'id di una nazione: ");
                try {
                    choosenId = Integer.parseInt(scan.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Devi scrivere un numero");
                }
            }while(choosenId == null);

            //definisco la query che otterrà la nazione
            String countryQuery = """
                    SELECT `countries`.`name`
                    FROM `countries`
                    WHERE `countries`.`country_id` = ?;
            """;

            String countryName = null;

            try(PreparedStatement ps = c.prepareStatement(countryQuery)) {
                ps.setInt(1, choosenId); //setto il valore ? della query
                try(ResultSet rs = ps.executeQuery()) { //eseguo la query e uso il ResultSet
                    while (rs.next()) {
                        countryName = rs.getString("name");
                    }
                }
            }

            if(countryName != null) { //se mi è stato dato un id valido

                System.out.println("Dettagli della nazione: " + countryName); //stampo la nazione

                //definisco la query che otterrà le lingue
                String languageQuery = """
                        SELECT `countries`.`name` AS `country_name`, `languages`.`language`
                        FROM `countries`
                        JOIN `country_languages` ON `countries`.`country_id` = `country_languages`.`country_id`
                        JOIN `languages` ON `country_languages`.`language_id` = `languages`.`language_id`
                        WHERE `countries`.`country_id` = ?;
                """;

                try(PreparedStatement ps = c.prepareStatement(languageQuery)) {

                    ps.setInt(1, choosenId); //setto il valore ? della query
                    try(ResultSet rs = ps.executeQuery()) { //eseguo la query e uso il ResultSet
                        List<String> languages = new ArrayList<>(); //lista che conterrà le lingue
                        while (rs.next()) {
                            String language = rs.getString("language");
                            languages.add(language);//aggiungo la lingua alla lista
                        }
                        String langString = "Lingue: "; //preparo la stringa da mostrare
                        for (int i = 0; i < languages.size(); i++) { //tutto 'sto casino per le virgole (╯°□°）╯︵ ┻━┻
                            langString += languages.get(i);
                            if(i < languages.size() - 1) {
                                langString += ", "; //se non sono all'ultimo elemento della lista metto la virgola
                            }
                        }
                        System.out.println(langString);
                    }
                }


            } else {
                System.out.println("L'id " + choosenId + " non è valido");
            }


            //definisco la query che otterrà le statistiche più recenti
//            String statisticQuery = """
//                   SELECT `countries`.`name`, `country_stats`.`year`, `country_stats`.`population`, `country_stats`.`gdp`
//                   FROM `countries`
//                   JOIN `country_stats` ON `countries`.`country_id` = `country_stats`.`country_id`
//                   WHERE `countries`.`country_id` = ?
//                   ORDER BY `country_stats`.`year` DESC
//                   LIMIT 1;
//            """;



        }catch (SQLException e) {  //in caso il collegamento non abbia funzionato
            System.out.println("Errore durante il collegamento al DB");
            e.printStackTrace();
        }

        //chiudo lo scanner
        scan.close();

    }

}
