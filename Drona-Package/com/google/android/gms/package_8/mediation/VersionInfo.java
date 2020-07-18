package com.google.android.gms.package_8.mediation;

public final class VersionInfo
{
  private final int zzenq;
  private final int zzenr;
  private final int zzens;
  
  public VersionInfo(int paramInt1, int paramInt2, int paramInt3)
  {
    zzenq = paramInt1;
    zzenr = paramInt2;
    zzens = paramInt3;
  }
  
  public final int getMajorVersion()
  {
    return zzenq;
  }
  
  public final int getMicroVersion()
  {
    return zzens;
  }
  
  public final int getMinorVersion()
  {
    return zzenr;
  }
}
