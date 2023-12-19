package model;

import java.util.Date;

public class Reservation {

    // TODO: add all ids that belong to one reservation entry
    // ids
    private int reservationId;
    private int cinemaId;
    private int movieId;
    private int datePlaytimeId;

    /*
     * TODO: define all attributes a reservation has with its correct data type.
     * These information should later correspond to the table view that is
     * initialized in the class ProfilePanelController. Thus, the amount of columns
     * you have in the layout file ProfilePanel.fxml must be the same as the amount
     * of attributes you define here.
     *
     * The order in which you define your attributes does not play a role here.
     */
    // reservations attributes
    private String cinema;
    private String movie;
    private String time;
    private int reservedSeats;
    private Date date;

    /*
     * TODO: define a constructor which assigns the respective value to each class
     * attribute.
     *
     * Cinema Case: there are 9 attributes in total, thus the constructor has 9
     * parameters and assigns them accordingly.
     */
    public Reservation(int reservationId, int cinemaId, int movieId, int datePlaytimeId, String cinema, String movie,
                       String time, int reservedSeats, Date date) {
        super();
        this.reservationId = reservationId;
        this.cinemaId = cinemaId;
        this.movieId = movieId;
        this.datePlaytimeId = datePlaytimeId;
        this.cinema = cinema;
        this.movie = movie;
        this.time = time;
        this.reservedSeats = reservedSeats;
        this.date = date;
    }

    /*
     * TODO: change the strings inside the String-array that gets returned. The
     * array represents the order of columns in the reservation table view and will
     * serve as a connection between a class attribute and the FXML-column that is
     * defined in the layout file.
     *
     * The order of the strings in the array !MUST! be the same as the order of the
     * columns from the table view in the layout file ProfilePanel.fxml.
     *
     * Each string must correspond to a previously defined class attribute
     * maintaining the exact same spelling. E.g. there is a class attribute
     * "cinema", thus in the array the string is named "cinema".
     *
     * Cinema Case: In ProfilePanel.fxml the first column is the date column.
     * Therefore, in the array the first entry is "date". "date" also corresponds to
     * the class attribute date, maintaining the same spelling.
     *
     * @return the array with attributes defining the order of columns in the table
     *         view
     */

    /**
     * @return the array with attributes defining the order of columns in the table
     * view
     */
    public static String[] getVariableNames() {
        return new String[]{"date", "time", "movie", "cinema", "reservedSeats"};
    }

    // GETTER and SETTER

    /*
     * TODO: once you defined the attributes, autogenerate a GET and SET method for each class attribute:
     * 1. Right click in code window
     * 2. Select "Generate"
     * 3. Select "Getter and Setter"
     * 4. Select all attributes
     * 5. Click OK
     */

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getDatePlaytimeId() {
        return datePlaytimeId;
    }

    public void setDatePlaytimeId(int datePlaytimeId) {
        this.datePlaytimeId = datePlaytimeId;
    }

    public String getCinema() {
        return cinema;
    }

    public void setCinema(String cinema) {
        this.cinema = cinema;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(String movie) {
        this.movie = movie;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getReservedSeats() {
        return reservedSeats;
    }

    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /*
     * TODO: autogenerate a toString() method with all your class attributes:
     * 1. Right click in code window
     * 2. Select "Generate"
     * 3. Select "toString()"
     * 4. Make sure all attributes are selected, and button "add @Override" is selected
     * 5. Click OK
     */
    @Override
    public String toString() {
        return "Reservation [reservationId=" + reservationId + ", cinemaId=" + cinemaId + ", movieId=" + movieId
                + ", datePlaytimeId=" + datePlaytimeId + ", cinema=" + cinema + ", movie=" + movie + ", time=" + time
                + ", reservedSeats=" + reservedSeats + ", date=" + date + "]";
    }

    /*
     * TODO: autogenerate a equals() method with all your class attributes:
     * 1. Right click in code window
     * 2. Select "Generate"
     * 3. Select "equals() and hashCode()"
     * 4. Select IntelliJ Default as Template
     * 5. Click Next
     * 6. Select all attributes twice
     * 7. Select attributes that must not be null
     * 8. Click ok
     *
     * Note: we don't need the hashCode function. You can either delete or ignore it
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Reservation other = (Reservation) obj;
        if (cinema == null) {
            if (other.cinema != null)
                return false;
        } else if (!cinema.equals(other.cinema))
            return false;
        if (cinemaId != other.cinemaId)
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (datePlaytimeId != other.datePlaytimeId)
            return false;
        if (movie == null) {
            if (other.movie != null)
                return false;
        } else if (!movie.equals(other.movie))
            return false;
        if (movieId != other.movieId)
            return false;
        if (reservationId != other.reservationId)
            return false;
        if (reservedSeats != other.reservedSeats)
            return false;
        if (time == null) {
            return other.time == null;
        } else return time.equals(other.time);
    }

}
