package nz.co.maitech.popularmovies;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Movie is a data wrapper, to hold information about a movie.
 */
public class Movie implements Parcelable {


    private String overview;
    private String title;
    private String releaseDate;
    private String rating;
    private String posterPath;

    public Movie() {
    }

    protected Movie(Parcel in) {
        overview = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        rating = in.readString();
        posterPath = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(overview);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(rating);
        dest.writeString(posterPath);
    }

    public String getYear() {
        return releaseDate.substring(0,4);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}