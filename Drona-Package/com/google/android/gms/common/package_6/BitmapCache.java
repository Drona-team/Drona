package com.google.android.gms.common.package_6;

import com.google.android.gms.common.api.zac;
import com.google.android.gms.common.internal.ShowFirstParty;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.concurrent.GuardedBy;

@ShowFirstParty
public abstract class BitmapCache
{
  private static final Object sLock = new Object();
  @GuardedBy("sLock")
  private static final Map<Object, zac> zack = new WeakHashMap();
  
  public BitmapCache() {}
  
  public abstract void remove(int paramInt);
}
