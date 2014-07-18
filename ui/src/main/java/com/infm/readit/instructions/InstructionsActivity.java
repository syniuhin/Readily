package com.infm.readit.instructions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import com.infm.readit.R;

/**
 * created on 7/16/14 by infm. Enjoy ;)
 */
public class InstructionsActivity extends FragmentActivity {
	private static final int NUM_PAGES = 4;

	private ViewPager pager;
	private PagerAdapter pagerAdapter;

	public static void start(Context context){
		context.startActivity(new Intent(context, InstructionsActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instructions);

		pager = (ViewPager) findViewById(R.id.instructions_pager);
		pagerAdapter = new InstructionsPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position){
				invalidateOptionsMenu();
			}
		});
	}

	private class InstructionsPagerAdapter extends FragmentStatePagerAdapter {

		public InstructionsPagerAdapter(FragmentManager fm){
			super(fm);
		}

		@Override
		public Fragment getItem(int i){
			return InstructionsFragment.create(i);
		}

		@Override
		public int getCount(){
			return NUM_PAGES;
		}
	}
}
