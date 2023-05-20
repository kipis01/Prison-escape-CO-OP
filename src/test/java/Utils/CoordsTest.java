package Utils;

import static org.junit.jupiter.api.Assertions.*;

class CoordsTest {

    @org.junit.jupiter.api.Test
    void isToLeftOf() {
        Coords coords = new Coords(0, 0);
        assert(coords.isToLeftOf(new Coords(1, 1)));
        assert(!coords.isToLeftOf(new Coords(-1, -1)));
        assert(!coords.isToLeftOf(new Coords(0, 0), false));
        assert(coords.isToLeftOf(new Coords(0, 0), true));
    }

    @org.junit.jupiter.api.Test
    void isToRightOf() {
        Coords coords = new Coords(0, 0);
        assert(!coords.isToRightOf(new Coords(1, 1)));
        assert(coords.isToRightOf(new Coords(-1, -1)));
        assert(!coords.isToRightOf(new Coords(0, 0), false));
        assert(coords.isToRightOf(new Coords(0, 0), true));
    }

    @org.junit.jupiter.api.Test
    void isLowerThen() {
        Coords coords = new Coords(0, 0);
        assert(coords.isLowerThen(new Coords(1, 1)));
        assert(!coords.isLowerThen(new Coords(-1, -1)));
        assert(!coords.isLowerThen(new Coords(0, 0), false));
        assert(coords.isLowerThen(new Coords(0, 0), true));
    }

    @org.junit.jupiter.api.Test
    void isHigherThen() {
        Coords coords = new Coords(0, 0);
        assert(!coords.isHigherThen(new Coords(1, 1)));
        assert(coords.isHigherThen(new Coords(-1, -1)));
        assert(!coords.isHigherThen(new Coords(0, 0), false));
        assert(coords.isHigherThen(new Coords(0, 0), true));
    }
}