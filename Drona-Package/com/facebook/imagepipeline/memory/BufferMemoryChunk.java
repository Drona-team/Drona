package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import java.io.Closeable;
import java.nio.ByteBuffer;

public class BufferMemoryChunk
  implements MemoryChunk, Closeable
{
  private static final String PAGE_KEY = "BufferMemoryChunk";
  private ByteBuffer mBuffer;
  private final int mSize;
  private final long uniqueId;
  
  public BufferMemoryChunk(int paramInt)
  {
    mBuffer = ByteBuffer.allocateDirect(paramInt);
    mSize = paramInt;
    uniqueId = System.identityHashCode(this);
  }
  
  private void doCopy(int paramInt1, MemoryChunk paramMemoryChunk, int paramInt2, int paramInt3)
  {
    if ((paramMemoryChunk instanceof BufferMemoryChunk))
    {
      Preconditions.checkState(isClosed() ^ true);
      Preconditions.checkState(paramMemoryChunk.isClosed() ^ true);
      MemoryChunkUtil.checkBounds(paramInt1, paramMemoryChunk.getSize(), paramInt2, paramInt3, mSize);
      mBuffer.position(paramInt1);
      paramMemoryChunk.getByteBuffer().position(paramInt2);
      byte[] arrayOfByte = new byte[paramInt3];
      mBuffer.get(arrayOfByte, 0, paramInt3);
      paramMemoryChunk.getByteBuffer().put(arrayOfByte, 0, paramInt3);
      return;
    }
    throw new IllegalArgumentException("Cannot copy two incompatible MemoryChunks");
  }
  
  public void close()
  {
    try
    {
      mBuffer = null;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  public void copy(int paramInt1, MemoryChunk paramMemoryChunk, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_2
    //   1: invokestatic 95	com/facebook/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_2
    //   6: invokeinterface 99 1 0
    //   11: aload_0
    //   12: invokevirtual 100	com/facebook/imagepipeline/memory/BufferMemoryChunk:getUniqueId	()J
    //   15: lcmp
    //   16: ifne +79 -> 95
    //   19: new 102	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 103	java/lang/StringBuilder:<init>	()V
    //   26: astore 5
    //   28: aload 5
    //   30: ldc 105
    //   32: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: pop
    //   36: aload 5
    //   38: aload_0
    //   39: invokevirtual 100	com/facebook/imagepipeline/memory/BufferMemoryChunk:getUniqueId	()J
    //   42: invokestatic 115	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   45: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: pop
    //   49: aload 5
    //   51: ldc 117
    //   53: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: aload 5
    //   59: aload_2
    //   60: invokeinterface 99 1 0
    //   65: invokestatic 115	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   68: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   71: pop
    //   72: aload 5
    //   74: ldc 119
    //   76: invokevirtual 109	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   79: pop
    //   80: ldc 12
    //   82: aload 5
    //   84: invokevirtual 123	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   87: invokestatic 129	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   90: pop
    //   91: iconst_0
    //   92: invokestatic 132	com/facebook/common/internal/Preconditions:checkArgument	(Z)V
    //   95: aload_2
    //   96: invokeinterface 99 1 0
    //   101: aload_0
    //   102: invokevirtual 100	com/facebook/imagepipeline/memory/BufferMemoryChunk:getUniqueId	()J
    //   105: lcmp
    //   106: ifge +35 -> 141
    //   109: aload_2
    //   110: monitorenter
    //   111: aload_0
    //   112: monitorenter
    //   113: aload_0
    //   114: iload_1
    //   115: aload_2
    //   116: iload_3
    //   117: iload 4
    //   119: invokespecial 134	com/facebook/imagepipeline/memory/BufferMemoryChunk:doCopy	(ILcom/facebook/imagepipeline/memory/MemoryChunk;II)V
    //   122: aload_0
    //   123: monitorexit
    //   124: aload_2
    //   125: monitorexit
    //   126: return
    //   127: astore 5
    //   129: aload_0
    //   130: monitorexit
    //   131: aload 5
    //   133: athrow
    //   134: astore 5
    //   136: aload_2
    //   137: monitorexit
    //   138: aload 5
    //   140: athrow
    //   141: aload_0
    //   142: monitorenter
    //   143: aload_2
    //   144: monitorenter
    //   145: aload_0
    //   146: iload_1
    //   147: aload_2
    //   148: iload_3
    //   149: iload 4
    //   151: invokespecial 134	com/facebook/imagepipeline/memory/BufferMemoryChunk:doCopy	(ILcom/facebook/imagepipeline/memory/MemoryChunk;II)V
    //   154: aload_2
    //   155: monitorexit
    //   156: aload_0
    //   157: monitorexit
    //   158: return
    //   159: astore 5
    //   161: aload_2
    //   162: monitorexit
    //   163: aload 5
    //   165: athrow
    //   166: astore_2
    //   167: aload_0
    //   168: monitorexit
    //   169: aload_2
    //   170: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	171	0	this	BufferMemoryChunk
    //   0	171	1	paramInt1	int
    //   0	171	2	paramMemoryChunk	MemoryChunk
    //   0	171	3	paramInt2	int
    //   0	171	4	paramInt3	int
    //   26	57	5	localStringBuilder	StringBuilder
    //   127	5	5	localThrowable1	Throwable
    //   134	5	5	localThrowable2	Throwable
    //   159	5	5	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   113	124	127	java/lang/Throwable
    //   129	131	127	java/lang/Throwable
    //   111	113	134	java/lang/Throwable
    //   124	126	134	java/lang/Throwable
    //   131	134	134	java/lang/Throwable
    //   136	138	134	java/lang/Throwable
    //   145	156	159	java/lang/Throwable
    //   161	163	159	java/lang/Throwable
    //   143	145	166	java/lang/Throwable
    //   156	158	166	java/lang/Throwable
    //   163	166	166	java/lang/Throwable
    //   167	169	166	java/lang/Throwable
  }
  
  public ByteBuffer getByteBuffer()
  {
    try
    {
      ByteBuffer localByteBuffer = mBuffer;
      return localByteBuffer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getNativePtr()
  {
    throw new UnsupportedOperationException("Cannot get the pointer of a BufferMemoryChunk");
  }
  
  public int getSize()
  {
    return mSize;
  }
  
  public long getUniqueId()
  {
    return uniqueId;
  }
  
  public boolean isClosed()
  {
    try
    {
      ByteBuffer localByteBuffer = mBuffer;
      boolean bool;
      if (localByteBuffer == null) {
        bool = true;
      } else {
        bool = false;
      }
      return bool;
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
        Preconditions.checkState(isClosed() ^ true);
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
          byte b = mBuffer.get(paramInt);
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
    try
    {
      Preconditions.checkNotNull(paramArrayOfByte);
      Preconditions.checkState(isClosed() ^ true);
      paramInt3 = MemoryChunkUtil.adjustByteCount(paramInt1, paramInt3, mSize);
      MemoryChunkUtil.checkBounds(paramInt1, paramArrayOfByte.length, paramInt2, paramInt3, mSize);
      mBuffer.position(paramInt1);
      mBuffer.get(paramArrayOfByte, paramInt2, paramInt3);
      return paramInt3;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
  
  public int write(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
  {
    try
    {
      Preconditions.checkNotNull(paramArrayOfByte);
      Preconditions.checkState(isClosed() ^ true);
      paramInt3 = MemoryChunkUtil.adjustByteCount(paramInt1, paramInt3, mSize);
      MemoryChunkUtil.checkBounds(paramInt1, paramArrayOfByte.length, paramInt2, paramInt3, mSize);
      mBuffer.position(paramInt1);
      mBuffer.put(paramArrayOfByte, paramInt2, paramInt3);
      return paramInt3;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
}
