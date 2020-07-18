package com.google.android.gms.package_8;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.common.util.VisibleForTesting;
import com.google.android.gms.internal.ads.zzazt;
import com.google.android.gms.internal.ads.zzyd;
import com.google.android.gms.internal.ads.zzyt;

@VisibleForTesting
public final class AdSize
{
  public static final int AUTO_HEIGHT = -2;
  public static final AdSize BANNER = new AdSize(320, 50, "320x50_mb");
  public static final AdSize FLUID;
  public static final AdSize FULL_BANNER = new AdSize(468, 60, "468x60_as");
  public static final int FULL_WIDTH = -1;
  public static final AdSize LARGE_BANNER = new AdSize(320, 100, "320x100_as");
  public static final AdSize LEADERBOARD = new AdSize(728, 90, "728x90_as");
  public static final AdSize MEDIUM_RECTANGLE = new AdSize(300, 250, "300x250_as");
  public static final AdSize SEARCH = new AdSize(-3, 0, "search_v2");
  public static final AdSize SMART_BANNER;
  public static final AdSize WIDE_SKYSCRAPER = new AdSize(160, 600, "160x600_as");
  public static final AdSize zzaao;
  private final int height;
  private final int width;
  private final String zzaap;
  
  static
  {
    SMART_BANNER = new AdSize(-1, -2, "smart_banner");
    FLUID = new AdSize(-3, -4, "fluid");
    zzaao = new AdSize(50, 50, "50x50_mb");
  }
  
  public AdSize(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, localStringBuilder.toString());
  }
  
  AdSize(int paramInt1, int paramInt2, String paramString)
  {
    if ((paramInt1 < 0) && (paramInt1 != -1) && (paramInt1 != -3))
    {
      paramString = new StringBuilder(37);
      paramString.append("Invalid width for AdSize: ");
      paramString.append(paramInt1);
      throw new IllegalArgumentException(paramString.toString());
    }
    if ((paramInt2 < 0) && (paramInt2 != -2) && (paramInt2 != -4))
    {
      paramString = new StringBuilder(38);
      paramString.append("Invalid height for AdSize: ");
      paramString.append(paramInt2);
      throw new IllegalArgumentException(paramString.toString());
    }
    width = paramInt1;
    height = paramInt2;
    zzaap = paramString;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof AdSize)) {
      return false;
    }
    paramObject = (AdSize)paramObject;
    return (width == width) && (height == height) && (zzaap.equals(zzaap));
  }
  
  public final int getHeight()
  {
    return height;
  }
  
  public final int getHeightInPixels(Context paramContext)
  {
    switch (height)
    {
    default: 
      zzyt.zzpa();
      return zzazt.zza(paramContext, height);
    case -2: 
      return zzyd.zzc(paramContext.getResources().getDisplayMetrics());
    }
    return -1;
  }
  
  public final int getWidth()
  {
    return width;
  }
  
  public final int getWidthInPixels(Context paramContext)
  {
    int i = width;
    if (i != -1)
    {
      switch (i)
      {
      default: 
        zzyt.zzpa();
        return zzazt.zza(paramContext, width);
      }
      return -1;
    }
    return zzyd.zzb(paramContext.getResources().getDisplayMetrics());
  }
  
  public final int hashCode()
  {
    return zzaap.hashCode();
  }
  
  public final boolean isAutoHeight()
  {
    return height == -2;
  }
  
  public final boolean isFluid()
  {
    return (width == -3) && (height == -4);
  }
  
  public final boolean isFullWidth()
  {
    return width == -1;
  }
  
  public final String toString()
  {
    return zzaap;
  }
}
