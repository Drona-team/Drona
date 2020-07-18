package com.google.android.gms.common.util;

import android.content.Context;
import android.os.DropBoxManager;
import android.util.Log;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import javax.annotation.concurrent.GuardedBy;

@KeepForSdk
public final class CrashUtils
{
  private static final String[] zzgg = { "android.", "com.android.", "dalvik.", "java.", "javax." };
  private static DropBoxManager zzgh = null;
  private static boolean zzgi = false;
  private static int zzgj = -1;
  @GuardedBy("CrashUtils.class")
  private static int zzgk = 0;
  @GuardedBy("CrashUtils.class")
  private static int zzgl = 0;
  
  public CrashUtils() {}
  
  public static boolean addDynamiteErrorToDropBox(Context paramContext, Throwable paramThrowable)
  {
    return tryGCOrWait(paramContext, paramThrowable, 536870912);
  }
  
  private static boolean tryGCOrWait(Context paramContext, Throwable paramThrowable, int paramInt)
  {
    try
    {
      Preconditions.checkNotNull(paramContext);
      Preconditions.checkNotNull(paramThrowable);
      return false;
    }
    catch (Exception paramContext)
    {
      Log.e("CrashUtils", "Error adding exception to DropBox!", paramContext);
    }
    return false;
  }
}
