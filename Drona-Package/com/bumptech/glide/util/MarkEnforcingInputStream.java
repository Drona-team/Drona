package com.bumptech.glide.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MarkEnforcingInputStream
  extends FilterInputStream
{
  private static final int END_OF_STREAM = -1;
  private static final int UNSET = Integer.MIN_VALUE;
  private int availableBytes = Integer.MIN_VALUE;
  
  public MarkEnforcingInputStream(InputStream paramInputStream)
  {
    super(paramInputStream);
  }
  
  private long getBytesToRead(long paramLong)
  {
    if (availableBytes == 0) {
      return -1L;
    }
    long l = paramLong;
    if (availableBytes != Integer.MIN_VALUE)
    {
      l = paramLong;
      if (paramLong > availableBytes) {
        l = availableBytes;
      }
    }
    return l;
  }
  
  private void updateAvailableBytesAfterRead(long paramLong)
  {
    if ((availableBytes != Integer.MIN_VALUE) && (paramLong != -1L)) {
      availableBytes = ((int)(availableBytes - paramLong));
    }
  }
  
  public int available()
    throws IOException
  {
    if (availableBytes == Integer.MIN_VALUE) {
      return super.available();
    }
    return Math.min(availableBytes, super.available());
  }
  
  public void mark(int paramInt)
  {
    try
    {
      super.mark(paramInt);
      availableBytes = paramInt;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int read()
    throws IOException
  {
    if (getBytesToRead(1L) == -1L) {
      return -1;
    }
    int i = super.read();
    updateAvailableBytesAfterRead(1L);
    return i;
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    paramInt2 = (int)getBytesToRead(paramInt2);
    if (paramInt2 == -1) {
      return -1;
    }
    paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    updateAvailableBytesAfterRead(paramInt1);
    return paramInt1;
  }
  
  public void reset()
    throws IOException
  {
    try
    {
      super.reset();
      availableBytes = Integer.MIN_VALUE;
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long skip(long paramLong)
    throws IOException
  {
    paramLong = getBytesToRead(paramLong);
    if (paramLong == -1L) {
      return 0L;
    }
    paramLong = super.skip(paramLong);
    updateAvailableBytesAfterRead(paramLong);
    return paramLong;
  }
}
