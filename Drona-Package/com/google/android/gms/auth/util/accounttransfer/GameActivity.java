package com.google.android.gms.auth.util.accounttransfer;

import android.os.RemoteException;
import com.google.android.gms.internal.auth.zzad;
import com.google.android.gms.internal.auth.zzx;
import com.google.android.gms.internal.auth.zzz;

final class GameActivity
  extends com.google.android.gms.auth.api.accounttransfer.AccountTransferClient.zzb<byte[]>
{
  GameActivity(AccountTransferClient paramAccountTransferClient, zzad paramZzad)
  {
    super(null);
  }
  
  protected final void postCommand(zzz paramZzz)
    throws RemoteException
  {
    MD5 localMD5 = new MD5(this, this);
    zzad localZzad = zzap;
    paramZzz.zza((zzx)localMD5, localZzad);
  }
}
