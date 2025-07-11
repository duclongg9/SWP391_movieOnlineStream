/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.movie;

import java.util.ArrayList;
import java.util.List;
import model.Movie;
import java.util.Collections;
/**
 *
 * @author Dell-PC
 */


public class MovieDAO {
    private static final List<Movie> SAMPLE_MOVIES = new ArrayList<>();

    static {
        SAMPLE_MOVIES.add(new Movie(1, "The Northman", "assets/images/upcoming-1.png", "HD", 2022, "8.5",
                "Action", "Alexander Skarsg\u00e5rd",
                "A young Viking prince seeks revenge for his father's murder."));

        SAMPLE_MOVIES.add(new Movie(2, "Doctor Strange in the Multiverse of Madness",
                "assets/images/upcoming-2.png", "4K", 2022, "NR",
                "Fantasy", "Benedict Cumberbatch",
                "Doctor Strange faces the unknown dangers of the multiverse."));

        SAMPLE_MOVIES.add(new Movie(3, "Memory", "assets/images/upcoming-3.png", "2K", 2022, "NR",
                "Thriller", "Liam Neeson",
                "An assassin-for-hire becomes a target after refusing a job."));

        SAMPLE_MOVIES.add(new Movie(4, "The Unbearable Weight of Massive Talent",
                "assets/images/upcoming-4.png", "HD", 2022, "NR",
                "Comedy", "Nicolas Cage",
                "A fictionalized Nicolas Cage accepts a bizarre gig."));

        }

    public static List<Movie> getUpcomingMovies() {
        return new ArrayList<>(SAMPLE_MOVIES);
    }
       public static Movie getMovieById(int id) {
        for (Movie m : SAMPLE_MOVIES) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }
}