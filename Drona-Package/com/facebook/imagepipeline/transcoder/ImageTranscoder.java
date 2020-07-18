package com.facebook.imagepipeline.transcoder;

import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import java.io.IOException;
import java.io.OutputStream;

public abstract interface ImageTranscoder
{
  public abstract boolean canResize(EncodedImage paramEncodedImage, RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions);
  
  public abstract boolean canTranscode(ImageFormat paramImageFormat);
  
  public abstract String getIdentifier();
  
  public abstract ImageTranscodeResult transcode(EncodedImage paramEncodedImage, OutputStream paramOutputStream, RotationOptions paramRotationOptions, ResizeOptions paramResizeOptions, ImageFormat paramImageFormat, Integer paramInteger)
    throws IOException;
}