package com.google.android.gms.auth.util.accounttransfer;

import android.os.RemoteException;
import com.google.android.gms.internal.auth.zzah;
import com.google.android.gms.internal.auth.zzz;

final class Threads
  extends AccountTransferClient.zzc
{
  Threads(AccountTransferClient paramAccountTransferClient, zzah paramZzah)
  {
    super(null);
  }
  
  protected final void postCommand(zzz paramZzz)
    throws RemoteException
  {
    paramZzz.zza(zzax, zzat);
  }
}
