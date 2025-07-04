/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao.movie;

import java.util.ArrayList;
import java.util.List;
import model.Movie;
/**
 *
 * @author Dell-PC
 */


public class MovieDAO {
    public static List<Movie> getUpcomingMovies() {
        List<Movie> list = new ArrayList<>();
        list.add(new Movie(1, "The Northman", "assets/images/upcoming-1.png", "HD", 2022, "8.5"));
        list.add(new Movie(2, "Doctor Strange in the Multiverse of Madness", "assets/images/upcoming-2.png", "4K", 2022, "NR"));
        list.add(new Movie(3, "Memory", "assets/images/upcoming-3.png", "2K", 2022, "NR"));
        list.add(new Movie(4, "The Unbearable Weight of Massive Talent", "assets/images/upcoming-4.png", "HD", 2022, "NR"));
        return list;
    }
}