package com.facebook.imagepipeline.image;

import com.facebook.common.logging.FLog;
import java.io.Closeable;

public abstract class CloseableImage
  implements Closeable, ImageInfo
{
  private static final String PAGE_KEY = "CloseableImage";
  
  public CloseableImage() {}
  
  public abstract void close();
  
  protected void finalize()
    throws Throwable
  {
    if (isClosed()) {
      return;
    }
    FLog.w("CloseableImage", "finalize: %s %x still open.", new Object[] { getClass().getSimpleName(), Integer.valueOf(System.identityHashCode(this)) });
    try
    {
      close();
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public QualityInfo getQualityInfo()
  {
    return ImmutableQualityInfo.FULL_QUALITY;
  }
  
  public abstract int getSizeInBytes();
  
  public abstract boolean isClosed();
  
  public boolean isStateful()
  {
    return false;
  }
}
