package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import java.util.Map;

public abstract interface DataSource
{
  public abstract void addTransferListener(TransferListener paramTransferListener);
  
  public abstract void close()
    throws IOException;
  
  public abstract Map getResponseHeaders();
  
  public abstract Uri getUri();
  
  public abstract long open(DataSpec paramDataSpec)
    throws IOException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;
  
  public static abstract interface Factory
  {
    public abstract DataSource createDataSource();
  }
}
