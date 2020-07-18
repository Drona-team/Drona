package com.facebook.common.streams;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TailAppendingInputStream
  extends FilterInputStream
{
  private int mMarkedTailOffset;
  private final byte[] mTail;
  private int mTailOffset;
  
  public TailAppendingInputStream(InputStream paramInputStream, byte[] paramArrayOfByte)
  {
    super(paramInputStream);
    if (paramInputStream != null)
    {
      if (paramArrayOfByte != null)
      {
        mTail = paramArrayOfByte;
        return;
      }
      throw new NullPointerException();
    }
    throw new NullPointerException();
  }
  
  private int readNextTailByte()
  {
    if (mTailOffset >= mTail.length) {
      return -1;
    }
    byte[] arrayOfByte = mTail;
    int i = mTailOffset;
    mTailOffset = (i + 1);
    return arrayOfByte[i] & 0xFF;
  }
  
  public void mark(int paramInt)
  {
    if (in.markSupported())
    {
      super.mark(paramInt);
      mMarkedTailOffset = mTailOffset;
    }
  }
  
  public int read()
    throws IOException
  {
    int i = in.read();
    if (i != -1) {
      return i;
    }
    return readNextTailByte();
  }
  
  public int read(byte[] paramArrayOfByte)
    throws IOException
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }
  
  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException
  {
    int i = in.read(paramArrayOfByte, paramInt1, paramInt2);
    if (i != -1) {
      return i;
    }
    i = 0;
    if (paramInt2 == 0) {
      return 0;
    }
    while (i < paramInt2)
    {
      int j = readNextTailByte();
      if (j == -1) {
        break;
      }
      paramArrayOfByte[(paramInt1 + i)] = ((byte)j);
      i += 1;
    }
    if (i > 0) {
      return i;
    }
    return -1;
  }
  
  public void reset()
    throws IOException
  {
    if (in.markSupported())
    {
      in.reset();
      mTailOffset = mMarkedTailOffset;
      return;
    }
    throw new IOException("mark is not supported");
  }
}
