package com.bumptech.glide.load.engine.bitmap_recycle;

public final class IntegerArrayAdapter
  implements ArrayAdapterInterface<int[]>
{
  private static final String PAGE_KEY = "IntegerArrayPool";
  
  public IntegerArrayAdapter() {}
  
  public int getArrayLength(int[] paramArrayOfInt)
  {
    return paramArrayOfInt.length;
  }
  
  public int getElementSizeInBytes()
  {
    return 4;
  }
  
  public String getTag()
  {
    return "IntegerArrayPool";
  }
  
  public int[] newArray(int paramInt)
  {
    return new int[paramInt];
  }
}
