package com.bumptech.glide.load.data;

import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import java.io.IOException;
import java.io.OutputStream;

public final class BufferedOutputStream
  extends OutputStream
{
  private ArrayPool arrayPool;
  private byte[] buffer;
  private int index;
  @NonNull
  private final OutputStream out;
  
  public BufferedOutputStream(OutputStream paramOutputStream, ArrayPool paramArrayPool)
  {
    this(paramOutputStream, paramArrayPool, 65536);
  }
  
  BufferedOutputStream(OutputStream paramOutputStream, ArrayPool paramArrayPool, int paramInt)
  {
    out = paramOutputStream;
    arrayPool = paramArrayPool;
    buffer = ((byte[])paramArrayPool.w(paramInt, [B.class));
  }
  
  private void flushBuffer()
    throws IOException
  {
    if (index > 0)
    {
      out.write(buffer, 0, index);
      index = 0;
    }
  }
  
  private void maybeFlushBuffer()
    throws IOException
  {
    if (index == buffer.length) {
      flushBuffer();
    }
  }
  
  private void release()
  {
    if (buffer != null)
    {
      arrayPool.put(buffer);
      buffer = null;
    }
  }
  
  public void close()
    throws IOException
  {
    try
    {
      flush();
      out.close();
      release();
      return;
    }
    catch (Throwable localThrowable)
    {
      out.close();
      throw localThrowable;
    }
  }
  
  public void flush()
    throws IOException
  {
    flushBuffer();
    out.flush();
  }
  
  public void write(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = buffer;
    int i = index;
    index = (i + 1);
    arrayOfByte[i] = ((byte)paramInt);
    maybeFlushBuffer();
  }
  
  public void write(byte[] paramArrayOfByte)
    throws IOException
  {
    write(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = 0;
    int j;
    do
    {
      int k = paramInt2 - i;
      j = paramInt1 + i;
      if ((index == 0) && (k >= buffer.length))
      {
        out.write(paramArrayOfByte, j, k);
        return;
      }
      k = Math.min(k, buffer.length - index);
      System.arraycopy(paramArrayOfByte, j, buffer, index, k);
      index += k;
      j = i + k;
      maybeFlushBuffer();
      i = j;
    } while (j < paramInt2);
  }
}
