package com.bumptech.glide.manager;

import android.content.Context;

public abstract interface ConnectivityMonitorFactory
{
  public abstract ConnectivityMonitor build(Context paramContext, ConnectivityMonitor.ConnectivityListener paramConnectivityListener);
}
