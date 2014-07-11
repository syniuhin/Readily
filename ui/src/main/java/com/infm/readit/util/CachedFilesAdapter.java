package com.infm.readit.util;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.infm.readit.R;
import com.infm.readit.readable.MiniReadable;

import java.util.List;

public class CachedFilesAdapter extends BaseAdapter {

	private Context context;
	private List<MiniReadable> objects;
	private LayoutInflater inflater;

	public CachedFilesAdapter(Context context, List<MiniReadable> objects){
		this.context = context;
		this.objects = objects;

		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount(){
		return objects.size();
	}

	@Override
	public Object getItem(int position){
		return objects.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View view = convertView;
		if (view == null)
			view = inflater.inflate(R.layout.list_element_main, parent, false);

		MiniReadable readable = objects.get(position);
		((TextView) view.findViewById(R.id.text_view_title)).setText(readable.getHeader());
		((TextView) view.findViewById(R.id.text_view_path)).setText(readable.getPath());
		((TextView) view.findViewById(R.id.text_view_percent)).setText(readable.getPercent());
		(view.findViewById(R.id.editButton)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v){
				Toast.makeText(context, R.string.text_null, Toast.LENGTH_SHORT).show();
			}
		});
		return view;
	}

	public void updateAll(Cursor cursor){
		objects = MiniReadable.getFromCursor(cursor);
		notifyDataSetChanged();
	}
}