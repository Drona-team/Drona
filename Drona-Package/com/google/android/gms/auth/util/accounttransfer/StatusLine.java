package com.google.android.gms.auth.util.accounttransfer;

final class StatusLine
  extends com.google.android.gms.auth.api.accounttransfer.AccountTransferClient.zza<com.google.android.gms.auth.api.accounttransfer.DeviceMetaData>
{
  StatusLine(TokenStream paramTokenStream, AccountTransferClient.zzb paramZzb)
  {
    super(paramZzb);
  }
  
  public final void showInfo(DeviceMetaData paramDeviceMetaData)
  {
    zzas.setResult(paramDeviceMetaData);
  }
}
