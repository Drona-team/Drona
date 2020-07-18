package com.facebook.imagepipeline.common;

import android.graphics.Bitmap.Config;
import android.graphics.ColorSpace;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.transformation.BitmapTransformation;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ImageDecodeOptions
{
  private static final ImageDecodeOptions DEFAULTS = newBuilder().build();
  public final Bitmap.Config bitmapConfig;
  @Nullable
  public final BitmapTransformation bitmapTransformation;
  @Nullable
  public final ColorSpace colorSpace;
  @Nullable
  public final ImageDecoder customImageDecoder;
  public final boolean decodeAllFrames;
  public final boolean decodePreviewFrame;
  public final boolean forceStaticImage;
  public final int minDecodeIntervalMs;
  public final boolean useLastFrameForPreview;
  
  public ImageDecodeOptions(ImageDecodeOptionsBuilder paramImageDecodeOptionsBuilder)
  {
    minDecodeIntervalMs = paramImageDecodeOptionsBuilder.getMinDecodeIntervalMs();
    decodePreviewFrame = paramImageDecodeOptionsBuilder.getDecodePreviewFrame();
    useLastFrameForPreview = paramImageDecodeOptionsBuilder.getUseLastFrameForPreview();
    decodeAllFrames = paramImageDecodeOptionsBuilder.getDecodeAllFrames();
    forceStaticImage = paramImageDecodeOptionsBuilder.getForceStaticImage();
    bitmapConfig = paramImageDecodeOptionsBuilder.getBitmapConfig();
    customImageDecoder = paramImageDecodeOptionsBuilder.getCustomImageDecoder();
    bitmapTransformation = paramImageDecodeOptionsBuilder.getBitmapTransformation();
    colorSpace = paramImageDecodeOptionsBuilder.getColorSpace();
  }
  
  public static ImageDecodeOptions defaults()
  {
    return DEFAULTS;
  }
  
  public static ImageDecodeOptionsBuilder newBuilder()
  {
    return new ImageDecodeOptionsBuilder();
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (ImageDecodeOptions)paramObject;
      if (decodePreviewFrame != decodePreviewFrame) {
        return false;
      }
      if (useLastFrameForPreview != useLastFrameForPreview) {
        return false;
      }
      if (decodeAllFrames != decodeAllFrames) {
        return false;
      }
      if (forceStaticImage != forceStaticImage) {
        return false;
      }
      if (bitmapConfig != bitmapConfig) {
        return false;
      }
      if (customImageDecoder != customImageDecoder) {
        return false;
      }
      if (bitmapTransformation != bitmapTransformation) {
        return false;
      }
      return colorSpace == colorSpace;
    }
    return false;
  }
  
  public int hashCode()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  public String toString()
  {
    return String.format(null, "%d-%b-%b-%b-%b-%b-%s-%s-%s", new Object[] { Integer.valueOf(minDecodeIntervalMs), Boolean.valueOf(decodePreviewFrame), Boolean.valueOf(useLastFrameForPreview), Boolean.valueOf(decodeAllFrames), Boolean.valueOf(forceStaticImage), bitmapConfig.name(), customImageDecoder, bitmapTransformation, colorSpace });
  }
}