package com.google.android.gms.package_7;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.util.concurrent.NamedThreadFactory;
import com.google.android.gms.internal.gcm.zzf;
import com.google.android.gms.internal.gcm.zzg;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import java.util.concurrent.ScheduledExecutorService;
import javax.annotation.concurrent.GuardedBy;

public final class DownloadFile
{
  private final Context context;
  private final ScheduledExecutorService zzce;
  @GuardedBy("this")
  private HttpHeaders zzcf = new HttpHeaders(this, null);
  @GuardedBy("this")
  private int zzcg = 1;
  
  public DownloadFile(Context paramContext)
  {
    this(paramContext, zzg.zzaa().zze(1, new NamedThreadFactory("MessengerIpcClient"), 9));
  }
  
  private DownloadFile(Context paramContext, ScheduledExecutorService paramScheduledExecutorService)
  {
    context = paramContext.getApplicationContext();
    zzce = paramScheduledExecutorService;
  }
  
  private final int download()
  {
    try
    {
      int i = zzcg;
      zzcg = (i + 1);
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private final Task download(Dictionary paramDictionary)
  {
    try
    {
      if (Log.isLoggable("MessengerIpcClient", 3))
      {
        String str = String.valueOf(paramDictionary);
        StringBuilder localStringBuilder = new StringBuilder(String.valueOf(str).length() + 9);
        localStringBuilder.append("Queueing ");
        localStringBuilder.append(str);
        Log.d("MessengerIpcClient", localStringBuilder.toString());
      }
      if (!zzcf.execute(paramDictionary))
      {
        zzcf = new HttpHeaders(this, null);
        zzcf.execute(paramDictionary);
      }
      paramDictionary = zzcq.getTask();
      return paramDictionary;
    }
    catch (Throwable paramDictionary)
    {
      throw paramDictionary;
    }
  }
  
  public final Task download(int paramInt, Bundle paramBundle)
  {
    return download(new zzab(download(), 1, paramBundle));
  }
}
