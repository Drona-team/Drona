package com.bumptech.glide.load.data;

import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.resource.bitmap.RecyclableBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class InputStreamRewinder
  implements DataRewinder<InputStream>
{
  private static final int MARK_LIMIT = 5242880;
  private final RecyclableBufferedInputStream bufferedStream;
  
  InputStreamRewinder(InputStream paramInputStream, ArrayPool paramArrayPool)
  {
    bufferedStream = new RecyclableBufferedInputStream(paramInputStream, paramArrayPool);
    bufferedStream.mark(5242880);
  }
  
  public void cleanup()
  {
    bufferedStream.release();
  }
  
  public InputStream rewindAndGet()
    throws IOException
  {
    bufferedStream.reset();
    return bufferedStream;
  }
  
  public static final class Factory
    implements DataRewinder.Factory<InputStream>
  {
    private final ArrayPool byteArrayPool;
    
    public Factory(ArrayPool paramArrayPool)
    {
      byteArrayPool = paramArrayPool;
    }
    
    public DataRewinder build(InputStream paramInputStream)
    {
      return new InputStreamRewinder(paramInputStream, byteArrayPool);
    }
    
    public Class getDataClass()
    {
      return InputStream.class;
    }
  }
}
