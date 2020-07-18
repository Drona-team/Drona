package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.internal.ApiExceptionUtil;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.CancellationException;

public class zabu
  extends AbstractGalleryActivity
{
  private TaskCompletionSource<Void> zajp = new TaskCompletionSource();
  
  private zabu(LifecycleFragment paramLifecycleFragment)
  {
    super(paramLifecycleFragment);
    mLifecycleFragment.addCallback("GmsAvailabilityHelper", this);
  }
  
  public static zabu findAll(Activity paramActivity)
  {
    paramActivity = LifecycleCallback.getFragment(paramActivity);
    zabu localZabu = (zabu)paramActivity.getCallbackOrNull("GmsAvailabilityHelper", com.google.android.gms.common.api.internal.zabu.class);
    if (localZabu != null)
    {
      paramActivity = localZabu;
      if (zajp.getTask().isComplete())
      {
        zajp = new TaskCompletionSource();
        return localZabu;
      }
    }
    else
    {
      paramActivity = new zabu(paramActivity);
    }
    return paramActivity;
  }
  
  protected final void add(ConnectionResult paramConnectionResult, int paramInt)
  {
    zajp.setException(ApiExceptionUtil.fromStatus(new Status(paramConnectionResult.getErrorCode(), paramConnectionResult.getErrorMessage(), paramConnectionResult.getResolution())));
  }
  
  protected final void getPeers()
  {
    int i = zacd.isGooglePlayServicesAvailable(mLifecycleFragment.getLifecycleActivity());
    if (i == 0)
    {
      zajp.setResult(null);
      return;
    }
    if (!zajp.getTask().isComplete()) {
      next(new ConnectionResult(i, null), 0);
    }
  }
  
  public final Task getTask()
  {
    return zajp.getTask();
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    zajp.trySetException(new CancellationException("Host activity was destroyed before Google Play services could be made available."));
  }
}
