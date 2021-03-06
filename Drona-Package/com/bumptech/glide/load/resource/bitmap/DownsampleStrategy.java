package com.bumptech.glide.load.resource.bitmap;

import com.bumptech.glide.load.Option;

public abstract class DownsampleStrategy
{
  public static final DownsampleStrategy AT_LEAST;
  public static final DownsampleStrategy AT_MOST;
  public static final DownsampleStrategy CENTER_INSIDE;
  public static final DownsampleStrategy CENTER_OUTSIDE;
  public static final DownsampleStrategy DEFAULT = CENTER_OUTSIDE;
  public static final DownsampleStrategy FIT_CENTER = new FitCenter();
  public static final DownsampleStrategy NONE;
  public static final Option<DownsampleStrategy> OPTION = Option.memory("com.bumptech.glide.load.resource.bitmap.Downsampler.DownsampleStrategy", DEFAULT);
  
  static
  {
    CENTER_OUTSIDE = new CenterOutside();
    AT_LEAST = new AtLeast();
    AT_MOST = new AtMost();
    CENTER_INSIDE = new CenterInside();
    NONE = new None();
  }
  
  public DownsampleStrategy() {}
  
  public abstract SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  private static class AtLeast
    extends DownsampleStrategy
  {
    AtLeast() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt1 = Math.min(paramInt2 / paramInt4, paramInt1 / paramInt3);
      if (paramInt1 == 0) {
        return 1.0F;
      }
      return 1.0F / Integer.highestOneBit(paramInt1);
    }
  }
  
  private static class AtMost
    extends DownsampleStrategy
  {
    AtMost() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.MEMORY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      paramInt3 = (int)Math.ceil(Math.max(paramInt2 / paramInt4, paramInt1 / paramInt3));
      paramInt2 = Integer.highestOneBit(paramInt3);
      paramInt1 = 1;
      paramInt2 = Math.max(1, paramInt2);
      if (paramInt2 >= paramInt3) {
        paramInt1 = 0;
      }
      return 1.0F / (paramInt2 << paramInt1);
    }
  }
  
  private static class CenterInside
    extends DownsampleStrategy
  {
    CenterInside() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.min(1.0F, DownsampleStrategy.FIT_CENTER.getScaleFactor(paramInt1, paramInt2, paramInt3, paramInt4));
    }
  }
  
  private static class CenterOutside
    extends DownsampleStrategy
  {
    CenterOutside() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.max(paramInt3 / paramInt1, paramInt4 / paramInt2);
    }
  }
  
  private static class FitCenter
    extends DownsampleStrategy
  {
    FitCenter() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return Math.min(paramInt3 / paramInt1, paramInt4 / paramInt2);
    }
  }
  
  private static class None
    extends DownsampleStrategy
  {
    None() {}
    
    public DownsampleStrategy.SampleSizeRounding getSampleSizeRounding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return DownsampleStrategy.SampleSizeRounding.QUALITY;
    }
    
    public float getScaleFactor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return 1.0F;
    }
  }
  
  public static enum SampleSizeRounding
  {
    MEMORY,  QUALITY;
  }
}
