package com.google.android.gms.package_8.internal;

import android.content.Context;
import android.os.IInterface;
import android.os.Looper;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.BaseGmsClient.BaseConnectionCallbacks;
import com.google.android.gms.common.internal.BaseGmsClient.BaseOnConnectionFailedListener;

public abstract class TableStatements<T extends IInterface>
  extends BaseGmsClient<T>
{
  protected TableStatements(Context paramContext, Looper paramLooper, int paramInt, BaseGmsClient.BaseConnectionCallbacks paramBaseConnectionCallbacks, BaseGmsClient.BaseOnConnectionFailedListener paramBaseOnConnectionFailedListener, String paramString)
  {
    super(paramContext, paramLooper, paramInt, paramBaseConnectionCallbacks, paramBaseOnConnectionFailedListener, null);
  }
}
