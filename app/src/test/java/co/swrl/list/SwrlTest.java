package co.swrl.list;

import org.junit.Test;

import co.swrl.list.item.Swrl;

import static org.junit.Assert.*;

public class SwrlTest {
    @Test
    public void testEqualsOverride() throws Exception {
        Swrl swrl1 = new Swrl("Swrl");
        Swrl swrl2 = new Swrl("Swrl");

        assertTrue(swrl1.equals(swrl1));
        assertTrue(swrl1.equals(swrl2));
        assertTrue(swrl2.equals(swrl1));
    }

}