package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.OOMSoftReference;
import com.facebook.common.references.ResourceReleaser;
import java.util.concurrent.Semaphore;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class SharedByteArray
  implements MemoryTrimmable
{
  @VisibleForTesting
  final OOMSoftReference<byte[]> mByteArraySoftRef;
  @VisibleForTesting
  final int mMaxByteArraySize;
  @VisibleForTesting
  final int mMinByteArraySize;
  private final ResourceReleaser<byte[]> mResourceReleaser;
  @VisibleForTesting
  final Semaphore mSemaphore;
  
  public SharedByteArray(MemoryTrimmableRegistry paramMemoryTrimmableRegistry, PoolParams paramPoolParams)
  {
    Preconditions.checkNotNull(paramMemoryTrimmableRegistry);
    int i = minBucketSize;
    boolean bool2 = false;
    if (i > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (maxBucketSize >= minBucketSize) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    mMaxByteArraySize = maxBucketSize;
    mMinByteArraySize = minBucketSize;
    mByteArraySoftRef = new OOMSoftReference();
    mSemaphore = new Semaphore(1);
    mResourceReleaser = new ResourceReleaser()
    {
      public void release(byte[] paramAnonymousArrayOfByte)
      {
        mSemaphore.release();
      }
    };
    paramMemoryTrimmableRegistry.registerMemoryTrimmable(this);
  }
  
  private byte[] allocateByteArray(int paramInt)
  {
    try
    {
      mByteArraySoftRef.clear();
      byte[] arrayOfByte = new byte[paramInt];
      mByteArraySoftRef.offer(arrayOfByte);
      return arrayOfByte;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private byte[] getByteArray(int paramInt)
  {
    paramInt = getBucketedSize(paramInt);
    byte[] arrayOfByte2 = (byte[])mByteArraySoftRef.get();
    byte[] arrayOfByte1;
    if (arrayOfByte2 != null)
    {
      arrayOfByte1 = arrayOfByte2;
      if (arrayOfByte2.length >= paramInt) {}
    }
    else
    {
      arrayOfByte1 = allocateByteArray(paramInt);
    }
    return arrayOfByte1;
  }
  
  public CloseableReference get(int paramInt)
  {
    boolean bool2 = false;
    if (paramInt > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1, "Size must be greater than zero");
    boolean bool1 = bool2;
    if (paramInt <= mMaxByteArraySize) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1, "Requested size is too big");
    mSemaphore.acquireUninterruptibly();
    try
    {
      CloseableReference localCloseableReference = CloseableReference.of(getByteArray(paramInt), mResourceReleaser);
      return localCloseableReference;
    }
    catch (Throwable localThrowable)
    {
      mSemaphore.release();
      throw Throwables.propagate(localThrowable);
    }
  }
  
  int getBucketedSize(int paramInt)
  {
    return Integer.highestOneBit(Math.max(paramInt, mMinByteArraySize) - 1) * 2;
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType)
  {
    if (!mSemaphore.tryAcquire()) {
      return;
    }
    try
    {
      mByteArraySoftRef.clear();
      mSemaphore.release();
      return;
    }
    catch (Throwable paramMemoryTrimType)
    {
      mSemaphore.release();
      throw paramMemoryTrimType;
    }
  }
}
