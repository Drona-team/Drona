package com.facebook.binaryresource;

import com.facebook.common.internal.Preconditions;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayBinaryResource
  implements BinaryResource
{
  private final byte[] mBytes;
  
  public ByteArrayBinaryResource(byte[] paramArrayOfByte)
  {
    mBytes = ((byte[])Preconditions.checkNotNull(paramArrayOfByte));
  }
  
  public InputStream openStream()
    throws IOException
  {
    return new ByteArrayInputStream(mBytes);
  }
  
  public byte[] read()
  {
    return mBytes;
  }
  
  public long size()
  {
    return mBytes.length;
  }
}
