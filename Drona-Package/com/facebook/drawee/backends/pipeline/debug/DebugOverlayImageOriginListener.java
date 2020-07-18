package com.facebook.drawee.backends.pipeline.debug;

import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginUtils;

public class DebugOverlayImageOriginListener
  implements ImageOriginListener
{
  private int mImageOrigin = 1;
  
  public DebugOverlayImageOriginListener() {}
  
  public String getImageOrigin()
  {
    return ImageOriginUtils.toString(mImageOrigin);
  }
  
  public void onImageLoaded(String paramString1, int paramInt, boolean paramBoolean, String paramString2)
  {
    mImageOrigin = paramInt;
  }
}
