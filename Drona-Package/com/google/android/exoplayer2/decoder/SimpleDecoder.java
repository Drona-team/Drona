package com.google.android.exoplayer2.decoder;

import com.google.android.exoplayer2.util.Assertions;
import java.util.ArrayDeque;

public abstract class SimpleDecoder<I extends DecoderInputBuffer, O extends OutputBuffer, E extends Exception>
  implements Decoder<I, O, E>
{
  private int availableInputBufferCount;
  private final I[] availableInputBuffers;
  private int availableOutputBufferCount;
  private final O[] availableOutputBuffers;
  private final Thread decodeThread;
  private I dequeuedInputBuffer;
  private E exception;
  private boolean flushed;
  private final Object lock = new Object();
  private final ArrayDeque<I> queuedInputBuffers = new ArrayDeque();
  private final ArrayDeque<O> queuedOutputBuffers = new ArrayDeque();
  private boolean released;
  private int skippedOutputBufferCount;
  
  protected SimpleDecoder(DecoderInputBuffer[] paramArrayOfDecoderInputBuffer, OutputBuffer[] paramArrayOfOutputBuffer)
  {
    availableInputBuffers = paramArrayOfDecoderInputBuffer;
    availableInputBufferCount = paramArrayOfDecoderInputBuffer.length;
    int j = 0;
    int i = 0;
    while (i < availableInputBufferCount)
    {
      availableInputBuffers[i] = createInputBuffer();
      i += 1;
    }
    availableOutputBuffers = paramArrayOfOutputBuffer;
    availableOutputBufferCount = paramArrayOfOutputBuffer.length;
    i = j;
    while (i < availableOutputBufferCount)
    {
      availableOutputBuffers[i] = createOutputBuffer();
      i += 1;
    }
    decodeThread = new Thread()
    {
      public void run()
      {
        SimpleDecoder.this.parseParameters();
      }
    };
    decodeThread.start();
  }
  
  private boolean canDecodeBuffer()
  {
    return (!queuedInputBuffers.isEmpty()) && (availableOutputBufferCount > 0);
  }
  
  /* Error */
  private boolean decode()
    throws InterruptedException
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 44	com/google/android/exoplayer2/decoder/SimpleDecoder:lock	Ljava/lang/Object;
    //   4: astore 5
    //   6: aload 5
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 98	com/google/android/exoplayer2/decoder/SimpleDecoder:released	Z
    //   13: ifne +20 -> 33
    //   16: aload_0
    //   17: invokespecial 100	com/google/android/exoplayer2/decoder/SimpleDecoder:canDecodeBuffer	()Z
    //   20: ifne +13 -> 33
    //   23: aload_0
    //   24: getfield 44	com/google/android/exoplayer2/decoder/SimpleDecoder:lock	Ljava/lang/Object;
    //   27: invokevirtual 103	java/lang/Object:wait	()V
    //   30: goto -21 -> 9
    //   33: aload_0
    //   34: getfield 98	com/google/android/exoplayer2/decoder/SimpleDecoder:released	Z
    //   37: ifeq +8 -> 45
    //   40: aload 5
    //   42: monitorexit
    //   43: iconst_0
    //   44: ireturn
    //   45: aload_0
    //   46: getfield 49	com/google/android/exoplayer2/decoder/SimpleDecoder:queuedInputBuffers	Ljava/util/ArrayDeque;
    //   49: invokevirtual 107	java/util/ArrayDeque:removeFirst	()Ljava/lang/Object;
    //   52: checkcast 109	com/google/android/exoplayer2/decoder/DecoderInputBuffer
    //   55: astore_3
    //   56: aload_0
    //   57: getfield 61	com/google/android/exoplayer2/decoder/SimpleDecoder:availableOutputBuffers	[Lcom/google/android/exoplayer2/decoder/OutputBuffer;
    //   60: astore 4
    //   62: aload_0
    //   63: getfield 63	com/google/android/exoplayer2/decoder/SimpleDecoder:availableOutputBufferCount	I
    //   66: iconst_1
    //   67: isub
    //   68: istore_1
    //   69: aload_0
    //   70: iload_1
    //   71: putfield 63	com/google/android/exoplayer2/decoder/SimpleDecoder:availableOutputBufferCount	I
    //   74: aload 4
    //   76: iload_1
    //   77: aaload
    //   78: astore 4
    //   80: aload_0
    //   81: getfield 111	com/google/android/exoplayer2/decoder/SimpleDecoder:flushed	Z
    //   84: istore_2
    //   85: aload_0
    //   86: iconst_0
    //   87: putfield 111	com/google/android/exoplayer2/decoder/SimpleDecoder:flushed	Z
    //   90: aload 5
    //   92: monitorexit
    //   93: aload_3
    //   94: invokevirtual 116	com/google/android/exoplayer2/decoder/Buffer:isEndOfStream	()Z
    //   97: ifeq +12 -> 109
    //   100: aload 4
    //   102: iconst_4
    //   103: invokevirtual 120	com/google/android/exoplayer2/decoder/Buffer:addFlag	(I)V
    //   106: goto +88 -> 194
    //   109: aload_3
    //   110: invokevirtual 123	com/google/android/exoplayer2/decoder/Buffer:isDecodeOnly	()Z
    //   113: ifeq +10 -> 123
    //   116: aload 4
    //   118: ldc 124
    //   120: invokevirtual 120	com/google/android/exoplayer2/decoder/Buffer:addFlag	(I)V
    //   123: aload_0
    //   124: aload_3
    //   125: aload 4
    //   127: iload_2
    //   128: invokevirtual 127	com/google/android/exoplayer2/decoder/SimpleDecoder:decode	(Lcom/google/android/exoplayer2/decoder/DecoderInputBuffer;Lcom/google/android/exoplayer2/decoder/OutputBuffer;Z)Ljava/lang/Exception;
    //   131: astore 5
    //   133: aload_0
    //   134: aload 5
    //   136: putfield 129	com/google/android/exoplayer2/decoder/SimpleDecoder:exception	Ljava/lang/Exception;
    //   139: goto +30 -> 169
    //   142: astore 5
    //   144: aload_0
    //   145: aload_0
    //   146: aload 5
    //   148: invokevirtual 133	com/google/android/exoplayer2/decoder/SimpleDecoder:createUnexpectedDecodeException	(Ljava/lang/Throwable;)Ljava/lang/Exception;
    //   151: putfield 129	com/google/android/exoplayer2/decoder/SimpleDecoder:exception	Ljava/lang/Exception;
    //   154: goto +15 -> 169
    //   157: astore 5
    //   159: aload_0
    //   160: aload_0
    //   161: aload 5
    //   163: invokevirtual 133	com/google/android/exoplayer2/decoder/SimpleDecoder:createUnexpectedDecodeException	(Ljava/lang/Throwable;)Ljava/lang/Exception;
    //   166: putfield 129	com/google/android/exoplayer2/decoder/SimpleDecoder:exception	Ljava/lang/Exception;
    //   169: aload_0
    //   170: getfield 129	com/google/android/exoplayer2/decoder/SimpleDecoder:exception	Ljava/lang/Exception;
    //   173: ifnull +21 -> 194
    //   176: aload_0
    //   177: getfield 44	com/google/android/exoplayer2/decoder/SimpleDecoder:lock	Ljava/lang/Object;
    //   180: astore_3
    //   181: aload_3
    //   182: monitorenter
    //   183: aload_3
    //   184: monitorexit
    //   185: iconst_0
    //   186: ireturn
    //   187: astore 4
    //   189: aload_3
    //   190: monitorexit
    //   191: aload 4
    //   193: athrow
    //   194: aload_0
    //   195: getfield 44	com/google/android/exoplayer2/decoder/SimpleDecoder:lock	Ljava/lang/Object;
    //   198: astore 5
    //   200: aload 5
    //   202: monitorenter
    //   203: aload_0
    //   204: getfield 111	com/google/android/exoplayer2/decoder/SimpleDecoder:flushed	Z
    //   207: ifeq +11 -> 218
    //   210: aload 4
    //   212: invokevirtual 138	com/google/android/exoplayer2/decoder/OutputBuffer:release	()V
    //   215: goto +52 -> 267
    //   218: aload 4
    //   220: invokevirtual 123	com/google/android/exoplayer2/decoder/Buffer:isDecodeOnly	()Z
    //   223: ifeq +21 -> 244
    //   226: aload_0
    //   227: aload_0
    //   228: getfield 140	com/google/android/exoplayer2/decoder/SimpleDecoder:skippedOutputBufferCount	I
    //   231: iconst_1
    //   232: iadd
    //   233: putfield 140	com/google/android/exoplayer2/decoder/SimpleDecoder:skippedOutputBufferCount	I
    //   236: aload 4
    //   238: invokevirtual 138	com/google/android/exoplayer2/decoder/OutputBuffer:release	()V
    //   241: goto +26 -> 267
    //   244: aload 4
    //   246: aload_0
    //   247: getfield 140	com/google/android/exoplayer2/decoder/SimpleDecoder:skippedOutputBufferCount	I
    //   250: putfield 141	com/google/android/exoplayer2/decoder/OutputBuffer:skippedOutputBufferCount	I
    //   253: aload_0
    //   254: iconst_0
    //   255: putfield 140	com/google/android/exoplayer2/decoder/SimpleDecoder:skippedOutputBufferCount	I
    //   258: aload_0
    //   259: getfield 51	com/google/android/exoplayer2/decoder/SimpleDecoder:queuedOutputBuffers	Ljava/util/ArrayDeque;
    //   262: aload 4
    //   264: invokevirtual 145	java/util/ArrayDeque:addLast	(Ljava/lang/Object;)V
    //   267: aload_0
    //   268: aload_3
    //   269: invokespecial 149	com/google/android/exoplayer2/decoder/SimpleDecoder:releaseInputBufferInternal	(Lcom/google/android/exoplayer2/decoder/DecoderInputBuffer;)V
    //   272: aload 5
    //   274: monitorexit
    //   275: iconst_1
    //   276: ireturn
    //   277: astore_3
    //   278: aload 5
    //   280: monitorexit
    //   281: aload_3
    //   282: athrow
    //   283: astore_3
    //   284: aload 5
    //   286: monitorexit
    //   287: aload_3
    //   288: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	289	0	this	SimpleDecoder
    //   68	9	1	i	int
    //   84	44	2	bool	boolean
    //   55	214	3	localObject1	Object
    //   277	5	3	localThrowable1	Throwable
    //   283	5	3	localThrowable2	Throwable
    //   60	66	4	localObject2	Object
    //   187	76	4	localThrowable3	Throwable
    //   4	131	5	localObject3	Object
    //   142	5	5	localOutOfMemoryError	OutOfMemoryError
    //   157	5	5	localRuntimeException	RuntimeException
    //   198	87	5	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   123	133	142	java/lang/OutOfMemoryError
    //   123	133	157	java/lang/RuntimeException
    //   133	139	157	java/lang/RuntimeException
    //   183	185	187	java/lang/Throwable
    //   189	191	187	java/lang/Throwable
    //   203	215	277	java/lang/Throwable
    //   218	241	277	java/lang/Throwable
    //   244	267	277	java/lang/Throwable
    //   267	275	277	java/lang/Throwable
    //   278	281	277	java/lang/Throwable
    //   9	30	283	java/lang/Throwable
    //   33	43	283	java/lang/Throwable
    //   45	74	283	java/lang/Throwable
    //   80	93	283	java/lang/Throwable
    //   284	287	283	java/lang/Throwable
  }
  
  private void maybeNotifyDecodeLoop()
  {
    if (canDecodeBuffer()) {
      lock.notify();
    }
  }
  
  private void maybeThrowException()
    throws Exception
  {
    if (exception == null) {
      return;
    }
    throw exception;
  }
  
  private void parseParameters()
  {
    try
    {
      boolean bool;
      do
      {
        bool = decode();
      } while (bool);
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      throw new IllegalStateException(localInterruptedException);
    }
  }
  
  private void releaseInputBufferInternal(DecoderInputBuffer paramDecoderInputBuffer)
  {
    paramDecoderInputBuffer.clear();
    DecoderInputBuffer[] arrayOfDecoderInputBuffer = availableInputBuffers;
    int i = availableInputBufferCount;
    availableInputBufferCount = (i + 1);
    arrayOfDecoderInputBuffer[i] = paramDecoderInputBuffer;
  }
  
  private void releaseOutputBufferInternal(OutputBuffer paramOutputBuffer)
  {
    paramOutputBuffer.clear();
    OutputBuffer[] arrayOfOutputBuffer = availableOutputBuffers;
    int i = availableOutputBufferCount;
    availableOutputBufferCount = (i + 1);
    arrayOfOutputBuffer[i] = paramOutputBuffer;
  }
  
  protected abstract DecoderInputBuffer createInputBuffer();
  
  protected abstract OutputBuffer createOutputBuffer();
  
  protected abstract Exception createUnexpectedDecodeException(Throwable paramThrowable);
  
  protected abstract Exception decode(DecoderInputBuffer paramDecoderInputBuffer, OutputBuffer paramOutputBuffer, boolean paramBoolean);
  
  public final DecoderInputBuffer dequeueInputBuffer()
    throws Exception
  {
    Object localObject2 = lock;
    for (;;)
    {
      try
      {
        maybeThrowException();
        if (dequeuedInputBuffer == null)
        {
          bool = true;
          Assertions.checkState(bool);
          if (availableInputBufferCount == 0)
          {
            localObject1 = null;
          }
          else
          {
            localObject1 = availableInputBuffers;
            int i = availableInputBufferCount - 1;
            availableInputBufferCount = i;
            localObject1 = localObject1[i];
          }
          dequeuedInputBuffer = ((DecoderInputBuffer)localObject1);
          Object localObject1 = dequeuedInputBuffer;
          return localObject1;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  public final OutputBuffer dequeueOutputBuffer()
    throws Exception
  {
    Object localObject = lock;
    try
    {
      maybeThrowException();
      if (queuedOutputBuffers.isEmpty()) {
        return null;
      }
      OutputBuffer localOutputBuffer = (OutputBuffer)queuedOutputBuffers.removeFirst();
      return localOutputBuffer;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void flush()
  {
    Object localObject = lock;
    try
    {
      flushed = true;
      skippedOutputBufferCount = 0;
      if (dequeuedInputBuffer != null)
      {
        releaseInputBufferInternal(dequeuedInputBuffer);
        dequeuedInputBuffer = null;
      }
      while (!queuedInputBuffers.isEmpty()) {
        releaseInputBufferInternal((DecoderInputBuffer)queuedInputBuffers.removeFirst());
      }
      while (!queuedOutputBuffers.isEmpty()) {
        ((OutputBuffer)queuedOutputBuffers.removeFirst()).release();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public final void queueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
    throws Exception
  {
    Object localObject = lock;
    for (;;)
    {
      try
      {
        maybeThrowException();
        if (paramDecoderInputBuffer == dequeuedInputBuffer)
        {
          bool = true;
          Assertions.checkArgument(bool);
          queuedInputBuffers.addLast(paramDecoderInputBuffer);
          maybeNotifyDecodeLoop();
          dequeuedInputBuffer = null;
          return;
        }
      }
      catch (Throwable paramDecoderInputBuffer)
      {
        throw paramDecoderInputBuffer;
      }
      boolean bool = false;
    }
  }
  
  public void release()
  {
    localObject = lock;
    try
    {
      released = true;
      lock.notify();
      localObject = decodeThread;
    }
    catch (Throwable localThrowable)
    {
      label31:
      throw localThrowable;
    }
    try
    {
      ((Thread)localObject).join();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      break label31;
    }
    Thread.currentThread().interrupt();
  }
  
  protected void releaseOutputBuffer(OutputBuffer paramOutputBuffer)
  {
    Object localObject = lock;
    try
    {
      releaseOutputBufferInternal(paramOutputBuffer);
      maybeNotifyDecodeLoop();
      return;
    }
    catch (Throwable paramOutputBuffer)
    {
      throw paramOutputBuffer;
    }
  }
  
  protected final void setInitialInputBufferSize(int paramInt)
  {
    int j = availableInputBufferCount;
    int k = availableInputBuffers.length;
    int i = 0;
    boolean bool;
    if (j == k) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    DecoderInputBuffer[] arrayOfDecoderInputBuffer = availableInputBuffers;
    j = arrayOfDecoderInputBuffer.length;
    while (i < j)
    {
      arrayOfDecoderInputBuffer[i].ensureSpaceForWrite(paramInt);
      i += 1;
    }
  }
}
