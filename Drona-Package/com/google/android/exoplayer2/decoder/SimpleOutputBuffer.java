package com.google.android.exoplayer2.decoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SimpleOutputBuffer
  extends OutputBuffer
{
  public ByteBuffer data;
  private final SimpleDecoder<?, SimpleOutputBuffer, ?> owner;
  
  public SimpleOutputBuffer(SimpleDecoder paramSimpleDecoder)
  {
    owner = paramSimpleDecoder;
  }
  
  public void clear()
  {
    super.clear();
    if (data != null) {
      data.clear();
    }
  }
  
  public ByteBuffer init(long paramLong, int paramInt)
  {
    timeUs = paramLong;
    if ((data == null) || (data.capacity() < paramInt)) {
      data = ByteBuffer.allocateDirect(paramInt).order(ByteOrder.nativeOrder());
    }
    data.position(0);
    data.limit(paramInt);
    return data;
  }
  
  public void release()
  {
    owner.releaseOutputBuffer(this);
  }
}
