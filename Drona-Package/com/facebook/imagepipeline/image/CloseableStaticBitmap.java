package com.facebook.imagepipeline.image;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imageutils.BitmapUtil;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CloseableStaticBitmap
  extends CloseableBitmap
{
  private volatile Bitmap mBitmap;
  @GuardedBy("this")
  private CloseableReference<Bitmap> mBitmapReference;
  private final int mExifOrientation;
  private final QualityInfo mQualityInfo;
  private final int mRotationAngle;
  
  public CloseableStaticBitmap(Bitmap paramBitmap, ResourceReleaser paramResourceReleaser, QualityInfo paramQualityInfo, int paramInt)
  {
    this(paramBitmap, paramResourceReleaser, paramQualityInfo, paramInt, 0);
  }
  
  public CloseableStaticBitmap(Bitmap paramBitmap, ResourceReleaser paramResourceReleaser, QualityInfo paramQualityInfo, int paramInt1, int paramInt2)
  {
    mBitmap = ((Bitmap)Preconditions.checkNotNull(paramBitmap));
    mBitmapReference = CloseableReference.of(mBitmap, (ResourceReleaser)Preconditions.checkNotNull(paramResourceReleaser));
    mQualityInfo = paramQualityInfo;
    mRotationAngle = paramInt1;
    mExifOrientation = paramInt2;
  }
  
  public CloseableStaticBitmap(CloseableReference paramCloseableReference, QualityInfo paramQualityInfo, int paramInt)
  {
    this(paramCloseableReference, paramQualityInfo, paramInt, 0);
  }
  
  public CloseableStaticBitmap(CloseableReference paramCloseableReference, QualityInfo paramQualityInfo, int paramInt1, int paramInt2)
  {
    mBitmapReference = ((CloseableReference)Preconditions.checkNotNull(paramCloseableReference.cloneOrNull()));
    mBitmap = ((Bitmap)mBitmapReference.get());
    mQualityInfo = paramQualityInfo;
    mRotationAngle = paramInt1;
    mExifOrientation = paramInt2;
  }
  
  private CloseableReference detachBitmapReference()
  {
    try
    {
      CloseableReference localCloseableReference = mBitmapReference;
      mBitmapReference = null;
      mBitmap = null;
      return localCloseableReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static int getBitmapHeight(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return 0;
    }
    return paramBitmap.getHeight();
  }
  
  private static int getBitmapWidth(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return 0;
    }
    return paramBitmap.getWidth();
  }
  
  public CloseableReference cloneUnderlyingBitmapReference()
  {
    try
    {
      CloseableReference localCloseableReference = CloseableReference.cloneOrNull(mBitmapReference);
      return localCloseableReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void close()
  {
    CloseableReference localCloseableReference = detachBitmapReference();
    if (localCloseableReference != null) {
      localCloseableReference.close();
    }
  }
  
  public CloseableReference convertToBitmapReference()
  {
    try
    {
      Preconditions.checkNotNull(mBitmapReference, "Cannot convert a closed static bitmap");
      CloseableReference localCloseableReference = detachBitmapReference();
      return localCloseableReference;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getExifOrientation()
  {
    return mExifOrientation;
  }
  
  public int getHeight()
  {
    if ((mRotationAngle % 180 == 0) && (mExifOrientation != 5) && (mExifOrientation != 7)) {
      return getBitmapHeight(mBitmap);
    }
    return getBitmapWidth(mBitmap);
  }
  
  public QualityInfo getQualityInfo()
  {
    return mQualityInfo;
  }
  
  public int getRotationAngle()
  {
    return mRotationAngle;
  }
  
  public int getSizeInBytes()
  {
    return BitmapUtil.getSizeInBytes(mBitmap);
  }
  
  public Bitmap getUnderlyingBitmap()
  {
    return mBitmap;
  }
  
  public int getWidth()
  {
    if ((mRotationAngle % 180 == 0) && (mExifOrientation != 5) && (mExifOrientation != 7)) {
      return getBitmapWidth(mBitmap);
    }
    return getBitmapHeight(mBitmap);
  }
  
  public boolean isClosed()
  {
    try
    {
      CloseableReference localCloseableReference = mBitmapReference;
      boolean bool;
      if (localCloseableReference == null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}