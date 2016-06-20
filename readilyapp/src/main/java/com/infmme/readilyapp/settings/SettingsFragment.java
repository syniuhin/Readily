package com.infmme.readilyapp.settings;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import com.infmme.readilyapp.R;
import com.infmme.readilyapp.ReceiverActivity;
import com.infmme.readilyapp.readable.type.ReadableType;
import com.infmme.readilyapp.readable.type.ReadingSource;
import com.infmme.readilyapp.util.Constants;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with love, by infm dated on 6/20/16.
 */

public class SettingsFragment extends PreferenceFragmentCompat {

  private int mWordsPerMinute;

  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mWordsPerMinute = Integer.parseInt(
        PreferenceManager.getDefaultSharedPreferences(getActivity()).
            getString(Constants.Preferences.WPM, Constants.DEFAULT_WPM));
  }

  @Override
  public boolean onPreferenceTreeClick(Preference preference) {
    String key = preference.getKey();
    if (!TextUtils.isEmpty(key)) {
      if (key.equals(Constants.Preferences.WPM)) {
        chooseSpeed(getActivity());
        return true;
      }
      if (key.equals(Constants.Preferences.TEST)) {
        ReceiverActivity.startReceiverActivity(
            getActivity(), ReadableType.RAW, ReadingSource.SHARE,
            getResources().getString(R.string.sample_text));
        return true;
      }
      if (key.equals(Constants.Preferences.FEEDBACK)) {
        try {
          Resources resources = getResources();
          startActivity(Intent.createChooser(
              new Intent(Intent.ACTION_SENDTO,
                         Uri.parse(resources.getString(R.string.mail_to_me))),
              resources.getString(R.string.send_email)));
        } catch (ActivityNotFoundException e) {
          Toast.makeText(getActivity(), R.string.email_app_not_found,
                         Toast.LENGTH_SHORT).show();
        }
      }
      if (key.equals(Constants.Preferences.FONT_SIZE)) {
        chooseFontSize(getActivity());
      }
    }
    return super.onPreferenceTreeClick(preference);
  }

  private int pxFromDp(int dp) {
    return (int) (dp * this.getResources().getDisplayMetrics().density + 0.5f);
  }

  private float spToPixels(Context context, float sp) {
    float scaledDensity = context.getResources()
                                 .getDisplayMetrics().scaledDensity;
    return sp * scaledDensity;
  }

  private void chooseFontSize(final Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      showFontSizeNumberPicker(context);
    } else {
      showFontSizeEditText(context);
    }
  }

  private void chooseSpeed(final Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      showSpeedNumberPicker(context, Constants.MIN_WPM, Constants.MAX_WPM);
    } else {
      showSpeedEditText(context, Constants.MIN_WPM, Constants.MAX_WPM);
    }
  }

  private boolean changeTextViewSize(TextView text, String nSize) {
    return StringUtil.isNumeric(nSize) && changeTextViewSize(text,
                                                             Integer.parseInt(
                                                                 nSize));
  }

  private boolean changeTextViewSize(TextView text, int nSize) {
    if (nSize >= Constants.MIN_FONT_SIZE && nSize <= Constants.MAX_FONT_SIZE) {
      text.setTextSize(TypedValue.COMPLEX_UNIT_SP, nSize);
      return true;
    }
    return false;
  }

  private TextView getIndicatorText(final Context context, int initSize) {
    final TextView sampleText = new TextView(context);
    sampleText.setBackgroundColor(
        getResources().getColor(android.R.color.white));
    sampleText.setText(R.string.just_sample);
    sampleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, initSize);
    sampleText.setHeight(
        (int) (spToPixels(getActivity(), Constants.MAX_FONT_SIZE + 10) + .5f));
    sampleText.setGravity(Gravity.CENTER_HORIZONTAL);
    sampleText.setTypeface(Typeface.MONOSPACE);
    return sampleText;
  }

  private LinearLayout getDialogLayout(final Context context) {
    final LinearLayout linearLayout = new LinearLayout(context);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setLayoutParams(
        new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                     ViewGroup.LayoutParams.WRAP_CONTENT));
    return linearLayout;
  }

  private void showFontSizeEditText(final Context context) {
    int currentFontSize = Integer.parseInt(
        PreferenceManager.getDefaultSharedPreferences(context).
            getString(Constants.Preferences.FONT_SIZE, "18"));

    final EditText editText = new EditText(context);
    editText.setText(Integer.toString(currentFontSize));
    editText.setHint(R.string.hint_font_size);

    final LinearLayout linearLayout = getDialogLayout(context);
    final TextView sampleText = getIndicatorText(context, currentFontSize);
    linearLayout.addView(sampleText);
    linearLayout.addView(editText);

    editText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count,
                                    int after) {}

      @Override
      public void onTextChanged(CharSequence s, int start, int before,
                                int count) {}

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0)
          changeTextViewSize(sampleText, String.valueOf(s));
      }
    });

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.preference_font_size).
        setView(linearLayout).
               setPositiveButton(android.R.string.ok,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     String nSize = String.valueOf(
                                         editText.getText())
                                                          .replaceAll("\\s+",
                                                                      "");
                                     if (changeTextViewSize(sampleText, nSize))
                                       PreferenceManager
                                           .getDefaultSharedPreferences(
                                               context)
                                           .edit()
                                           .putString(
                                               Constants
                                                   .Preferences
                                                   .FONT_SIZE,
                                               nSize)
                                           .commit();
                                     else
                                       Toast.makeText(context,
                                                      R.string.illegal_value,
                                                      Toast.LENGTH_SHORT)
                                            .show();
                                   }
                                 }
               ).
               setNegativeButton(android.R.string.cancel,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     dialog.cancel();
                                   }
                                 }
               );
    AlertDialog dialog = builder.create();
    dialog.show();

    dialog.getWindow().setLayout(pxFromDp(Constants.DIALOG_PICKER_WIDTH),
                                 pxFromDp(Constants.DIALOG_PICKER_HEIGHT) +
                                     (int) (spToPixels(getActivity(),
                                                       Constants
                                                           .MAX_FONT_SIZE +
                                                           10) + .5f));

  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @SuppressLint("NewApi")
  private void showFontSizeNumberPicker(final Context context) {
    int currentFontSize = Integer.parseInt(
        PreferenceManager.getDefaultSharedPreferences(context).
            getString(Constants.Preferences.FONT_SIZE,
                      Constants.DEFAULT_FONT_SIZE));

    final NumberPicker numberPicker = new NumberPicker(context);
    numberPicker.setMinValue(Constants.MIN_FONT_SIZE);
    numberPicker.setMaxValue(Constants.MAX_FONT_SIZE);
    numberPicker.setValue(currentFontSize);

    final LinearLayout linearLayout = getDialogLayout(context);
    final TextView sampleText = getIndicatorText(context, currentFontSize);
    linearLayout.addView(sampleText);
    linearLayout.addView(numberPicker);
    numberPicker.setOnValueChangedListener(
        new NumberPicker.OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker picker, int oldVal,
                                    int newVal) {
            changeTextViewSize(sampleText, newVal);
          }
        });
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.preference_font_size).
        setView(linearLayout).
               setPositiveButton(android.R.string.ok,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     PreferenceManager
                                         .getDefaultSharedPreferences(
                                             context)
                                         .edit()
                                         .putString(
                                             Constants
                                                 .Preferences
                                                 .FONT_SIZE,
                                             String.valueOf(
                                                 numberPicker
                                                     .getValue()))
                                         .commit();
                                   }
                                 }
               ).
               setNegativeButton(android.R.string.cancel,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     dialog.cancel();
                                   }
                                 }
               );
    AlertDialog dialog = builder.create();
    dialog.show();

    int textHeight = (int) (spToPixels(getActivity(),
                                       Constants.MAX_FONT_SIZE + 10) + .5f);
    dialog.getWindow().setLayout(pxFromDp(Constants.DIALOG_PICKER_WIDTH),
                                 pxFromDp(
                                     Constants.DIALOG_PICKER_HEIGHT) +
                                     textHeight);
  }

  private void showSpeedEditText(final Context context, final int min,
                                 final int max) {
    final LinearLayout linearLayout = getDialogLayout(context);
    final EditText editText = new EditText(context);
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    editText.setText(Integer.toString(mWordsPerMinute));
    linearLayout.addView(editText);

    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.preferences_set_wpm).
        setView(linearLayout).
               setPositiveButton(android.R.string.ok,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     int nWPM = 0;
                                     if (StringUtil.isNumeric(
                                         String.valueOf(editText.getText())))
                                       nWPM = Integer.parseInt(
                                           String.valueOf(editText.getText()));
                                     if (nWPM >= min && nWPM <= max)
                                       PreferenceManager
                                           .getDefaultSharedPreferences(
                                               context)
                                           .edit()
                                           .putString(
                                               Constants.Preferences.WPM,
                                               Integer.toString(
                                                   mWordsPerMinute = nWPM))
                                           .commit();
                                     else
                                       Toast.makeText(context,
                                                      R.string.illegal_value,
                                                      Toast.LENGTH_SHORT).
                                                show();
                                   }
                                 }
               ).
               setNegativeButton(android.R.string.cancel,
                                 new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog,
                                                       int which) {
                                     dialog.cancel();
                                   }
                                 }
               );
    AlertDialog dialog = builder.create();
    dialog.show();
    dialog.getWindow().setLayout(pxFromDp(Constants.DIALOG_PICKER_WIDTH),
                                 pxFromDp(Constants.DIALOG_PICKER_HEIGHT));

  }


  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  @SuppressLint("NewApi")
  private void showSpeedNumberPicker(final Context context, final int min,
                                     final int max) {
    final NumberPicker numberPicker = new NumberPicker(context);
    numberPicker.setMinValue(min);
    numberPicker.setMaxValue(
        min + (max - min) / Constants.WPM_STEP_PREFERENCES);

    List<String> values = new ArrayList<String>();
    for (Integer i = min; i <= max; i += Constants.WPM_STEP_PREFERENCES) {
      values.add(i.toString());
    }
    numberPicker.setDisplayedValues(values.toArray(new String[values.size()]));
    numberPicker.setValue(
        min + (mWordsPerMinute - min) / Constants.WPM_STEP_PREFERENCES);
    AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setTitle(R.string.preferences_set_wpm)
           .
               setView(numberPicker)
           .setPositiveButton(
               android.R.string.ok,
               (dialog, which) -> {
                 mWordsPerMinute = min + Constants.WPM_STEP_PREFERENCES *
                     (numberPicker.getValue() - min);
                 PreferenceManager.getDefaultSharedPreferences(context)
                                  .edit()
                                  .putString(Constants.Preferences.WPM,
                                             Integer.toString(mWordsPerMinute))
                                  .apply();
               })
           .setNegativeButton(android.R.string.cancel,
                              (dialog, which) -> dialog.cancel());
    AlertDialog dialog = builder.create();
    dialog.show();
    dialog.getWindow().setLayout(pxFromDp(Constants.DIALOG_PICKER_WIDTH),
                                 pxFromDp(Constants.DIALOG_PICKER_HEIGHT));
  }
}
