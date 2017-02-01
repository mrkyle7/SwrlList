package co.swrl.list.item.details;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

import static co.swrl.list.item.details.FilmDetails.KEY_ID;
import static co.swrl.list.item.details.FilmDetails.KEY_OVERVIEW;
import static co.swrl.list.item.details.FilmDetails.KEY_POSTER_URL;
import static co.swrl.list.item.details.FilmDetails.KEY_TITLE;

/**
 * Created by kyle on 10/01/2017.
 */

public class FilmDetailsBuilder implements DetailsBuilder {

    @Override
    public FilmDetails fromJSON(JSONObject json) {
        FilmDetails details = null;
        try {
            details = new FilmDetails(json.getString(KEY_TITLE),
                    json.getString(KEY_OVERVIEW),
                    json.getString(KEY_ID),
                    new URL(json.getString(KEY_POSTER_URL)));
        } catch (JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
        return details;
    }
}

