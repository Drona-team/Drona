package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import java.io.IOException;
import java.util.Map;

public final class PriorityDataSource
  implements DataSource
{
  private final int priority;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource upstream;
  
  public PriorityDataSource(DataSource paramDataSource, PriorityTaskManager paramPriorityTaskManager, int paramInt)
  {
    upstream = ((DataSource)Assertions.checkNotNull(paramDataSource));
    priorityTaskManager = ((PriorityTaskManager)Assertions.checkNotNull(paramPriorityTaskManager));
    priority = paramInt;
  }
  
  public void addTransferListener(TransferListener paramTransferListener)
  {
    upstream.addTransferListener(paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    upstream.close();
  }
  
  public Map getResponseHeaders()
  {
    return upstream.getResponseHeaders();
  }
  
  public Uri getUri()
  {
    return upstream.getUri();
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    priorityTaskManager.proceedOrThrow(priority);
    return upstream.open(paramDataSpec);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    priorityTaskManager.proceedOrThrow(priority);
    return upstream.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}
