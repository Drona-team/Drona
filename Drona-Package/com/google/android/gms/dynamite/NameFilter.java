package com.google.android.gms.dynamite;

import android.content.Context;

final class NameFilter
  implements DynamiteModule.VersionPolicy
{
  NameFilter() {}
  
  public final DynamiteModule.VersionPolicy.zzb getValue(Context paramContext, String paramString, DynamiteModule.VersionPolicy.zza paramZza)
    throws DynamiteModule.LoadingException
  {
    DynamiteModule.VersionPolicy.zzb localZzb = new DynamiteModule.VersionPolicy.zzb();
    zzir = paramZza.getLocalVersion(paramContext, paramString);
    if (zzir != 0)
    {
      zzit = -1;
      return localZzb;
    }
    zzis = paramZza.get(paramContext, paramString, true);
    if (zzis != 0) {
      zzit = 1;
    }
    return localZzb;
  }
}
