package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.ByteBufferUtil;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteBufferBitmapDecoder
  implements ResourceDecoder<ByteBuffer, Bitmap>
{
  private final Downsampler downsampler;
  
  public ByteBufferBitmapDecoder(Downsampler paramDownsampler)
  {
    downsampler = paramDownsampler;
  }
  
  public Resource decode(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    paramByteBuffer = ByteBufferUtil.toStream(paramByteBuffer);
    return downsampler.decode(paramByteBuffer, paramInt1, paramInt2, paramOptions);
  }
  
  public boolean handles(ByteBuffer paramByteBuffer, Options paramOptions)
  {
    return downsampler.handles(paramByteBuffer);
  }
}
