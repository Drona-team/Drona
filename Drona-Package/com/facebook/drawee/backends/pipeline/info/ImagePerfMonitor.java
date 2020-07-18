package com.facebook.drawee.backends.pipeline.info;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.facebook.common.time.MonotonicClock;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.info.internal.ImagePerfControllerListener;
import com.facebook.drawee.backends.pipeline.info.internal.ImagePerfImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.internal.ImagePerfRequestListener;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.imagepipeline.listener.ForwardingRequestListener;
import com.facebook.imagepipeline.listener.RequestListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

public class ImagePerfMonitor
{
  private boolean mEnabled;
  @Nullable
  private ForwardingRequestListener mForwardingRequestListener;
  @Nullable
  private ImageOriginListener mImageOriginListener;
  @Nullable
  private ImageOriginRequestListener mImageOriginRequestListener;
  @Nullable
  private ImagePerfControllerListener mImagePerfControllerListener;
  @Nullable
  private List<ImagePerfDataListener> mImagePerfDataListeners;
  @Nullable
  private ImagePerfRequestListener mImagePerfRequestListener;
  private final ImagePerfState mImagePerfState;
  private final MonotonicClock mMonotonicClock;
  private final PipelineDraweeController mPipelineDraweeController;
  
  public ImagePerfMonitor(MonotonicClock paramMonotonicClock, PipelineDraweeController paramPipelineDraweeController)
  {
    mMonotonicClock = paramMonotonicClock;
    mPipelineDraweeController = paramPipelineDraweeController;
    mImagePerfState = new ImagePerfState();
  }
  
  private void setupListeners()
  {
    if (mImagePerfControllerListener == null) {
      mImagePerfControllerListener = new ImagePerfControllerListener(mMonotonicClock, mImagePerfState, this);
    }
    if (mImagePerfRequestListener == null) {
      mImagePerfRequestListener = new ImagePerfRequestListener(mMonotonicClock, mImagePerfState);
    }
    if (mImageOriginListener == null) {
      mImageOriginListener = new ImagePerfImageOriginListener(mImagePerfState, this);
    }
    if (mImageOriginRequestListener == null) {
      mImageOriginRequestListener = new ImageOriginRequestListener(mPipelineDraweeController.getId(), mImageOriginListener);
    } else {
      mImageOriginRequestListener.init(mPipelineDraweeController.getId());
    }
    if (mForwardingRequestListener == null) {
      mForwardingRequestListener = new ForwardingRequestListener(new RequestListener[] { mImagePerfRequestListener, mImageOriginRequestListener });
    }
  }
  
  public void addImagePerfDataListener(ImagePerfDataListener paramImagePerfDataListener)
  {
    if (paramImagePerfDataListener == null) {
      return;
    }
    if (mImagePerfDataListeners == null) {
      mImagePerfDataListeners = new LinkedList();
    }
    mImagePerfDataListeners.add(paramImagePerfDataListener);
  }
  
  public void addViewportData()
  {
    Object localObject = mPipelineDraweeController.getHierarchy();
    if ((localObject != null) && (((DraweeHierarchy)localObject).getTopLevelDrawable() != null))
    {
      localObject = ((DraweeHierarchy)localObject).getTopLevelDrawable().getBounds();
      mImagePerfState.setOnScreenWidth(((Rect)localObject).width());
      mImagePerfState.setOnScreenHeight(((Rect)localObject).height());
    }
  }
  
  public void clearImagePerfDataListeners()
  {
    if (mImagePerfDataListeners != null) {
      mImagePerfDataListeners.clear();
    }
  }
  
  public void notifyListenersOfVisibilityStateUpdate(ImagePerfState paramImagePerfState, int paramInt)
  {
    if ((mEnabled) && (mImagePerfDataListeners != null))
    {
      if (mImagePerfDataListeners.isEmpty()) {
        return;
      }
      paramImagePerfState = paramImagePerfState.snapshot();
      Iterator localIterator = mImagePerfDataListeners.iterator();
      while (localIterator.hasNext()) {
        ((ImagePerfDataListener)localIterator.next()).onImageVisibilityUpdated(paramImagePerfState, paramInt);
      }
    }
  }
  
  public void notifyStatusUpdated(ImagePerfState paramImagePerfState, int paramInt)
  {
    paramImagePerfState.setImageLoadStatus(paramInt);
    if ((mEnabled) && (mImagePerfDataListeners != null))
    {
      if (mImagePerfDataListeners.isEmpty()) {
        return;
      }
      if (paramInt == 3) {
        addViewportData();
      }
      paramImagePerfState = paramImagePerfState.snapshot();
      Iterator localIterator = mImagePerfDataListeners.iterator();
      while (localIterator.hasNext()) {
        ((ImagePerfDataListener)localIterator.next()).onImageLoadStatusUpdated(paramImagePerfState, paramInt);
      }
    }
  }
  
  public void removeImagePerfDataListener(ImagePerfDataListener paramImagePerfDataListener)
  {
    if (mImagePerfDataListeners == null) {
      return;
    }
    mImagePerfDataListeners.remove(paramImagePerfDataListener);
  }
  
  public void reset()
  {
    clearImagePerfDataListeners();
    setEnabled(false);
    mImagePerfState.reset();
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    mEnabled = paramBoolean;
    if (paramBoolean)
    {
      setupListeners();
      if (mImageOriginListener != null) {
        mPipelineDraweeController.addImageOriginListener(mImageOriginListener);
      }
      if (mImagePerfControllerListener != null) {
        mPipelineDraweeController.addControllerListener(mImagePerfControllerListener);
      }
      if (mForwardingRequestListener != null) {
        mPipelineDraweeController.addRequestListener(mForwardingRequestListener);
      }
    }
    else
    {
      if (mImageOriginListener != null) {
        mPipelineDraweeController.removeImageOriginListener(mImageOriginListener);
      }
      if (mImagePerfControllerListener != null) {
        mPipelineDraweeController.removeControllerListener(mImagePerfControllerListener);
      }
      if (mForwardingRequestListener != null) {
        mPipelineDraweeController.removeRequestListener(mForwardingRequestListener);
      }
    }
  }
}
