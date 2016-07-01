package nz.co.maitech.popularmovies.data;

import io.realm.RealmObject;

/**
 * Created by Grant on 25/06/2016.
 */
public class Review extends RealmObject {

    private String author;
    private String content;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
