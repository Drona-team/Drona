package com.facebook.react.uimanager.events;

import android.util.SparseIntArray;

public class TouchEventCoalescingKeyHelper
{
  private final SparseIntArray mDownTimeToCoalescingKey = new SparseIntArray();
  
  public TouchEventCoalescingKeyHelper() {}
  
  public void addCoalescingKey(long paramLong)
  {
    mDownTimeToCoalescingKey.put((int)paramLong, 0);
  }
  
  public short getCoalescingKey(long paramLong)
  {
    int i = mDownTimeToCoalescingKey.get((int)paramLong, -1);
    if (i != -1) {
      return (short)(i & 0xFFFF);
    }
    throw new RuntimeException("Tried to get non-existent cookie");
  }
  
  public boolean hasCoalescingKey(long paramLong)
  {
    return mDownTimeToCoalescingKey.get((int)paramLong, -1) != -1;
  }
  
  public void incrementCoalescingKey(long paramLong)
  {
    SparseIntArray localSparseIntArray = mDownTimeToCoalescingKey;
    int i = (int)paramLong;
    int j = localSparseIntArray.get(i, -1);
    if (j != -1)
    {
      mDownTimeToCoalescingKey.put(i, j + 1);
      return;
    }
    throw new RuntimeException("Tried to increment non-existent cookie");
  }
  
  public void removeCoalescingKey(long paramLong)
  {
    mDownTimeToCoalescingKey.delete((int)paramLong);
  }
}
