package com.infmme.readilyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import com.infmme.readilyapp.fragment.InstructionsFragment;

/**
 * created on 7/16/14 by infm. Enjoy ;)
 */
public class InstructionsActivity extends ActionBarActivity {
  private static final int NUM_PAGES = 6;

  private ViewPager pager;
  private PagerAdapter pagerAdapter;
  private Button nextButton;

  public static void start(Context context) {
    context.startActivity(new Intent(context, InstructionsActivity.class));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_instructions);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null)
      actionBar.hide();

    pager = (ViewPager) findViewById(R.id.instructions_pager);
    pagerAdapter = new InstructionsPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);
    pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
      @Override
      public void onPageSelected(int position) {
        supportInvalidateOptionsMenu();
        updateNextButton();
      }
    });

    nextButton = (Button) findViewById(R.id.nextButton);
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        boolean action = updateNextButton();
        if (action)
          pager.setCurrentItem(pager.getCurrentItem() + 1, true);
        else
          onStop();
      }
    });
  }

  private boolean updateNextButton() {
    int pageNum = pager.getCurrentItem();
    if (pageNum < NUM_PAGES - 2) {
      if (pageNum == NUM_PAGES - 3)
        nextButton.setText(R.string.finish);
      else
        nextButton.setText(R.string.next);
      return true;
    } else {
      return false;
    }
  }

  private class InstructionsPagerAdapter extends FragmentStatePagerAdapter {

    public InstructionsPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int i) {
      return InstructionsFragment.create(i);
    }

    @Override
    public int getCount() {
      return NUM_PAGES;
    }
  }
}
