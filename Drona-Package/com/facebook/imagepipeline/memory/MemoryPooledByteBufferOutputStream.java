package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import java.io.IOException;
import java.io.OutputStream;
import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class MemoryPooledByteBufferOutputStream
  extends PooledByteBufferOutputStream
{
  private CloseableReference<MemoryChunk> mBufRef;
  private int mCount;
  private final MemoryChunkPool mPool;
  
  public MemoryPooledByteBufferOutputStream(MemoryChunkPool paramMemoryChunkPool)
  {
    this(paramMemoryChunkPool, paramMemoryChunkPool.getMinBufferSize());
  }
  
  public MemoryPooledByteBufferOutputStream(MemoryChunkPool paramMemoryChunkPool, int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    mPool = ((MemoryChunkPool)Preconditions.checkNotNull(paramMemoryChunkPool));
    mCount = 0;
    mBufRef = CloseableReference.of(mPool.get(paramInt), mPool);
  }
  
  private void ensureValid()
  {
    if (CloseableReference.isValid(mBufRef)) {
      return;
    }
    throw new InvalidStreamException();
  }
  
  public void close()
  {
    CloseableReference.closeSafely(mBufRef);
    mBufRef = null;
    mCount = -1;
    super.close();
  }
  
  void realloc(int paramInt)
  {
    ensureValid();
    if (paramInt <= ((MemoryChunk)mBufRef.get()).getSize()) {
      return;
    }
    MemoryChunk localMemoryChunk = (MemoryChunk)mPool.get(paramInt);
    ((MemoryChunk)mBufRef.get()).copy(0, localMemoryChunk, 0, mCount);
    mBufRef.close();
    mBufRef = CloseableReference.of(localMemoryChunk, mPool);
  }
  
  public int size()
  {
    return mCount;
  }
  
  public MemoryPooledByteBuffer toByteBuffer()
  {
    ensureValid();
    return new MemoryPooledByteBuffer(mBufRef, mCount);
  }
  
  public void write(int paramInt)
    throws IOException
  {
    write(new byte[] { (byte)paramInt });
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    if ((paramInt1 >= 0) && (paramInt2 >= 0) && (paramInt1 + paramInt2 <= paramArrayOfByte.length))
    {
      ensureValid();
      realloc(mCount + paramInt2);
      ((MemoryChunk)mBufRef.get()).write(mCount, paramArrayOfByte, paramInt1, paramInt2);
      mCount += paramInt2;
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("length=");
    localStringBuilder.append(paramArrayOfByte.length);
    localStringBuilder.append("; regionStart=");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append("; regionLength=");
    localStringBuilder.append(paramInt2);
    throw new ArrayIndexOutOfBoundsException(localStringBuilder.toString());
  }
  
  public static class InvalidStreamException
    extends RuntimeException
  {
    public InvalidStreamException()
    {
      super();
    }
  }
}
