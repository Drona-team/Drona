package com.google.ads;

import android.content.Context;

@Deprecated
public final class AdSize
{
  public static final int AUTO_HEIGHT = -2;
  public static final AdSize BANNER;
  public static final int FULL_WIDTH = -1;
  public static final AdSize IAB_BANNER = new AdSize(468, 60, "as");
  public static final AdSize IAB_LEADERBOARD = new AdSize(728, 90, "as");
  public static final AdSize IAB_MRECT;
  public static final AdSize IAB_WIDE_SKYSCRAPER = new AdSize(160, 600, "as");
  public static final int LANDSCAPE_AD_HEIGHT = 32;
  public static final int LARGE_AD_HEIGHT = 90;
  public static final int PORTRAIT_AD_HEIGHT = 50;
  public static final AdSize SMART_BANNER = new AdSize(-1, -2, "mb");
  private final com.google.android.gms.package_8.AdSize zzdh;
  
  static
  {
    BANNER = new AdSize(320, 50, "mb");
    IAB_MRECT = new AdSize(300, 250, "as");
  }
  
  public AdSize(int paramInt1, int paramInt2)
  {
    this(new com.google.android.gms.package_8.AdSize(paramInt1, paramInt2));
  }
  
  private AdSize(int paramInt1, int paramInt2, String paramString)
  {
    this(new com.google.android.gms.package_8.AdSize(paramInt1, paramInt2));
  }
  
  public AdSize(com.google.android.gms.package_8.AdSize paramAdSize)
  {
    zzdh = paramAdSize;
  }
  
  public final boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof AdSize)) {
      return false;
    }
    paramObject = (AdSize)paramObject;
    return zzdh.equals(zzdh);
  }
  
  public final AdSize findBestSize(AdSize... paramVarArgs)
  {
    Object localObject1 = null;
    if (paramVarArgs == null) {
      return null;
    }
    float f2 = 0.0F;
    int j = getWidth();
    int k = getHeight();
    int m = paramVarArgs.length;
    int i = 0;
    while (i < m)
    {
      AdSize localAdSize = paramVarArgs[i];
      int n = localAdSize.getWidth();
      int i1 = localAdSize.getHeight();
      Object localObject2 = localObject1;
      float f3 = f2;
      if (isSizeAppropriate(n, i1))
      {
        f3 = n * i1 / (j * k);
        float f1 = f3;
        if (f3 > 1.0F) {
          f1 = 1.0F / f3;
        }
        localObject2 = localObject1;
        f3 = f2;
        if (f1 > f2)
        {
          localObject2 = localAdSize;
          f3 = f1;
        }
      }
      i += 1;
      localObject1 = localObject2;
      f2 = f3;
    }
    return localObject1;
  }
  
  public final int getHeight()
  {
    return zzdh.getHeight();
  }
  
  public final int getHeightInPixels(Context paramContext)
  {
    return zzdh.getHeightInPixels(paramContext);
  }
  
  public final int getWidth()
  {
    return zzdh.getWidth();
  }
  
  public final int getWidthInPixels(Context paramContext)
  {
    return zzdh.getWidthInPixels(paramContext);
  }
  
  public final int hashCode()
  {
    return zzdh.hashCode();
  }
  
  public final boolean isAutoHeight()
  {
    return zzdh.isAutoHeight();
  }
  
  public final boolean isCustomAdSize()
  {
    return false;
  }
  
  public final boolean isFullWidth()
  {
    return zzdh.isFullWidth();
  }
  
  public final boolean isSizeAppropriate(int paramInt1, int paramInt2)
  {
    int i = getWidth();
    int j = getHeight();
    float f1 = paramInt1;
    float f2 = i;
    if ((f1 <= f2 * 1.25F) && (f1 >= f2 * 0.8F))
    {
      f1 = paramInt2;
      f2 = j;
      if ((f1 <= 1.25F * f2) && (f1 >= f2 * 0.8F)) {
        return true;
      }
    }
    return false;
  }
  
  public final String toString()
  {
    return zzdh.toString();
  }
}
