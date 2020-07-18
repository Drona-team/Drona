package com.facebook.imageformat;

import com.facebook.common.internal.ImmutableList;
import java.util.ArrayList;
import java.util.List;

public final class DefaultImageFormats
{
  public static final ImageFormat BMP;
  public static final ImageFormat GIF;
  public static final ImageFormat HEIF = new ImageFormat("HEIF", "heif");
  public static final ImageFormat JPEG = new ImageFormat("JPEG", "jpeg");
  public static final ImageFormat PNG = new ImageFormat("PNG", "png");
  public static final ImageFormat UNKNOWN = new ImageFormat("GIF", "gif");
  public static final ImageFormat WEBP_ANIMATED;
  public static final ImageFormat WEBP_EXTENDED;
  public static final ImageFormat WEBP_EXTENDED_WITH_ALPHA;
  public static final ImageFormat WEBP_LOSSLESS;
  public static final ImageFormat WEBP_SIMPLE;
  private static ImmutableList<ImageFormat> sAllDefaultFormats;
  
  static
  {
    BMP = new ImageFormat("BMP", "bmp");
    GIF = new ImageFormat("ICO", "ico");
    WEBP_SIMPLE = new ImageFormat("WEBP_SIMPLE", "webp");
    WEBP_LOSSLESS = new ImageFormat("WEBP_LOSSLESS", "webp");
    WEBP_EXTENDED = new ImageFormat("WEBP_EXTENDED", "webp");
    WEBP_EXTENDED_WITH_ALPHA = new ImageFormat("WEBP_EXTENDED_WITH_ALPHA", "webp");
    WEBP_ANIMATED = new ImageFormat("WEBP_ANIMATED", "webp");
  }
  
  private DefaultImageFormats() {}
  
  public static List getDefaultFormats()
  {
    if (sAllDefaultFormats == null)
    {
      ArrayList localArrayList = new ArrayList(9);
      localArrayList.add(JPEG);
      localArrayList.add(PNG);
      localArrayList.add(UNKNOWN);
      localArrayList.add(BMP);
      localArrayList.add(GIF);
      localArrayList.add(WEBP_SIMPLE);
      localArrayList.add(WEBP_LOSSLESS);
      localArrayList.add(WEBP_EXTENDED);
      localArrayList.add(WEBP_EXTENDED_WITH_ALPHA);
      localArrayList.add(WEBP_ANIMATED);
      localArrayList.add(HEIF);
      sAllDefaultFormats = ImmutableList.copyOf(localArrayList);
    }
    return sAllDefaultFormats;
  }
  
  public static boolean isStaticWebpFormat(ImageFormat paramImageFormat)
  {
    return (paramImageFormat == WEBP_SIMPLE) || (paramImageFormat == WEBP_LOSSLESS) || (paramImageFormat == WEBP_EXTENDED) || (paramImageFormat == WEBP_EXTENDED_WITH_ALPHA);
  }
  
  public static boolean isWebpFormat(ImageFormat paramImageFormat)
  {
    return (isStaticWebpFormat(paramImageFormat)) || (paramImageFormat == WEBP_ANIMATED);
  }
}
