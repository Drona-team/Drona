package com.google.android.gms.common.package_6.internal;

import android.os.Looper;
import android.os.Message;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.base.zap;

@KeepForSdk
public final class ListenerHolder<L>
{
  private final com.google.android.gms.common.api.internal.ListenerHolder.zaa zajj;
  private volatile L zajk;
  private final com.google.android.gms.common.api.internal.ListenerHolder.ListenerKey<L> zajl;
  
  ListenerHolder(Looper paramLooper, Object paramObject, String paramString)
  {
    zajj = new zaa(paramLooper);
    zajk = Preconditions.checkNotNull(paramObject, "Listener must not be null");
    zajl = new ListenerKey(paramObject, Preconditions.checkNotEmpty(paramString));
  }
  
  public final void clear()
  {
    zajk = null;
  }
  
  public final ListenerKey getListenerKey()
  {
    return zajl;
  }
  
  public final boolean hasListener()
  {
    return zajk != null;
  }
  
  public final void notifyListener(Notifier paramNotifier)
  {
    Preconditions.checkNotNull(paramNotifier, "Notifier must not be null");
    paramNotifier = zajj.obtainMessage(1, paramNotifier);
    zajj.sendMessage(paramNotifier);
  }
  
  final void notifyListenerInternal(Notifier paramNotifier)
  {
    Object localObject = zajk;
    if (localObject == null)
    {
      paramNotifier.onNotifyListenerFailed();
      return;
    }
    try
    {
      paramNotifier.notifyListener(localObject);
      return;
    }
    catch (RuntimeException localRuntimeException)
    {
      paramNotifier.onNotifyListenerFailed();
      throw localRuntimeException;
    }
  }
  
  @KeepForSdk
  public final class ListenerKey<L>
  {
    private final String zajn;
    
    ListenerKey(String paramString)
    {
      zajn = paramString;
    }
    
    public final boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (!(paramObject instanceof ListenerKey)) {
        return false;
      }
      paramObject = (ListenerKey)paramObject;
      return (ListenerHolder.this == zajk) && (zajn.equals(zajn));
    }
    
    public final int hashCode()
    {
      return System.identityHashCode(ListenerHolder.this) * 31 + zajn.hashCode();
    }
  }
  
  @KeepForSdk
  public abstract interface Notifier<L>
  {
    public abstract void notifyListener(Object paramObject);
    
    public abstract void onNotifyListenerFailed();
  }
  
  final class zaa
    extends zap
  {
    public zaa(Looper paramLooper)
    {
      super();
    }
    
    public final void handleMessage(Message paramMessage)
    {
      int i = what;
      boolean bool = true;
      if (i != 1) {
        bool = false;
      }
      Preconditions.checkArgument(bool);
      notifyListenerInternal((ListenerHolder.Notifier)obj);
    }
  }
}
