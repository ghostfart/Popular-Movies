package nz.co.maitech.popularmovies;

/**
 * Movie is a data wrapper, to hold information about a movie.
 */
public class Movie {

    private Integer imageResourceId;
    private String overview;
    private String title;
    private String releaseDate;
    private String rating;
    private String posterPath;

    public Movie() {
        imageResourceId = R.drawable.sample_0;
    }

    public Movie(Integer imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public Integer getImageId(){
        return imageResourceId;
    }

    public void setImageResourceId(Integer imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public String getOverview() { return overview; }

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
        return rating;
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

}
