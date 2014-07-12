package com.infm.readit.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infm.readit.Constants;
import com.infm.readit.R;
import com.infm.readit.ReceiverActivity;
import com.infm.readit.readable.MiniReadable;
import com.infm.readit.readable.Storable;
import com.infm.readit.service.LastReadService;

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

                    Bundle args = new Bundle();
                    args.putInt(Constants.EXTRA_TYPE, Storable.TYPE_FILE);
                    args.putString(Constants.EXTRA_PATH, path);
                    args.putString(Constants.EXTRA_HEADER, textViewTitle.getText().toString());
                    ReceiverActivity.startReceiverActivity(CachedFilesAdapter.this.context, args);
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
                                getConfirmation(path, position);
                                hideActionView();
                            }
                        });
                        (finalView.findViewById(R.id.imageViewEdit)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                buildEditorDialog(readable);
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

    private void getConfirmation(final String path, final int position) {
        new AlertDialog.Builder(context).
                setTitle(R.string.confirmation_dialog_title).
                setMessage(R.string.gonna_delete).
                setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LastReadService.start(context, path, Constants.DB_OPERATION_DELETE);
                                remove(position);
                            }
                        }).
                setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).
                show();
    }

    private void buildEditorDialog(final MiniReadable readable) {
        View dialogView = inflater.inflate(R.layout.dialog_editor, null);
        final EditText headerView = (EditText) dialogView.findViewById(R.id.editTextHeader);
        final EditText positionView = (EditText) dialogView.findViewById(R.id.editTextPosition);

        headerView.setText(readable.getHeader());
        positionView.setText(readable.getPosition().toString());

        new AlertDialog.Builder(context).
                setTitle(R.string.editor_dialog_title).
                setView(dialogView).
                setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String nHeader = headerView.getText().toString();
                                if (!TextUtils.isEmpty(nHeader))
                                    readable.setHeader(nHeader);

                                int nPosition = Integer.parseInt(positionView.getText().toString());
                                if (nPosition >= 0)
                                    readable.setPosition(nPosition);

                                Intent intent = MiniReadable.createDBServiceIntent(context, readable);
                                intent.putExtra(Constants.EXTRA_DB_OPERATION, Constants.DB_OPERATION_INSERT);
                                context.startService(intent);
                            }
                        }).
                setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).
                show();
    }
}