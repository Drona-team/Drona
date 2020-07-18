package com.google.android.exoplayer2.upstream.crypto;

import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import java.io.IOException;

public final class AesCipherDataSink
  implements DataSink
{
  private AesFlushingCipher cipher;
  private final byte[] scratch;
  private final byte[] secretKey;
  private final DataSink wrappedDataSink;
  
  public AesCipherDataSink(byte[] paramArrayOfByte, DataSink paramDataSink)
  {
    this(paramArrayOfByte, paramDataSink, null);
  }
  
  public AesCipherDataSink(byte[] paramArrayOfByte1, DataSink paramDataSink, byte[] paramArrayOfByte2)
  {
    wrappedDataSink = paramDataSink;
    secretKey = paramArrayOfByte1;
    scratch = paramArrayOfByte2;
  }
  
  public void close()
    throws IOException
  {
    cipher = null;
    wrappedDataSink.close();
  }
  
  public void open(DataSpec paramDataSpec)
    throws IOException
  {
    wrappedDataSink.open(paramDataSpec);
    long l = CryptoUtil.getFNV64Hash(key);
    cipher = new AesFlushingCipher(1, secretKey, l, absoluteStreamPosition);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (scratch == null)
    {
      cipher.updateInPlace(paramArrayOfByte, paramInt1, paramInt2);
      wrappedDataSink.write(paramArrayOfByte, paramInt1, paramInt2);
      return;
    }
    int i = 0;
    while (i < paramInt2)
    {
      int j = Math.min(paramInt2 - i, scratch.length);
      cipher.update(paramArrayOfByte, paramInt1 + i, j, scratch, 0);
      wrappedDataSink.write(scratch, 0, j);
      i += j;
    }
  }
}
