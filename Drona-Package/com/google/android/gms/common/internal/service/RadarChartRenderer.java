package com.google.android.gms.common.internal.service;

import android.os.RemoteException;

final class RadarChartRenderer
  extends LineRadarRenderer
{
  private final com.google.android.gms.common.api.internal.BaseImplementation.ResultHolder<com.google.android.gms.common.api.Status> mResultHolder;
  
  public RadarChartRenderer(com.google.android.gms.common.package_6.internal.BaseImplementation.ResultHolder paramResultHolder)
  {
    mResultHolder = paramResultHolder;
  }
  
  public final void zaj(int paramInt)
    throws RemoteException
  {
    mResultHolder.setResult(new com.google.android.gms.common.package_6.Status(paramInt));
  }
}
