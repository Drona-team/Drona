package com.facebook.imagepipeline.memory;

import android.util.Log;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.internal.Preconditions;
import com.facebook.imagepipeline.nativecode.ImagePipelineNativeLoader;
import java.io.Closeable;
import java.nio.ByteBuffer;

@DoNotStrip
public class NativeMemoryChunk
  implements MemoryChunk, Closeable
{
  private static final String PAGE_KEY = "NativeMemoryChunk";
  private boolean mIsClosed;
  private final long mNativePtr;
  private final int mSize;
  
  static {}
  
  public NativeMemoryChunk()
  {
    mSize = 0;
    mNativePtr = 0L;
    mIsClosed = true;
  }
  
  public NativeMemoryChunk(int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool);
    mSize = paramInt;
    mNativePtr = nativeAllocate(mSize);
    mIsClosed = false;
  }
  
  private void doCopy(int paramInt1, MemoryChunk paramMemoryChunk, int paramInt2, int paramInt3)
  {
    if ((paramMemoryChunk instanceof NativeMemoryChunk))
    {
      Preconditions.checkState(isClosed() ^ true);
      Preconditions.checkState(paramMemoryChunk.isClosed() ^ true);
      MemoryChunkUtil.checkBounds(paramInt1, paramMemoryChunk.getSize(), paramInt2, paramInt3, mSize);
      nativeMemcpy(paramMemoryChunk.getNativePtr() + paramInt2, mNativePtr + paramInt1, paramInt3);
      return;
    }
    throw new IllegalArgumentException("Cannot copy two incompatible MemoryChunks");
  }
  
  private static native long nativeAllocate(int paramInt);
  
  private static native void nativeCopyFromByteArray(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeCopyToByteArray(long paramLong, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  private static native void nativeFree(long paramLong);
  
  private static native void nativeMemcpy(long paramLong1, long paramLong2, int paramInt);
  
  private static native byte nativeReadByte(long paramLong);
  
  public void close()
  {
    try
    {
      if (!mIsClosed)
      {
        mIsClosed = true;
        nativeFree(mNativePtr);
      }
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
    //   1: invokestatic 99	com/facebook/common/internal/Preconditions:checkNotNull	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4: pop
    //   5: aload_2
    //   6: invokeinterface 102 1 0
    //   11: aload_0
    //   12: invokevirtual 103	com/facebook/imagepipeline/memory/NativeMemoryChunk:getUniqueId	()J
    //   15: lcmp
    //   16: ifne +90 -> 106
    //   19: new 105	java/lang/StringBuilder
    //   22: dup
    //   23: invokespecial 106	java/lang/StringBuilder:<init>	()V
    //   26: astore 5
    //   28: aload 5
    //   30: ldc 108
    //   32: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   35: pop
    //   36: aload 5
    //   38: aload_0
    //   39: invokestatic 118	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   42: invokestatic 124	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   45: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: pop
    //   49: aload 5
    //   51: ldc 126
    //   53: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   56: pop
    //   57: aload 5
    //   59: aload_2
    //   60: invokestatic 118	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   63: invokestatic 124	java/lang/Integer:toHexString	(I)Ljava/lang/String;
    //   66: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   69: pop
    //   70: aload 5
    //   72: ldc -128
    //   74: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload 5
    //   80: aload_0
    //   81: getfield 34	com/facebook/imagepipeline/memory/NativeMemoryChunk:mNativePtr	J
    //   84: invokestatic 133	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   87: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: pop
    //   91: ldc 13
    //   93: aload 5
    //   95: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokestatic 143	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   101: pop
    //   102: iconst_0
    //   103: invokestatic 43	com/facebook/common/internal/Preconditions:checkArgument	(Z)V
    //   106: aload_2
    //   107: invokeinterface 102 1 0
    //   112: aload_0
    //   113: invokevirtual 103	com/facebook/imagepipeline/memory/NativeMemoryChunk:getUniqueId	()J
    //   116: lcmp
    //   117: ifge +35 -> 152
    //   120: aload_2
    //   121: monitorenter
    //   122: aload_0
    //   123: monitorenter
    //   124: aload_0
    //   125: iload_1
    //   126: aload_2
    //   127: iload_3
    //   128: iload 4
    //   130: invokespecial 145	com/facebook/imagepipeline/memory/NativeMemoryChunk:doCopy	(ILcom/facebook/imagepipeline/memory/MemoryChunk;II)V
    //   133: aload_0
    //   134: monitorexit
    //   135: aload_2
    //   136: monitorexit
    //   137: return
    //   138: astore 5
    //   140: aload_0
    //   141: monitorexit
    //   142: aload 5
    //   144: athrow
    //   145: astore 5
    //   147: aload_2
    //   148: monitorexit
    //   149: aload 5
    //   151: athrow
    //   152: aload_0
    //   153: monitorenter
    //   154: aload_2
    //   155: monitorenter
    //   156: aload_0
    //   157: iload_1
    //   158: aload_2
    //   159: iload_3
    //   160: iload 4
    //   162: invokespecial 145	com/facebook/imagepipeline/memory/NativeMemoryChunk:doCopy	(ILcom/facebook/imagepipeline/memory/MemoryChunk;II)V
    //   165: aload_2
    //   166: monitorexit
    //   167: aload_0
    //   168: monitorexit
    //   169: return
    //   170: astore 5
    //   172: aload_2
    //   173: monitorexit
    //   174: aload 5
    //   176: athrow
    //   177: astore_2
    //   178: aload_0
    //   179: monitorexit
    //   180: aload_2
    //   181: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	182	0	this	NativeMemoryChunk
    //   0	182	1	paramInt1	int
    //   0	182	2	paramMemoryChunk	MemoryChunk
    //   0	182	3	paramInt2	int
    //   0	182	4	paramInt3	int
    //   26	68	5	localStringBuilder	StringBuilder
    //   138	5	5	localThrowable1	Throwable
    //   145	5	5	localThrowable2	Throwable
    //   170	5	5	localThrowable3	Throwable
    // Exception table:
    //   from	to	target	type
    //   124	135	138	java/lang/Throwable
    //   140	142	138	java/lang/Throwable
    //   122	124	145	java/lang/Throwable
    //   135	137	145	java/lang/Throwable
    //   142	145	145	java/lang/Throwable
    //   147	149	145	java/lang/Throwable
    //   156	167	170	java/lang/Throwable
    //   172	174	170	java/lang/Throwable
    //   154	156	177	java/lang/Throwable
    //   167	169	177	java/lang/Throwable
    //   174	177	177	java/lang/Throwable
    //   178	180	177	java/lang/Throwable
  }
  
  protected void finalize()
    throws Throwable
  {
    if (isClosed()) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("finalize: Chunk ");
    localStringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
    localStringBuilder.append(" still active. ");
    Log.w("NativeMemoryChunk", localStringBuilder.toString());
    try
    {
      close();
      super.finalize();
      return;
    }
    catch (Throwable localThrowable)
    {
      super.finalize();
      throw localThrowable;
    }
  }
  
  public ByteBuffer getByteBuffer()
  {
    return null;
  }
  
  public long getNativePtr()
  {
    return mNativePtr;
  }
  
  public int getSize()
  {
    return mSize;
  }
  
  public long getUniqueId()
  {
    return mNativePtr;
  }
  
  public boolean isClosed()
  {
    try
    {
      boolean bool = mIsClosed;
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
          byte b = nativeReadByte(mNativePtr + paramInt);
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
      nativeCopyToByteArray(mNativePtr + paramInt1, paramArrayOfByte, paramInt2, paramInt3);
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
      nativeCopyFromByteArray(mNativePtr + paramInt1, paramArrayOfByte, paramInt2, paramInt3);
      return paramInt3;
    }
    catch (Throwable paramArrayOfByte)
    {
      throw paramArrayOfByte;
    }
  }
}