package com.google.android.exoplayer2.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class ReusableBufferedOutputStream
  extends BufferedOutputStream
{
  private boolean closed;
  
  public ReusableBufferedOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }
  
  public ReusableBufferedOutputStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream, paramInt);
  }
  
  public void close()
    throws IOException
  {
    closed = true;
    try
    {
      flush();
      Object localObject1 = null;
    }
    catch (Throwable localThrowable1) {}
    Object localObject2;
    try
    {
      out.close();
      localObject2 = localThrowable1;
    }
    catch (Throwable localThrowable2)
    {
      localObject2 = localThrowable1;
      if (localThrowable1 == null) {
        localObject2 = localThrowable2;
      }
    }
    if (localObject2 != null) {
      Util.sneakyThrow((Throwable)localObject2);
    }
  }
  
  public void reset(OutputStream paramOutputStream)
  {
    Assertions.checkState(closed);
    out = paramOutputStream;
    count = 0;
    closed = false;
  }
}
