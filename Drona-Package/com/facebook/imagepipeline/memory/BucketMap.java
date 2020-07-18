package com.facebook.imagepipeline.memory;

import android.util.SparseArray;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.infer.annotation.ThreadSafe;
import java.util.LinkedList;
import javax.annotation.Nullable;

@ThreadSafe
public class BucketMap<T>
{
  @Nullable
  @VisibleForTesting
  LinkedEntry<T> mHead;
  protected final SparseArray<LinkedEntry<T>> mMap = new SparseArray();
  @Nullable
  @VisibleForTesting
  LinkedEntry<T> mTail;
  
  public BucketMap() {}
  
  private void maybePrune(LinkedEntry paramLinkedEntry)
  {
    if ((paramLinkedEntry != null) && (value.isEmpty()))
    {
      prune(paramLinkedEntry);
      mMap.remove(last);
    }
  }
  
  private void moveToFront(LinkedEntry paramLinkedEntry)
  {
    if (mHead == paramLinkedEntry) {
      return;
    }
    prune(paramLinkedEntry);
    if (mHead == null)
    {
      mHead = paramLinkedEntry;
      mTail = paramLinkedEntry;
      return;
    }
    next = mHead;
    mHead.prev = paramLinkedEntry;
    mHead = paramLinkedEntry;
  }
  
  private void prune(LinkedEntry paramLinkedEntry)
  {
    try
    {
      LinkedEntry localLinkedEntry1 = prev;
      LinkedEntry localLinkedEntry2 = next;
      if (localLinkedEntry1 != null) {
        next = localLinkedEntry2;
      }
      if (localLinkedEntry2 != null) {
        prev = localLinkedEntry1;
      }
      prev = null;
      next = null;
      if (paramLinkedEntry == mHead) {
        mHead = localLinkedEntry2;
      }
      if (paramLinkedEntry == mTail) {
        mTail = localLinkedEntry1;
      }
      return;
    }
    catch (Throwable paramLinkedEntry)
    {
      throw paramLinkedEntry;
    }
  }
  
  public Object acquire(int paramInt)
  {
    try
    {
      LinkedEntry localLinkedEntry = (LinkedEntry)mMap.get(paramInt);
      if (localLinkedEntry == null) {
        return null;
      }
      Object localObject = value.pollFirst();
      moveToFront(localLinkedEntry);
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void release(int paramInt, Object paramObject)
  {
    try
    {
      LinkedEntry localLinkedEntry2 = (LinkedEntry)mMap.get(paramInt);
      LinkedEntry localLinkedEntry1 = localLinkedEntry2;
      if (localLinkedEntry2 == null)
      {
        localLinkedEntry1 = new LinkedEntry(null, paramInt, new LinkedList(), null, null);
        mMap.put(paramInt, localLinkedEntry1);
      }
      value.addLast(paramObject);
      moveToFront(localLinkedEntry1);
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public Object removeFromEnd()
  {
    try
    {
      LinkedEntry localLinkedEntry = mTail;
      if (localLinkedEntry == null) {
        return null;
      }
      Object localObject = value.pollLast();
      maybePrune(localLinkedEntry);
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  int valueCount()
  {
    int i = 0;
    try
    {
      LinkedEntry localLinkedEntry = mHead;
      while (localLinkedEntry != null)
      {
        int j = i;
        if (value != null) {
          j = i + value.size();
        }
        localLinkedEntry = next;
        i = j;
      }
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  @VisibleForTesting
  static class LinkedEntry<I>
  {
    int last;
    @Nullable
    LinkedEntry<I> next;
    @Nullable
    LinkedEntry<I> prev;
    LinkedList<I> value;
    
    private LinkedEntry(LinkedEntry paramLinkedEntry1, int paramInt, LinkedList paramLinkedList, LinkedEntry paramLinkedEntry2)
    {
      prev = paramLinkedEntry1;
      last = paramInt;
      value = paramLinkedList;
      next = paramLinkedEntry2;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("LinkedEntry(key: ");
      localStringBuilder.append(last);
      localStringBuilder.append(")");
      return localStringBuilder.toString();
    }
  }
}
