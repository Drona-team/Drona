package com.facebook.common.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

public class Files
{
  private Files() {}
  
  static byte[] readFile(InputStream paramInputStream, long paramLong)
    throws IOException
  {
    if (paramLong <= 2147483647L)
    {
      if (paramLong == 0L) {
        return ByteStreams.toByteArray(paramInputStream);
      }
      return ByteStreams.toByteArray(paramInputStream, (int)paramLong);
    }
    paramInputStream = new StringBuilder();
    paramInputStream.append("file is too large to fit in a byte array: ");
    paramInputStream.append(paramLong);
    paramInputStream.append(" bytes");
    throw new OutOfMemoryError(paramInputStream.toString());
  }
  
  public static byte[] toByteArray(File paramFile)
    throws IOException
  {
    try
    {
      paramFile = new FileInputStream(paramFile);
      try
      {
        byte[] arrayOfByte = readFile(paramFile, paramFile.getChannel().size());
        paramFile.close();
        return arrayOfByte;
      }
      catch (Throwable localThrowable1) {}
      if (paramFile == null) {
        break label42;
      }
    }
    catch (Throwable localThrowable2)
    {
      paramFile = null;
    }
    paramFile.close();
    label42:
    throw localThrowable2;
  }
}