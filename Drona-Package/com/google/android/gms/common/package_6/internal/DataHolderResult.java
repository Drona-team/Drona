package com.google.android.gms.common.package_6.internal;

import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.common.package_6.Releasable;
import com.google.android.gms.common.package_6.Result;
import com.google.android.gms.common.package_6.Status;

@KeepForSdk
public class DataHolderResult
  implements Releasable, Result
{
  @KeepForSdk
  protected final DataHolder mDataHolder;
  @KeepForSdk
  protected final Status mStatus;
  
  protected DataHolderResult(DataHolder paramDataHolder)
  {
    this(paramDataHolder, new Status(paramDataHolder.getStatusCode()));
  }
  
  protected DataHolderResult(DataHolder paramDataHolder, Status paramStatus)
  {
    mStatus = paramStatus;
    mDataHolder = paramDataHolder;
  }
  
  public Status getStatus()
  {
    return mStatus;
  }
  
  public void release()
  {
    if (mDataHolder != null) {
      mDataHolder.close();
    }
  }
}
