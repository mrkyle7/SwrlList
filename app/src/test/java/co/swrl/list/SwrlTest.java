package co.swrl.list;

import com.google.gson.Gson;

import org.junit.Test;

import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SwrlTest {
    @Test
    public void testEqualsOverride() throws Exception {
        Swrl swrl1 = new Swrl("Swrl");
        Swrl swrl2 = new Swrl("Swrl");
        Swrl swrl3 = new Swrl("Swrl", Type.UNKNOWN);
        Swrl swrl4 = new Swrl("Swrl", Type.ALBUM);

        assertTrue(swrl1.equals(swrl1));
        assertTrue(swrl1.equals(swrl2));
        assertTrue(swrl2.equals(swrl1));
        assertTrue(swrl1.equals(swrl3)); //because default is type UNKNOWN
        assertTrue(swrl3.equals(swrl1));
        assertFalse(swrl1.equals(swrl4));
        assertFalse(swrl3.equals(swrl4));

        swrl2.setDetails(new Gson().fromJson("{\"title\":\"The Matrix (1991)\",\"overview\":\"overview\"}", Details.class));
        assertTrue(swrl1.equals(swrl2));
    }

}