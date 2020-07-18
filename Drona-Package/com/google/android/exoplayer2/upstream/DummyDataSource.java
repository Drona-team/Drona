package com.google.android.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;

public final class DummyDataSource
  implements DataSource
{
  public static final DataSource.Factory FACTORY = -..Lambda.DummyDataSource.5JL9ytmtADrptG840gjTuddaBKA.INSTANCE;
  public static final DummyDataSource INSTANCE = new DummyDataSource();
  
  private DummyDataSource() {}
  
  public void addTransferListener(TransferListener paramTransferListener) {}
  
  public void close()
    throws IOException
  {}
  
  public Uri getUri()
  {
    return null;
  }
  
  public long open(DataSpec paramDataSpec)
    throws IOException
  {
    throw new IOException("Dummy source");
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    throw new UnsupportedOperationException();
  }
}
