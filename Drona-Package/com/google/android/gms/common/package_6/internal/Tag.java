package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.Preconditions;

final class Tag
{
  private final int zadh;
  private final ConnectionResult zadi;
  
  Tag(ConnectionResult paramConnectionResult, int paramInt)
  {
    Preconditions.checkNotNull(paramConnectionResult);
    zadi = paramConnectionResult;
    zadh = paramInt;
  }
  
  final ConnectionResult getConnectionResult()
  {
    return zadi;
  }
  
  final int getId()
  {
    return zadh;
  }
}
