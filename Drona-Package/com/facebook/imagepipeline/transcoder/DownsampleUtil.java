package com.facebook.imagepipeline.transcoder;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;

public class DownsampleUtil
{
  public static final int DEFAULT_SAMPLE_SIZE = 1;
  private static final float INTERVAL_ROUNDING = 0.33333334F;
  
  private DownsampleUtil() {}
  
  public static float determineDownsampleRatio(RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions, EncodedImage paramEncodedImage)
  {
    Preconditions.checkArgument(EncodedImage.isMetaDataAvailable(paramEncodedImage));
    if ((paramResizeOptions != null) && (height > 0) && (width > 0) && (paramEncodedImage.getWidth() != 0) && (paramEncodedImage.getHeight() != 0))
    {
      int i = getRotationAngle(paramRotationOptions, paramEncodedImage);
      int j;
      if ((i != 90) && (i != 270)) {
        j = 0;
      } else {
        j = 1;
      }
      if (j != 0) {
        i = paramEncodedImage.getHeight();
      } else {
        i = paramEncodedImage.getWidth();
      }
      if (j != 0) {
        j = paramEncodedImage.getWidth();
      } else {
        j = paramEncodedImage.getHeight();
      }
      float f1 = width / i;
      float f2 = height / j;
      float f3 = Math.max(f1, f2);
      FLog.v("DownsampleUtil", "Downsample - Specified size: %dx%d, image size: %dx%d ratio: %.1f x %.1f, ratio: %.3f", new Object[] { Integer.valueOf(width), Integer.valueOf(height), Integer.valueOf(i), Integer.valueOf(j), Float.valueOf(f1), Float.valueOf(f2), Float.valueOf(f3) });
      return f3;
    }
    return 1.0F;
  }
  
  public static int determineSampleSize(RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions, EncodedImage paramEncodedImage, int paramInt)
  {
    if (!EncodedImage.isMetaDataAvailable(paramEncodedImage)) {
      return 1;
    }
    float f = determineDownsampleRatio(paramRotationOptions, paramResizeOptions, paramEncodedImage);
    int i;
    if (paramEncodedImage.getImageFormat() == DefaultImageFormats.JPEG) {
      i = ratioToSampleSizeJPEG(f);
    } else {
      i = ratioToSampleSize(f);
    }
    int j = Math.max(paramEncodedImage.getHeight(), paramEncodedImage.getWidth());
    if (paramResizeOptions != null) {
      f = maxBitmapSize;
    } else {
      f = paramInt;
    }
    while (j / i > f) {
      if (paramEncodedImage.getImageFormat() == DefaultImageFormats.JPEG) {
        i *= 2;
      } else {
        i += 1;
      }
    }
    return i;
  }
  
  private static int getRotationAngle(RotationOptions paramRotationOptions, EncodedImage paramEncodedImage)
  {
    boolean bool2 = paramRotationOptions.useImageMetadata();
    boolean bool1 = false;
    if (!bool2) {
      return 0;
    }
    int i = paramEncodedImage.getRotationAngle();
    if ((i == 0) || (i == 90) || (i == 180) || (i == 270)) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    return i;
  }
  
  public static int ratioToSampleSize(float paramFloat)
  {
    if (paramFloat > 0.6666667F) {
      return 1;
    }
    int i = 2;
    for (;;)
    {
      double d1 = i;
      double d2 = 1.0D / (Math.pow(d1, 2.0D) - d1);
      if (1.0D / d1 + d2 * 0.3333333432674408D <= paramFloat) {
        return i - 1;
      }
      i += 1;
    }
  }
  
  public static int ratioToSampleSizeJPEG(float paramFloat)
  {
    if (paramFloat > 0.6666667F) {
      return 1;
    }
    int j;
    for (int i = 2;; i = j)
    {
      j = i * 2;
      double d = 1.0D / j;
      if (d + 0.3333333432674408D * d <= paramFloat) {
        return i;
      }
    }
  }
  
  public static int roundToPowerOfTwo(int paramInt)
  {
    int i = 1;
    for (;;)
    {
      if (i >= paramInt) {
        return i;
      }
      i *= 2;
    }
  }
}