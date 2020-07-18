package com.google.android.gms.package_8.internal;

import android.content.Context;
import android.text.TextUtils;
import com.google.android.gms.common.util.Clock;
import com.google.android.gms.internal.ads.zzacj;
import com.google.android.gms.internal.ads.zzacr;
import com.google.android.gms.internal.ads.zzacu;
import com.google.android.gms.internal.ads.zzalj;
import com.google.android.gms.internal.ads.zzalk;
import com.google.android.gms.internal.ads.zzalo;
import com.google.android.gms.internal.ads.zzalr;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzawl;
import com.google.android.gms.internal.ads.zzawz;
import com.google.android.gms.internal.ads.zzbai;
import com.google.android.gms.internal.ads.zzbao;
import com.google.android.gms.internal.ads.zzbar;
import com.google.android.gms.internal.ads.zzbbh;
import com.google.android.gms.internal.ads.zzbbm;
import com.google.android.gms.internal.ads.zzyt;
import javax.annotation.ParametersAreNonnullByDefault;
import org.json.JSONObject;

@zzard
@ParametersAreNonnullByDefault
public final class Notifier
{
  private long zzbqy = 0L;
  private Context zzlj;
  
  public Notifier() {}
  
  private final void showToast(Context paramContext, zzbai paramZzbai, boolean paramBoolean, zzawl paramZzawl, String paramString1, String paramString2, Runnable paramRunnable)
  {
    if (UserData.zzln().elapsedRealtime() - zzbqy < 5000L)
    {
      zzawz.zzep("Not retrying to fetch app settings");
      return;
    }
    zzbqy = UserData.zzln().elapsedRealtime();
    int k = 1;
    int j;
    if (paramZzawl == null)
    {
      j = k;
    }
    else
    {
      long l1 = paramZzawl.zzuq();
      long l2 = UserData.zzln().currentTimeMillis();
      localObject = zzacu.zzcsy;
      int i;
      if (l2 - l1 > ((Long)zzyt.zzpe().zzd((zzacj)localObject)).longValue()) {
        i = 1;
      } else {
        i = 0;
      }
      j = k;
      if (i == 0) {
        if (!paramZzawl.zzur()) {
          j = k;
        } else {
          j = 0;
        }
      }
    }
    if (j == 0) {
      return;
    }
    if (paramContext == null)
    {
      zzawz.zzep("Context not provided to fetch application settings");
      return;
    }
    if ((TextUtils.isEmpty(paramString1)) && (TextUtils.isEmpty(paramString2)))
    {
      zzawz.zzep("App settings could not be fetched. Required parameters missing");
      return;
    }
    Object localObject = paramContext.getApplicationContext();
    paramZzawl = (zzawl)localObject;
    if (localObject == null) {
      paramZzawl = paramContext;
    }
    zzlj = paramZzawl;
    paramZzbai = UserData.zzlt().zzb(zzlj, paramZzbai).zza("google.afma.config.fetchAppSettings", zzalo.zzddi, zzalo.zzddi);
    try
    {
      paramZzawl = new JSONObject();
      boolean bool = TextUtils.isEmpty(paramString1);
      if (!bool)
      {
        paramZzawl.put("app_id", paramString1);
      }
      else
      {
        bool = TextUtils.isEmpty(paramString2);
        if (!bool) {
          paramZzawl.put("ad_unit_id", paramString2);
        }
      }
      paramZzawl.put("is_init", paramBoolean);
      paramZzawl.put("pn", paramContext.getPackageName());
      paramContext = paramZzbai.zzi(paramZzawl);
      paramZzbai = Element.zzbqz;
      paramZzawl = zzbbm.zzeaf;
      paramZzbai = zzbar.zza(paramContext, paramZzbai, paramZzawl);
      if (paramRunnable != null)
      {
        paramZzawl = zzbbm.zzeaf;
        paramContext.zza(paramRunnable, paramZzawl);
      }
      zzbao.zza(paramZzbai, "ConfigLoader.maybeFetchNewAppSettings");
      return;
    }
    catch (Exception paramContext)
    {
      zzawz.zzc("Error requesting application settings", paramContext);
    }
  }
  
  public final void showToast(Context paramContext, zzbai paramZzbai, String paramString, zzawl paramZzawl)
  {
    if (paramZzawl != null) {}
    for (String str = paramZzawl.zzut();; str = null) {
      break;
    }
    showToast(paramContext, paramZzbai, false, paramZzawl, str, paramString, null);
  }
  
  public final void showToast(Context paramContext, zzbai paramZzbai, String paramString, Runnable paramRunnable)
  {
    showToast(paramContext, paramZzbai, true, null, paramString, null, paramRunnable);
  }
}
