package co.swrl.list.item;

import java.io.Serializable;

public class Swrl implements Serializable {
    private String title;

    public Swrl(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Swrl)) return false;

        Swrl swrl = (Swrl) o;

        return title.equals(swrl.title);

    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }

    @Override
    public String toString() {
        return title;
    }
}
