package utils;

public class Tuple<X, Y> {
    public X first;
    public Y second;

    public Tuple(X first, Y second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return "(" + first + "," + second + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Tuple)) {
            return false;
        }
        Tuple<X, Y> other_ = (Tuple<X, Y>) other;
        return other_.first.equals(this.first) && other_.second.equals(this.second);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }
}