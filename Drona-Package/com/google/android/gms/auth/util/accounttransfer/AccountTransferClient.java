package com.google.android.gms.auth.util.accounttransfer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.os.RemoteException;
import com.google.android.gms.auth.api.accounttransfer.zzn;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.package_6.GoogleApi.Settings.Builder;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.common.package_6.Status;
import com.google.android.gms.common.package_6.internal.ApiExceptionMapper;
import com.google.android.gms.internal.auth.zzab;
import com.google.android.gms.internal.auth.zzad;
import com.google.android.gms.internal.auth.zzaf;
import com.google.android.gms.internal.auth.zzah;
import com.google.android.gms.internal.auth.zzs;
import com.google.android.gms.internal.auth.zzu;
import com.google.android.gms.internal.auth.zzv;
import com.google.android.gms.internal.auth.zzy;
import com.google.android.gms.internal.auth.zzz;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

public class AccountTransferClient
  extends com.google.android.gms.common.api.GoogleApi<zzn>
{
  private static final com.google.android.gms.common.api.Api.ClientKey<zzu> zzaj = new com.google.android.gms.common.package_6.Api.ClientKey();
  private static final Api.AbstractClientBuilder<zzu, zzn> zzak = new ASN1OctetString();
  private static final Api<zzn> zzal = new Sample("AccountTransfer.ACCOUNT_TRANSFER_API", zzak, zzaj);
  
  AccountTransferClient(Activity paramActivity)
  {
    super(paramActivity, zzal, null, new GoogleApi.Settings.Builder().setMapper(new ApiExceptionMapper()).build());
  }
  
  AccountTransferClient(Context paramContext)
  {
    super(paramContext, zzal, null, new GoogleApi.Settings.Builder().setMapper(new ApiExceptionMapper()).build());
  }
  
  private static void rebase(TaskCompletionSource paramTaskCompletionSource, Status paramStatus)
  {
    paramTaskCompletionSource.setException(new AccountTransferException(paramStatus));
  }
  
  public Task getDeviceMetaData(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    return doRead(new TokenStream(this, new zzv(paramString)));
  }
  
  public Task notifyCompletion(String paramString, int paramInt)
  {
    Preconditions.checkNotNull(paramString);
    return doWrite(new JSONParser(this, new zzab(paramString, paramInt)));
  }
  
  public Task retrieveData(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    return doRead(new GameActivity(this, new zzad(paramString)));
  }
  
  public Task sendData(String paramString, byte[] paramArrayOfByte)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramArrayOfByte);
    return doWrite(new Bookmark(this, new zzaf(paramString, paramArrayOfByte)));
  }
  
  public Task showUserChallenge(String paramString, PendingIntent paramPendingIntent)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(paramPendingIntent);
    return doWrite(new Threads(this, new zzah(paramString, paramPendingIntent)));
  }
  
  class zza<T>
    extends zzs
  {
    public zza() {}
    
    public final void onFailure(Status paramStatus)
    {
      setResult(paramStatus);
    }
  }
  
  abstract class zzb<T>
    extends TaskApiCall<zzu, T>
  {
    private TaskCompletionSource<T> zzaw;
    
    private zzb() {}
    
    protected abstract void postCommand(zzz paramZzz)
      throws RemoteException;
    
    protected final void setResult(Status paramStatus)
    {
      AccountTransferClient.offer(zzaw, paramStatus);
    }
    
    protected final void setResult(Object paramObject)
    {
      zzaw.setResult(paramObject);
    }
  }
  
  abstract class zzc
    extends com.google.android.gms.auth.api.accounttransfer.AccountTransferClient.zzb<Void>
  {
    zzy zzax = (zzy)new LoginActivity.3(this);
    
    private zzc()
    {
      super();
    }
  }
}
