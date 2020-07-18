package com.google.android.gms.flags;

import com.google.android.gms.common.annotation.KeepForSdk;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@KeepForSdk
public @interface FlagSource
{
  @KeepForSdk
  public static final int TYPE_DIALOG = 0;
}
