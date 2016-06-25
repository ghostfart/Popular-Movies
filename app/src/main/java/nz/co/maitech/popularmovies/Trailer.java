package nz.co.maitech.popularmovies;

import io.realm.RealmObject;

/**
 * Created by Grant on 25/06/2016.
 */
public class Trailer extends RealmObject {

    private String key;
    private String type;
    private String name;
    private String site;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
