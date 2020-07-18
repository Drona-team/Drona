package com.facebook.imagepipeline.transcoder;

import android.graphics.Matrix;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import java.util.ArrayList;

public class JpegTranscoderUtils
{
  @VisibleForTesting
  public static final int DEFAULT_JPEG_QUALITY = 85;
  private static final int FULL_ROUND = 360;
  public static final ImmutableList<Integer> INVERTED_EXIF_ORIENTATIONS = ImmutableList.of(new Integer[] { Integer.valueOf(2), Integer.valueOf(7), Integer.valueOf(4), Integer.valueOf(5) });
  public static final int MAX_QUALITY = 100;
  public static final int MAX_SCALE_NUMERATOR = 16;
  public static final int MIN_QUALITY = 0;
  public static final int MIN_SCALE_NUMERATOR = 1;
  public static final int SCALE_DENOMINATOR = 8;
  
  public JpegTranscoderUtils() {}
  
  public static int calculateDownsampleNumerator(int paramInt)
  {
    return Math.max(1, 8 / paramInt);
  }
  
  public static float determineResizeRatio(ResizeOptions paramResizeOptions, int paramInt1, int paramInt2)
  {
    if (paramResizeOptions == null) {
      return 1.0F;
    }
    float f1 = width;
    float f3 = paramInt1;
    f1 /= f3;
    float f2 = height;
    float f4 = paramInt2;
    f2 = Math.max(f1, f2 / f4);
    f1 = f2;
    if (f3 * f2 > maxBitmapSize) {
      f1 = maxBitmapSize / f3;
    }
    if (f4 * f1 > maxBitmapSize) {
      return maxBitmapSize / f4;
    }
    return f1;
  }
  
  private static int extractOrientationFromMetadata(EncodedImage paramEncodedImage)
  {
    int i = paramEncodedImage.getRotationAngle();
    if ((i != 90) && (i != 180) && (i != 270)) {
      return 0;
    }
    return paramEncodedImage.getRotationAngle();
  }
  
  public static int getForceRotatedInvertedExifOrientation(RotationOptions paramRotationOptions, EncodedImage paramEncodedImage)
  {
    int i = paramEncodedImage.getExifOrientation();
    int j = INVERTED_EXIF_ORIENTATIONS.indexOf(Integer.valueOf(i));
    if (j >= 0)
    {
      i = 0;
      if (!paramRotationOptions.useImageMetadata()) {
        i = paramRotationOptions.getForcedAngle();
      }
      i /= 90;
      return ((Integer)INVERTED_EXIF_ORIENTATIONS.get((j + i) % INVERTED_EXIF_ORIENTATIONS.size())).intValue();
    }
    throw new IllegalArgumentException("Only accepts inverted exif orientations");
  }
  
  public static int getRotationAngle(RotationOptions paramRotationOptions, EncodedImage paramEncodedImage)
  {
    if (!paramRotationOptions.rotationEnabled()) {
      return 0;
    }
    int i = extractOrientationFromMetadata(paramEncodedImage);
    if (paramRotationOptions.useImageMetadata()) {
      return i;
    }
    return (i + paramRotationOptions.getForcedAngle()) % 360;
  }
  
  public static int getSoftwareNumerator(RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions, EncodedImage paramEncodedImage, boolean paramBoolean)
  {
    if (!paramBoolean) {
      return 8;
    }
    if (paramResizeOptions == null) {
      return 8;
    }
    int k = getRotationAngle(paramRotationOptions, paramEncodedImage);
    paramBoolean = INVERTED_EXIF_ORIENTATIONS.contains(Integer.valueOf(paramEncodedImage.getExifOrientation()));
    int i = 0;
    int j;
    if (paramBoolean) {
      j = getForceRotatedInvertedExifOrientation(paramRotationOptions, paramEncodedImage);
    } else {
      j = 0;
    }
    if ((k == 90) || (k == 270) || (j == 5) || (j == 7)) {
      i = 1;
    }
    if (i != 0) {
      j = paramEncodedImage.getHeight();
    } else {
      j = paramEncodedImage.getWidth();
    }
    if (i != 0) {
      i = paramEncodedImage.getWidth();
    } else {
      i = paramEncodedImage.getHeight();
    }
    i = roundNumerator(determineResizeRatio(paramResizeOptions, j, i), roundUpFraction);
    if (i > 8) {
      return 8;
    }
    if (i < 1) {
      return 1;
    }
    return i;
  }
  
  public static Matrix getTransformationMatrix(EncodedImage paramEncodedImage, RotationOptions paramRotationOptions)
  {
    if (INVERTED_EXIF_ORIENTATIONS.contains(Integer.valueOf(paramEncodedImage.getExifOrientation()))) {
      return getTransformationMatrixFromInvertedExif(getForceRotatedInvertedExifOrientation(paramRotationOptions, paramEncodedImage));
    }
    int i = getRotationAngle(paramRotationOptions, paramEncodedImage);
    if (i != 0)
    {
      paramEncodedImage = new Matrix();
      paramEncodedImage.setRotate(i);
      return paramEncodedImage;
    }
    return null;
  }
  
  private static Matrix getTransformationMatrixFromInvertedExif(int paramInt)
  {
    Matrix localMatrix = new Matrix();
    if (paramInt != 2)
    {
      if (paramInt != 7)
      {
        switch (paramInt)
        {
        default: 
          return null;
        case 5: 
          localMatrix.setRotate(90.0F);
          localMatrix.postScale(-1.0F, 1.0F);
          return localMatrix;
        }
        localMatrix.setRotate(180.0F);
        localMatrix.postScale(-1.0F, 1.0F);
        return localMatrix;
      }
      localMatrix.setRotate(-90.0F);
      localMatrix.postScale(-1.0F, 1.0F);
      return localMatrix;
    }
    localMatrix.setScale(-1.0F, 1.0F);
    return localMatrix;
  }
  
  public static boolean isExifOrientationAllowed(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public static boolean isRotationAngleAllowed(int paramInt)
  {
    return (paramInt >= 0) && (paramInt <= 270) && (paramInt % 90 == 0);
  }
  
  public static int roundNumerator(float paramFloat1, float paramFloat2)
  {
    return (int)(paramFloat2 + paramFloat1 * 8.0F);
  }
}