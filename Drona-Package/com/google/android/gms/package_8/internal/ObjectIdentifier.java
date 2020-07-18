package com.google.android.gms.package_8.internal;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.internal.ads.zzark;
import com.google.android.gms.internal.ads.zzauy;
import com.google.android.gms.internal.ads.zzavb;
import com.google.android.gms.internal.ads.zzaxi;
import java.util.Iterator;
import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

@zzard
@ParametersAreNonnullByDefault
public final class ObjectIdentifier
{
  private boolean zzbqv;
  private zzavb zzbqw;
  private zzark zzbqx;
  private final Context zzlj;
  
  public ObjectIdentifier(Context paramContext, zzavb paramZzavb, zzark paramZzark)
  {
    zzlj = paramContext;
    zzbqw = paramZzavb;
    zzbqx = null;
    if (zzbqx == null) {
      zzbqx = new zzark();
    }
  }
  
  private final boolean zzkw()
  {
    return ((zzbqw != null) && (zzbqw.zzuc().zzdrw)) || (zzbqx.zzdom);
  }
  
  public final void recordClick()
  {
    zzbqv = true;
  }
  
  public final void zzbk(String paramString)
  {
    if (!zzkw()) {
      return;
    }
    if (paramString == null) {
      paramString = "";
    }
    if (zzbqw != null)
    {
      zzbqw.zza(paramString, null, 3);
      return;
    }
    if ((zzbqx.zzdom) && (zzbqx.zzdon != null))
    {
      Iterator localIterator = zzbqx.zzdon.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if (!TextUtils.isEmpty(str))
        {
          str = str.replace("{NAVIGATION_URL}", Uri.encode(paramString));
          UserData.zzlg();
          zzaxi.zzb(zzlj, "", str);
        }
      }
    }
  }
  
  public final boolean zzkx()
  {
    return (!zzkw()) || (zzbqv);
  }
}