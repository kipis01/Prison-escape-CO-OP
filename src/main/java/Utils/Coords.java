package Utils;

public class Coords {
    private double x = 0, y = 0;
    public Coords(){}
    public Coords(double x, double y){ this.x = x; this.y = y; }
    public Coords moveBy(double x, double y){
        this.x += x; this.y += y;
        return this;
    }

    public Coords moveBy(Coords move){
        this.x += move.getX();
        this.y += move.getY();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coords)
            return x == ((Coords) obj).getX() && y == ((Coords) obj).getY();
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "x: " + x + " y: " + y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    //Cuz java doesn't support default parameters
    public boolean isToLeftOf(Coords point){ return isToLeftOf(point, false); }
    public boolean isToRightOf(Coords point){ return isToRightOf(point, false); }
    public boolean isLowerThen(Coords point){ return isLowerThen(point, false); }
    public boolean isHigherThen(Coords point) { return isHigherThen(point, false); }

    public boolean isToLeftOf(Coords point, boolean inclusive) {
        return (inclusive && point.getX() >= x) || point.getX() > x;
    }

    public boolean isToRightOf(Coords point, boolean inclusive) {
        return (inclusive && point.getX() <= x) || point.getX() < x;
    }

    public boolean isLowerThen(Coords point, boolean inclusive) {
        return (inclusive && point.getY() >= y) || point.getY() > y;
    }

    public boolean isHigherThen(Coords point, boolean inclusive) {
        return (inclusive && point.getY() <= y) || point.getY() < y;
    }
}
