package com.facebook.imagepipeline.common;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.util.HashCodeUtil;

public class ResizeOptions
{
  public static final float DEFAULT_ROUNDUP_FRACTION = 0.6666667F;
  public final int height;
  public final float maxBitmapSize;
  public final float roundUpFraction;
  public final int width;
  
  public ResizeOptions(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 2048.0F);
  }
  
  public ResizeOptions(int paramInt1, int paramInt2, float paramFloat)
  {
    this(paramInt1, paramInt2, paramFloat, 0.6666667F);
  }
  
  public ResizeOptions(int paramInt1, int paramInt2, float paramFloat1, float paramFloat2)
  {
    boolean bool2 = false;
    if (paramInt1 > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt2 > 0) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    width = paramInt1;
    height = paramInt2;
    maxBitmapSize = paramFloat1;
    roundUpFraction = paramFloat2;
  }
  
  public static ResizeOptions forDimensions(int paramInt1, int paramInt2)
  {
    if ((paramInt1 > 0) && (paramInt2 > 0)) {
      return new ResizeOptions(paramInt1, paramInt2);
    }
    return null;
  }
  
  public static ResizeOptions forSquareSize(int paramInt)
  {
    if (paramInt <= 0) {
      return null;
    }
    return new ResizeOptions(paramInt, paramInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if (!(paramObject instanceof ResizeOptions)) {
      return false;
    }
    paramObject = (ResizeOptions)paramObject;
    return (width == width) && (height == height);
  }
  
  public int hashCode()
  {
    return HashCodeUtil.hashCode(width, height);
  }
  
  public String toString()
  {
    return String.format(null, "%dx%d", new Object[] { Integer.valueOf(width), Integer.valueOf(height) });
  }
}