package com.infm.readit.instructions;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.infm.readit.R;

/**
 * created on 7/16/14 by infm. Enjoy ;)
 */
public class InstructionsFragment extends Fragment {
	public static final String ARG_PAGE = "page";

	public InstructionsFragment(){
	}

	public static InstructionsFragment create(int pageNumber){
		InstructionsFragment fragment = new InstructionsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_instructions, container, false);
		View view;
		switch (getArguments().getInt(ARG_PAGE)){
			case 0:
				view = inflater.inflate(R.layout.instructions_page1, rootView, false);
				break;
			case 1:
				view = inflater.inflate(R.layout.instructions_page2, rootView, false);
				break;
			case 2:
				view = inflater.inflate(R.layout.instructions_page3, rootView, false);
				break;
			default:
				view = inflater.inflate(R.layout.instructions_page4, rootView, false);
				view.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v){
						getActivity().finish();
					}
				});
				break;
		}
		rootView.addView(view);
		return rootView;
	}
}
