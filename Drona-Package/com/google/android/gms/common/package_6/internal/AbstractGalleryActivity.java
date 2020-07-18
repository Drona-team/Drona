package com.google.android.gms.common.package_6.internal;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.BaseBundle;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.internal.zam;
import com.google.android.gms.internal.base.zap;
import java.util.concurrent.atomic.AtomicReference;

public abstract class AbstractGalleryActivity
  extends LifecycleCallback
  implements DialogInterface.OnCancelListener
{
  protected volatile boolean mStarted;
  protected final GoogleApiAvailability zacd;
  protected final AtomicReference<zam> zadf = new AtomicReference(null);
  private final Handler zadg = (Handler)new zap(Looper.getMainLooper());
  
  protected AbstractGalleryActivity(LifecycleFragment paramLifecycleFragment)
  {
    this(paramLifecycleFragment, GoogleApiAvailability.getInstance());
  }
  
  private AbstractGalleryActivity(LifecycleFragment paramLifecycleFragment, GoogleApiAvailability paramGoogleApiAvailability)
  {
    super(paramLifecycleFragment);
    zacd = paramGoogleApiAvailability;
  }
  
  private static int get(Tag paramTag)
  {
    if (paramTag == null) {
      return -1;
    }
    return paramTag.getId();
  }
  
  protected abstract void add(ConnectionResult paramConnectionResult, int paramInt);
  
  protected abstract void getPeers();
  
  public final void next(ConnectionResult paramConnectionResult, int paramInt)
  {
    paramConnectionResult = new Tag(paramConnectionResult, paramInt);
    if (zadf.compareAndSet(null, paramConnectionResult)) {
      zadg.post(new SyncCampaign(this, paramConnectionResult));
    }
  }
  
  protected final void onActivityResult()
  {
    zadf.set(null);
    getPeers();
  }
  
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Tag localTag2 = (Tag)zadf.get();
    int j = 1;
    int i = 1;
    Tag localTag1;
    switch (paramInt1)
    {
    default: 
      localTag1 = localTag2;
      break;
    case 2: 
      j = zacd.isGooglePlayServicesAvailable(getActivity());
      if (j == 0) {
        paramInt1 = i;
      } else {
        paramInt1 = 0;
      }
      if (localTag2 == null) {
        return;
      }
      localTag1 = localTag2;
      paramInt2 = paramInt1;
      if (localTag2.getConnectionResult().getErrorCode() != 18) {
        break label192;
      }
      localTag1 = localTag2;
      paramInt2 = paramInt1;
      if (j != 18) {
        break label192;
      }
      return;
    case 1: 
      if (paramInt2 == -1)
      {
        localTag1 = localTag2;
        paramInt2 = j;
        break label192;
      }
      localTag1 = localTag2;
      if (paramInt2 == 0)
      {
        paramInt1 = 13;
        if (paramIntent != null) {
          paramInt1 = paramIntent.getIntExtra("<<ResolutionFailureErrorDetail>>", 13);
        }
        localTag1 = new Tag(new ConnectionResult(paramInt1, null), get(localTag2));
        zadf.set(localTag1);
      }
      break;
    }
    paramInt2 = 0;
    label192:
    if (paramInt2 != 0)
    {
      onActivityResult();
      return;
    }
    if (localTag1 != null) {
      add(localTag1.getConnectionResult(), localTag1.getId());
    }
  }
  
  public void onCancel(DialogInterface paramDialogInterface)
  {
    add(new ConnectionResult(13, null), get((Tag)zadf.get()));
    onActivityResult();
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      AtomicReference localAtomicReference = zadf;
      if (paramBundle.getBoolean("resolving_error", false)) {
        paramBundle = new Tag(new ConnectionResult(paramBundle.getInt("failed_status"), (PendingIntent)paramBundle.getParcelable("failed_resolution")), paramBundle.getInt("failed_client_id", -1));
      } else {
        paramBundle = null;
      }
      localAtomicReference.set(paramBundle);
    }
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    Tag localTag = (Tag)zadf.get();
    if (localTag != null)
    {
      paramBundle.putBoolean("resolving_error", true);
      paramBundle.putInt("failed_client_id", localTag.getId());
      paramBundle.putInt("failed_status", localTag.getConnectionResult().getErrorCode());
      paramBundle.putParcelable("failed_resolution", localTag.getConnectionResult().getResolution());
    }
  }
  
  public void onStart()
  {
    super.onStart();
    mStarted = true;
  }
  
  public void onStop()
  {
    super.onStop();
    mStarted = false;
  }
}
