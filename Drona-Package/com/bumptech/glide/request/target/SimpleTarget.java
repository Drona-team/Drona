package com.bumptech.glide.request.target;

import com.bumptech.glide.util.Util;

@Deprecated
public abstract class SimpleTarget<Z>
  extends BaseTarget<Z>
{
  private final int height;
  private final int width;
  
  public SimpleTarget()
  {
    this(Integer.MIN_VALUE, Integer.MIN_VALUE);
  }
  
  public SimpleTarget(int paramInt1, int paramInt2)
  {
    width = paramInt1;
    height = paramInt2;
  }
  
  public final void getSize(SizeReadyCallback paramSizeReadyCallback)
  {
    if (Util.isValidDimensions(width, height))
    {
      paramSizeReadyCallback.onSizeReady(width, height);
      return;
    }
    paramSizeReadyCallback = new StringBuilder();
    paramSizeReadyCallback.append("Width and height must both be > 0 or Target#SIZE_ORIGINAL, but given width: ");
    paramSizeReadyCallback.append(width);
    paramSizeReadyCallback.append(" and height: ");
    paramSizeReadyCallback.append(height);
    paramSizeReadyCallback.append(", either provide dimensions in the constructor or call override()");
    throw new IllegalArgumentException(paramSizeReadyCallback.toString());
  }
  
  public void removeCallback(SizeReadyCallback paramSizeReadyCallback) {}
}
