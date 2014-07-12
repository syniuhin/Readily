package com.infm.readit.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infm.readit.R;
import com.infm.readit.ReceiverActivity;
import com.infm.readit.readable.MiniReadable;
import com.infm.readit.readable.Storable;

import java.util.HashMap;
import java.util.List;

public class CachedFilesAdapter extends BaseAdapter {

    private static final String LOGTAG = "CachedFilesAdapter";

    private static final int DURATION = 300;

    private List<MiniReadable> objects;
    private LayoutInflater inflater;
    private Context context;

    private HashMap<String, Boolean> viewMap = new HashMap<String, Boolean>();

    private View usedView;

    public CachedFilesAdapter(Context context, List<MiniReadable> objects) {
        this.context = context;
        this.objects = objects;
        updateMap(objects);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public MiniReadable getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_element_main, parent, false);
        }

        final MiniReadable readable = getItem(position);

        final TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
        final TextView textViewPath = (TextView) view.findViewById(R.id.text_view_path);
        final TextView textViewPercent = (TextView) view.findViewById(R.id.text_view_percent);

        textViewTitle.setText(readable.getHeader());
        textViewPath.setText(readable.getPath());
        textViewPercent.setText(readable.getPercent());

        final View finalView = view;
        view.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onClick() {
                Log.d(LOGTAG, "listView's onItemClick called()");
                String path = textViewPath.getText().toString();
                if (usedView == null ||
                        !path.equals(((TextView) usedView.findViewById(R.id.text_view_path)).getText().toString())) {
                    hideActionView();
                    ReceiverActivity.startReceiverActivity(CachedFilesAdapter.this.context,
                            Storable.TYPE_FILE,
                            path);
                }
            }

            @Override
            public void onLongClick() {
                hideActionView();
                final String path = textViewPath.getText().toString();
                if (path != null && viewMap.get(path) != null) {
                    if (viewMap.get(path)) {
                        Log.d(LOGTAG, "already inflated");
                    } else {
                        inflateActionMenu(finalView);
                        viewMap.put(path, true);
                        usedView = finalView;
                        (finalView.findViewById(R.id.imageViewDelete)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                remove(position);
                                hideActionView();
                            }
                        });
                        (finalView.findViewById(R.id.imageViewEdit)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "lol", Toast.LENGTH_SHORT).show();
                                hideActionView();
                            }
                        });
                        (finalView.findViewById(R.id.imageViewBack)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                hideActionView();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    private void inflateActionMenu(View v) {
        View actionView = v.findViewById(R.id.action_view);
        View mainView = v.findViewById(R.id.main_view);

        int height = mainView.getLayoutParams().height;
        actionView.setMinimumHeight(height);

        YoYo.with(Techniques.SlideOutRight).
                duration(DURATION). //put this in Constants class
                playOn(mainView);

        actionView.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft).
                duration(DURATION).
                playOn(actionView);
    }

    private void inflateMainMenu(View v) {
        View actionView = v.findViewById(R.id.action_view);
        View mainView = v.findViewById(R.id.main_view);

        YoYo.with(Techniques.SlideOutLeft).
                duration(DURATION). //put this in Constants class
                playOn(actionView);

        actionView.setVisibility(View.GONE);
        YoYo.with(Techniques.SlideInRight).
                duration(DURATION).
                playOn(mainView);
    }

    public void updateAll(Cursor cursor) {
        objects = MiniReadable.getFromCursor(cursor);
        updateMap(objects);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        objects.remove(position);
        notifyDataSetChanged();
    }

    public void hideActionView() {
        if (usedView != null) {
            String path = ((TextView) usedView.findViewById(R.id.text_view_path)).getText().toString();
            viewMap.put(path, false);
            inflateMainMenu(usedView);
            usedView = null;
        }
    }

    private void updateMap(List<MiniReadable> objects) {
        viewMap.clear();
        for (MiniReadable r : objects)
            viewMap.put(r.getPath(), false);
    }
}