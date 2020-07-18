package com.facebook.react.modules.datepicker;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.BaseBundle;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Window;
import android.widget.DatePicker;
import androidx.annotation.Nullable;
import androidx.fragment.package_5.DialogFragment;
import androidx.fragment.package_5.Fragment;
import java.util.Calendar;
import java.util.Locale;

@SuppressLint({"ValidFragment"})
public class DatePickerDialogFragment
  extends DialogFragment
{
  private static final long DEFAULT_MIN_DATE = -2208988800001L;
  @Nullable
  private DatePickerDialog.OnDateSetListener mOnDateSetListener;
  @Nullable
  private DialogInterface.OnDismissListener mOnDismissListener;
  
  public DatePickerDialogFragment() {}
  
  static Dialog createDialog(Bundle paramBundle, Context paramContext, DatePickerDialog.OnDateSetListener paramOnDateSetListener)
  {
    Calendar localCalendar = Calendar.getInstance();
    if ((paramBundle != null) && (paramBundle.containsKey("date"))) {
      localCalendar.setTimeInMillis(paramBundle.getLong("date"));
    }
    int i = localCalendar.get(1);
    int j = localCalendar.get(2);
    int k = localCalendar.get(5);
    DatePickerMode localDatePickerMode2 = DatePickerMode.DEFAULT;
    DatePickerMode localDatePickerMode1 = localDatePickerMode2;
    if (paramBundle != null)
    {
      localDatePickerMode1 = localDatePickerMode2;
      if (paramBundle.getString("mode", null) != null) {
        localDatePickerMode1 = DatePickerMode.valueOf(paramBundle.getString("mode").toUpperCase(Locale.US));
      }
    }
    if (Build.VERSION.SDK_INT >= 21)
    {
      switch (1.$SwitchMap$com$facebook$react$modules$datepicker$DatePickerMode[localDatePickerMode1.ordinal()])
      {
      default: 
        paramContext = null;
        break;
      case 3: 
        paramContext = new DismissableDatePickerDialog(paramContext, paramOnDateSetListener, i, j, k);
        break;
      case 2: 
        paramContext = new DismissableDatePickerDialog(paramContext, 16973939, paramOnDateSetListener, i, j, k);
        paramContext.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        break;
      case 1: 
        paramContext = new DismissableDatePickerDialog(paramContext, paramContext.getResources().getIdentifier("CalendarDatePickerDialog", "style", paramContext.getPackageName()), paramOnDateSetListener, i, j, k);
        break;
      }
    }
    else
    {
      paramContext = new DismissableDatePickerDialog(paramContext, paramOnDateSetListener, i, j, k);
      switch (1.$SwitchMap$com$facebook$react$modules$datepicker$DatePickerMode[localDatePickerMode1.ordinal()])
      {
      default: 
        break;
      case 2: 
        paramContext.getDatePicker().setCalendarViewShown(false);
        break;
      case 1: 
        paramContext.getDatePicker().setCalendarViewShown(true);
        paramContext.getDatePicker().setSpinnersShown(false);
      }
    }
    paramOnDateSetListener = paramContext.getDatePicker();
    if ((paramBundle != null) && (paramBundle.containsKey("minDate")))
    {
      localCalendar.setTimeInMillis(paramBundle.getLong("minDate"));
      localCalendar.set(11, 0);
      localCalendar.set(12, 0);
      localCalendar.set(13, 0);
      localCalendar.set(14, 0);
      paramOnDateSetListener.setMinDate(localCalendar.getTimeInMillis());
    }
    else
    {
      paramOnDateSetListener.setMinDate(-2208988800001L);
    }
    if ((paramBundle != null) && (paramBundle.containsKey("maxDate")))
    {
      localCalendar.setTimeInMillis(paramBundle.getLong("maxDate"));
      localCalendar.set(11, 23);
      localCalendar.set(12, 59);
      localCalendar.set(13, 59);
      localCalendar.set(14, 999);
      paramOnDateSetListener.setMaxDate(localCalendar.getTimeInMillis());
    }
    return paramContext;
  }
  
  public Dialog onCreateDialog(Bundle paramBundle)
  {
    return createDialog(getArguments(), getActivity(), mOnDateSetListener);
  }
  
  public void onDismiss(DialogInterface paramDialogInterface)
  {
    super.onDismiss(paramDialogInterface);
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(paramDialogInterface);
    }
  }
  
  void setOnDateSetListener(DatePickerDialog.OnDateSetListener paramOnDateSetListener)
  {
    mOnDateSetListener = paramOnDateSetListener;
  }
  
  void setOnDismissListener(DialogInterface.OnDismissListener paramOnDismissListener)
  {
    mOnDismissListener = paramOnDismissListener;
  }
}
