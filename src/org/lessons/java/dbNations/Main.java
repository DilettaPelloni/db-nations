package org.lessons.java.dbNations;

//IMPORT
import java.sql.*;

public class Main {

    public static void main(String[] args) {

        //parametri di connessione
        String url = "jdbc:mysql://localhost:3306/db-nations";
        String user = "root";
        String password = "root";

        //apro la connessione
        try(Connection c = DriverManager.getConnection(url, user, password)) {

            //definisco la query
            String query = """
                    SELECT `countries`.`name` AS `country_name`, `countries`.`country_id`, `regions`.`name` AS `region_name`, `continents`.`name` AS `continent_name`
                    FROM `countries`
                    JOIN `regions` ON `countries`.`region_id` = `regions`.`region_id`
                    JOIN `continents` ON `regions`.`continent_id` = `continents`.`continent_id`
                    ORDER BY `country_name`;
            """;

            //creo lo statement passando la mia query
            try(PreparedStatement ps = c.prepareStatement(query)) {

                //eseguo la query e uso il ResultSet
                try(ResultSet rs = ps.executeQuery()) {
                    while(rs.next()) { //per ogni riga del ResultSet
                        //salvo i valori delle colonne
                        String countryName = rs.getString("country_name");
                        int countryId = rs.getInt("country_id");
                        String regionName = rs.getString("region_name");
                        String continentName = rs.getString("continent_name");
                        //stampo i valori
                        System.out.println(
                                    "Nazione: " + countryName +
                                    " - Id Nazione: " + countryId +
                                    " - Regione: " + regionName +
                                    " - Continente: " + continentName
                                );
                    }
                }
            }

        }catch (SQLException e) {  //in caso il collegamento non abbia funzionato
            System.out.println("Errore durante il collegamento al DB");
            e.printStackTrace();
        }

    }

}
