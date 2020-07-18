package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap.Config;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.util.Preconditions;

public final class PreFillType
{
  @VisibleForTesting
  static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.RGB_565;
  private final Bitmap.Config config;
  private final int height;
  private final int weight;
  private final int width;
  
  PreFillType(int paramInt1, int paramInt2, Bitmap.Config paramConfig, int paramInt3)
  {
    config = ((Bitmap.Config)Preconditions.checkNotNull(paramConfig, "Config must not be null"));
    width = paramInt1;
    height = paramInt2;
    weight = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof PreFillType))
    {
      paramObject = (PreFillType)paramObject;
      if ((height == height) && (width == width) && (weight == weight) && (config == config)) {
        return true;
      }
    }
    return false;
  }
  
  Bitmap.Config getConfig()
  {
    return config;
  }
  
  int getHeight()
  {
    return height;
  }
  
  int getWeight()
  {
    return weight;
  }
  
  int getWidth()
  {
    return width;
  }
  
  public int hashCode()
  {
    return ((width * 31 + height) * 31 + config.hashCode()) * 31 + weight;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("PreFillSize{width=");
    localStringBuilder.append(width);
    localStringBuilder.append(", height=");
    localStringBuilder.append(height);
    localStringBuilder.append(", config=");
    localStringBuilder.append(config);
    localStringBuilder.append(", weight=");
    localStringBuilder.append(weight);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public static class Builder
  {
    private Bitmap.Config config;
    private final int height;
    private int weight = 1;
    private final int width;
    
    public Builder(int paramInt)
    {
      this(paramInt, paramInt);
    }
    
    public Builder(int paramInt1, int paramInt2)
    {
      if (paramInt1 > 0)
      {
        if (paramInt2 > 0)
        {
          width = paramInt1;
          height = paramInt2;
          return;
        }
        throw new IllegalArgumentException("Height must be > 0");
      }
      throw new IllegalArgumentException("Width must be > 0");
    }
    
    PreFillType build()
    {
      return new PreFillType(width, height, config, weight);
    }
    
    Bitmap.Config getConfig()
    {
      return config;
    }
    
    public Builder setConfig(Bitmap.Config paramConfig)
    {
      config = paramConfig;
      return this;
    }
    
    public Builder setWeight(int paramInt)
    {
      if (paramInt > 0)
      {
        weight = paramInt;
        return this;
      }
      throw new IllegalArgumentException("Weight must be > 0");
    }
  }
}
