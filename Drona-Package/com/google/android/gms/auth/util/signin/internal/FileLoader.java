package com.google.android.gms.auth.util.signin.internal;

import android.content.Context;
import android.util.Log;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import com.google.android.gms.common.api.internal.SignInConnectionListener;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public final class FileLoader
  extends AsyncTaskLoader<Void>
  implements SignInConnectionListener
{
  private Semaphore zzbg = new Semaphore(0);
  private Set<com.google.android.gms.common.api.GoogleApiClient> zzbh;
  
  public FileLoader(Context paramContext, Set paramSet)
  {
    super(paramContext);
    zzbh = paramSet;
  }
  
  private final Void get()
  {
    Object localObject = zzbh.iterator();
    int i = 0;
    while (((Iterator)localObject).hasNext()) {
      if (((com.google.android.gms.common.package_6.GoogleApiClient)((Iterator)localObject).next()).maybeSignIn(this)) {
        i += 1;
      }
    }
    localObject = zzbg;
    TimeUnit localTimeUnit = TimeUnit.SECONDS;
    try
    {
      ((Semaphore)localObject).tryAcquire(i, 5L, localTimeUnit);
    }
    catch (InterruptedException localInterruptedException)
    {
      Log.i("GACSignInLoader", "Unexpected InterruptedException", localInterruptedException);
      Thread.currentThread().interrupt();
    }
    return null;
  }
  
  public final void onComplete()
  {
    zzbg.release();
  }
  
  protected final void onStartLoading()
  {
    zzbg.drainPermits();
    forceLoad();
  }
}
