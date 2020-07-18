package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;
import java.io.InputStream;

public final class DataSourceInputStream
  extends InputStream
{
  private boolean closed = false;
  private final DataSource dataSource;
  private final DataSpec dataSpec;
  private boolean opened = false;
  private final byte[] singleByteArray;
  private long totalBytesRead;
  
  public DataSourceInputStream(DataSource paramDataSource, DataSpec paramDataSpec)
  {
    dataSource = paramDataSource;
    dataSpec = paramDataSpec;
    singleByteArray = new byte[1];
  }
  
  private void checkOpened()
    throws IOException
  {
    if (!opened)
    {
      dataSource.open(dataSpec);
      opened = true;
    }
  }
  
  public long bytesRead()
  {
    return totalBytesRead;
  }
  
  public void close()
    throws IOException
  {
    if (!closed)
    {
      dataSource.close();
      closed = true;
    }
  }
  
  public void open()
    throws IOException
  {
    checkOpened();
  }
  
  public int read()
    throws IOException
  {
    if (read(singleByteArray) == -1) {
      return -1;
    }
    return singleByteArray[0] & 0xFF;
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    Assertions.checkState(closed ^ true);
    checkOpened();
    paramInt1 = dataSource.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt1 == -1) {
      return -1;
    }
    totalBytesRead += paramInt1;
    return paramInt1;
  }
}
