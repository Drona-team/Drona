package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBuffer.ClosedException;
import com.facebook.common.references.CloseableReference;
import java.nio.ByteBuffer;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class MemoryPooledByteBuffer
  implements PooledByteBuffer
{
  @VisibleForTesting
  @GuardedBy("this")
  CloseableReference<MemoryChunk> mBufRef;
  private final int mSize;
  
  public MemoryPooledByteBuffer(CloseableReference paramCloseableReference, int paramInt)
  {
    Preconditions.checkNotNull(paramCloseableReference);
    boolean bool;
    if ((paramInt >= 0) && (paramInt <= ((MemoryChunk)paramCloseableReference.get()).getSize())) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    mBufRef = paramCloseableReference.clone();
    mSize = paramInt;
  }
  
  public void close()
  {
    try
    {
      CloseableReference.closeSafely(mBufRef);
      mBufRef = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void ensureValid()
  {
    try
    {
      boolean bool = isClosed();
      if (!bool) {
        return;
      }
      throw new PooledByteBuffer.ClosedException();
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public ByteBuffer getByteBuffer()
  {
    try
    {
      ByteBuffer localByteBuffer = ((MemoryChunk)mBufRef.get()).getByteBuffer();
      return localByteBuffer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getNativePtr()
    throws UnsupportedOperationException
  {
    try
    {
      ensureValid();
      long l = ((MemoryChunk)mBufRef.get()).getNativePtr();
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isClosed()
  {
    try
    {
      boolean bool = CloseableReference.isValid(mBufRef);
      return bool ^ true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public byte read(int paramInt)
  {
    for (;;)
    {
      try
      {
        ensureValid();
        boolean bool2 = false;
        if (paramInt >= 0)
        {
          bool1 = true;
          Preconditions.checkArgument(bool1);
          bool1 = bool2;
          if (paramInt < mSize) {
            bool1 = true;
          }
          Preconditions.checkArgument(bool1);
          byte b = ((MemoryChunk)mBufRef.get()).read(paramInt);
          return b;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool1 = false;
    }
  }
  
  public int read(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    for (;;)
    {
      try
      {
        ensureValid();
        if (paramInt1 + paramInt3 <= mSize)
        {
          bool = true;
          Preconditions.checkArgument(bool);
          paramInt1 = ((MemoryChunk)mBufRef.get()).read(paramInt1, paramArrayOfByte, paramInt2, paramInt3);
          return paramInt1;
        }
      }
      catch (Throwable paramArrayOfByte)
      {
        throw paramArrayOfByte;
      }
      boolean bool = false;
    }
  }
  
  public int size()
  {
    try
    {
      ensureValid();
      int i = mSize;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}
