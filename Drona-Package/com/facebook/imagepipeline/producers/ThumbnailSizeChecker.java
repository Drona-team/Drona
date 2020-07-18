package com.facebook.imagepipeline.producers;

import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.EncodedImage;

public final class ThumbnailSizeChecker
{
  public static final float ACCEPTABLE_REQUESTED_TO_ACTUAL_SIZE_RATIO = 1.3333334F;
  private static final int ROTATED_90_DEGREES_CLOCKWISE = 90;
  private static final int ROTATED_90_DEGREES_COUNTER_CLOCKWISE = 270;
  
  public ThumbnailSizeChecker() {}
  
  public static int getAcceptableSize(int paramInt)
  {
    return (int)(paramInt * 1.3333334F);
  }
  
  public static boolean isImageBigEnough(int paramInt1, int paramInt2, ResizeOptions paramResizeOptions)
  {
    if (paramResizeOptions == null) {
      return (getAcceptableSize(paramInt1) >= 2048.0F) && (getAcceptableSize(paramInt2) >= 2048);
    }
    return (getAcceptableSize(paramInt1) >= width) && (getAcceptableSize(paramInt2) >= height);
  }
  
  public static boolean isImageBigEnough(EncodedImage paramEncodedImage, ResizeOptions paramResizeOptions)
  {
    if (paramEncodedImage == null) {
      return false;
    }
    int i = paramEncodedImage.getRotationAngle();
    if ((i != 90) && (i != 270)) {
      return isImageBigEnough(paramEncodedImage.getWidth(), paramEncodedImage.getHeight(), paramResizeOptions);
    }
    return isImageBigEnough(paramEncodedImage.getHeight(), paramEncodedImage.getWidth(), paramResizeOptions);
  }
}