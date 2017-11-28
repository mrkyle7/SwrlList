package co.swrl.list;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import co.swrl.list.collection.SQLiteCollectionManager;
import co.swrl.list.item.Details;
import co.swrl.list.item.Swrl;
import co.swrl.list.item.Type;

import static co.swrl.list.Helpers.BILLIONS;
import static co.swrl.list.Helpers.GARDEN_STATE_RECOMMENDATION;
import static co.swrl.list.Helpers.THE_MATRIX;
import static co.swrl.list.Helpers.THE_MATRIX_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED;
import static co.swrl.list.Helpers.THE_MATRIX_RELOADED_DETAILS;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS;
import static co.swrl.list.Helpers.THE_MATRIX_REVOLUTIONS_DETAILS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SQLiteCollectionManagerTest {

    private SQLiteCollectionManager db;

    @Before
    public void setUp() throws Exception {
        db = new SQLiteCollectionManager(InstrumentationRegistry.getTargetContext());
        db.permanentlyDeleteAll();
    }

    @After
    public void tearDown() throws Exception {
        db.permanentlyDeleteAll();
    }

    @Test
    public void canAddAndGetActiveSwrlsFromDB() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.save(GARDEN_STATE_RECOMMENDATION);

        List<Swrl> saved = db.getActive();

        assertThat(saved, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED, GARDEN_STATE_RECOMMENDATION));
    }

    @Test
    public void recommendationsDoNotLoseDetails() throws Exception {
        db.save(GARDEN_STATE_RECOMMENDATION);

        List<Swrl> saved = db.getActive();
        List<Swrl> savedFilms = db.getActive(Type.FILM);
        List<Swrl> all = db.getAll();

        Swrl gardenStateFromDB = saved.get(0);
        Swrl gardenStateFromDBFilmFilter = savedFilms.get(0);
        Swrl gardenStateFromAll = all.get(0);


        assertEquals(GARDEN_STATE_RECOMMENDATION, gardenStateFromDB);
        assertEquals(GARDEN_STATE_RECOMMENDATION.getReview(), gardenStateFromDB.getReview());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthor(), gardenStateFromDB.getAuthor());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorId(), gardenStateFromDB.getAuthorId());

        assertEquals(GARDEN_STATE_RECOMMENDATION.getId(), gardenStateFromDB.getId());


        assertEquals(GARDEN_STATE_RECOMMENDATION, gardenStateFromDBFilmFilter);
        assertEquals(GARDEN_STATE_RECOMMENDATION.getReview(), gardenStateFromDBFilmFilter.getReview());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthor(), gardenStateFromDBFilmFilter.getAuthor());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorId(), gardenStateFromDBFilmFilter.getAuthorId());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorAvatarURL(), gardenStateFromDBFilmFilter.getAuthorAvatarURL());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getId(), gardenStateFromDBFilmFilter.getId());

        assertEquals(GARDEN_STATE_RECOMMENDATION, gardenStateFromAll);
        assertEquals(GARDEN_STATE_RECOMMENDATION.getReview(), gardenStateFromAll.getReview());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthor(), gardenStateFromAll.getAuthor());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorId(), gardenStateFromAll.getAuthorId());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorAvatarURL(), gardenStateFromAll.getAuthorAvatarURL());
        assertEquals(GARDEN_STATE_RECOMMENDATION.getId(), gardenStateFromAll.getId());

    }

    @Test
    public void canAddAndGetActiveSwrlsByTypeFromDB() throws Exception {
        db.save(THE_MATRIX);
        db.save(THE_MATRIX_RELOADED);
        db.save(BILLIONS);

        List<Swrl> films = db.getActive(Type.FILM);

        assertThat(films, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED));

        List<Swrl> tvShows = db.getActive(Type.TV);

        assertThat(tvShows, containsInAnyOrder(BILLIONS));

        List<Swrl> books = db.getActive(Type.BOOK);

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
        List<Swrl> doneFilms = db.getDone(Type.FILM);
        List<Swrl> all = db.getAll();

        assertThat(active, contains(THE_MATRIX_RELOADED));
        assertThat(done, contains(THE_MATRIX));
        assertThat(doneFilms, contains(THE_MATRIX));
        assertThat(all, containsInAnyOrder(THE_MATRIX, THE_MATRIX_RELOADED));
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
    public void doesNotExplodeIfUpdatingDetailsForASwrlThatDoesNotExist() throws Exception {
        db.saveDetails(THE_MATRIX, THE_MATRIX_DETAILS);
        assertEquals(Collections.EMPTY_LIST, db.getAll());
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
    public void canUpdateAvatarURL() throws Exception {
        db.save(THE_MATRIX);
        db.save(GARDEN_STATE_RECOMMENDATION);

        Swrl gardenStateFromDB = db.getActive().get(0);

        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorAvatarURL(), gardenStateFromDB.getAuthorAvatarURL());

        db.updateAuthorAvatarURL(GARDEN_STATE_RECOMMENDATION, "newURL");

        Swrl gardenStateFromDBUpdated = db.getActive().get(0);
        assertEquals("newURL", gardenStateFromDBUpdated.getAuthorAvatarURL());
    }

    @Test
    public void canUpdateAuthorId() throws Exception {
        db.save(THE_MATRIX);
        db.save(GARDEN_STATE_RECOMMENDATION);

        Swrl gardenStateFromDB = db.getActive().get(0);

        assertEquals(GARDEN_STATE_RECOMMENDATION.getAuthorId(), gardenStateFromDB.getAuthorId());

        db.updateAuthorID(GARDEN_STATE_RECOMMENDATION, 10304);

        Swrl gardenStateFromDBUpdated = db.getActive().get(0);
        assertEquals(10304, gardenStateFromDBUpdated.getAuthorId());
    }

    @Test
    public void countSwrls() throws Exception {
        Swrl film = new Swrl("The Matrix", Type.FILM);
        db.save(film);

        assertEquals(1, db.countActive(Type.FILM));
        assertEquals(0, db.countActive(Type.UNKNOWN));
        assertEquals(0, db.countActive(Type.TV));
        assertEquals(1, db.countActive());

        Swrl film2 = new Swrl("Another Film Swrl", Type.FILM);
        db.save(film2);

        assertEquals(2, db.countActive(Type.FILM));
        assertEquals(0, db.countActive(Type.TV));
        assertEquals(2, db.countActive());


        db.markAsDone(film);
        assertEquals(1, db.countActive(Type.FILM));
        assertEquals(1, db.countActive());
        assertEquals(1, db.countDone());
        assertEquals(1, db.countDone(Type.FILM));
        assertEquals(0, db.countDone(Type.TV));

        Swrl tv = new Swrl("TV Swrl", Type.TV);
        db.save(tv);

        assertEquals(1, db.countActive(Type.FILM));
        assertEquals(1, db.countActive(Type.TV));
        assertEquals(0, db.countActive(Type.BOOK));
        assertEquals(2, db.countActive());
    }
}