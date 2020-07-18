package com.bumptech.glide.disklrucache;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;

final class Util
{
  static final Charset US_ASCII = Charset.forName("US-ASCII");
  static final Charset UTF_8 = Charset.forName("UTF-8");
  
  private Util() {}
  
  static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
        return;
      }
      catch (RuntimeException paramCloseable)
      {
        throw paramCloseable;
      }
      catch (Exception paramCloseable) {}
    }
  }
  
  static void deleteContents(File paramFile)
    throws IOException
  {
    Object localObject = paramFile.listFiles();
    if (localObject != null)
    {
      int j = localObject.length;
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        paramFile = localObject[i];
        if (paramFile.isDirectory()) {
          deleteContents(paramFile);
        }
        if (!paramFile.delete()) {
          break;
        }
        i += 1;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("failed to delete file: ");
      ((StringBuilder)localObject).append(paramFile);
      throw new IOException(((StringBuilder)localObject).toString());
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("not a readable directory: ");
    ((StringBuilder)localObject).append(paramFile);
    throw new IOException(((StringBuilder)localObject).toString());
  }
  
  static String readFully(Reader paramReader)
    throws IOException
  {
    try
    {
      Object localObject = new StringWriter();
      char[] arrayOfChar = new char['?'];
      for (;;)
      {
        int i = paramReader.read(arrayOfChar);
        if (i == -1) {
          break;
        }
        ((StringWriter)localObject).write(arrayOfChar, 0, i);
      }
      localObject = ((StringWriter)localObject).toString();
      paramReader.close();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      paramReader.close();
      throw localThrowable;
    }
  }
}
