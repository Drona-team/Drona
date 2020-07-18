package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.bumptech.glide.util.MarkEnforcingInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamBitmapDecoder
  implements ResourceDecoder<InputStream, Bitmap>
{
  private final ArrayPool byteArrayPool;
  private final Downsampler downsampler;
  
  public StreamBitmapDecoder(Downsampler paramDownsampler, ArrayPool paramArrayPool)
  {
    downsampler = paramDownsampler;
    byteArrayPool = paramArrayPool;
  }
  
  public Resource decode(InputStream paramInputStream, int paramInt1, int paramInt2, Options paramOptions)
    throws IOException
  {
    int i;
    if ((paramInputStream instanceof RecyclableBufferedInputStream))
    {
      paramInputStream = (RecyclableBufferedInputStream)paramInputStream;
      i = 0;
    }
    else
    {
      paramInputStream = new RecyclableBufferedInputStream(paramInputStream, byteArrayPool);
      i = 1;
    }
    ExceptionCatchingInputStream localExceptionCatchingInputStream = ExceptionCatchingInputStream.obtain(paramInputStream);
    MarkEnforcingInputStream localMarkEnforcingInputStream = new MarkEnforcingInputStream(localExceptionCatchingInputStream);
    UntrustedCallbacks localUntrustedCallbacks = new UntrustedCallbacks(paramInputStream, localExceptionCatchingInputStream);
    try
    {
      paramOptions = downsampler.decode(localMarkEnforcingInputStream, paramInt1, paramInt2, paramOptions, localUntrustedCallbacks);
      localExceptionCatchingInputStream.release();
      if (i != 0)
      {
        paramInputStream.release();
        return paramOptions;
      }
    }
    catch (Throwable paramOptions)
    {
      localExceptionCatchingInputStream.release();
      if (i != 0) {
        paramInputStream.release();
      }
      throw paramOptions;
    }
    return paramOptions;
  }
  
  public boolean handles(InputStream paramInputStream, Options paramOptions)
  {
    return downsampler.handles(paramInputStream);
  }
  
  static class UntrustedCallbacks
    implements Downsampler.DecodeCallbacks
  {
    private final RecyclableBufferedInputStream bufferedStream;
    private final ExceptionCatchingInputStream exceptionStream;
    
    UntrustedCallbacks(RecyclableBufferedInputStream paramRecyclableBufferedInputStream, ExceptionCatchingInputStream paramExceptionCatchingInputStream)
    {
      bufferedStream = paramRecyclableBufferedInputStream;
      exceptionStream = paramExceptionCatchingInputStream;
    }
    
    public void onDecodeComplete(BitmapPool paramBitmapPool, Bitmap paramBitmap)
      throws IOException
    {
      IOException localIOException = exceptionStream.getException();
      if (localIOException != null)
      {
        if (paramBitmap != null) {
          paramBitmapPool.put(paramBitmap);
        }
        throw localIOException;
      }
    }
    
    public void onObtainBounds()
    {
      bufferedStream.fixMarkLimit();
    }
  }
}
