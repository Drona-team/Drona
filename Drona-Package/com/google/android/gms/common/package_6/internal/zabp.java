package com.google.android.gms.common.package_6.internal;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.api.Api.ApiOptions;

public final class zabp<O extends Api.ApiOptions>
  extends com.google.android.gms.common.api.internal.zaag
{
  private final com.google.android.gms.common.api.GoogleApi<O> zajh;
  
  public zabp(com.google.android.gms.common.package_6.GoogleApi paramGoogleApi)
  {
    super("Method is not supported by connectionless client. APIs supporting connectionless client must not call this method.");
    zajh = paramGoogleApi;
  }
  
  public final BaseImplementation.ApiMethodImpl enqueue(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    return zajh.doRead(paramApiMethodImpl);
  }
  
  public final void ensureInitialized(zacm paramZacm) {}
  
  public final BaseImplementation.ApiMethodImpl execute(BaseImplementation.ApiMethodImpl paramApiMethodImpl)
  {
    return zajh.doWrite(paramApiMethodImpl);
  }
  
  public final Context getContext()
  {
    return zajh.getApplicationContext();
  }
  
  public final Looper getLooper()
  {
    return zajh.getLooper();
  }
  
  public final void removeAccount(zacm paramZacm) {}
}
