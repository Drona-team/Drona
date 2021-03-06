package com.facebook.drawee.backends.pipeline.info;

import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Objects.ToStringHelper;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import javax.annotation.Nullable;

public class ImagePerfData
{
  public static final int UNSET = -1;
  @Nullable
  private final Object mCallerContext;
  @Nullable
  private final String mComponentTag;
  private final long mControllerCancelTimeMs;
  private final long mControllerFailureTimeMs;
  private final long mControllerFinalImageSetTimeMs;
  @Nullable
  private final String mControllerId;
  private final long mControllerIntermediateImageSetTimeMs;
  private final long mControllerSubmitTimeMs;
  @Nullable
  private final ImageInfo mImageInfo;
  private final int mImageOrigin;
  @Nullable
  private final ImageRequest mImageRequest;
  private final long mImageRequestEndTimeMs;
  private final long mImageRequestStartTimeMs;
  private final long mInvisibilityEventTimeMs;
  private final boolean mIsPrefetch;
  private final int mOnScreenHeightPx;
  private final int mOnScreenWidthPx;
  @Nullable
  private final String mRequestId;
  @Nullable
  private final String mUltimateProducerName;
  private final long mVisibilityEventTimeMs;
  private final int mVisibilityState;
  
  public ImagePerfData(String paramString1, String paramString2, ImageRequest paramImageRequest, Object paramObject, ImageInfo paramImageInfo, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, long paramLong6, long paramLong7, int paramInt1, String paramString3, boolean paramBoolean, int paramInt2, int paramInt3, int paramInt4, long paramLong8, long paramLong9, String paramString4)
  {
    mControllerId = paramString1;
    mRequestId = paramString2;
    mImageRequest = paramImageRequest;
    mCallerContext = paramObject;
    mImageInfo = paramImageInfo;
    mControllerSubmitTimeMs = paramLong1;
    mControllerIntermediateImageSetTimeMs = paramLong2;
    mControllerFinalImageSetTimeMs = paramLong3;
    mControllerFailureTimeMs = paramLong4;
    mControllerCancelTimeMs = paramLong5;
    mImageRequestStartTimeMs = paramLong6;
    mImageRequestEndTimeMs = paramLong7;
    mImageOrigin = paramInt1;
    mUltimateProducerName = paramString3;
    mIsPrefetch = paramBoolean;
    mOnScreenWidthPx = paramInt2;
    mOnScreenHeightPx = paramInt3;
    mVisibilityState = paramInt4;
    mVisibilityEventTimeMs = paramLong8;
    mInvisibilityEventTimeMs = paramLong9;
    mComponentTag = paramString4;
  }
  
  public String createDebugString()
  {
    return Objects.toStringHelper(this).addValue("controller ID", mControllerId).addValue("request ID", mRequestId).addValue("controller submit", mControllerSubmitTimeMs).addValue("controller final image", mControllerFinalImageSetTimeMs).addValue("controller failure", mControllerFailureTimeMs).addValue("controller cancel", mControllerCancelTimeMs).addValue("start time", mImageRequestStartTimeMs).addValue("end time", mImageRequestEndTimeMs).addValue("origin", ImageOriginUtils.toString(mImageOrigin)).addValue("ultimateProducerName", mUltimateProducerName).addValue("prefetch", mIsPrefetch).addValue("caller context", mCallerContext).addValue("image request", mImageRequest).addValue("image info", mImageInfo).addValue("on-screen width", mOnScreenWidthPx).addValue("on-screen height", mOnScreenHeightPx).addValue("visibility state", mVisibilityState).addValue("component tag", mComponentTag).toString();
  }
  
  public Object getCallerContext()
  {
    return mCallerContext;
  }
  
  public String getComponentTag()
  {
    return mComponentTag;
  }
  
  public long getControllerFailureTimeMs()
  {
    return mControllerFailureTimeMs;
  }
  
  public long getControllerFinalImageSetTimeMs()
  {
    return mControllerFinalImageSetTimeMs;
  }
  
  public String getControllerId()
  {
    return mControllerId;
  }
  
  public long getControllerIntermediateImageSetTimeMs()
  {
    return mControllerIntermediateImageSetTimeMs;
  }
  
  public long getControllerSubmitTimeMs()
  {
    return mControllerSubmitTimeMs;
  }
  
  public long getFinalImageLoadTimeMs()
  {
    if (getImageRequestEndTimeMs() != -1L)
    {
      if (getImageRequestStartTimeMs() == -1L) {
        return -1L;
      }
      return getImageRequestEndTimeMs() - getImageRequestStartTimeMs();
    }
    return -1L;
  }
  
  public ImageInfo getImageInfo()
  {
    return mImageInfo;
  }
  
  public int getImageOrigin()
  {
    return mImageOrigin;
  }
  
  public ImageRequest getImageRequest()
  {
    return mImageRequest;
  }
  
  public long getImageRequestEndTimeMs()
  {
    return mImageRequestEndTimeMs;
  }
  
  public long getImageRequestStartTimeMs()
  {
    return mImageRequestStartTimeMs;
  }
  
  public long getIntermediateImageLoadTimeMs()
  {
    if (getControllerIntermediateImageSetTimeMs() != -1L)
    {
      if (getControllerSubmitTimeMs() == -1L) {
        return -1L;
      }
      return getControllerIntermediateImageSetTimeMs() - getControllerSubmitTimeMs();
    }
    return -1L;
  }
  
  public long getInvisibilityEventTimeMs()
  {
    return mInvisibilityEventTimeMs;
  }
  
  public int getOnScreenHeightPx()
  {
    return mOnScreenHeightPx;
  }
  
  public int getOnScreenWidthPx()
  {
    return mOnScreenWidthPx;
  }
  
  public String getRequestId()
  {
    return mRequestId;
  }
  
  public String getUltimateProducerName()
  {
    return mUltimateProducerName;
  }
  
  public long getVisibilityEventTimeMs()
  {
    return mVisibilityEventTimeMs;
  }
  
  public int getVisibilityState()
  {
    return mVisibilityState;
  }
  
  public boolean isPrefetch()
  {
    return mIsPrefetch;
  }
}
