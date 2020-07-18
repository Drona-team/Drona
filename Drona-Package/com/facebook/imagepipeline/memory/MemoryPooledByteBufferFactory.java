package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.common.references.CloseableReference;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class MemoryPooledByteBufferFactory
  implements PooledByteBufferFactory
{
  private final MemoryChunkPool mPool;
  private final PooledByteStreams mPooledByteStreams;
  
  public MemoryPooledByteBufferFactory(MemoryChunkPool paramMemoryChunkPool, PooledByteStreams paramPooledByteStreams)
  {
    mPool = paramMemoryChunkPool;
    mPooledByteStreams = paramPooledByteStreams;
  }
  
  MemoryPooledByteBuffer newByteBuf(InputStream paramInputStream, MemoryPooledByteBufferOutputStream paramMemoryPooledByteBufferOutputStream)
    throws IOException
  {
    mPooledByteStreams.copy(paramInputStream, paramMemoryPooledByteBufferOutputStream);
    return paramMemoryPooledByteBufferOutputStream.toByteBuffer();
  }
  
  public MemoryPooledByteBuffer newByteBuffer(int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    CloseableReference localCloseableReference = CloseableReference.of(mPool.get(paramInt), mPool);
    try
    {
      MemoryPooledByteBuffer localMemoryPooledByteBuffer = new MemoryPooledByteBuffer(localCloseableReference, paramInt);
      localCloseableReference.close();
      return localMemoryPooledByteBuffer;
    }
    catch (Throwable localThrowable)
    {
      localCloseableReference.close();
      throw localThrowable;
    }
  }
  
  public MemoryPooledByteBuffer newByteBuffer(InputStream paramInputStream)
    throws IOException
  {
    MemoryPooledByteBufferOutputStream localMemoryPooledByteBufferOutputStream = new MemoryPooledByteBufferOutputStream(mPool);
    try
    {
      paramInputStream = newByteBuf(paramInputStream, localMemoryPooledByteBufferOutputStream);
      localMemoryPooledByteBufferOutputStream.close();
      return paramInputStream;
    }
    catch (Throwable paramInputStream)
    {
      localMemoryPooledByteBufferOutputStream.close();
      throw paramInputStream;
    }
  }
  
  public MemoryPooledByteBuffer newByteBuffer(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    MemoryPooledByteBufferOutputStream localMemoryPooledByteBufferOutputStream = new MemoryPooledByteBufferOutputStream(mPool, paramInt);
    try
    {
      paramInputStream = newByteBuf(paramInputStream, localMemoryPooledByteBufferOutputStream);
      localMemoryPooledByteBufferOutputStream.close();
      return paramInputStream;
    }
    catch (Throwable paramInputStream)
    {
      localMemoryPooledByteBufferOutputStream.close();
      throw paramInputStream;
    }
  }
  
  public MemoryPooledByteBuffer newByteBuffer(byte[] paramArrayOfByte)
  {
    MemoryPooledByteBufferOutputStream localMemoryPooledByteBufferOutputStream = new MemoryPooledByteBufferOutputStream(mPool, paramArrayOfByte.length);
    int i = paramArrayOfByte.length;
    try
    {
      localMemoryPooledByteBufferOutputStream.write(paramArrayOfByte, 0, i);
      paramArrayOfByte = localMemoryPooledByteBufferOutputStream.toByteBuffer();
      localMemoryPooledByteBufferOutputStream.close();
      return paramArrayOfByte;
    }
    catch (Throwable paramArrayOfByte) {}catch (IOException paramArrayOfByte)
    {
      throw Throwables.propagate(paramArrayOfByte);
    }
    localMemoryPooledByteBufferOutputStream.close();
    throw paramArrayOfByte;
  }
  
  public MemoryPooledByteBufferOutputStream newOutputStream()
  {
    return new MemoryPooledByteBufferOutputStream(mPool);
  }
  
  public MemoryPooledByteBufferOutputStream newOutputStream(int paramInt)
  {
    return new MemoryPooledByteBufferOutputStream(mPool, paramInt);
  }
}
