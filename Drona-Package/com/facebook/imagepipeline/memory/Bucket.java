package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import java.util.LinkedList;
import java.util.Queue;
import javax.annotation.concurrent.NotThreadSafe;

@VisibleForTesting
@NotThreadSafe
class Bucket<V>
{
  private static final String thread = "BUCKET";
  private final boolean mFixBucketsReinitialization;
  final Queue mFreeList;
  private int mInUseLength;
  public final int mItemSize;
  public final int mMaxLength;
  
  public Bucket(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    boolean bool2 = false;
    if (paramInt1 > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkState(bool1);
    if (paramInt2 >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkState(bool1);
    boolean bool1 = bool2;
    if (paramInt3 >= 0) {
      bool1 = true;
    }
    Preconditions.checkState(bool1);
    mItemSize = paramInt1;
    mMaxLength = paramInt2;
    mFreeList = new LinkedList();
    mInUseLength = paramInt3;
    mFixBucketsReinitialization = paramBoolean;
  }
  
  void addToFreeList(Object paramObject)
  {
    mFreeList.add(paramObject);
  }
  
  public void decrementInUseCount()
  {
    boolean bool;
    if (mInUseLength > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkState(bool);
    mInUseLength -= 1;
  }
  
  public Object get()
  {
    Object localObject = pop();
    if (localObject != null) {
      mInUseLength += 1;
    }
    return localObject;
  }
  
  int getFreeListSize()
  {
    return mFreeList.size();
  }
  
  public int getInUseCount()
  {
    return mInUseLength;
  }
  
  public void incrementInUseCount()
  {
    mInUseLength += 1;
  }
  
  public boolean isMaxLengthExceeded()
  {
    return mInUseLength + getFreeListSize() > mMaxLength;
  }
  
  public Object pop()
  {
    return mFreeList.poll();
  }
  
  public void release(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    boolean bool2 = mFixBucketsReinitialization;
    boolean bool1 = false;
    if (bool2)
    {
      if (mInUseLength > 0) {
        bool1 = true;
      }
      Preconditions.checkState(bool1);
      mInUseLength -= 1;
      addToFreeList(paramObject);
      return;
    }
    if (mInUseLength > 0)
    {
      mInUseLength -= 1;
      addToFreeList(paramObject);
      return;
    }
    FLog.e("BUCKET", "Tried to release value %s from an empty bucket!", new Object[] { paramObject });
  }
}
