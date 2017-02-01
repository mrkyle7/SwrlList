package co.swrl.list.item.details;

import org.json.JSONObject;

public interface Details {
    /**
     * Details objects should be serializable to JSON for the Collection Manager to persist.
     */
    JSONObject toJSON();
}
