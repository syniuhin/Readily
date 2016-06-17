package com.infmme.readilyapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.infmme.readilyapp.fragment.FileListFragment;
import com.infmme.readilyapp.readable.Utils;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.settings.SettingsActivity;
import com.infmme.readilyapp.util.Constants;
import com.ipaulpro.afilechooser.utils.FileUtils;

public class MainActivity extends BaseActivity {

  private static final int FILE_SELECT_CODE = 7331;
  private static final int READ_EXTERNAL_STORAGE_REQUEST = 7878;

  private Toolbar mToolbar;
  private RecyclerView mRecyclerView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    isAnybodyOutThere(this);

    findViews();
    setupToolbar();
    startFileListFragment();
  }

  @Override
  protected void findViews() {
    mToolbar = (Toolbar) findViewById(R.id.main_toolbar);
    mRecyclerView = (RecyclerView) findViewById(R.id.cache_list);
  }

  private void setupToolbar() {
    setSupportActionBar(mToolbar);

    getSupportActionBar().setDisplayShowTitleEnabled(false);
    mToolbar.setLogo(R.drawable.logo_up);
  }

  /**
   * Checks if user opens app first time
   *
   * @param context is used to get SharedPreferences
   */
  private void isAnybodyOutThere(Context context) {
    if (!PreferenceManager.getDefaultSharedPreferences(context)
                          .getBoolean(Constants.Preferences.NEWCOMER, false)) {
      InstructionsActivity.start(this);
      PreferenceManager.getDefaultSharedPreferences(context)
                       .edit()
                       .putBoolean(Constants.Preferences.NEWCOMER, true)
                       .apply();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        startSettingsActivity();
        break;
      case R.id.action_clipboard:
        getFromClipboard();
        break;
      case R.id.action_file:
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
          int permissionCheck = ContextCompat.checkSelfPermission(
              this, Manifest.permission.READ_EXTERNAL_STORAGE);
          if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                READ_EXTERNAL_STORAGE_REQUEST);
            break;
          }
        }
        getFromFile();
        break;
      case R.id.action_instructions:
        InstructionsActivity.start(this);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void getFromClipboard() {
    ReceiverActivity.startReceiverActivity(this, ReadableType.CLIPBOARD,
                                           ReadingSource.SHARE, null);
  }

  private void getFromFile() {
    // Implicitly allow the user to select a particular kind of data
    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    // The MIME data type filter
    intent.setType("*/*");
    // Only return URIs that can be opened with ContentResolver
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    startActivityForResult(
        Intent.createChooser(FileUtils.createGetContentIntent(),
                             getResources().getString(R.string.choose_file)),
        FILE_SELECT_CODE);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    switch (requestCode) {
      case FILE_SELECT_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null) {
            String relativePath = FileUtils.getPath(this, data.getData());
            if (Utils.isExtensionValid(
                FileUtils.getExtension(relativePath))) {
              String extension = FileUtils.getExtension(relativePath);
              ReadableType type;
              if (extension.equals(".epub")) {
                type = ReadableType.EPUB;
              } else if (extension.equals(".fb2")) {
                type = ReadableType.FB2;
              } else {
                type = ReadableType.TXT;
              }
              ReceiverActivity.startReceiverActivity(
                  this, type, ReadingSource.CACHE, relativePath);
            } else {
              Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT)
                   .show();
            }
          }
        }
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onRequestPermissionsResult(
      int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    switch (requestCode) {
      case READ_EXTERNAL_STORAGE_REQUEST: {
        if (grantResults.length > 0
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          getFromFile();
        } else {
          Toast.makeText(this, "This permission is a must here!",
                         Toast.LENGTH_SHORT).show();
        }
      }
    }
  }

  private void startSettingsActivity() {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  private void startFileListFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null,
                                 FragmentManager.POP_BACK_STACK_INCLUSIVE);
    fragmentManager.beginTransaction().
        replace(R.id.content_layout, new FileListFragment()).
                       commit();
  }
}
