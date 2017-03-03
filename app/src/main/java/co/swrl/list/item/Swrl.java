package co.swrl.list.item;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Swrl implements Serializable {
    private final String title;
    private final Type type;
    private Details details;

    public Swrl(@NonNull String title, @NonNull Type type) {
        this.title = (String) assertNonNull(title, "title");
        this.type = (Type) assertNonNull(type, "type");
    }

    public Swrl(String title) {
        this(title, Type.UNKNOWN);
    }

    public String getTitle() {
        return title;
    }

    public Type getType() {
        return type;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return title + " (" + type.toString() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swrl)) return false;

        Swrl swrl = (Swrl) o;

        return title.equals(swrl.title) && type == swrl.type;
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    private Object assertNonNull(Object value, String parameter) {
        if (value == null){
            throw new IllegalArgumentException(parameter + " is null");
        } else {
            return value;
        }
    }
}
