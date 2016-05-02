package nz.co.maitech.popularmovies;

/**
 * Movie is a data wrapper, to hold information about a movie.
 */
public class Movie {

    private Integer imageResourceId;

    public Movie(Integer imageResourceId) {
        this.imageResourceId = imageResourceId;
    }

    public Integer getImageId(){
        return imageResourceId;
    }

    public void setImageResourceId(Integer imageResourceId) {
        this.imageResourceId = imageResourceId;
    }
}
