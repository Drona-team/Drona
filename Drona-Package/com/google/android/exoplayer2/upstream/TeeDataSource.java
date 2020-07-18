package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.util.Map;

public final class TeeDataSource
  implements DataSource
{
  private long bytesRemaining;
  private final DataSink dataSink;
  private boolean dataSinkNeedsClosing;
  private final DataSource upstream;
  
  public TeeDataSource(DataSource paramDataSource, DataSink paramDataSink)
  {
    upstream = ((DataSource)Assertions.checkNotNull(paramDataSource));
    dataSink = ((DataSink)Assertions.checkNotNull(paramDataSink));
  }
  
  public void addTransferListener(TransferListener paramTransferListener)
  {
    upstream.addTransferListener(paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    try
    {
      upstream.close();
      if (dataSinkNeedsClosing)
      {
        dataSinkNeedsClosing = false;
        dataSink.close();
        return;
      }
    }
    catch (Throwable localThrowable)
    {
      if (dataSinkNeedsClosing)
      {
        dataSinkNeedsClosing = false;
        dataSink.close();
      }
      throw localThrowable;
    }
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
    bytesRemaining = upstream.open(paramDataSpec);
    if (bytesRemaining == 0L) {
      return 0L;
    }
    DataSpec localDataSpec = paramDataSpec;
    if (length == -1L)
    {
      localDataSpec = paramDataSpec;
      if (bytesRemaining != -1L) {
        localDataSpec = paramDataSpec.subrange(0L, bytesRemaining);
      }
    }
    dataSinkNeedsClosing = true;
    dataSink.open(localDataSpec);
    return bytesRemaining;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (bytesRemaining == 0L) {
      return -1;
    }
    paramInt2 = upstream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 > 0)
    {
      dataSink.write(paramArrayOfByte, paramInt1, paramInt2);
      if (bytesRemaining != -1L) {
        bytesRemaining -= paramInt2;
      }
    }
    return paramInt2;
  }
}
