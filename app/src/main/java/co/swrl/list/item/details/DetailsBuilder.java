package co.swrl.list.item.details;

import org.json.JSONObject;

/**
 * Created by kyle on 10/01/2017.
 */

public interface DetailsBuilder {
    Details fromJSON(JSONObject json);
}
