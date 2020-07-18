package com.bumptech.glide.util;

import androidx.annotation.NonNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicReference;

public final class ByteBufferUtil
{
  private static final AtomicReference<byte[]> BUFFER_REF = new AtomicReference();
  private static final int BUFFER_SIZE = 16384;
  
  private ByteBufferUtil() {}
  
  /* Error */
  public static ByteBuffer fromFile(java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_0
    //   4: invokevirtual 39	java/io/File:length	()J
    //   7: lstore_1
    //   8: lload_1
    //   9: ldc2_w 40
    //   12: lcmp
    //   13: ifgt +78 -> 91
    //   16: lload_1
    //   17: lconst_0
    //   18: lcmp
    //   19: ifeq +62 -> 81
    //   22: new 43	java/io/RandomAccessFile
    //   25: dup
    //   26: aload_0
    //   27: ldc 45
    //   29: invokespecial 48	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   32: astore_3
    //   33: aload_3
    //   34: invokevirtual 52	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   37: astore 5
    //   39: aload 5
    //   41: getstatic 58	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   44: lconst_0
    //   45: lload_1
    //   46: invokevirtual 64	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   49: invokevirtual 70	java/nio/MappedByteBuffer:load	()Ljava/nio/MappedByteBuffer;
    //   52: astore_0
    //   53: aload 5
    //   55: ifnull +8 -> 63
    //   58: aload 5
    //   60: invokevirtual 73	java/nio/channels/FileChannel:close	()V
    //   63: aload_3
    //   64: invokevirtual 74	java/io/RandomAccessFile:close	()V
    //   67: aload_0
    //   68: areturn
    //   69: astore_0
    //   70: aload 5
    //   72: astore 4
    //   74: goto +30 -> 104
    //   77: astore_0
    //   78: goto +26 -> 104
    //   81: new 31	java/io/IOException
    //   84: dup
    //   85: ldc 76
    //   87: invokespecial 79	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   90: athrow
    //   91: new 31	java/io/IOException
    //   94: dup
    //   95: ldc 81
    //   97: invokespecial 79	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   100: athrow
    //   101: astore_0
    //   102: aconst_null
    //   103: astore_3
    //   104: aload 4
    //   106: ifnull +11 -> 117
    //   109: aload 4
    //   111: invokevirtual 73	java/nio/channels/FileChannel:close	()V
    //   114: goto +3 -> 117
    //   117: aload_3
    //   118: ifnull +7 -> 125
    //   121: aload_3
    //   122: invokevirtual 74	java/io/RandomAccessFile:close	()V
    //   125: aload_0
    //   126: athrow
    //   127: astore 4
    //   129: goto -66 -> 63
    //   132: astore_3
    //   133: aload_0
    //   134: areturn
    //   135: astore 4
    //   137: goto -20 -> 117
    //   140: astore_3
    //   141: goto -16 -> 125
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	144	0	paramFile	java.io.File
    //   7	39	1	l	long
    //   32	90	3	localRandomAccessFile	java.io.RandomAccessFile
    //   132	1	3	localIOException1	IOException
    //   140	1	3	localIOException2	IOException
    //   1	109	4	localObject	Object
    //   127	1	4	localIOException3	IOException
    //   135	1	4	localIOException4	IOException
    //   37	34	5	localFileChannel	java.nio.channels.FileChannel
    // Exception table:
    //   from	to	target	type
    //   39	53	69	java/lang/Throwable
    //   33	39	77	java/lang/Throwable
    //   3	8	101	java/lang/Throwable
    //   22	33	101	java/lang/Throwable
    //   81	91	101	java/lang/Throwable
    //   91	101	101	java/lang/Throwable
    //   58	63	127	java/io/IOException
    //   63	67	132	java/io/IOException
    //   109	114	135	java/io/IOException
    //   121	125	140	java/io/IOException
  }
  
