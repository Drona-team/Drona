package com.facebook.react.modules.timepicker;

import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.format.DateFormat;
import androidx.annotation.Nullable;
import androidx.fragment.package_5.DialogFragment;
import androidx.fragment.package_5.Fragment;
import java.util.Calendar;
import java.util.Locale;

public class TimePickerDialogFragment
  extends DialogFragment
{
  @Nullable
  private DialogInterface.OnDismissListener mOnDismissListener;
  @Nullable
  private TimePickerDialog.OnTimeSetListener mOnTimeSetListener;
  
  public TimePickerDialogFragment() {}
  
  static Dialog createDialog(Bundle paramBundle, Context paramContext, TimePickerDialog.OnTimeSetListener paramOnTimeSetListener)
  {
    Calendar localCalendar = Calendar.getInstance();
    int i = localCalendar.get(11);
    int j = localCalendar.get(12);
    boolean bool = DateFormat.is24HourFormat(paramContext);
    TimePickerMode localTimePickerMode2 = TimePickerMode.DEFAULT;
    TimePickerMode localTimePickerMode1 = localTimePickerMode2;
    if (paramBundle != null)
    {
      localTimePickerMode1 = localTimePickerMode2;
      if (paramBundle.getString("mode", null) != null) {
        localTimePickerMode1 = TimePickerMode.valueOf(paramBundle.getString("mode").toUpperCase(Locale.US));
      }
    }
    if (paramBundle != null)
    {
      i = paramBundle.getInt("hour", localCalendar.get(11));
      j = paramBundle.getInt("minute", localCalendar.get(12));
      bool = paramBundle.getBoolean("is24Hour", DateFormat.is24HourFormat(paramContext));
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      if (localTimePickerMode1 == TimePickerMode.CLOCK) {
        return new DismissableTimePickerDialog(paramContext, paramContext.getResources().getIdentifier("ClockTimePickerDialog", "style", paramContext.getPackageName()), paramOnTimeSetListener, i, j, bool);
      }
      if (localTimePickerMode1 == TimePickerMode.SPINNER) {
        return new DismissableTimePickerDialog(paramContext, paramContext.getResources().getIdentifier("SpinnerTimePickerDialog", "style", paramContext.getPackageName()), paramOnTimeSetListener, i, j, bool);
      }
    }
    return new DismissableTimePickerDialog(paramContext, paramOnTimeSetListener, i, j, bool);
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return createDialog(getArguments(), getActivity(), mOnTimeSetListener);
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    super.onDismiss(paramDialogInterface);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(paramDialogInterface);
    }
  }
  
  public void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    mOnDismissListener = paramOnDismissListener;
  }
  
  public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener paramOnTimeSetListener)
  {
    mOnTimeSetListener = paramOnTimeSetListener;
  }
}
