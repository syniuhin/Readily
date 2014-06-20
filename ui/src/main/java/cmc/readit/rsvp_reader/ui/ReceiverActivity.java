package cmc.readit.rsvp_reader.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cmc.readit.rsvp_reader.ui.ess.PrepareForView;
import cmc.readit.rsvp_reader.ui.ess.Reader;
import cmc.readit.rsvp_reader.ui.ess.TextParser;
import cmc.readit.rsvp_reader.ui.readable.CopiedFromClipboard;
import cmc.readit.rsvp_reader.ui.readable.FileReadable;
import cmc.readit.rsvp_reader.ui.readable.HtmlReadable;
import cmc.readit.rsvp_reader.ui.readable.Readable;
import cmc.readit.rsvp_reader.ui.readable.TestSettingsText;
import cmc.readit.rsvp_reader.ui.utils.FileUtils;
import cmc.readit.rsvp_reader.ui.utils.LastReadContentProvider;
import cmc.readit.rsvp_reader.ui.utils.OnSwipeTouchListener;
import cmc.readit.rsvp_reader.ui.utils.Utils;

public class ReceiverActivity extends Activity {

    public static final int TYPE_SHARED_LINK = 3;
    public static final int TYPE_SHARED_TEXT = 4;
    public static final String LOGTAG = "ReceiverActivity";
    private PrepareForView prep;
    private Reader reader;
    private SharedPreferences sPref;
    private RelativeLayout readerLayout;
    private cmc.readit.rsvp_reader.ui.readable.Readable readable;

    /**
     * Starts receiver activity
     *
     * @param utils = all needed is put in Utils abstract class
     */
    public static void startReceiverActivity(Context context, Utils utils) {
        utils.process();
        if (utils.isProcessFailed())
            return;

        Intent intent = new Intent(context, ReceiverActivity.class);

        String text = utils.getSb().toString();
        Pair<Integer, String> existingData = utils.getExistingData();
        int type = utils.getType();

        // why should I use MIME types?
        if (type != FileUtils.TYPE_EPUB)
            intent.setType("text/plain");
        else
            intent.setType("text/html");

        intent.putExtra(Intent.EXTRA_TEXT, text);
        intent.putExtra("source_type", type);
        if (existingData != null) {
            intent.putExtra("position", existingData.first);
            intent.putExtra("path", existingData.second);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * receive data
         */
        Intent intent = getIntent();
        Integer type = intent.getIntExtra("source_type", -1);
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        String path = intent.getStringExtra("path");

        if (TextUtils.isEmpty(text))
            readable = new TestSettingsText(this);
        else switch (type) {
            case Utils.TYPE_CLIPBOARD:
                readable = new CopiedFromClipboard(this);
                readable.setText(text);
                break;
            case Utils.TYPE_TXT:
                readable = new FileReadable();
                readable.setText(text);
                readable.setTextType("text/plain");
                readable.setPath(path);
                break;
            case Utils.TYPE_EPUB:
                readable = new FileReadable();
                readable.setText(text);
                readable.setTextType("text/html");
                readable.setPath(path);
                break;
            default:
                String link = TextParser.findLink(text);
                if (!TextUtils.isEmpty(link))
                    readable = new HtmlReadable(link);
                else
                    readable = new CopiedFromClipboard(this); // actually I don't know what to do here
        }

        /**
         * set window props
         */
        setViewProperties();

        sPref = PreferenceManager.getDefaultSharedPreferences(this);
        prep = new PrepareForView(readable, getWindowManager().getDefaultDisplay(), sPref);
        setLayoutSize();

        int pos = intent.getIntExtra("position", 0);
        readerLayout = (RelativeLayout) mkView((ViewGroup) getWindow().getDecorView(), pos);
        setContentView(readerLayout);

        final Handler handler = new Handler();
        reader = new Reader(handler, readerLayout, this, prep, pos);
        handler.postDelayed(reader, 2000);
    }

    private void setViewProperties() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.6f;
        getWindow().setAttributes(params);
        getWindow().getDecorView().setLayoutParams(params);
    }

