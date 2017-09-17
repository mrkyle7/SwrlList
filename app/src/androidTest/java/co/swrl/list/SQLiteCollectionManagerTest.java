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
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static co.swrl.list.Helpers.BILLIONS;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS_DETAILS;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

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
    public void canAddAndGetActiveSwrlsByTypeFromDB() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.save(BILLIONS);

        List<Swrl> films = db.getActiveWithFilter(Type.FILM);

        assertThat(films, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED));

        List<Swrl> tvShows = db.getActiveWithFilter(Type.TV);

        assertThat(tvShows, containsInAnyOrder(BILLIONS));

        List<Swrl> books = db.getActiveWithFilter(Type.BOOK);

        assertThat(books, is(emptyCollectionOf(Swrl.class)));
    }

    @Test
    public void ifNoActiveSwrlsThenReturnEmptyList() throws Exception {
        List<Swrl> saved = db.getActive();
        assertThat(saved, is(emptyCollectionOf(Swrl.class)));
    }

    @Test
    public void cannotSaveDuplicateSwrls() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX);

        List<Swrl> saved = db.getActive();

        assertThat(saved, contains(THE_MATRIX));
    }

    @Test
    public void canSaveSwrlsWithSameTitlesButDifferentTypes() throws Exception {
        Swrl film = new Swrl("The Hunger Games", Type.FILM);
        Swrl book = new Swrl("The Hunger Games", Type.BOOK);
        db.save(film);
        db.save(book);

        List<Swrl> saved = db.getActive();

        assertThat(saved, containsInAnyOrder(film, book));
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
    public void savingASwrlMarkedAsDoneReplacesItAsActive() throws Exception {
        db.save(THE_MATRIX);
        db.markAsDone(THE_MATRIX);

        assertThat(db.getDone(), contains(THE_MATRIX));

        db.save(THE_MATRIX);

        assertThat(db.getActive(), contains(THE_MATRIX));
        assertThat(db.getDone(), is(emptyCollectionOf(Swrl.class)));
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

    @Test
    public void canAddDetailsToASwrlAndRetrieveThem() throws Exception {
        db.save(THE_MATRIX);
        db.saveDetails(THE_MATRIX, THE_MATRIX_RELOADED_DETAILS);

        assertThat(db.getActive(), contains(THE_MATRIX));

        Details detailsFromDB = db.getActive().get(0).getDetails();

        assertEquals(THE_MATRIX_RELOADED_DETAILS, detailsFromDB);
    }

    @Test
    public void canUpdateASwrlsDetails() throws Exception {
        db.save(THE_MATRIX);
        db.saveDetails(THE_MATRIX, THE_MATRIX_DETAILS);

        db.saveDetails(THE_MATRIX, THE_MATRIX_REVOLUTIONS_DETAILS);

        assertThat(db.getActive(), contains(THE_MATRIX));

        Details detailsFromDB = db.getActive().get(0).getDetails();

        assertEquals(THE_MATRIX_REVOLUTIONS_DETAILS, detailsFromDB);
    }

    @Test
    public void modifyingTheSwrlInOtherWaysDoesNotAffectTheDetails() throws Exception {
        db.save(THE_MATRIX);
        db.saveDetails(THE_MATRIX, THE_MATRIX_DETAILS);

        db.markAsDone(THE_MATRIX);
        db.markAsActive(THE_MATRIX);

        assertThat(db.getActive(), contains(THE_MATRIX));
        Details detailsFromDB = db.getActive().get(0).getDetails();

        assertEquals(THE_MATRIX_DETAILS, detailsFromDB);
    }

    @Test
    public void savingTheSameSwrlAgainRemovesDetails() throws Exception {
        Swrl SWRL = new Swrl("Swrl", Type.FILM);
        db.save(SWRL);
        db.saveDetails(SWRL, THE_MATRIX_DETAILS);

        db.save(SWRL);

        assertThat(db.getActive(), contains(SWRL));
        Details detailsFromDB = db.getActive().get(0).getDetails();

        assertNull(detailsFromDB);
    }

    @Test
    public void savingASwrlWithDetails() throws Exception {
        Swrl swrlWithDetails = new Swrl("The Matrix", Type.FILM);
        swrlWithDetails.setDetails(THE_MATRIX_DETAILS);
        db.save(swrlWithDetails);

        assertThat(db.getActive(), contains(swrlWithDetails));

        Details detailsFromDB = db.getActive().get(0).getDetails();
        Details detailsFromOriginalSwrl = swrlWithDetails.getDetails();
        assertEquals(detailsFromOriginalSwrl, detailsFromDB);
    }

    @Test
    public void updatingASwrlToAnExistingSwrlWithTheSameTitleReplacesExistingSwrl() throws Exception {
        Swrl swrl = new Swrl("The Matrix", Type.FILM);
        db.save(swrl);

        assertThat(db.getActive(), contains(swrl));

        db.markAsDone(swrl);

        assertThat(db.getActive(), is(emptyCollectionOf(Swrl.class)));
        assertThat(db.getDone(), contains(swrl));

        Swrl swrl2 = new Swrl("Matrix", Type.FILM);
        db.save(swrl2);

        assertThat(db.getActive(), contains(swrl2));
        assertThat(db.getDone(), contains(swrl));

        db.updateTitle(swrl2, "The Matrix");

        assertEquals("The Matrix", db.getActive().get(0).getTitle());
        assertThat(db.getDone(), is(emptyCollectionOf(Swrl.class)));
    }

    @Test
    public void countActiveSwrls() throws Exception {
        Swrl swrl = new Swrl("The Matrix", Type.FILM);
        db.save(swrl);

        assertEquals(1, db.countActiveByFilter(Type.FILM));
        assertEquals(0, db.countActiveByFilter(Type.UNKNOWN));
        assertEquals(0, db.countActiveByFilter(Type.TV));
        assertEquals(1, db.countActive());

        Swrl swrl2 = new Swrl("Another Film Swrl", Type.FILM);
        db.save(swrl2);

        assertEquals(2, db.countActiveByFilter(Type.FILM));
        assertEquals(0, db.countActiveByFilter(Type.TV));
        assertEquals(2, db.countActive());


        db.markAsDone(swrl);
        assertEquals(1, db.countActiveByFilter(Type.FILM));
        assertEquals(1, db.countActive());

        Swrl tvSwrl = new Swrl("TV Swrl", Type.TV);
        db.save(tvSwrl);

        assertEquals(1, db.countActiveByFilter(Type.FILM));
        assertEquals(1, db.countActiveByFilter(Type.TV));
        assertEquals(0, db.countActiveByFilter(Type.BOOK));
        assertEquals(2, db.countActive());
    }
}