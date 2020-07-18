package com.google.android.gms.dynamite;

import android.content.Context;

final class UnsignedInteger
  implements DynamiteModule.VersionPolicy
{
  UnsignedInteger() {}
  
  public final DynamiteModule.VersionPolicy.zzb getValue(Context paramContext, String paramString, DynamiteModule.VersionPolicy.zza paramZza)
    throws DynamiteModule.LoadingException
  {
    DynamiteModule.VersionPolicy.zzb localZzb = new DynamiteModule.VersionPolicy.zzb();
    zzir = paramZza.getLocalVersion(paramContext, paramString);
    if (zzir != 0) {
      zzis = paramZza.get(paramContext, paramString, false);
    } else {
      zzis = paramZza.get(paramContext, paramString, true);
    }
    if ((zzir == 0) && (zzis == 0))
    {
      zzit = 0;
      return localZzb;
    }
    if (zzir >= zzis)
    {
      zzit = -1;
      return localZzb;
    }
    zzit = 1;
    return localZzb;
  }
}
