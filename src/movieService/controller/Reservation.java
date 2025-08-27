package movieService.controller;

import movieService.model.Movie;
import movieService.model.Theater;
import movieService.model.User;

public class Reservation {
    private User user;
    private Movie movie;
    private Theater theater;
    private String date;
    private String time;
    private int people;
    private String selectedSeat;
    private String keyId;


    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    //생성자
    public Reservation(String keyId , User user){
        this.keyId = keyId;
        this.user = user;
    }

    //getter setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Theater getTheater() {
        return theater;
    }

    public void setTheater(Theater theater) {
        this.theater = theater;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPeople() {
        return people;
    }

    public void setPeople(int people) {
        this.people = people;
    }

    public String getSeat() {
        return selectedSeat;
    }

    public void setSeat(String seat) {
       this.selectedSeat = seat;
    }
    
}
