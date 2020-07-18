package com.facebook.react.modules.vibration;

import android.annotation.SuppressLint;
import android.os.Vibrator;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="Vibration")
@SuppressLint({"MissingPermission"})
public class VibrationModule
  extends ReactContextBaseJavaModule
{
  public static final String NAME = "Vibration";
  
  public VibrationModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  public void cancel()
  {
    Vibrator localVibrator = (Vibrator)getReactApplicationContext().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.cancel();
    }
  }
  
  public String getName()
  {
    return "Vibration";
  }
  
  public void vibrate(int paramInt)
  {
    Vibrator localVibrator = (Vibrator)getReactApplicationContext().getSystemService("vibrator");
    if (localVibrator != null) {
      localVibrator.vibrate(paramInt);
    }
  }
  
  public void vibrateByPattern(ReadableArray paramReadableArray, int paramInt)
  {
    Vibrator localVibrator = (Vibrator)getReactApplicationContext().getSystemService("vibrator");
    if (localVibrator != null)
    {
      long[] arrayOfLong = new long[paramReadableArray.size()];
      int i = 0;
      while (i < paramReadableArray.size())
      {
        arrayOfLong[i] = paramReadableArray.getInt(i);
        i += 1;
      }
      localVibrator.vibrate(arrayOfLong, paramInt);
    }
  }
}
