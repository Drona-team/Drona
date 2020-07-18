package com.google.android.gms.auth.util.accounttransfer;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.auth.api.accounttransfer.zzn;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AbstractClientBuilder;
import com.google.android.gms.common.package_6.Sample;
import com.google.android.gms.internal.auth.zzt;
import com.google.android.gms.internal.auth.zzu;

public final class AccountTransfer
{
  public static final String ACTION_ACCOUNT_EXPORT_DATA_AVAILABLE = "com.google.android.gms.auth.ACCOUNT_EXPORT_DATA_AVAILABLE";
  public static final String ACTION_ACCOUNT_IMPORT_DATA_AVAILABLE = "com.google.android.gms.auth.ACCOUNT_IMPORT_DATA_AVAILABLE";
  public static final String ACTION_START_ACCOUNT_EXPORT = "com.google.android.gms.auth.START_ACCOUNT_EXPORT";
  public static final String KEY_EXTRA_ACCOUNT_TYPE = "key_extra_account_type";
  private static final com.google.android.gms.common.api.Api.ClientKey<zzu> zzaj = new com.google.android.gms.common.package_6.Api.ClientKey();
  private static final Api.AbstractClientBuilder<zzu, zzn> zzak = new SettingsActivity.2();
  private static final Api<zzn> zzal = new Sample("AccountTransfer.ACCOUNT_TRANSFER_API", zzak, zzaj);
  @Deprecated
  private static final DiskBasedCache zzam = (DiskBasedCache)new zzt();
  private static final DecoderFactory zzan = (DecoderFactory)new zzt();
  
  private AccountTransfer() {}
  
  public static AccountTransferClient getAccountTransferClient(Activity paramActivity)
  {
    return new AccountTransferClient(paramActivity);
  }
  
  public static AccountTransferClient getAccountTransferClient(Context paramContext)
  {
    return new AccountTransferClient(paramContext);
  }
}
