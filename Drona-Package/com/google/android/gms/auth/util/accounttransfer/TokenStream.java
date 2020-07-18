package com.google.android.gms.auth.util.accounttransfer;

import android.os.RemoteException;
import com.google.android.gms.auth.api.accounttransfer.DeviceMetaData;
import com.google.android.gms.internal.auth.zzv;
import com.google.android.gms.internal.auth.zzx;
import com.google.android.gms.internal.auth.zzz;

final class TokenStream
  extends com.google.android.gms.auth.api.accounttransfer.AccountTransferClient.zzb<DeviceMetaData>
{
  TokenStream(AccountTransferClient paramAccountTransferClient, zzv paramZzv)
  {
    super(null);
  }
  
  protected final void postCommand(zzz paramZzz)
    throws RemoteException
  {
    StatusLine localStatusLine = new StatusLine(this, this);
    zzv localZzv = zzar;
    paramZzz.zza((zzx)localStatusLine, localZzv);
  }
}
