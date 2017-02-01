package co.swrl.list.item;

import android.support.annotation.NonNull;

import java.io.Serializable;

import co.swrl.list.item.details.Details;

public class Swrl implements Serializable {
    private final String title;
    private final Type type;
    private Details details;

    public Swrl(@NonNull String title, @NonNull Type type) {
        assertNonNull(title, type);
        this.title = title;
        this.type = type;
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

    private void assertNonNull(String title, Type type) {
        if (title == null){
            throw new NullPointerException("title");
        }
        if (type == null){
            throw new NullPointerException("type");
        }
    }
}
