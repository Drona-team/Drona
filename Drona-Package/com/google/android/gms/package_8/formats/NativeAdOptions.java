package com.google.android.gms.package_8.formats;

import com.google.android.gms.internal.ads.zzard;
import com.google.android.gms.package_8.VideoOptions;
import java.lang.annotation.Annotation;

@zzard
public final class NativeAdOptions
{
  public static final int ADCHOICES_BOTTOM_LEFT = 3;
  public static final int ADCHOICES_BOTTOM_RIGHT = 2;
  public static final int ADCHOICES_TOP_LEFT = 0;
  public static final int ADCHOICES_TOP_RIGHT = 1;
  public static final int ORIENTATION_ANY = 0;
  public static final int ORIENTATION_LANDSCAPE = 2;
  public static final int ORIENTATION_PORTRAIT = 1;
  private final boolean zzbqb;
  private final int zzbqc;
  private final int zzbqd;
  private final boolean zzbqe;
  private final int zzbqf;
  private final VideoOptions zzbqg;
  private final boolean zzbqh;
  
  private NativeAdOptions(Builder paramBuilder)
  {
    zzbqb = Builder.s(paramBuilder);
    zzbqc = Builder.getTransfer(paramBuilder);
    zzbqd = 0;
    zzbqe = Builder.getSoundPath(paramBuilder);
    zzbqf = Builder.getUnreadMessagesCount(paramBuilder);
    zzbqg = Builder.newFromParcel(paramBuilder);
    zzbqh = Builder.getArticleUrl(paramBuilder);
  }
  
  public final int getAdChoicesPlacement()
  {
    return zzbqf;
  }
  
  public final int getImageOrientation()
  {
    return zzbqc;
  }
  
  public final VideoOptions getVideoOptions()
  {
    return zzbqg;
  }
  
  public final boolean shouldRequestMultipleImages()
  {
    return zzbqe;
  }
  
  public final boolean shouldReturnUrlsForImageAssets()
  {
    return zzbqb;
  }
  
  public final boolean zzkr()
  {
    return zzbqh;
  }
  
  public @interface AdChoicesPlacement {}
  
  public final class Builder
  {
    private boolean zzbqb = false;
    private int zzbqc = -1;
    private int zzbqd = 0;
    private boolean zzbqe = false;
    private int zzbqf = 1;
    private VideoOptions zzbqg;
    private boolean zzbqh = false;
    
    public Builder() {}
    
    public final NativeAdOptions build()
    {
      return new NativeAdOptions(this, null);
    }
    
    public final Builder setAdChoicesPlacement(int paramInt)
    {
      zzbqf = paramInt;
      return this;
    }
    
    public final Builder setImageOrientation(int paramInt)
    {
      zzbqc = paramInt;
      return this;
    }
    
    public final Builder setRequestCustomMuteThisAd(boolean paramBoolean)
    {
      zzbqh = paramBoolean;
      return this;
    }
    
    public final Builder setRequestMultipleImages(boolean paramBoolean)
    {
      zzbqe = paramBoolean;
      return this;
    }
    
    public final Builder setReturnUrlsForImageAssets(boolean paramBoolean)
    {
      zzbqb = paramBoolean;
      return this;
    }
    
    public final Builder setVideoOptions(VideoOptions paramVideoOptions)
    {
      zzbqg = paramVideoOptions;
      return this;
    }
  }
}
