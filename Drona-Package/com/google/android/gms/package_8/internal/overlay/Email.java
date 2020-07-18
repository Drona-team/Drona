package com.google.android.gms.package_8.internal.overlay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.google.android.gms.common.util.PlatformVersion;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzaxi;
import com.google.android.gms.internal.ads.zzbai;
import com.google.android.gms.internal.ads.zzxr;
import com.google.android.gms.package_8.internal.UserData;

@zzard
public final class Email
{
  public Email() {}
  
  public static void handleResult(Context paramContext, AdOverlayInfoParcel paramAdOverlayInfoParcel, boolean paramBoolean)
  {
    if ((zzdkr == 4) && (zzdkm == null))
    {
      if (zzcgi != null) {
        zzcgi.onAdClicked();
      }
      UserData.zzle();
      RequestQueue.start(paramContext, zzdkl, zzdkq);
      return;
    }
    Intent localIntent = new Intent();
    localIntent.setClassName(paramContext, "com.google.android.gms.ads.AdActivity");
    localIntent.putExtra("com.google.android.gms.ads.internal.overlay.useClientJar", zzbtc.zzdze);
    localIntent.putExtra("shouldCallOnOverlayOpened", paramBoolean);
    AdOverlayInfoParcel.onPostExecute(localIntent, paramAdOverlayInfoParcel);
    if (!PlatformVersion.isAtLeastLollipop()) {
      localIntent.addFlags(524288);
    }
    if (!(paramContext instanceof Activity)) {
      localIntent.addFlags(268435456);
    }
    UserData.zzlg();
    zzaxi.zza(paramContext, localIntent);
  }
}
