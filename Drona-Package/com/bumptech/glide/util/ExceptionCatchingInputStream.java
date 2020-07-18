package com.bumptech.glide.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;

public class ExceptionCatchingInputStream
  extends InputStream
{
  private static final Queue<ExceptionCatchingInputStream> QUEUE = Util.createQueue(0);
  private IOException exception;
  private InputStream wrapped;
  
  ExceptionCatchingInputStream() {}
  
  static void clearQueue()
  {
    while (!QUEUE.isEmpty()) {
      QUEUE.remove();
    }
  }
  
  public static ExceptionCatchingInputStream obtain(InputStream paramInputStream)
  {
    Object localObject = QUEUE;
    try
    {
      ExceptionCatchingInputStream localExceptionCatchingInputStream = (ExceptionCatchingInputStream)QUEUE.poll();
      localObject = localExceptionCatchingInputStream;
      if (localExceptionCatchingInputStream == null) {
        localObject = new ExceptionCatchingInputStream();
      }
      ((ExceptionCatchingInputStream)localObject).setInputStream(paramInputStream);
      return localObject;
    }
    catch (Throwable paramInputStream)
    {
      throw paramInputStream;
    }
  }
  
  public int available()
    throws IOException
  {
    return wrapped.available();
  }
  
  public void close()
    throws IOException
  {
    wrapped.close();
  }
  
  public IOException getException()
  {
    return exception;
  }
  
  public void mark(int paramInt)
  {
    wrapped.mark(paramInt);
  }
  
  public boolean markSupported()
  {
    return wrapped.markSupported();
  }
  
  public int read()
  {
    InputStream localInputStream = wrapped;
    try
    {
      int i = localInputStream.read();
      return i;
    }
    catch (IOException localIOException)
    {
      exception = localIOException;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte)
  {
    InputStream localInputStream = wrapped;
    try
    {
      int i = localInputStream.read(paramArrayOfByte);
      return i;
    }
    catch (IOException paramArrayOfByte)
    {
      exception = paramArrayOfByte;
    }
    return -1;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    InputStream localInputStream = wrapped;
    try
    {
      paramInt1 = localInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
      return paramInt1;
    }
    catch (IOException paramArrayOfByte)
    {
      exception = paramArrayOfByte;
    }
    return -1;
  }
  
  public void release()
  {
    exception = null;
    wrapped = null;
    Queue localQueue = QUEUE;
    try
    {
      QUEUE.offer(this);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void reset()
    throws IOException
  {
    try
    {
      wrapped.reset();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void setInputStream(InputStream paramInputStream)
  {
    wrapped = paramInputStream;
  }
  
  public long skip(long paramLong)
  {
    InputStream localInputStream = wrapped;
    try
    {
      paramLong = localInputStream.skip(paramLong);
      return paramLong;
    }
    catch (IOException localIOException)
    {
      exception = localIOException;
    }
    return 0L;
  }
}