    public View mkView(ViewGroup parent, int pos) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout readerLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_reader, parent, false);
        prep.initializeView(readerLayout);
        prep.updateView(readerLayout, pos);

        /* why there?? */
        RelativeLayout rl = (RelativeLayout) readerLayout.findViewById(R.id.reader_layout);

        rl.setOnTouchListener(new OnSwipeTouchListener(this) {
            /**
             * Changes speed through swipes.
             */
            @Override
            public void onSwipeTop() {
                int wpm = Integer.parseInt(sPref.getString(SettingsActivity.PREF_WPM, "250"));
                SharedPreferences.Editor q = sPref.edit();
                q.putString(SettingsActivity.PREF_WPM, Integer.toString(wpm + 50));
                q.commit();
                Log.d(LOGTAG, "WPM increase to " + Integer.toString(wpm + 50));
                prep.showSpeedo(wpm + 50, readerLayout);
            }

            @Override
            public void onSwipeBottom() {
                int wpm = Integer.parseInt(sPref.getString(SettingsActivity.PREF_WPM, "250"));
                SharedPreferences.Editor q = sPref.edit();
                q.putString(SettingsActivity.PREF_WPM, Integer.toString(Math.max(wpm - 50, 50)));
                q.commit();
                Log.d(LOGTAG, "WPM decreased to " + Integer.toString(Math.max(wpm - 50, 50)));
                prep.showSpeedo(Math.max(wpm - 50, 50), readerLayout);
            }

            /**
             * If pref set to true, perform orientation through swipes.
             * Conflicts with currentTextView, need to be fixed
             */
            @Override
            public void onSwipeRight() {
                if (reader.isCancelled() && sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                    int pos = reader.getPosition();
                    if (pos > 0) {
                        prep.updateView(readerLayout, pos - 1);
                        reader.setPosition(pos - 1);
                    }
                } else if (!reader.isCancelled()) {
                    Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                    if (!sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                        ImageButton prevButton = (ImageButton) readerLayout.findViewById(R.id.previousWordImageButton);
                        prevButton.setVisibility(View.VISIBLE);
                        prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
                        prevButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = reader.getPosition();
                                if (pos > 0) {
                                    prep.updateView(readerLayout, pos - 1);
                                    reader.setPosition(pos - 1);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onSwipeLeft() {
                if (reader.isCancelled() && sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                    int pos = reader.getPosition();
                    if (pos < prep.getParser().getReadable().getWordList().size() - 1) {
                        prep.updateView(readerLayout, pos + 1);
                        reader.setPosition(pos + 1);
                    }
                } else if (!reader.isCancelled()) {
                    Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                    if (!sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                        ImageButton prevButton = (ImageButton) readerLayout.findViewById(R.id.previousWordImageButton);
                        prevButton.setVisibility(View.VISIBLE);
                        prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
                        prevButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int pos = reader.getPosition();
                                if (pos > 0) {
                                    prep.updateView(readerLayout, pos - 1);
                                    reader.setPosition(pos - 1);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onClick() {
                if (reader.isCompleted()) {
                    finish();
                } else {
                    reader.incCancelled();
                    if (reader.isCancelled()) {
                        Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
                        if (!sPref.getBoolean(SettingsActivity.PREF_SWIPE, false)) {
                            ImageButton prevButton = (ImageButton) readerLayout.findViewById(R.id.previousWordImageButton);
                            prevButton.setVisibility(View.VISIBLE);
                            prevButton.setImageResource(R.drawable.abc_ic_ab_back_holo_light);
                            prevButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int pos = reader.getPosition();
                                    if (pos > 0) {
                                        prep.updateView(readerLayout, pos - 1);
                                        reader.setPosition(pos - 1);
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Play", Toast.LENGTH_SHORT).show();
                        ImageButton prevButton = (ImageButton) readerLayout.findViewById(R.id.previousWordImageButton);
                        prevButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        return readerLayout;
    }

    /**
     * updates view on orientation change.
     * Checks if reader is paused, otherwise click on a TextView
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!reader.isCancelled())
            reader.incCancelled();
        setLayoutSize();
        prep.initializeView(readerLayout);
        reader.setPosition(Math.max(0, reader.getPosition() - 5));
        prep.updateView(readerLayout, reader.getPosition());
        setContentView(readerLayout);
    }

    private void setLayoutSize() {
        int width = prep.getAppWidth();
        int height = prep.getAppHeight();

        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .15));
        } else {
            getWindow().setLayout((int) (width * .9), (int) (height * .3));
        }
    }

    @Override
    public void onPause() {
        if (!reader.isCancelled()) {
            reader.isCancelled();
            reader.setPosition(Math.max(0, reader.getPosition() - 2));
        }
        insertReading();
        Log.d(LOGTAG, "onPause() called");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(LOGTAG, "onStop() called");
        super.onStop();
    }

    /**
     * actually, updates it as well
     */
    protected void insertReading() {
        readable.setPosition(reader.getPosition());
        ContentValues vals = readable.getContentValues();
        ContentResolver cr = getContentResolver();
        Pair<Integer, String> existingData = Readable.getRowData(cr.query(LastReadContentProvider.CONTENT_URI,
                null, null, null, null), readable.getPath());

        if (existingData == null)
            cr.insert(LastReadContentProvider.CONTENT_URI, vals);
        else
            cr.update(ContentUris.withAppendedId(LastReadContentProvider.CONTENT_URI, existingData.first),
                    vals, null, null);
    }

}
