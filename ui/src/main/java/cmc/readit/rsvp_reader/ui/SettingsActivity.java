package cmc.readit.rsvp_reader.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String PREF_SAVE_CLIPBOARD = "pref_save_clipboard";
    public static final String PREF_WPM = "pref_wpm";
    public static final String PREF_PUNCTUATION_DIFFERS = "pref_punct";
    public static final String PREF_COMA_OR_LONG = "pref_comaOrLong";
    public static final String PREF_END_OF_SENTENCE = "pref_endOfSentence";
    public static final String PREF_DASH_OR_COLON = "pref_dashOrColon";
    public static final String PREF_BEGINNING_OF_PARAGRAPH = "pref_begOfPar";
    public static final String PREF_SHOW_CONTEXT = "pref_showContext";
    public static final String PREF_SWIPE = "pref_swipe";
    public static final String PREF_TYPEFACE = "pref_typeface";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    }

    /*
    private class RowData {
        private String title;
        private Resource resource;
        public RowData() { super(); }
        public String getTitle() { return title; }
        public Resource getResource() { return resource; }
        public void setTitle(String title) { this.title = title; }
        public void setResource(Resource resource) { this.resource = resource; }
    }

    private void logContentsTable(List<TOCReference> tocReferences, int depth, List<RowData> contentDetails) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference:tocReferences) {
            StringBuilder tocString = new StringBuilder();
            for (int i = 0; i < depth; i++) {
                tocString.append("\t");
            }
            tocString.append(tocReference.getTitle());
            RowData row = new RowData();
            row.setTitle(tocString.toString());
            row.setResource(tocReference.getResource());
            contentDetails.add(row);
            logContentsTable(tocReference.getChildren(), depth + 1, contentDetails);
        }
    }
    */

    /*
    private class ChapterChooserDialog extends DialogFragment {
        List<RowData> values;

        public ChapterChooserDialog(List<RowData> _values) {
            super();
            values = _values;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            List<CharSequence> titles = new ArrayList<CharSequence>();
            for (RowData item : values)
                titles.add(item.getTitle());

            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
            builder.setMessage(getResources().getString(R.string.choose_chapter)).
                    setItems((CharSequence[]) titles.toArray(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            values.get(which).getResource().getData()
                        }
                    });
            return builder.create();
        }
    }
    */
}