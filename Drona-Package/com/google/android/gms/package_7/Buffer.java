package com.google.android.gms.package_7;

import android.util.Base64;
import com.google.android.gms.common.internal.Objects;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

final class Buffer
{
  private final KeyPair zzcb;
  private final long zzcc;
  
  Buffer(KeyPair paramKeyPair, long paramLong)
  {
    zzcb = paramKeyPair;
    zzcc = paramLong;
  }
  
  private final String encode()
  {
    return Base64.encodeToString(zzcb.getPublic().getEncoded(), 11);
  }
  
  private final String write()
  {
    return Base64.encodeToString(zzcb.getPrivate().getEncoded(), 11);
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Buffer)) {
      return false;
    }
    paramObject = (Buffer)paramObject;
    return (zzcc == zzcc) && (zzcb.getPublic().equals(zzcb.getPublic())) && (zzcb.getPrivate().equals(zzcb.getPrivate()));
  }
  
  final long getCreationTime()
  {
    return zzcc;
  }
  
  final KeyPair getKeyPair()
  {
    return zzcb;
  }
  
  public final int hashCode()
  {
    return Objects.hashCode(new Object[] { zzcb.getPublic(), zzcb.getPrivate(), Long.valueOf(zzcc) });
  }
}
