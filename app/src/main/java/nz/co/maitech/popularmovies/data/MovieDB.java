package nz.co.maitech.popularmovies.data;

import android.content.Context;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by Grant on 30/06/2016.
 */
public class MovieDB {

    private final String LOG_TAG = this.getClass().getSimpleName();

    Context mContext;
    RealmConfiguration favoriteRealmConfig;


    public MovieDB(Context context) {
        mContext = context;
        favoriteRealmConfig = new RealmConfiguration.Builder(mContext)
                .name("favorites.realm")
                .build();
    }

    public void saveMovieToFavorite (Movie movie) {
        Realm realm = Realm.getInstance(favoriteRealmConfig);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(movie);
        realm.commitTransaction();
        realm.close();
    }

    public ArrayList<Movie> getFavoriteMovies() {
        ArrayList<Movie> movieList = new ArrayList<>();
        Realm favRealm = Realm.getInstance(favoriteRealmConfig);
        RealmResults<Movie> results = favRealm.where(Movie.class).findAll();
        for (Movie movie : results) {
            movieList.add(favRealm.copyFromRealm(movie));
        }
        favRealm.close();
        saveMovies(movieList.toArray(new Movie[movieList.size()] ));
        return movieList;
    }

    private void deleteTempMovies() {
        Realm tempRealm = Realm.getDefaultInstance();
        RealmResults<Movie> results = tempRealm.where(Movie.class).findAll();
        tempRealm.beginTransaction();
        results.deleteAllFromRealm();
        tempRealm.commitTransaction();
        tempRealm.close();
    }

    public void saveMovies(Movie[] movies) {
        deleteTempMovies();
        Realm tempRealm = Realm.getDefaultInstance();
        tempRealm.beginTransaction();
        for (Movie movie : movies) {
            tempRealm.copyToRealmOrUpdate(movie);
        }
        tempRealm.commitTransaction();
        tempRealm.close();
    }

    public void saveTrailers(ArrayList<Trailer> trailers, Movie movie) {
        Realm tempRealm = Realm.getDefaultInstance();
        tempRealm.beginTransaction();
        for (Trailer trailer : trailers) {
            if (trailer.getType().equals("Trailer")) {
                tempRealm.copyToRealm(trailer);
                movie.trailers.add(trailer);
            }
        }
        tempRealm.copyToRealmOrUpdate(movie);
        tempRealm.commitTransaction();
        tempRealm.close();
    }

    public void saveReviews(ArrayList<Review> reviews, Movie movie) {
        Realm tempRealm = Realm.getDefaultInstance();
        tempRealm.beginTransaction();
        for (Review review : reviews) {
            tempRealm.copyToRealm(review);
            movie.reviews.add(review);
        }
        tempRealm.copyToRealmOrUpdate(movie);
        tempRealm.commitTransaction();
        tempRealm.close();
    }

    public ArrayList<Review> getReviews(String movieId) {
        ArrayList<Review> reviewList = new ArrayList<>();
        Realm tempRealm = Realm.getDefaultInstance();
        Movie movie = tempRealm.where(Movie.class).contains("id", movieId).findFirst();
        for (Review review : movie.reviews) {
            reviewList.add(tempRealm.copyFromRealm(review));
        }
        tempRealm.close();
        return reviewList;
    }

    public ArrayList<Trailer> getTrailers(String movieId) {
        ArrayList<Trailer> trailerList = new ArrayList<>();
        Realm tempRealm = Realm.getDefaultInstance();
        Movie movie = tempRealm.where(Movie.class).contains("id", movieId).findFirst();
        for (Trailer trailer : movie.trailers) {
            trailerList.add(tempRealm.copyFromRealm(trailer));
        }
        tempRealm.close();
        return trailerList;
    }

    public Movie getMovie(String movieId) {
        Realm tempRealm = Realm.getDefaultInstance();
        Movie movie = tempRealm.where(Movie.class).contains("id", movieId).findFirst();
        movie = tempRealm.copyFromRealm(movie);
        tempRealm.close();
        return movie;
    }

    public Movie getDefaultMovie() {
        Realm tempRealm = Realm.getDefaultInstance();
        Movie movie = tempRealm.where(Movie.class).findFirst();
        if (movie != null) {
            movie = tempRealm.copyFromRealm(movie);
        }
        tempRealm.close();
        return movie;
    }
}
