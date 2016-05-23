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
    
    private static final ParserOwlManager MANAGER = new ParserOwlManager();
    private static final OMDBProxy OMDB_PROXY = new OMDBProxy();
    
    public static void main(String[] args) {
        startParisWithData("/Users/gautier/Documents/web_sem/sources/paris_tournages2.csv");
        startMontpellierWithData("/Users/gautier/Documents/web_sem/sources/mtp_tournages2.csv");
        MANAGER.save();
    }
    
    public static void startParisWithData(String inputData) {
        try {
            File csvData = new File("/Users/gautier/Documents/web_sem/sources/paris_tournages2.csv");
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withHeader());
            System.out.println("Starting Paris (input file: "+csvData.getAbsolutePath()+")...");
            for (CSVRecord csvRecord : parser) {
                Film film = new Film(
                        csvRecord.get("titre"),
                        csvRecord.get("realisateur"),
                        csvRecord.get("date_debut_evenement"),
                        csvRecord.get("date_fin_evenement"),
                        csvRecord.get("cadre"),
                        csvRecord.get("lieu"),
                        csvRecord.get("adresse"),
                        csvRecord.get("arrondissement"),
                        csvRecord.get("adresse_complete"),
                        csvRecord.get("geo_coordinates"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("imdbRating"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Runtime"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Country"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Actors"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Year")
                );
                MANAGER.store(film);
            }
            System.out.println("Ending Paris.");
        } catch (IOException e) {
            System.out.println("error: "+e);
        }
    }
        
    public static void startMontpellierWithData(String inputData) {
        try {
            System.out.println("Starting Montpellier (input file: "+inputData+")...");
            File csvData = new File(inputData);
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.EXCEL.withHeader());
            for (CSVRecord csvRecord : parser) {
                Film film = new Film(
                        csvRecord.get("titre"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Director"),
                        csvRecord.get("date_debut_evenement"),
                        csvRecord.get("date_fin_evenement"),
                        "",
                        csvRecord.get("lieu"),
                        csvRecord.get("lieu"),
                        "",
                        csvRecord.get("lieu"),
                        "",
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("imdbRating"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Runtime"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Country"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Actors"),
                        OMDB_PROXY.getMovieInfos(csvRecord.get("titre")).get("Year")
                );
                MANAGER.store(film);
            }
            System.out.println("Ending Montpellier.");
        } catch (IOException e) {
            System.out.println("error: "+e);
        }
    }
    
}
