package expo.modules.barcodescanner.utils;

public class ImageDimensions
{
  private int mFacing;
  private int mHeight;
  private int mRotation;
  private int mWidth;
  
  public ImageDimensions(int paramInt1, int paramInt2)
  {
    this(paramInt1, paramInt2, 0);
  }
  
  public ImageDimensions(int paramInt1, int paramInt2, int paramInt3)
  {
    this(paramInt1, paramInt2, paramInt3, -1);
  }
  
  public ImageDimensions(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    mWidth = paramInt1;
    mHeight = paramInt2;
    mFacing = paramInt4;
    mRotation = paramInt3;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ImageDimensions))
    {
      paramObject = (ImageDimensions)paramObject;
      return (paramObject.getWidth() == getWidth()) && (paramObject.getHeight() == getHeight()) && (paramObject.getFacing() == getFacing()) && (paramObject.getRotation() == getRotation());
    }
    return super.equals(paramObject);
  }
  
  public int getFacing()
  {
    return mFacing;
  }
  
  public int getHeight()
  {
    if (isLandscape()) {
      return mWidth;
    }
    return mHeight;
  }
  
  public int getRotation()
  {
    return mRotation;
  }
  
  public int getWidth()
  {
    if (isLandscape()) {
      return mHeight;
    }
    return mWidth;
  }
  
  public boolean isLandscape()
  {
    return mRotation % 180 == 90;
  }
}
