package co.swrl.swrllist;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListActivityUnitTest {
    @Test
    public void showWhatsNewDialogIfNewVersion() throws Exception {
        SwrlPreferences preferences = mock(SwrlPreferences.class);
        SwrlDialogs dialogs = mock(SwrlDialogs.class);

        when(preferences.isPackageNewVersion()).thenReturn(true);

        ListActivity.showWhatsNewDialogIfNewVersion(preferences, dialogs);

        verify(dialogs).buildAndShowWhatsNewDialog();
        verify(preferences).savePackageVersionAsCurrentVersion();
    }

    @Test
    public void doNotShowWhatsNewDialogIfNotNewVersion() throws Exception {
        SwrlPreferences preferences = mock(SwrlPreferences.class);
        SwrlDialogs dialogs = mock(SwrlDialogs.class);

        when(preferences.isPackageNewVersion()).thenReturn(false);

        ListActivity.showWhatsNewDialogIfNewVersion(preferences, dialogs);

        verify(dialogs, never()).buildAndShowWhatsNewDialog();
        verify(preferences, never()).savePackageVersionAsCurrentVersion();
    }

}