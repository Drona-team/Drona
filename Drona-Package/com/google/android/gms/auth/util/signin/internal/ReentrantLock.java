package com.google.android.gms.auth.util.signin.internal;

import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.logging.Logger;
import com.google.android.gms.common.package_6.PendingResult;
import com.google.android.gms.common.package_6.PendingResults;
import com.google.android.gms.common.package_6.Result;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.internal.BasePendingResult;
import com.google.android.gms.common.package_6.internal.StatusPendingResult;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class ReentrantLock
  implements Runnable
{
  private static final Logger zzbd = new Logger("RevokeAccessOperation", new String[0]);
  private final String zzbe;
  private final StatusPendingResult zzbf;
  
  private ReentrantLock(String paramString)
  {
    Preconditions.checkNotEmpty(paramString);
    zzbe = paramString;
    zzbf = new StatusPendingResult(null);
  }
  
  public static PendingResult unlock(String paramString)
  {
    if (paramString == null) {
      return PendingResults.immediateFailedResult(new Status(4), null);
    }
    paramString = new ReentrantLock(paramString);
    new Thread(paramString).start();
    return zzbf;
  }
  
  public final void run()
  {
    Status localStatus = Status.RESULT_INTERNAL_ERROR;
    Object localObject3 = localStatus;
    Object localObject4 = localStatus;
    Object localObject2;
    try
    {
      Object localObject5 = String.valueOf("https://accounts.google.com/o/oauth2/revoke?token=");
      Object localObject6 = zzbe;
      localObject3 = localStatus;
      localObject4 = localStatus;
      localObject6 = String.valueOf(localObject6);
      localObject3 = localStatus;
      localObject4 = localStatus;
      int i = ((String)localObject6).length();
      if (i != 0)
      {
        localObject3 = localStatus;
        localObject4 = localStatus;
        localObject5 = ((String)localObject5).concat((String)localObject6);
      }
      else
      {
        localObject3 = localStatus;
        localObject4 = localStatus;
        localObject5 = new String((String)localObject5);
      }
      localObject3 = localStatus;
      localObject4 = localStatus;
      localObject5 = new URL((String)localObject5).openConnection();
      localObject5 = (HttpURLConnection)localObject5;
      localObject3 = localStatus;
      localObject4 = localStatus;
      ((HttpURLConnection)localObject5).setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      localObject3 = localStatus;
      localObject4 = localStatus;
      i = ((HttpURLConnection)localObject5).getResponseCode();
      if (i == 200)
      {
        localStatus = Status.RESULT_SUCCESS;
      }
      else
      {
        localObject5 = zzbd;
        localObject3 = localStatus;
        localObject4 = localStatus;
        ((Logger)localObject5).log("Unable to revoke access!", new Object[0]);
      }
      localObject5 = zzbd;
      localObject3 = localStatus;
      localObject4 = localStatus;
      localObject6 = new StringBuilder(26);
      localObject3 = localStatus;
      localObject4 = localStatus;
      ((StringBuilder)localObject6).append("Response Code: ");
      localObject3 = localStatus;
      localObject4 = localStatus;
      ((StringBuilder)localObject6).append(i);
      localObject3 = localStatus;
      localObject4 = localStatus;
      localObject6 = ((StringBuilder)localObject6).toString();
      localObject3 = localStatus;
      localObject4 = localStatus;
      ((Logger)localObject5).d((String)localObject6, new Object[0]);
    }
    catch (Exception localException)
    {
      localObject4 = zzbd;
      Object localObject1 = String.valueOf(localException.toString());
      if (((String)localObject1).length() != 0) {
        localObject1 = "Exception when revoking access: ".concat((String)localObject1);
      } else {
        localObject1 = new String("Exception when revoking access: ");
      }
      ((Logger)localObject4).log((String)localObject1, new Object[0]);
      localObject1 = localObject3;
    }
    catch (IOException localIOException)
    {
      localObject3 = zzbd;
      localObject2 = String.valueOf(localIOException.toString());
      if (((String)localObject2).length() != 0) {
        localObject2 = "IOException when revoking access: ".concat((String)localObject2);
      } else {
        localObject2 = new String("IOException when revoking access: ");
      }
      ((Logger)localObject3).log((String)localObject2, new Object[0]);
      localObject2 = localObject4;
    }
    zzbf.setResult((Result)localObject2);
  }
}
