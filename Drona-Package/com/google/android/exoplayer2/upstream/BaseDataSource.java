package com.google.android.exoplayer2.upstream;

import androidx.annotation.Nullable;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;

public abstract class BaseDataSource
  implements DataSource
{
  @Nullable
  private DataSpec dataSpec;
  private final boolean isNetwork;
  private int listenerCount;
  private final ArrayList<TransferListener> listeners;
  
  protected BaseDataSource(boolean paramBoolean)
  {
    isNetwork = paramBoolean;
    listeners = new ArrayList(1);
  }
  
  public final void addTransferListener(TransferListener paramTransferListener)
  {
    if (!listeners.contains(paramTransferListener))
    {
      listeners.add(paramTransferListener);
      listenerCount += 1;
    }
  }
  
  protected final void bytesTransferred(int paramInt)
  {
    DataSpec localDataSpec = (DataSpec)Util.castNonNull(dataSpec);
    int i = 0;
    while (i < listenerCount)
    {
      ((TransferListener)listeners.get(i)).onBytesTransferred(this, localDataSpec, isNetwork, paramInt);
      i += 1;
    }
  }
  
  protected final void transferEnded()
  {
    DataSpec localDataSpec = (DataSpec)Util.castNonNull(dataSpec);
    int i = 0;
    while (i < listenerCount)
    {
      ((TransferListener)listeners.get(i)).onTransferEnd(this, localDataSpec, isNetwork);
      i += 1;
    }
    dataSpec = null;
  }
  
  protected final void transferInitializing(DataSpec paramDataSpec)
  {
    int i = 0;
    while (i < listenerCount)
    {
      ((TransferListener)listeners.get(i)).onTransferInitializing(this, paramDataSpec, isNetwork);
      i += 1;
    }
  }
  
  protected final void transferStarted(DataSpec paramDataSpec)
  {
    dataSpec = paramDataSpec;
    int i = 0;
    while (i < listenerCount)
    {
      ((TransferListener)listeners.get(i)).onTransferStart(this, paramDataSpec, isNetwork);
      i += 1;
    }
  }
}
