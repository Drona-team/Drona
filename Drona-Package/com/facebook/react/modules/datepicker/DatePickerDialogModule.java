package com.facebook.react.modules.datepicker;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.BaseBundle;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.fragment.package_5.DialogFragment;
import androidx.fragment.package_5.Fragment;
import androidx.fragment.package_5.FragmentActivity;
import androidx.fragment.package_5.FragmentManager;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="DatePickerAndroid")
public class DatePickerDialogModule
  extends ReactContextBaseJavaModule
{
  static final String ACTION_DATE_SET = "dateSetAction";
  static final String ACTION_DISMISSED = "dismissedAction";
  static final String ARG_DATE = "date";
  static final String ARG_MAXDATE = "maxDate";
  static final String ARG_MINDATE = "minDate";
  static final String ARG_MODE = "mode";
  private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
  @VisibleForTesting
  public static final String FRAGMENT_TAG = "DatePickerAndroid";
  
  public DatePickerDialogModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private Bundle createFragmentArguments(ReadableMap paramReadableMap)
  {
    Bundle localBundle = new Bundle();
    if ((paramReadableMap.hasKey("date")) && (!paramReadableMap.isNull("date"))) {
      localBundle.putLong("date", paramReadableMap.getDouble("date"));
    }
    if ((paramReadableMap.hasKey("minDate")) && (!paramReadableMap.isNull("minDate"))) {
      localBundle.putLong("minDate", paramReadableMap.getDouble("minDate"));
    }
    if ((paramReadableMap.hasKey("maxDate")) && (!paramReadableMap.isNull("maxDate"))) {
      localBundle.putLong("maxDate", paramReadableMap.getDouble("maxDate"));
    }
    if ((paramReadableMap.hasKey("mode")) && (!paramReadableMap.isNull("mode"))) {
      localBundle.putString("mode", paramReadableMap.getString("mode"));
    }
    return localBundle;
  }
  
  public String getName()
  {
    return "DatePickerAndroid";
  }
  
  public void open(ReadableMap paramReadableMap, Promise paramPromise)
  {
    Object localObject1 = (FragmentActivity)getCurrentActivity();
    if (localObject1 == null)
    {
      paramPromise.reject("E_NO_ACTIVITY", "Tried to open a DatePicker dialog while not attached to an Activity");
      return;
    }
    localObject1 = ((FragmentActivity)localObject1).getSupportFragmentManager();
    Object localObject2 = (DialogFragment)((FragmentManager)localObject1).findFragmentByTag("DatePickerAndroid");
    if (localObject2 != null) {
      ((DialogFragment)localObject2).dismiss();
    }
    localObject2 = new DatePickerDialogFragment();
    if (paramReadableMap != null) {
      ((Fragment)localObject2).setArguments(createFragmentArguments(paramReadableMap));
    }
    paramReadableMap = new DatePickerDialogListener(paramPromise);
    ((DatePickerDialogFragment)localObject2).setOnDismissListener(paramReadableMap);
    ((DatePickerDialogFragment)localObject2).setOnDateSetListener(paramReadableMap);
    ((DialogFragment)localObject2).show((FragmentManager)localObject1, "DatePickerAndroid");
  }
  
  private class DatePickerDialogListener
    implements DatePickerDialog.OnDateSetListener, DialogInterface.OnDismissListener
  {
    private final Promise mPromise;
    private boolean mPromiseResolved = false;
    
    public DatePickerDialogListener(Promise paramPromise)
    {
      mPromise = paramPromise;
    }
    
    public void onDateSet(DatePicker paramDatePicker, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((!mPromiseResolved) && (getReactApplicationContext().hasActiveCatalystInstance()))
      {
        paramDatePicker = new WritableNativeMap();
        paramDatePicker.putString("action", "dateSetAction");
        paramDatePicker.putInt("year", paramInt1);
        paramDatePicker.putInt("month", paramInt2);
        paramDatePicker.putInt("day", paramInt3);
        mPromise.resolve(paramDatePicker);
        mPromiseResolved = true;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if ((!mPromiseResolved) && (getReactApplicationContext().hasActiveCatalystInstance()))
      {
        paramDialogInterface = new WritableNativeMap();
        paramDialogInterface.putString("action", "dismissedAction");
        mPromise.resolve(paramDialogInterface);
        mPromiseResolved = true;
      }
    }
  }
}