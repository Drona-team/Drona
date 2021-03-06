package com.facebook.imagepipeline.common;

import android.graphics.Bitmap.Config;
import android.graphics.ColorSpace;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.transformation.BitmapTransformation;
import javax.annotation.Nullable;

public class ImageDecodeOptionsBuilder
{
  private Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
  @Nullable
  private BitmapTransformation mBitmapTransformation;
  @Nullable
  private ColorSpace mColorSpace;
  @Nullable
  private ImageDecoder mCustomImageDecoder;
  private boolean mDecodeAllFrames;
  private boolean mDecodePreviewFrame;
  private boolean mForceStaticImage;
  private int mMinDecodeIntervalMs = 100;
  private boolean mUseLastFrameForPreview;
  
  public ImageDecodeOptionsBuilder() {}
  
  public ImageDecodeOptions build()
  {
    return new ImageDecodeOptions(this);
  }
  
  public Bitmap.Config getBitmapConfig()
  {
    return mBitmapConfig;
  }
  
  public BitmapTransformation getBitmapTransformation()
  {
    return mBitmapTransformation;
  }
  
  public ColorSpace getColorSpace()
  {
    return mColorSpace;
  }
  
  public ImageDecoder getCustomImageDecoder()
  {
    return mCustomImageDecoder;
  }
  
  public boolean getDecodeAllFrames()
  {
    return mDecodeAllFrames;
  }
  
  public boolean getDecodePreviewFrame()
  {
    return mDecodePreviewFrame;
  }
  
  public boolean getForceStaticImage()
  {
    return mForceStaticImage;
  }
  
  public int getMinDecodeIntervalMs()
  {
    return mMinDecodeIntervalMs;
  }
  
  public boolean getUseLastFrameForPreview()
  {
    return mUseLastFrameForPreview;
  }
  
  public ImageDecodeOptionsBuilder setBitmapConfig(Bitmap.Config paramConfig)
  {
    mBitmapConfig = paramConfig;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setBitmapTransformation(BitmapTransformation paramBitmapTransformation)
  {
    mBitmapTransformation = paramBitmapTransformation;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setColorSpace(ColorSpace paramColorSpace)
  {
    mColorSpace = paramColorSpace;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setCustomImageDecoder(ImageDecoder paramImageDecoder)
  {
    mCustomImageDecoder = paramImageDecoder;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setDecodeAllFrames(boolean paramBoolean)
  {
    mDecodeAllFrames = paramBoolean;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setDecodePreviewFrame(boolean paramBoolean)
  {
    mDecodePreviewFrame = paramBoolean;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setForceStaticImage(boolean paramBoolean)
  {
    mForceStaticImage = paramBoolean;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setFrom(ImageDecodeOptions paramImageDecodeOptions)
  {
    mDecodePreviewFrame = decodePreviewFrame;
    mUseLastFrameForPreview = useLastFrameForPreview;
    mDecodeAllFrames = decodeAllFrames;
    mForceStaticImage = forceStaticImage;
    mBitmapConfig = bitmapConfig;
    mCustomImageDecoder = customImageDecoder;
    mBitmapTransformation = bitmapTransformation;
    mColorSpace = colorSpace;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setMinDecodeIntervalMs(int paramInt)
  {
    mMinDecodeIntervalMs = paramInt;
    return this;
  }
  
  public ImageDecodeOptionsBuilder setUseLastFrameForPreview(boolean paramBoolean)
  {
    mUseLastFrameForPreview = paramBoolean;
    return this;
  }
}
