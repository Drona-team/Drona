package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import androidx.collection.ArraySet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.zai;
import com.google.android.gms.common.internal.Preconditions;

public class zaae
  extends AbstractGalleryActivity
{
  private GoogleApiManager zabm;
  private final ArraySet<zai<?>> zafp = new ArraySet();
  
  private zaae(LifecycleFragment paramLifecycleFragment)
  {
    super(paramLifecycleFragment);
    mLifecycleFragment.addCallback("ConnectionlessLifecycleHelper", this);
  }
  
  public static void doIt(Activity paramActivity, GoogleApiManager paramGoogleApiManager, Msg paramMsg)
  {
    LifecycleFragment localLifecycleFragment = LifecycleCallback.getFragment(paramActivity);
    zaae localZaae = (zaae)localLifecycleFragment.getCallbackOrNull("ConnectionlessLifecycleHelper", com.google.android.gms.common.api.internal.zaae.class);
    paramActivity = localZaae;
    if (localZaae == null) {
      paramActivity = new zaae(localLifecycleFragment);
    }
    zabm = paramGoogleApiManager;
    Preconditions.checkNotNull(paramMsg, "ApiKey cannot be null");
    zafp.add(paramMsg);
    paramGoogleApiManager.read(paramActivity);
  }
  
  private final void zaak()
  {
    if (!zafp.isEmpty()) {
      zabm.read(this);
    }
  }
  
  protected final void add(ConnectionResult paramConnectionResult, int paramInt)
  {
    zabm.close(paramConnectionResult, paramInt);
  }
  
  protected final void getPeers()
  {
    zabm.close();
  }
  
  public void onResume()
  {
    super.onResume();
    zaak();
  }
  
  public void onStart()
  {
    super.onStart();
    zaak();
  }
  
  public void onStop()
  {
    super.onStop();
    zabm.setDisplayMode(this);
  }
  
  final ArraySet zaaj()
  {
    return zafp;
  }
}
