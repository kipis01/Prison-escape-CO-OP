package Utils;

public class Rectangle {
    private Coords upperLeft, lowerRight;

    public Rectangle(Coords upperLeft, int width, int height) {
        this(upperLeft, new Coords(upperLeft.getX() + width, upperLeft.getY() + height));
    }

    public Rectangle(Coords upperLeft, Coords lowerRight) {
        this.upperLeft = upperLeft; this.lowerRight = lowerRight;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle)
            return ((Rectangle) obj).getUpperLeft() == upperLeft && ((Rectangle) obj).getLowerRight() == lowerRight;
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "UpperLeft: (" + upperLeft + "), LowerRight: (" + lowerRight + ")";
    }

    public Coords getUpperLeft() { return upperLeft; }
    public void setUpperLeft(Coords upperLeft) { this.upperLeft = upperLeft; }
    public Coords getUpperRight(){ return new Coords(lowerRight.getX(), upperLeft.getY()); }
    public void setUpperRight(Coords upperRight){ upperLeft.setY(upperRight.getY()); lowerRight.setX(upperRight.getX()); }
    public Coords getLowerLeft(){ return new Coords(upperLeft.getX(), lowerRight.getY()); }
    public void setLowerLeft(Coords lowerLeft){ upperLeft.setX(lowerLeft.getX()); lowerRight.setY(lowerLeft.getY()); }
    public Coords getLowerRight() { return lowerRight; }
    public void setLowerRight(Coords lowerRight) { this.lowerRight = lowerRight; }


    public boolean isInside(Coords coords) {
        return upperLeft.isToLeftOf(coords, true)
                && upperLeft.isHigherThen(coords, true)
                && lowerRight.isToRightOf(coords, true)
                && lowerRight.isLowerThen(coords, true);
    }

    /**
     * Checks if this rectangle is entirely inside an area
     * @param area Area in which the rectangle is supposed to be
     * @return true, if rectangle is entirely inside the area
     */
    public boolean isInside(Rectangle area) {
        return area.getUpperLeft().isToLeftOf(upperLeft, true)
                && area.getUpperLeft().isHigherThen(upperLeft, true)
                && area.getLowerRight().isLowerThen(lowerRight, true)
                && area.getLowerRight().isToRightOf(lowerRight, true);
    }

    public boolean overlaps(Rectangle rectangle){
        return !lowerRight.isToLeftOf(rectangle.getLowerLeft()) // |this| |rectangle|
                && !upperLeft.isToRightOf(rectangle.getUpperRight()) // |rectangle| |this|
                && !upperLeft.isLowerThen(rectangle.getLowerLeft()) // rectangle above
                && !lowerRight.isHigherThen(rectangle.getUpperRight()); // rectangle bellow
    }
}
