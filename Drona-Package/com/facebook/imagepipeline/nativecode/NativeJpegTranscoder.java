package com.facebook.imagepipeline.nativecode;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.internal.Preconditions;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import com.facebook.imagepipeline.transcoder.ImageTranscodeResult;
import com.facebook.imagepipeline.transcoder.ImageTranscoder;
import com.facebook.imagepipeline.transcoder.JpegTranscoderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

@DoNotStrip
public class NativeJpegTranscoder
  implements ImageTranscoder
{
  public static final String PAGE_KEY = "NativeJpegTranscoder";
  private int mMaxBitmapSize;
  private boolean mResizingEnabled;
  private boolean mUseDownsamplingRatio;
  
  static {}
  
  public NativeJpegTranscoder(boolean paramBoolean1, int paramInt, boolean paramBoolean2)
  {
    mResizingEnabled = paramBoolean1;
    mMaxBitmapSize = paramInt;
    mUseDownsamplingRatio = paramBoolean2;
  }
  
  private static native void nativeTranscodeJpeg(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt1, int paramInt2, int paramInt3)
    throws IOException;
  
  private static native void nativeTranscodeJpegWithExifOrientation(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt1, int paramInt2, int paramInt3)
    throws IOException;
  
  public static void transcodeJpeg(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    NativeJpegTranscoderSoLoader.ensure();
    boolean bool2 = false;
    boolean bool1;
    if (paramInt2 >= 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt2 <= 16) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt3 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt3 <= 100) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    Preconditions.checkArgument(JpegTranscoderUtils.isRotationAngleAllowed(paramInt1));
    if (paramInt2 == 8)
    {
      bool1 = bool2;
      if (paramInt1 == 0) {}
    }
    else
    {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1, "no transformation requested");
    nativeTranscodeJpeg((InputStream)Preconditions.checkNotNull(paramInputStream), (OutputStream)Preconditions.checkNotNull(paramOutputStream), paramInt1, paramInt2, paramInt3);
  }
  
  public static void transcodeJpegWithExifOrientation(InputStream paramInputStream, OutputStream paramOutputStream, int paramInt1, int paramInt2, int paramInt3)
    throws IOException
  {
    NativeJpegTranscoderSoLoader.ensure();
    boolean bool2 = false;
    boolean bool1;
    if (paramInt2 >= 1) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt2 <= 16) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt3 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    if (paramInt3 <= 100) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    Preconditions.checkArgument(JpegTranscoderUtils.isExifOrientationAllowed(paramInt1));
    if (paramInt2 == 8)
    {
      bool1 = bool2;
      if (paramInt1 == 1) {}
    }
    else
    {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1, "no transformation requested");
    nativeTranscodeJpegWithExifOrientation((InputStream)Preconditions.checkNotNull(paramInputStream), (OutputStream)Preconditions.checkNotNull(paramOutputStream), paramInt1, paramInt2, paramInt3);
  }
  
  public boolean canResize(EncodedImage paramEncodedImage, RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions)
  {
    RotationOptions localRotationOptions = paramRotationOptions;
    if (paramRotationOptions == null) {
      localRotationOptions = RotationOptions.autoRotate();
    }
    return JpegTranscoderUtils.getSoftwareNumerator(localRotationOptions, paramResizeOptions, paramEncodedImage, mResizingEnabled) < 8;
  }
  
  public boolean canTranscode(ImageFormat paramImageFormat)
  {
    return paramImageFormat == DefaultImageFormats.JPEG;
  }
  
  public String getIdentifier()
  {
    return "NativeJpegTranscoder";
  }
  
  public ImageTranscodeResult transcode(EncodedImage paramEncodedImage, OutputStream paramOutputStream, RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions, ImageFormat paramImageFormat, Integer paramInteger)
    throws IOException
  {
    paramImageFormat = paramInteger;
    if (paramInteger == null) {
      paramImageFormat = Integer.valueOf(85);
    }
    paramInteger = paramRotationOptions;
    if (paramRotationOptions == null) {
      paramInteger = RotationOptions.autoRotate();
    }
    int k = DownsampleUtil.determineSampleSize(paramInteger, paramResizeOptions, paramEncodedImage, mMaxBitmapSize);
    try
    {
      int i = JpegTranscoderUtils.getSoftwareNumerator(paramInteger, paramResizeOptions, paramEncodedImage, mResizingEnabled);
      int j = JpegTranscoderUtils.calculateDownsampleNumerator(k);
      boolean bool = mUseDownsamplingRatio;
      if (bool) {
        i = j;
      }
      paramResizeOptions = paramEncodedImage.getInputStream();
      paramRotationOptions = paramResizeOptions;
      try
      {
        bool = JpegTranscoderUtils.INVERTED_EXIF_ORIENTATIONS.contains(Integer.valueOf(paramEncodedImage.getExifOrientation()));
        if (bool) {
          transcodeJpegWithExifOrientation(paramResizeOptions, paramOutputStream, JpegTranscoderUtils.getForceRotatedInvertedExifOrientation(paramInteger, paramEncodedImage), i, paramImageFormat.intValue());
        } else {
          transcodeJpeg(paramResizeOptions, paramOutputStream, JpegTranscoderUtils.getRotationAngle(paramInteger, paramEncodedImage), i, paramImageFormat.intValue());
        }
        Closeables.closeQuietly(paramResizeOptions);
        i = 1;
        if (k != 1) {
          i = 0;
        }
        return new ImageTranscodeResult(i);
      }
      catch (Throwable paramEncodedImage) {}
      Closeables.closeQuietly(paramRotationOptions);
    }
    catch (Throwable paramEncodedImage)
    {
      paramRotationOptions = null;
    }
    throw paramEncodedImage;
  }
}
