package com.google.android.gms.common.package_6.internal;

import androidx.annotation.WorkerThread;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.internal.IAccountAccessor;
import java.util.Set;

@WorkerThread
public abstract interface zach
{
  public abstract void ignore(ConnectionResult paramConnectionResult);
  
  public abstract void startSession(IAccountAccessor paramIAccountAccessor, Set paramSet);
}
