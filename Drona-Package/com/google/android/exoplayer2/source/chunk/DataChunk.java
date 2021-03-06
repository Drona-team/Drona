package com.google.android.exoplayer2.source.chunk;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.StatsDataSource;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.util.Arrays;

public abstract class DataChunk
  extends Chunk
{
  private static final int READ_GRANULARITY = 16384;
  private byte[] data;
  private volatile boolean loadCanceled;
  
  public DataChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, byte[] paramArrayOfByte)
  {
    super(paramDataSource, paramDataSpec, paramInt1, paramFormat, paramInt2, paramObject, -9223372036854775807L, -9223372036854775807L);
    data = paramArrayOfByte;
  }
  
  private void maybeExpandData(int paramInt)
  {
    if (data == null)
    {
      data = new byte['?'];
      return;
    }
    if (data.length < paramInt + 16384) {
      data = Arrays.copyOf(data, data.length + 16384);
    }
  }
  
  public final void cancelLoad()
  {
    loadCanceled = true;
  }
  
  protected abstract void consume(byte[] paramArrayOfByte, int paramInt)
    throws IOException;
  
  public byte[] getDataHolder()
  {
    return data;
  }
  
  public final void load()
    throws IOException, InterruptedException
  {
    try
    {
      dataSource.open(dataSpec);
      int i = 0;
      int j = 0;
      while (i != -1)
      {
        bool = loadCanceled;
        if (bool) {
          break;
        }
        maybeExpandData(j);
        int m = dataSource.read(data, j, 16384);
        int k = m;
        i = k;
        if (m != -1)
        {
          j += m;
          i = k;
        }
      }
      boolean bool = loadCanceled;
      if (!bool) {
        consume(data, j);
      }
      Util.closeQuietly(dataSource);
      return;
    }
    catch (Throwable localThrowable)
    {
      Util.closeQuietly(dataSource);
      throw localThrowable;
    }
  }
}
