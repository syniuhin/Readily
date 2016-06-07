package com.infmme.readilyapp;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.infmme.readilyapp.instructions.InstructionsActivity;
import com.infmme.readilyapp.readable.EpubFileStorable;
import com.infmme.readilyapp.readable.FB2FileStorable;
import com.infmme.readilyapp.readable.FileStorable;
import com.infmme.readilyapp.readable.Readable;
import com.infmme.readilyapp.readable.storable.epub.EpubPart;
import com.infmme.readilyapp.readable.storable.fb2.FB2Part;
import com.infmme.readilyapp.service.StorageCheckerService;
import com.infmme.readilyapp.settings.SettingsActivity;
import com.infmme.readilyapp.util.BaseActivity;
import com.infmme.readilyapp.util.Constants;
import com.ipaulpro.afilechooser.utils.FileUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

  private static final int FILE_SELECT_CODE = 7331;
  private static final int EPUB_SELECT_CODE = 9871;
  private static final int FB2_SELECT_CODE = 1982;
  private static final int READ_EXTERNAL_STORAGE_REQUEST = 7878;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    isAnybodyOutThere(this);

    startService(createCheckerServiceIntent());

    changeActionBarIcon();
    startFileListFragment();
  }

  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  private void changeActionBarIcon() {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setIcon(R.drawable.logo_up);
    }
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
      case R.id.action_epub_bookpartflow:
        startActivityForResult(
            Intent.createChooser(FileUtils.createGetContentIntent(),
                                 getResources().getString(
                                     R.string.choose_file)),
            EPUB_SELECT_CODE);
        break;
      case R.id.action_fb2_bookpartflow:
        startActivityForResult(
            Intent.createChooser(FileUtils.createGetContentIntent(),
                                 getResources().getString(
                                     R.string.choose_file)),
            FB2_SELECT_CODE);
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void startBookPartListActivity(String relativePath) {
    EpubFileStorable epubFileStorable = new EpubFileStorable(relativePath);
    epubFileStorable.process(this);

    Intent i = new Intent(this, BookPartListActivity.class);
    Bundle args = new Bundle();
    args.putSerializable(
        Constants.EXTRA_TOC_REFERENCE_LIST,
        EpubPart.adaptList(
            epubFileStorable.getTableOfContents().getTocReferences()));
    i.putExtras(args);
    startActivity(i);
  }

  private void startBookPartFb2ListActivity(String relativePath) {
    final FB2FileStorable fb2FileStorable = new FB2FileStorable(
        relativePath);
    fb2FileStorable.process(this);

    Observable<ArrayList<FB2Part>> o = Observable.create(
        new Observable.OnSubscribe<ArrayList<FB2Part>>() {
          @Override
          public void call(
              Subscriber<? super ArrayList<FB2Part>> subscriber) {
            try {
              ArrayList<FB2Part> toc = fb2FileStorable.getTableOfContents();
              subscriber.onNext(toc);
              subscriber.onCompleted();
            } catch (IOException e) {
              subscriber.onError(e);
            }
          }
        });
    o.subscribeOn(Schedulers.newThread())
     .observeOn(AndroidSchedulers.mainThread())
     .subscribe(new Action1<ArrayList<FB2Part>>() {
       @Override
       public void call(ArrayList<FB2Part> fb2Parts) {
         Intent i = new Intent(MainActivity.this, BookPartListActivity.class);
         Bundle args = new Bundle();
         args.putSerializable(
             Constants.EXTRA_TOC_REFERENCE_LIST, fb2Parts);
         i.putExtras(args);
         startActivity(i);
       }
     }, new Action1<Throwable>() {
       @Override
       public void call(Throwable throwable) {
         throwable.printStackTrace();
       }
     });
  }

  private void getFromClipboard() {
    ReceiverActivity.startReceiverActivity(this, Readable.TYPE_CLIPBOARD, "");
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
            if (FileStorable.isExtensionValid(
                FileUtils.getExtension(relativePath))) {
              ReceiverActivity.startReceiverActivity(this, Readable.TYPE_FILE,
                                                     relativePath);
            } else {
              Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT)
                   .show();
            }
          }
        }
        break;
      case EPUB_SELECT_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null) {
            String relativePath = FileUtils.getPath(this, data.getData());
            if (FileUtils.getExtension(relativePath).equals(".epub")) {
              startBookPartListActivity(relativePath);
            } else {
              Toast.makeText(this, R.string.wrong_ext, Toast.LENGTH_SHORT)
                   .show();
            }
          }
        }
        break;
      case FB2_SELECT_CODE:
        if (resultCode == RESULT_OK) {
          if (data != null) {
            String relativePath = FileUtils.getPath(this, data.getData());
            if (FileUtils.getExtension(relativePath).equals(".fb2")) {
              startBookPartFb2ListActivity(relativePath);
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
  public void onRequestPermissionsResult(int requestCode,
                                         @NonNull String[] permissions,
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

  private Intent createCheckerServiceIntent() {
    return new Intent(this, StorageCheckerService.class);
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
