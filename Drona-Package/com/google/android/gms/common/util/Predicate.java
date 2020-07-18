package com.google.android.gms.common.util;

import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public abstract interface Predicate<T>
{
  public abstract boolean apply(Object paramObject);
}
