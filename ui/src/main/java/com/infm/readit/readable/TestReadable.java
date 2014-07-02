package com.infm.readit.readable;

import android.content.Context;

import com.infm.readit.R;

/**
 * Created by infm on 6/13/14. Enjoy ;)
 */
public class TestReadable extends Readable {

	public TestReadable(){
		super();
		path = null;
		type = TYPE_TEST;
	}

	@Override
	public void process(Context context){
		text = new StringBuilder(context.getResources().getString(R.string.sample_text));
	}
}
