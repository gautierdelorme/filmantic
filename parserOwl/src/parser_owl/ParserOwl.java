/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package parser_owl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.commons.csv.*;
/**
 *
 * @author gautier
 */
public class ParserOwl {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ParserOwlManager manager = new ParserOwlManager();
            OMDBProxy omdbProxy = new OMDBProxy();
            File csvData = new File("/Users/gautier/Documents/web_sem/sources/download2.csv");
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withHeader());
            for (CSVRecord csvRecord : parser) {
                Film film = new Film(csvRecord.get("titre"),
                        csvRecord.get("realisateur"),
                        csvRecord.get("date_debut_evenement"),
                        csvRecord.get("date_fin_evenement"),
                        csvRecord.get("cadre"),
                        csvRecord.get("lieu"),
                        csvRecord.get("adresse"),
                        csvRecord.get("arrondissement"),
                        csvRecord.get("adresse_complete"),
                        csvRecord.get("geo_coordinates"),
                        omdbProxy.getMovieInfos(csvRecord.get("titre")).get("imdbRating"),
                        omdbProxy.getMovieInfos(csvRecord.get("titre")).get("Runtime"),
                        omdbProxy.getMovieInfos(csvRecord.get("titre")).get("Country"),
                        omdbProxy.getMovieInfos(csvRecord.get("titre")).get("Actors"),
                        omdbProxy.getMovieInfos(csvRecord.get("titre")).get("Year")
                );
                manager.store(film);
            }
            manager.save();
        } catch (IOException e) {
            System.out.println("error: "+e);
        }
    }
    
}
