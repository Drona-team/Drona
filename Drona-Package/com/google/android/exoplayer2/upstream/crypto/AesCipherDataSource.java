package com.google.android.exoplayer2.upstream.crypto;

import android.net.Uri;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.IOException;
import java.util.Map;

public final class AesCipherDataSource
  implements DataSource
{
  @Nullable
  private AesFlushingCipher cipher;
  private final byte[] secretKey;
  private final DataSource upstream;
  
  public AesCipherDataSource(byte[] paramArrayOfByte, DataSource paramDataSource)
  {
    upstream = paramDataSource;
    secretKey = paramArrayOfByte;
  }
  
  public void addTransferListener(TransferListener paramTransferListener)
  {
    upstream.addTransferListener(paramTransferListener);
  }
  
  public void close()
    throws IOException
  {
    cipher = null;
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
    long l1 = upstream.open(paramDataSpec);
    long l2 = CryptoUtil.getFNV64Hash(key);
    cipher = new AesFlushingCipher(2, secretKey, l2, absoluteStreamPosition);
    return l1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if (paramInt2 == 0) {
      return 0;
    }
    paramInt2 = upstream.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt2 == -1) {
      return -1;
    }
    cipher.updateInPlace(paramArrayOfByte, paramInt1, paramInt2);
    return paramInt2;
  }
}
