package com.google.android.gms.common.package_6.internal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import androidx.collection.ArrayMap;
import androidx.fragment.package_5.FragmentManager;
import androidx.fragment.package_5.FragmentTransaction;
import com.google.android.gms.common.api.internal.zzc;
import com.google.android.gms.internal.common.zze;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;

public final class Fragment
  extends androidx.fragment.package_5.Fragment
  implements LifecycleFragment
{
  private static WeakHashMap<androidx.fragment.app.FragmentActivity, WeakReference<zzc>> zzbe = new WeakHashMap();
  private Map<String, com.google.android.gms.common.api.internal.LifecycleCallback> zzbf = new ArrayMap();
  private int zzbg = 0;
  private Bundle zzbh;
  
  public Fragment() {}
  
  public static Fragment getFragment(androidx.fragment.package_5.FragmentActivity paramFragmentActivity)
  {
    Object localObject = (WeakReference)zzbe.get(paramFragmentActivity);
    if (localObject != null)
    {
      localObject = (Fragment)((WeakReference)localObject).get();
      if (localObject != null) {
        return localObject;
      }
    }
    try
    {
      Fragment localFragment = (Fragment)paramFragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
      if (localFragment != null)
      {
        localObject = localFragment;
        if (!localFragment.isRemoving()) {}
      }
      else
      {
        localObject = new Fragment();
        paramFragmentActivity.getSupportFragmentManager().beginTransaction().add((androidx.fragment.package_5.Fragment)localObject, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
      }
      zzbe.put(paramFragmentActivity, new WeakReference(localObject));
      return localObject;
    }
    catch (ClassCastException paramFragmentActivity)
    {
      throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", paramFragmentActivity);
    }
  }
  
  public final void addCallback(String paramString, LifecycleCallback paramLifecycleCallback)
  {
    if (!zzbf.containsKey(paramString))
    {
      zzbf.put(paramString, paramLifecycleCallback);
      if (zzbg > 0) {
        new zze(Looper.getMainLooper()).post(new IonBitmapRequestBuilder.1(this, paramLifecycleCallback, paramString));
      }
    }
    else
    {
      paramLifecycleCallback = new StringBuilder(String.valueOf(paramString).length() + 59);
      paramLifecycleCallback.append("LifecycleCallback with tag ");
      paramLifecycleCallback.append(paramString);
      paramLifecycleCallback.append(" already added to this fragment.");
      throw new IllegalArgumentException(paramLifecycleCallback.toString());
    }
  }
  
  public final void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    super.dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).dump(paramString, paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  public final LifecycleCallback getCallbackOrNull(String paramString, Class paramClass)
  {
    return (LifecycleCallback)paramClass.cast(zzbf.get(paramString));
  }
  
  public final boolean isCreated()
  {
    return zzbg > 0;
  }
  
  public final boolean isStarted()
  {
    return zzbg >= 2;
  }
  
  public final void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    zzbg = 1;
    zzbh = paramBundle;
    Iterator localIterator = zzbf.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (Map.Entry)localIterator.next();
      LifecycleCallback localLifecycleCallback = (LifecycleCallback)((Map.Entry)localObject).getValue();
      if (paramBundle != null) {
        localObject = paramBundle.getBundle((String)((Map.Entry)localObject).getKey());
      } else {
        localObject = null;
      }
      localLifecycleCallback.onCreate((Bundle)localObject);
    }
  }
  
  public final void onDestroy()
  {
    super.onDestroy();
    zzbg = 5;
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onDestroy();
    }
  }
  
  public final void onResume()
  {
    super.onResume();
    zzbg = 3;
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onResume();
    }
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (paramBundle == null) {
      return;
    }
    Iterator localIterator = zzbf.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      Bundle localBundle = new Bundle();
      ((LifecycleCallback)localEntry.getValue()).onSaveInstanceState(localBundle);
      paramBundle.putBundle((String)localEntry.getKey(), localBundle);
    }
  }
  
  public final void onStart()
  {
    super.onStart();
    zzbg = 2;
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStart();
    }
  }
  
  public final void onStop()
  {
    super.onStop();
    zzbg = 4;
    Iterator localIterator = zzbf.values().iterator();
    while (localIterator.hasNext()) {
      ((LifecycleCallback)localIterator.next()).onStop();
    }
  }
}
