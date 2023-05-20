package Utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {
    @Test
    void getUpperRight() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        assertEquals(rect.getUpperRight(), new Coords(2, 10));
    }

    @Test
    void setUpperRight() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        rect.setUpperRight(new Coords(50, 100));
        assertEquals(rect.getUpperLeft(), new Coords(-5, 100));
        assertEquals(rect.getLowerRight(), new Coords(50, -20));
    }

    @Test
    void getLowerLeft() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        assertEquals(rect.getLowerLeft(), new Coords(-5, -20));
    }

    @Test
    void setLowerLeft() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        rect.setLowerLeft(new Coords(-50, -100));
        assertEquals(rect.getUpperLeft(), new Coords(-50, 10));
        assertEquals(rect.getLowerRight(), new Coords(2, -100));
    }

    @Test
    void isInside() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        assertTrue(rect.isInside(new Coords(0, 0)));
        assertFalse(rect.isInside(new Coords(1, 1000)));
        assertFalse(rect.isInside(new Coords(100, 2)));
        assertFalse(rect.isInside(new Coords(-200, 0)));
    }

    @Test
    void testIsInside() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        assertTrue(rect.isInside(rect));
        assertTrue(rect.isInside(new Rectangle(new Coords(-10, 10), new Coords(100, -100))));
        assertFalse(rect.isInside(new Rectangle(new Coords(-100, 100), new Coords(-50, 0))));
        assertFalse(rect.isInside(new Rectangle(new Coords(-100, 100), new Coords(0, 0))));
    }

    @Test
    void overlaps() {
        Rectangle rect = new Rectangle(new Coords(-5, 10), new Coords(2, -20));
        assertTrue(rect.isInside(rect));
        assertTrue(rect.overlaps(new Rectangle(new Coords(-1, 1), new Coords(1, -1))));
        assertTrue(rect.overlaps(new Rectangle(new Coords(-10, 1), new Coords(1, 1))));
        assertFalse(rect.overlaps(new Rectangle(new Coords(-100, 100), new Coords(0, 50))));
    }
}