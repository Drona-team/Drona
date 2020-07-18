package com.facebook.drawee.debug.listener;

import android.graphics.drawable.Animatable;
import com.facebook.drawee.controller.BaseControllerListener;
import javax.annotation.Nullable;

public class ImageLoadingTimeControllerListener
  extends BaseControllerListener
{
  private long mFinalImageSetTimeMs = -1L;
  @Nullable
  private ImageLoadingTimeListener mImageLoadingTimeListener;
  private long mRequestSubmitTimeMs = -1L;
  
  public ImageLoadingTimeControllerListener(ImageLoadingTimeListener paramImageLoadingTimeListener)
  {
    mImageLoadingTimeListener = paramImageLoadingTimeListener;
  }
  
  public void onFinalImageSet(String paramString, Object paramObject, Animatable paramAnimatable)
  {
    mFinalImageSetTimeMs = System.currentTimeMillis();
    if (mImageLoadingTimeListener != null) {
      mImageLoadingTimeListener.onFinalImageSet(mFinalImageSetTimeMs - mRequestSubmitTimeMs);
    }
  }
  
  public void onSubmit(String paramString, Object paramObject)
  {
    mRequestSubmitTimeMs = System.currentTimeMillis();
  }
}
