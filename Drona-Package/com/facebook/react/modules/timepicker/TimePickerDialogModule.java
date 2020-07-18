package com.facebook.react.modules.timepicker;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.BaseBundle;
import android.os.Bundle;
import android.widget.TimePicker;
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

@ReactModule(name="TimePickerAndroid")
public class TimePickerDialogModule
  extends ReactContextBaseJavaModule
{
  static final String ACTION_DISMISSED = "dismissedAction";
  static final String ACTION_TIME_SET = "timeSetAction";
  static final String ARG_HOUR = "hour";
  static final String ARG_IS24HOUR = "is24Hour";
  static final String ARG_MINUTE = "minute";
  static final String ARG_MODE = "mode";
  private static final String ERROR_NO_ACTIVITY = "E_NO_ACTIVITY";
  @VisibleForTesting
  public static final String FRAGMENT_TAG = "TimePickerAndroid";
  
  public TimePickerDialogModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private Bundle createFragmentArguments(ReadableMap paramReadableMap)
  {
    Bundle localBundle = new Bundle();
    if ((paramReadableMap.hasKey("hour")) && (!paramReadableMap.isNull("hour"))) {
      localBundle.putInt("hour", paramReadableMap.getInt("hour"));
    }
    if ((paramReadableMap.hasKey("minute")) && (!paramReadableMap.isNull("minute"))) {
      localBundle.putInt("minute", paramReadableMap.getInt("minute"));
    }
    if ((paramReadableMap.hasKey("is24Hour")) && (!paramReadableMap.isNull("is24Hour"))) {
      localBundle.putBoolean("is24Hour", paramReadableMap.getBoolean("is24Hour"));
    }
    if ((paramReadableMap.hasKey("mode")) && (!paramReadableMap.isNull("mode"))) {
      localBundle.putString("mode", paramReadableMap.getString("mode"));
    }
    return localBundle;
  }
  
  public String getName()
  {
    return "TimePickerAndroid";
  }
  
  public void open(ReadableMap paramReadableMap, Promise paramPromise)
  {
    Object localObject1 = (FragmentActivity)getCurrentActivity();
    if (localObject1 == null)
    {
      paramPromise.reject("E_NO_ACTIVITY", "Tried to open a TimePicker dialog while not attached to an Activity");
      return;
    }
    localObject1 = ((FragmentActivity)localObject1).getSupportFragmentManager();
    Object localObject2 = (DialogFragment)((FragmentManager)localObject1).findFragmentByTag("TimePickerAndroid");
    if (localObject2 != null) {
      ((DialogFragment)localObject2).dismiss();
    }
    localObject2 = new TimePickerDialogFragment();
    if (paramReadableMap != null) {
      ((Fragment)localObject2).setArguments(createFragmentArguments(paramReadableMap));
    }
    paramReadableMap = new TimePickerDialogListener(paramPromise);
    ((TimePickerDialogFragment)localObject2).setOnDismissListener(paramReadableMap);
    ((TimePickerDialogFragment)localObject2).setOnTimeSetListener(paramReadableMap);
    ((DialogFragment)localObject2).show((FragmentManager)localObject1, "TimePickerAndroid");
  }
  
  private class TimePickerDialogListener
    implements TimePickerDialog.OnTimeSetListener, DialogInterface.OnDismissListener
  {
    private final Promise mPromise;
    private boolean mPromiseResolved = false;
    
    public TimePickerDialogListener(Promise paramPromise)
    {
      mPromise = paramPromise;
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
    
    public void onTimeSet(TimePicker paramTimePicker, int paramInt1, int paramInt2)
    {
      if ((!mPromiseResolved) && (getReactApplicationContext().hasActiveCatalystInstance()))
      {
        paramTimePicker = new WritableNativeMap();
        paramTimePicker.putString("action", "timeSetAction");
        paramTimePicker.putInt("hour", paramInt1);
        paramTimePicker.putInt("minute", paramInt2);
        mPromise.resolve(paramTimePicker);
        mPromiseResolved = true;
      }
    }
  }
}
