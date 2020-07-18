package com.google.android.gms.common.package_6.internal;

import android.util.Log;
import android.util.SparseArray;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.zaj;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.GoogleApiClient;
import com.google.android.gms.common.package_6.GoogleApiClient.OnConnectionFailedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

public class Elements
  extends AbstractGalleryActivity
{
  private final SparseArray<com.google.android.gms.common.api.internal.zaj.zaa> zacw = new SparseArray();
  
  private Elements(LifecycleFragment paramLifecycleFragment)
  {
    super(paramLifecycleFragment);
    mLifecycleFragment.addCallback("AutoManageHelper", this);
  }
  
  private final zaj.zaa get(int paramInt)
  {
    if (zacw.size() <= paramInt) {
      return null;
    }
    return (zaj.zaa)zacw.get(zacw.keyAt(paramInt));
  }
  
  public static Elements select(LifecycleActivity paramLifecycleActivity)
  {
    paramLifecycleActivity = LifecycleCallback.getFragment(paramLifecycleActivity);
    Elements localElements = (Elements)paramLifecycleActivity.getCallbackOrNull("AutoManageHelper", zaj.class);
    if (localElements != null) {
      return localElements;
    }
    return new Elements(paramLifecycleActivity);
  }
  
  protected final void add(ConnectionResult paramConnectionResult, int paramInt)
  {
    Log.w("AutoManageHelper", "Unresolved error while connecting client. Stopping auto-manage.");
    if (paramInt < 0)
    {
      Log.wtf("AutoManageHelper", "AutoManageLifecycleHelper received onErrorResolutionFailed callback but no failing client ID is set", new Exception());
      return;
    }
    Object localObject = (zaj.zaa)zacw.get(paramInt);
    if (localObject != null)
    {
      remove(paramInt);
      localObject = zacz;
      if (localObject != null) {
        ((GoogleApiClient.OnConnectionFailedListener)localObject).onConnectionFailed(paramConnectionResult);
      }
    }
  }
  
  public void dump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    int i = 0;
    while (i < zacw.size())
    {
      zaj.zaa localZaa = get(i);
      if (localZaa != null)
      {
        paramPrintWriter.append(paramString).append("GoogleApiClient #").print(zacx);
        paramPrintWriter.println(":");
        zacy.dump(String.valueOf(paramString).concat("  "), paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      }
      i += 1;
    }
  }
  
  public final void get(int paramInt, GoogleApiClient paramGoogleApiClient, GoogleApiClient.OnConnectionFailedListener paramOnConnectionFailedListener)
  {
    Preconditions.checkNotNull(paramGoogleApiClient, "GoogleApiClient instance cannot be null");
    if (zacw.indexOfKey(paramInt) < 0) {
      bool = true;
    } else {
      bool = false;
    }
    Object localObject = new StringBuilder(54);
    ((StringBuilder)localObject).append("Already managing a GoogleApiClient with id ");
    ((StringBuilder)localObject).append(paramInt);
    Preconditions.checkState(bool, ((StringBuilder)localObject).toString());
    localObject = (Tag)zadf.get();
    boolean bool = mStarted;
    String str = String.valueOf(localObject);
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 49);
    localStringBuilder.append("starting AutoManage for client ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" ");
    localStringBuilder.append(bool);
    localStringBuilder.append(" ");
    localStringBuilder.append(str);
    Log.d("AutoManageHelper", localStringBuilder.toString());
    paramOnConnectionFailedListener = new zaj.zaa(this, paramInt, paramGoogleApiClient, paramOnConnectionFailedListener);
    zacw.put(paramInt, paramOnConnectionFailedListener);
    if ((mStarted) && (localObject == null))
    {
      paramOnConnectionFailedListener = String.valueOf(paramGoogleApiClient);
      localObject = new StringBuilder(String.valueOf(paramOnConnectionFailedListener).length() + 11);
      ((StringBuilder)localObject).append("connecting ");
      ((StringBuilder)localObject).append(paramOnConnectionFailedListener);
      Log.d("AutoManageHelper", ((StringBuilder)localObject).toString());
      paramGoogleApiClient.connect();
    }
  }
  
  protected final void getPeers()
  {
    int i = 0;
    while (i < zacw.size())
    {
      zaj.zaa localZaa = get(i);
      if (localZaa != null) {
        zacy.connect();
      }
      i += 1;
    }
  }
  
  public void onStart()
  {
    super.onStart();
    boolean bool = mStarted;
    Object localObject = String.valueOf(zacw);
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(localObject).length() + 14);
    localStringBuilder.append("onStart ");
    localStringBuilder.append(bool);
    localStringBuilder.append(" ");
    localStringBuilder.append((String)localObject);
    Log.d("AutoManageHelper", localStringBuilder.toString());
    if (zadf.get() == null)
    {
      int i = 0;
      while (i < zacw.size())
      {
        localObject = get(i);
        if (localObject != null) {
          zacy.connect();
        }
        i += 1;
      }
    }
  }
  
  public void onStop()
  {
    super.onStop();
    int i = 0;
    while (i < zacw.size())
    {
      zaj.zaa localZaa = get(i);
      if (localZaa != null) {
        zacy.disconnect();
      }
      i += 1;
    }
  }
  
  public final void remove(int paramInt)
  {
    zaj.zaa localZaa = (zaj.zaa)zacw.get(paramInt);
    zacw.remove(paramInt);
    if (localZaa != null)
    {
      zacy.unregisterConnectionFailedListener(localZaa);
      zacy.disconnect();
    }
  }
}
