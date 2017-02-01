package co.swrl.list.item.details;

import org.json.JSONObject;

/**
 * Created by kyle on 12/01/2017.
 */

public class UnknownDetailsBuilder implements DetailsBuilder {
    @Override
    public UnknownDetails fromJSON(JSONObject json) {
        return new UnknownDetails();
    }
}
