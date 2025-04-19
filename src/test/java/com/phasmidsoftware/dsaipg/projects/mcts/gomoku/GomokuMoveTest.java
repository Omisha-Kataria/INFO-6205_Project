package com.phasmidsoftware.dsaipg.projects.mcts.gomoku;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GomokuMoveTest {

    @Test
    public void testGetters() {
        GomokuMove m = new GomokuMove(2, 3);
        assertEquals(2, m.getRow());
        assertEquals(3, m.getCol());
    }

    @Test
    public void testSelfEquality() {
        GomokuMove m = new GomokuMove(1, 1);
        assertEquals(m, m);
    }

    @Test
    public void testEqualsNullAndDifferentType() {
        GomokuMove m = new GomokuMove(0, 0);
        assertNotEquals(null, m);
        assertNotEquals(m, new Object());
    }

    @Test
    public void testEqualsAndHashCode() {
        GomokuMove m1 = new GomokuMove(3, 5);
        GomokuMove m2 = new GomokuMove(3, 5);
        GomokuMove m3 = new GomokuMove(4, 5);
        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());
        assertNotEquals(m1, m3);
    }

    @Test
    public void testToString() {
        GomokuMove m = new GomokuMove(0, 0);
        assertEquals("(0, 0)", m.toString());
    }
}