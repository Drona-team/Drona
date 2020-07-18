package com.facebook.react.common;

public class LongArray
{
  private static final double INNER_ARRAY_GROWTH_FACTOR = 1.8D;
  private long[] mArray;
  private int mLength;
  
  private LongArray(int paramInt)
  {
    mArray = new long[paramInt];
    mLength = 0;
  }
  
  public static LongArray createWithInitialCapacity(int paramInt)
  {
    return new LongArray(paramInt);
  }
  
  private void growArrayIfNeeded()
  {
    if (mLength == mArray.length)
    {
      long[] arrayOfLong = new long[Math.max(mLength + 1, (int)(mLength * 1.8D))];
      System.arraycopy(mArray, 0, arrayOfLong, 0, mLength);
      mArray = arrayOfLong;
    }
  }
  
  public void add(int paramInt, long paramLong)
  {
    if (paramInt < mLength)
    {
      mArray[paramInt] = paramLong;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" >= ");
    localStringBuilder.append(mLength);
    throw new IndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public void dropTail(int paramInt)
  {
    if (paramInt <= mLength)
    {
      mLength -= paramInt;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Trying to drop ");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" items from array of length ");
    localStringBuilder.append(mLength);
    throw new IndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public void ensureCapacity(long paramLong)
  {
    growArrayIfNeeded();
    long[] arrayOfLong = mArray;
    int i = mLength;
    mLength = (i + 1);
    arrayOfLong[i] = paramLong;
  }
  
  public boolean isEmpty()
  {
    return mLength == 0;
  }
  
  public long set(int paramInt)
  {
    if (paramInt < mLength) {
      return mArray[paramInt];
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("");
    localStringBuilder.append(paramInt);
    localStringBuilder.append(" >= ");
    localStringBuilder.append(mLength);
    throw new IndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public int size()
  {
    return mLength;
  }
}
