package com.facebook.react.fabric.mounting.mountitems;

import com.facebook.react.fabric.mounting.MountingManager;

public class UpdateLayoutMountItem
  implements MountItem
{
  private final int mHeight;
  private final int mLayoutDirection;
  private final int mReactTag;
  private final int mWidth;
  private final int x;
  private final int y;
  
  public UpdateLayoutMountItem(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    mReactTag = paramInt1;
    x = paramInt2;
    y = paramInt3;
    mWidth = paramInt4;
    mHeight = paramInt5;
    mLayoutDirection = convertLayoutDirection(paramInt6);
  }
  
  private int convertLayoutDirection(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unsupported layout direction: ");
      localStringBuilder.append(paramInt);
      throw new IllegalArgumentException(localStringBuilder.toString());
    case 2: 
      return 1;
    case 1: 
      return 0;
    }
    return 2;
  }
  
  public void execute(MountingManager paramMountingManager)
  {
    paramMountingManager.updateLayout(mReactTag, x, y, mWidth, mHeight);
  }
  
  public int getHeight()
  {
    return mHeight;
  }
  
  public int getLayoutDirection()
  {
    return mLayoutDirection;
  }
  
  public int getWidth()
  {
    return mWidth;
  }
  
  public int getX()
  {
    return x;
  }
  
  public int getY()
  {
    return y;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("UpdateLayoutMountItem [");
    localStringBuilder.append(mReactTag);
    localStringBuilder.append("] - x: ");
    localStringBuilder.append(x);
    localStringBuilder.append(" - y: ");
    localStringBuilder.append(y);
    localStringBuilder.append(" - height: ");
    localStringBuilder.append(mHeight);
    localStringBuilder.append(" - width: ");
    localStringBuilder.append(mWidth);
    localStringBuilder.append(" - layoutDirection: ");
    localStringBuilder.append(mLayoutDirection);
    return localStringBuilder.toString();
  }
}
