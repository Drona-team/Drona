package com.bumptech.glide.load.resource.bytes;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public class BytesResource
  implements Resource<byte[]>
{
  private final byte[] bytes;
  
  public BytesResource(byte[] paramArrayOfByte)
  {
    bytes = ((byte[])Preconditions.checkNotNull(paramArrayOfByte));
  }
  
  public byte[] get()
  {
    return bytes;
  }
  
  public Class getResourceClass()
  {
    return [B.class;
  }
  
  public int getSize()
  {
    return bytes.length;
  }
  
  public void recycle() {}
}
