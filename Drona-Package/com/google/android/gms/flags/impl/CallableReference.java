package com.google.android.gms.flags.impl;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.android.gms.internal.flags.zze;

public final class CallableReference
  extends zza<Long>
{
  public static Long call(SharedPreferences paramSharedPreferences, String paramString, Long paramLong)
  {
    try
    {
      paramSharedPreferences = zze.zza(new Downloader(paramSharedPreferences, paramString, paramLong));
      return (Long)paramSharedPreferences;
    }
    catch (Exception paramSharedPreferences)
    {
      paramSharedPreferences = String.valueOf(paramSharedPreferences.getMessage());
      if (paramSharedPreferences.length() != 0) {
        paramSharedPreferences = "Flag value not available, returning default: ".concat(paramSharedPreferences);
      } else {
        paramSharedPreferences = new String("Flag value not available, returning default: ");
      }
      Log.w("FlagDataUtils", paramSharedPreferences);
    }
    return paramLong;
  }
}