  public static ByteBuffer fromStream(InputStream paramInputStream)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(16384);
    byte[] arrayOfByte2 = (byte[])BUFFER_REF.getAndSet(null);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null) {
      arrayOfByte1 = new byte['?'];
    }
    for (;;)
    {
      int i = paramInputStream.read(arrayOfByte1);
      if (i < 0) {
        break;
      }
      localByteArrayOutputStream.write(arrayOfByte1, 0, i);
    }
    BUFFER_REF.set(arrayOfByte1);
    paramInputStream = localByteArrayOutputStream.toByteArray();
    return (ByteBuffer)ByteBuffer.allocateDirect(paramInputStream.length).put(paramInputStream).position(0);
  }
  
  private static SafeArray getSafeArray(ByteBuffer paramByteBuffer)
  {
    if ((!paramByteBuffer.isReadOnly()) && (paramByteBuffer.hasArray())) {
      return new SafeArray(paramByteBuffer.array(), paramByteBuffer.arrayOffset(), paramByteBuffer.limit());
    }
    return null;
  }
  
  public static byte[] toBytes(ByteBuffer paramByteBuffer)
  {
    Object localObject = getSafeArray(paramByteBuffer);
    if ((localObject != null) && (offset == 0) && (limit == data.length)) {
      return paramByteBuffer.array();
    }
    paramByteBuffer = paramByteBuffer.asReadOnlyBuffer();
    localObject = new byte[paramByteBuffer.limit()];
    paramByteBuffer.position(0);
    paramByteBuffer.get((byte[])localObject);
    return localObject;
  }
  
  /* Error */
  public static void toFile(ByteBuffer paramByteBuffer, java.io.File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: iconst_0
    //   2: invokevirtual 127	java/nio/ByteBuffer:position	(I)Ljava/nio/Buffer;
    //   5: pop
    //   6: new 43	java/io/RandomAccessFile
    //   9: dup
    //   10: aload_1
    //   11: ldc -85
    //   13: invokespecial 48	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   16: astore_2
    //   17: aload_2
    //   18: invokevirtual 52	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
    //   21: astore_3
    //   22: aload_3
    //   23: astore_1
    //   24: aload_3
    //   25: aload_0
    //   26: invokevirtual 174	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
    //   29: pop
    //   30: aload_3
    //   31: iconst_0
    //   32: invokevirtual 178	java/nio/channels/FileChannel:force	(Z)V
    //   35: aload_3
    //   36: invokevirtual 73	java/nio/channels/FileChannel:close	()V
    //   39: aload_2
    //   40: invokevirtual 74	java/io/RandomAccessFile:close	()V
    //   43: aload_3
    //   44: ifnull +7 -> 51
    //   47: aload_3
    //   48: invokevirtual 73	java/nio/channels/FileChannel:close	()V
    //   51: aload_2
    //   52: invokevirtual 74	java/io/RandomAccessFile:close	()V
    //   55: return
    //   56: astore_0
    //   57: goto +14 -> 71
    //   60: astore_0
    //   61: aconst_null
    //   62: astore_1
    //   63: goto +8 -> 71
    //   66: astore_0
    //   67: aconst_null
    //   68: astore_1
    //   69: aconst_null
    //   70: astore_2
    //   71: aload_1
    //   72: ifnull +10 -> 82
    //   75: aload_1
    //   76: invokevirtual 73	java/nio/channels/FileChannel:close	()V
    //   79: goto +3 -> 82
    //   82: aload_2
    //   83: ifnull +7 -> 90
    //   86: aload_2
    //   87: invokevirtual 74	java/io/RandomAccessFile:close	()V
    //   90: aload_0
    //   91: athrow
    //   92: astore_0
    //   93: goto -42 -> 51
    //   96: astore_0
    //   97: return
    //   98: astore_1
    //   99: goto -17 -> 82
    //   102: astore_1
    //   103: goto -13 -> 90
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	106	0	paramByteBuffer	ByteBuffer
    //   0	106	1	paramFile	java.io.File
    //   16	71	2	localRandomAccessFile	java.io.RandomAccessFile
    //   21	27	3	localFileChannel	java.nio.channels.FileChannel
    // Exception table:
    //   from	to	target	type
    //   24	43	56	java/lang/Throwable
    //   17	22	60	java/lang/Throwable
    //   6	17	66	java/lang/Throwable
    //   47	51	92	java/io/IOException
    //   51	55	96	java/io/IOException
    //   75	79	98	java/io/IOException
    //   86	90	102	java/io/IOException
  }
  
  public static InputStream toStream(ByteBuffer paramByteBuffer)
  {
    return new ByteBufferStream(paramByteBuffer);
  }
  
  public static void toStream(ByteBuffer paramByteBuffer, OutputStream paramOutputStream)
    throws IOException
  {
    Object localObject = getSafeArray(paramByteBuffer);
    if (localObject != null)
    {
      paramOutputStream.write(data, offset, offset + limit);
      return;
    }
    byte[] arrayOfByte = (byte[])BUFFER_REF.getAndSet(null);
    localObject = arrayOfByte;
    if (arrayOfByte == null) {
      localObject = new byte['?'];
    }
    while (paramByteBuffer.remaining() > 0)
    {
      int i = Math.min(paramByteBuffer.remaining(), localObject.length);
      paramByteBuffer.get((byte[])localObject, 0, i);
      paramOutputStream.write((byte[])localObject, 0, i);
    }
    BUFFER_REF.set(localObject);
  }
  
  private static class ByteBufferStream
    extends InputStream
  {
    private static final int UNSET = -1;
    @NonNull
    private final ByteBuffer byteBuffer;
    private int markPos = -1;
    
    ByteBufferStream(ByteBuffer paramByteBuffer)
    {
      byteBuffer = paramByteBuffer;
    }
    
    public int available()
    {
      return byteBuffer.remaining();
    }
    
    public void mark(int paramInt)
    {
      try
      {
        markPos = byteBuffer.position();
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public boolean markSupported()
    {
      return true;
    }
    
    public int read()
    {
      if (!byteBuffer.hasRemaining()) {
        return -1;
      }
      return byteBuffer.get();
    }
    
    public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      if (!byteBuffer.hasRemaining()) {
        return -1;
      }
      paramInt2 = Math.min(paramInt2, available());
      byteBuffer.get(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt2;
    }
    
    public void reset()
      throws IOException
    {
      try
      {
        if (markPos != -1)
        {
          byteBuffer.position(markPos);
          return;
        }
        throw new IOException("Cannot reset to unset mark position");
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public long skip(long paramLong)
      throws IOException
    {
      if (!byteBuffer.hasRemaining()) {
        return -1L;
      }
      paramLong = Math.min(paramLong, available());
      byteBuffer.position((int)(byteBuffer.position() + paramLong));
      return paramLong;
    }
  }
  
  static final class SafeArray
  {
    final byte[] data;
    final int limit;
    final int offset;
    
    SafeArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      data = paramArrayOfByte;
      offset = paramInt1;
      limit = paramInt2;
    }
  }
}
