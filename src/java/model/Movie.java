/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Dell-PC
 */
public class Movie {
     private int id;
    private String title;
    private String posterUrl;
    private String quality;
    private int year;
    private String rating;
    private String genre;
    private String actor;
    private String description;

    public Movie() {
    }

    // Basic constructor used in DAO for upcoming movies
    public Movie(int id, String title, String posterUrl, String quality, int year, String rating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.quality = quality;
        this.year = year;
        this.rating = rating;
    }

    // Full constructor
    public Movie(int id, String title, String posterUrl, String quality,
                 int year, String rating, String genre,
                 String actor, String description) {
        this(id, title, posterUrl, quality, year, rating);
        this.genre = genre;
        this.actor = actor;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
