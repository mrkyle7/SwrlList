package co.swrl.swrllist;

class Swrl {
    private String title;

    Swrl(String title) {
        this.title = title;
    }

    String getTitle() {
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
}
