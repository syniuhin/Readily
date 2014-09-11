package com.infmme.readily.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.infmme.readily.Constants;
import com.infmme.readily.R;
import com.infmme.readily.ReceiverActivity;
import com.infmme.readily.readable.MiniReadable;
import com.infmme.readily.readable.Storable;
import com.infmme.readily.service.LastReadService;

public class CachedFilesAdapter extends SimpleCursorAdapter {

	private static final int DURATION = 300;

	private View usedView;
	private int usedPosition = -1;

	public CachedFilesAdapter(Context context){
		super(context, R.layout.list_element_main, null, new String[]{}, new int[]{}, 0);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent){
		return LayoutInflater.from(context).inflate(R.layout.list_element_main, parent, false);
	}

	@Override
	public void bindView(View v, final Context context, Cursor cursor){
		final MiniReadable readable = MiniReadable.singletonFromCursor(cursor);

		final int position = cursor.getPosition();
		final View view = v;
		final TextView textViewTitle = (TextView) view.findViewById(R.id.text_view_title);
		final TextView textViewFilename = (TextView) view.findViewById(R.id.text_view_filename);
		final TextView textViewPercent = (TextView) view.findViewById(R.id.text_view_percent);

		final String path = readable.getPath();
		String filename = path.substring(path.lastIndexOf('/') + 1);
		textViewTitle.setText(readable.getHeader());
		textViewFilename.setText(filename);
		textViewPercent.setText(readable.getPercent() + " " + context.getString(R.string.sth_left));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				if (usedPosition != position || usedView == null){
					hideActionView();

					Bundle args = new Bundle();
					args.putInt(Constants.EXTRA_TYPE, Storable.TYPE_FILE);
					args.putString(Constants.EXTRA_PATH, path);
					args.putString(Constants.EXTRA_HEADER, textViewTitle.getText().toString());
					ReceiverActivity.startReceiverActivity(context, args);
				}
			}
		});

		view.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v){
				if (usedPosition != position){
					hideActionView();
					inflateActionMenu(view);
					usedView = view;
					usedPosition = position;
					(view.findViewById(R.id.imageViewDelete)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v){
							getConfirmation(context, path, position);
							hideActionView();
						}
					});
					(view.findViewById(R.id.imageViewEdit)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v){
							buildEditorDialog(context, readable);
							hideActionView();
						}
					});
					(view.findViewById(R.id.imageViewBack)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v){
							hideActionView();
						}
					});
					(view.findViewById(R.id.imageViewAbout)).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v){
							buildInfoDialog(context, readable);
							hideActionView();
						}
					});
					return true;
				}
				return false;
			}
		});

	}

	private void inflateActionMenu(View v){
		View actionView = v.findViewById(R.id.action_view);
		View mainView = v.findViewById(R.id.main_view);

		int height = mainView.getHeight();
		actionView.setMinimumHeight(height);

		YoYo.with(Techniques.SlideOutRight).
				duration(DURATION).
				playOn(mainView);

		actionView.setVisibility(View.VISIBLE);
		YoYo.with(Techniques.FadeIn).
				duration(DURATION).
				playOn(actionView);
	}

	private void inflateMainMenu(View v){
		final View actionView = v.findViewById(R.id.action_view);
		View mainView = v.findViewById(R.id.main_view);

		YoYo.with(Techniques.FadeOut).
				duration(DURATION).
				playOn(actionView);

		YoYo.with(Techniques.SlideInRight).
				duration(DURATION).
				playOn(mainView);
		actionView.postDelayed(new Runnable() {
			@Override
			public void run(){
				actionView.setVisibility(View.GONE);
			}
		}, DURATION);
	}

	public void hideActionView(){
		if (usedView != null){
			inflateMainMenu(usedView);
			usedView = null;
			usedPosition = -1;
		}
	}

	private void getConfirmation(final Context context, final String path, final int position){
		new AlertDialog.Builder(context).
				setTitle(R.string.confirmation_dialog_title).
				setMessage(R.string.gonna_delete).
				setPositiveButton(android.R.string.ok,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  LastReadService.start(context, path, Constants.DB_OPERATION_DELETE);
									  }
								  }).
				setNegativeButton(android.R.string.cancel,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  dialog.cancel();
									  }
								  }).
				show();
	}

	private void buildEditorDialog(final Context context, final MiniReadable readable){
		View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_editor, null);
		final EditText headerView = (EditText) dialogView.findViewById(R.id.editTextHeader);

		headerView.setText(readable.getHeader());

		new AlertDialog.Builder(context).
				setTitle(R.string.editor_dialog_title).
				setView(dialogView).
				setPositiveButton(android.R.string.ok,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  String nHeader = headerView.getText().toString();
										  if (!TextUtils.isEmpty(nHeader)){ readable.setHeader(nHeader); }

										  Intent intent = MiniReadable.createDBServiceIntent(context, readable);
										  intent.putExtra(Constants.EXTRA_DB_OPERATION, Constants.DB_OPERATION_INSERT);
										  context.startService(intent);
									  }
								  }).
				setNegativeButton(android.R.string.cancel,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  dialog.dismiss();
									  }
								  }).
				show();
	}

	private void buildInfoDialog(Context context, final MiniReadable readable){
		new AlertDialog.Builder(context).
				setTitle(R.string.about).
				setView(createInfoView(context, readable)).
				setPositiveButton(android.R.string.ok,
								  new DialogInterface.OnClickListener() {
									  @Override
									  public void onClick(DialogInterface dialog, int which){
										  dialog.dismiss();
									  }
								  }).
				show();
	}

	private View createInfoView(Context context, final MiniReadable readable){
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_info, null);
		((TextView) view.findViewById(R.id.textViewHeader)).setText(readable.getHeader());
		((TextView) view.findViewById(R.id.textViewPath)).setText(readable.getPath());
		((TextView) view.findViewById(R.id.textViewPosition)).
				setText(readable.getPercent());
		return view;
	}
}