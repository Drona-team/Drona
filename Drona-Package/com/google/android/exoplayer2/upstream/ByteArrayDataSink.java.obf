package com.google.android.exoplayer2.upstream;

import com.google.android.exoplayer2.util.Assertions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ByteArrayDataSink
  implements DataSink
{
  private ByteArrayOutputStream stream;
  
  public ByteArrayDataSink() {}
  
  public void close()
    throws IOException
  {
    stream.close();
  }
  
  public byte[] getData()
  {
    if (stream == null) {
      return null;
    }
    return stream.toByteArray();
  }
  
  public void open(DataSpec paramDataSpec)
    throws IOException
  {
    if (length == -1L)
    {
      stream = new ByteArrayOutputStream();
      return;
    }
    boolean bool;
    if (length <= 2147483647L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    stream = new ByteArrayOutputStream((int)length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    stream.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}
