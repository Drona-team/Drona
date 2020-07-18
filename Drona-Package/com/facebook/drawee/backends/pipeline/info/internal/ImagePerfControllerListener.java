package com.facebook.drawee.backends.pipeline.info.internal;

import android.graphics.drawable.Animatable;
import com.facebook.common.time.MonotonicClock;
import com.facebook.drawee.backends.pipeline.info.ImagePerfMonitor;
import com.facebook.drawee.backends.pipeline.info.ImagePerfState;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

public class ImagePerfControllerListener
  extends BaseControllerListener<ImageInfo>
{
  private final MonotonicClock mClock;
  private final ImagePerfMonitor mImagePerfMonitor;
  private final ImagePerfState mImagePerfState;
  
  public ImagePerfControllerListener(MonotonicClock paramMonotonicClock, ImagePerfState paramImagePerfState, ImagePerfMonitor paramImagePerfMonitor)
  {
    mClock = paramMonotonicClock;
    mImagePerfState = paramImagePerfState;
    mImagePerfMonitor = paramImagePerfMonitor;
  }
  
  private void reportViewInvisible(long paramLong)
  {
    mImagePerfState.setVisible(false);
    mImagePerfState.setInvisibilityEventTimeMs(paramLong);
    mImagePerfMonitor.notifyListenersOfVisibilityStateUpdate(mImagePerfState, 2);
  }
  
  public void onFailure(String paramString, Throwable paramThrowable)
  {
    long l = mClock.now();
    mImagePerfState.setControllerFailureTimeMs(l);
    mImagePerfState.setControllerId(paramString);
    mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, 5);
    reportViewInvisible(l);
  }
  
  public void onFinalImageSet(String paramString, ImageInfo paramImageInfo, Animatable paramAnimatable)
  {
    long l = mClock.now();
    mImagePerfState.setControllerFinalImageSetTimeMs(l);
    mImagePerfState.setImageRequestEndTimeMs(l);
    mImagePerfState.setControllerId(paramString);
    mImagePerfState.setImageInfo(paramImageInfo);
    mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, 3);
  }
  
  public void onIntermediateImageSet(String paramString, ImageInfo paramImageInfo)
  {
    long l = mClock.now();
    mImagePerfState.setControllerIntermediateImageSetTimeMs(l);
    mImagePerfState.setControllerId(paramString);
    mImagePerfState.setImageInfo(paramImageInfo);
    mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, 2);
  }
  
  public void onRelease(String paramString)
  {
    super.onRelease(paramString);
    long l = mClock.now();
    int i = mImagePerfState.getImageLoadStatus();
    if ((i != 3) && (i != 5))
    {
      mImagePerfState.setControllerCancelTimeMs(l);
      mImagePerfState.setControllerId(paramString);
      mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, 4);
    }
    reportViewInvisible(l);
  }
  
  public void onSubmit(String paramString, Object paramObject)
  {
    long l = mClock.now();
    mImagePerfState.setControllerSubmitTimeMs(l);
    mImagePerfState.setControllerId(paramString);
    mImagePerfState.setCallerContext(paramObject);
    mImagePerfMonitor.notifyStatusUpdated(mImagePerfState, 0);
    reportViewVisible(l);
  }
  
  public void reportViewVisible(long paramLong)
  {
    mImagePerfState.setVisible(true);
    mImagePerfState.setVisibilityEventTimeMs(paramLong);
    mImagePerfMonitor.notifyListenersOfVisibilityStateUpdate(mImagePerfState, 1);
  }
}
