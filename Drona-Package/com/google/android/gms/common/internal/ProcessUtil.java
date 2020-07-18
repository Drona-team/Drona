package com.google.android.gms.common.internal;

import androidx.annotation.NonNull;

public final class ProcessUtil
{
  @NonNull
  private final String mPackageName;
  private final int zzdt;
  @NonNull
  private final String zzej;
  private final boolean zzek;
  
  public ProcessUtil(String paramString1, String paramString2, boolean paramBoolean, int paramInt)
  {
    mPackageName = paramString1;
    zzej = paramString2;
    zzek = paramBoolean;
    zzdt = 129;
  }
  
  final int getCityId()
  {
    return zzdt;
  }
  
  final String getInstance()
  {
    return zzej;
  }
  
  final String getPackageName()
  {
    return mPackageName;
  }
}
