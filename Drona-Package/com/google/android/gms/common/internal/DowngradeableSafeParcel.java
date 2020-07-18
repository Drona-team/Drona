package com.google.android.gms.common.internal;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

@KeepForSdk
public abstract class DowngradeableSafeParcel
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  private static final Object zzdc = new Object();
  private static ClassLoader zzdd = null;
  private static Integer zzde = null;
  private boolean zzdf = false;
  
  public DowngradeableSafeParcel() {}
  
  protected static boolean canUnparcelSafely(String paramString)
  {
    combine();
    return true;
  }
  
  private static ClassLoader combine()
  {
    Object localObject = zzdc;
    try
    {
      return null;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected static Integer getUnparcelClientVersion()
  {
    Object localObject = zzdc;
    try
    {
      return null;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected abstract boolean prepareForClientVersion(int paramInt);
  
  public void setShouldDowngrade(boolean paramBoolean)
  {
    zzdf = paramBoolean;
  }
  
  protected boolean shouldDowngrade()
  {
    return zzdf;
  }
}
