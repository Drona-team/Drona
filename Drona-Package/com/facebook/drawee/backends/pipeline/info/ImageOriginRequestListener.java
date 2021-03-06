package com.facebook.drawee.backends.pipeline.info;

import com.facebook.imagepipeline.listener.BaseRequestListener;
import javax.annotation.Nullable;

public class ImageOriginRequestListener
  extends BaseRequestListener
{
  private String mControllerId;
  @Nullable
  private final ImageOriginListener mImageOriginLister;
  
  public ImageOriginRequestListener(String paramString, ImageOriginListener paramImageOriginListener)
  {
    mImageOriginLister = paramImageOriginListener;
    init(paramString);
  }
  
  public void init(String paramString)
  {
    mControllerId = paramString;
  }
  
  public void onUltimateProducerReached(String paramString1, String paramString2, boolean paramBoolean)
  {
    if (mImageOriginLister != null) {
      mImageOriginLister.onImageLoaded(mControllerId, ImageOriginUtils.mapProducerNameToImageOrigin(paramString2), paramBoolean, paramString2);
    }
  }
}
