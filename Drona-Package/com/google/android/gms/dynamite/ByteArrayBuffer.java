package com.google.android.gms.dynamite;

import android.content.Context;

final class ByteArrayBuffer
  implements DynamiteModule.VersionPolicy.zza
{
  ByteArrayBuffer() {}
  
  public final int get(Context paramContext, String paramString, boolean paramBoolean)
    throws DynamiteModule.LoadingException
  {
    return DynamiteModule.create(paramContext, paramString, paramBoolean);
  }
  
  public final int getLocalVersion(Context paramContext, String paramString)
  {
    return DynamiteModule.getLocalVersion(paramContext, paramString);
  }
}
