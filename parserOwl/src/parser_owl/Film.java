/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package parser_owl;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author gautier
 */
public class Film {
    private String title;
    private String director;
    private String start_dates;
    private String end_dates;
    private String frames;
    private String places;
    private String addresses;
    private String districts;
    private String address;
    private String coordinates;
    private String rating;
    private String duration;
    private String country;
    private ArrayList<String> actors;
    private String year;

    public Film(String title,
            String director,
            String start_dates,
            String end_dates,
            String frames,
            String places,
            String addresses,
            String districts,
            String address,
            String coordinates,
            String rating,
            String duration,
            String country,
            String actors,
            String year
    ) {
        this.title = title;
        this.director = director;
        this.start_dates = start_dates;
        this.end_dates = end_dates;
        this.frames = frames;
        this.places = places;
        this.addresses = addresses;
        this.districts = districts;
        this.address = address;
        this.coordinates = coordinates;
        this.rating = rating;
        if (duration != null && duration.compareTo("N/A") != 0) {
            this.duration = duration.split(" ")[0];
        } else {
            this.duration = "N/A";
        }
        if (country != null && country.compareTo("N/A") != 0) {
            this.country = country;
        } else {
            this.country = "N/A";
        }
        this.actors = new ArrayList<String>();
        if (actors != null && actors.compareTo("N/A") != 0) {
            this.actors.addAll(Arrays.asList(actors.split(", ")));
        }
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public String getStart_dates() {
        return start_dates;
    }

    public String getEnd_dates() {
        return end_dates;
    }

    public String getFrames() {
        return frames;
    }

    public String getPlaces() {
        return places;
    }

    public String getAddresses() {
        return addresses;
    }

    public String getDistricts() {
        return districts;
    }

    public String getAddress() {
        return address;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getRating() {
        return rating;
    }

    public String getDuration() {
        return duration;
    }
    
    public String getCountry() {
        return country;
    }

    public String getYear() {
        return year;
    }
    
    public ArrayList<String> getActors() {
        return actors;
    }    
    
    public boolean hasRating() {
        return (rating != null && rating.compareTo("N/A") != 0);
    }
    
    public boolean hasDuration() {
        return (duration != null && duration.compareTo("N/A") != 0);
    }
    
    public boolean hasCountry() {
        return (country != null && country.compareTo("N/A") != 0);
    }
    
    public boolean hasYear() {
        return (year != null && year.compareTo("N/A") != 0);
    }

    @Override
    public String toString() {
        return "Film{" + "title=" + title + ", director=" + director + ", start_dates=" + start_dates + ", end_dates=" + end_dates + ", frames=" + frames + ", places=" + places + ", addresses=" + addresses + ", districts=" + districts + ", address=" + address + ", coordinates=" + coordinates + ", rating=" + rating + ", duration=" + duration + ", country=" + country + ", actors=" + actors + '}';
    }
}
