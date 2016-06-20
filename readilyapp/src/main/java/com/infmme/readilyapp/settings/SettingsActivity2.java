package com.infmme.readilyapp.settings;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.infmme.readilyapp.R;

public class SettingsActivity2 extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings2);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(
        view -> Snackbar.make(view, "Replace with your own action",
                              Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show());
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    getSupportFragmentManager().beginTransaction()
                               .replace(R.id.container, new SettingsFragment())
                               .commit();
  }

}
