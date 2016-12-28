package co.swrl.list;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Swrl;

import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SQLiteCollectionManagerTest extends AndroidTestCase {

    private SQLiteCollectionManager db;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        db.permanentlyDeleteAll();
    }

    @After
    public void tearDown() throws Exception {
        db.permanentlyDeleteAll();
        super.tearDown();
    }

    @Test
    public void canAddAndGetActiveSwrlsFromDB() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);

        List<Swrl> saved = db.getActive();

        assertThat(saved, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED));
    }

    @Test
    public void cannotSaveDuplicateSwrls() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX);

        List<Swrl> saved = db.getActive();

        assertThat(saved, contains(THE_MATRIX));
    }

    @Test
    public void canMarkSwrlsAsDoneAndGetThem() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.markAsDone(THE_MATRIX);

        List<Swrl> active = db.getActive();
        List<Swrl> done = db.getDone();

        assertThat(active, contains(THE_MATRIX_RELOADED));
        assertThat(done, contains(THE_MATRIX));
    }

    @Test
    public void canMarkDoneSwrlsAsActive() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.markAsDone(THE_MATRIX);

        List<Swrl> done = db.getDone();
        assertThat(done, contains(THE_MATRIX));

        db.markAsActive(THE_MATRIX);

        List<Swrl> active = db.getActive();
        assertThat(active, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED));
    }

    @Test
    public void canPermanentlyDeleteActiveAndDoneSwrls() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.save(THE_MATRIX_REVOLUTIONS);
        db.markAsDone(THE_MATRIX);

        assertThat(db.getActive(), containsInAnyOrder(THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS));
        assertThat(db.getDone(), contains(THE_MATRIX));

        db.permanentlyDelete(THE_MATRIX);
        db.permanentlyDelete(THE_MATRIX_RELOADED);

        assertThat(db.getActive(), contains(THE_MATRIX_REVOLUTIONS));
        assertThat(db.getDone(), is(emptyCollectionOf(Swrl.class)));

    }

    @Test
    public void permanentlyDeleteAll() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.save(THE_MATRIX_REVOLUTIONS);
        db.markAsDone(THE_MATRIX);

        assertThat(db.getActive(), containsInAnyOrder(THE_MATRIX_RELOADED, THE_MATRIX_REVOLUTIONS));
        assertThat(db.getDone(), contains(THE_MATRIX));

        db.permanentlyDeleteAll();

        assertThat(db.getActive(), is(emptyCollectionOf(Swrl.class)));
        assertThat(db.getDone(), is(emptyCollectionOf(Swrl.class)));
    }

}