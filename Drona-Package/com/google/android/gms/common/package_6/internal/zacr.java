package com.google.android.gms.common.package_6.internal;

import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import com.google.android.gms.common.api.zac;
import com.google.android.gms.common.package_6.BitmapCache;
import com.google.android.gms.common.package_6.PendingResult;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

final class zacr
  implements IBinder.DeathRecipient, zacs
{
  private final WeakReference<com.google.android.gms.common.api.internal.BasePendingResult<?>> zalc;
  private final WeakReference<zac> zald;
  private final WeakReference<IBinder> zale;
  
  private zacr(BasePendingResult paramBasePendingResult, BitmapCache paramBitmapCache, IBinder paramIBinder)
  {
    zald = new WeakReference(paramBitmapCache);
    zalc = new WeakReference(paramBasePendingResult);
    zale = new WeakReference(paramIBinder);
  }
  
  private final void zaby()
  {
    Object localObject = (BasePendingResult)zalc.get();
    BitmapCache localBitmapCache = (BitmapCache)zald.get();
    if ((localBitmapCache != null) && (localObject != null)) {
      localBitmapCache.remove(((PendingResult)localObject).getValue().intValue());
    }
    localObject = (IBinder)zale.get();
    if (localObject != null) {}
    try
    {
      ((IBinder)localObject).unlinkToDeath(this, 0);
      return;
    }
    catch (NoSuchElementException localNoSuchElementException) {}
  }
  
  public final void andNot(BasePendingResult paramBasePendingResult)
  {
    zaby();
  }
  
  public final void binderDied()
  {
    zaby();
  }
}
