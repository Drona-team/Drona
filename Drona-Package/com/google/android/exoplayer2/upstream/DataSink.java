package com.google.android.exoplayer2.upstream;

import java.io.IOException;

public abstract interface DataSink
{
  public abstract void close()
    throws IOException;
  
  public abstract void open(DataSpec paramDataSpec)
    throws IOException;
  
  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public static abstract interface Factory
  {
    public abstract DataSink createDataSink();
  }
}
