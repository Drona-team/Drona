package com.google.android.gms.common.images;

public final class Size
{
  private final int zane;
  private final int zanf;
  
  public Size(int paramInt1, int paramInt2)
  {
    zane = paramInt1;
    zanf = paramInt2;
  }
  
  private static NumberFormatException parseLong(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder(String.valueOf(paramString).length() + 16);
    localStringBuilder.append("Invalid Size: \"");
    localStringBuilder.append(paramString);
    localStringBuilder.append("\"");
    throw new NumberFormatException(localStringBuilder.toString());
  }
  
  public static Size parseSize(String paramString)
    throws NumberFormatException
  {
    int j;
    int i;
    if (paramString != null)
    {
      j = paramString.indexOf('*');
      i = j;
      if (j < 0) {
        i = paramString.indexOf('x');
      }
      if (i < 0) {}
    }
    try
    {
      j = Integer.parseInt(paramString.substring(0, i));
      Size localSize = new Size(j, Integer.parseInt(paramString.substring(i + 1)));
      return localSize;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    throw parseLong(paramString);
    throw parseLong(paramString);
    throw new IllegalArgumentException("string must not be null");
  }
  
  public final boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof Size))
    {
      paramObject = (Size)paramObject;
      if ((zane == zane) && (zanf == zanf)) {
        return true;
      }
    }
    return false;
  }
  
  public final int getHeight()
  {
    return zanf;
  }
  
  public final int getWidth()
  {
    return zane;
  }
  
  public final int hashCode()
  {
    return zanf ^ (zane << 16 | zane >>> 16);
  }
  
  public final String toString()
  {
    int i = zane;
    int j = zanf;
    StringBuilder localStringBuilder = new StringBuilder(23);
    localStringBuilder.append(i);
    localStringBuilder.append("x");
    localStringBuilder.append(j);
    return localStringBuilder.toString();
  }
}
