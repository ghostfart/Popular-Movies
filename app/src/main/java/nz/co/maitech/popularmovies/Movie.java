package nz.co.maitech.popularmovies;


import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Movie is a data wrapper, to hold information about a movie.
 */
public class Movie extends RealmObject {

    @PrimaryKey
    private String id;
    private String overview;
    private String title;
    private String releaseDate;
    private String rating;
    private String posterPath;
    private long timeStamp;
    public RealmList<Trailer> trailers;
    public RealmList<Review> reviews;

    public Movie() {
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getRating() {
        return rating + "/10";
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String toString() {
        return title + "\n"
                + overview + "\n"
                + rating + "\n"
                + releaseDate + "\n"
                + posterPath;
    }

    public String getYear() {
        return releaseDate.substring(0,4);
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}