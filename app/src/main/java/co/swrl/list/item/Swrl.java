package co.swrl.list.item;

import java.io.Serializable;

public class Swrl implements Serializable {
    private final String title;
    private final Type type;

    public Swrl(String title, Type type){
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

    @Override
    public String toString() {
        return title;
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
}
