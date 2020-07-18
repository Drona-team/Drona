package com.google.android.gms.auth.util.accounttransfer;

import android.os.RemoteException;
import com.google.android.gms.internal.auth.zzaf;
import com.google.android.gms.internal.auth.zzz;

final class Bookmark
  extends AccountTransferClient.zzc
{
  Bookmark(AccountTransferClient paramAccountTransferClient, zzaf paramZzaf)
  {
    super(null);
  }
  
  protected final void postCommand(zzz paramZzz)
    throws RemoteException
  {
    paramZzz.zza(zzax, zzao);
  }
}
