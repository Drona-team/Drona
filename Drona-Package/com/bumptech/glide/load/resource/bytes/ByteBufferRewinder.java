package com.bumptech.glide.load.resource.bytes;

import com.bumptech.glide.load.data.DataRewinder;
import com.bumptech.glide.load.data.DataRewinder.Factory;
import java.nio.ByteBuffer;

public class ByteBufferRewinder
  implements DataRewinder<ByteBuffer>
{
  private final ByteBuffer buffer;
  
  public ByteBufferRewinder(ByteBuffer paramByteBuffer)
  {
    buffer = paramByteBuffer;
  }
  
  public void cleanup() {}
  
  public ByteBuffer rewindAndGet()
  {
    buffer.position(0);
    return buffer;
  }
  
  public static class Factory
    implements DataRewinder.Factory<ByteBuffer>
  {
    public Factory() {}
    
    public DataRewinder build(ByteBuffer paramByteBuffer)
    {
      return new ByteBufferRewinder(paramByteBuffer);
    }
    
    public Class getDataClass()
    {
      return ByteBuffer.class;
    }
  }
}
