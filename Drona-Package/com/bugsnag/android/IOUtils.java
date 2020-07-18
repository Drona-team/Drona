package com.bugsnag.android;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URLConnection;

class IOUtils
{
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  private static final int EOF = -1;
  
  IOUtils() {}
  
  public static void close(URLConnection paramURLConnection)
  {
    if ((paramURLConnection instanceof HttpURLConnection)) {
      ((HttpURLConnection)paramURLConnection).disconnect();
    }
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null) {
      try
      {
        paramCloseable.close();
        return;
      }
      catch (Exception paramCloseable) {}
    }
  }
  
  public static int copy(Reader paramReader, Writer paramWriter)
    throws IOException
  {
    char[] arrayOfChar = new char['?'];
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramReader.read(arrayOfChar);
      if (-1 == i) {
        break;
      }
      paramWriter.write(arrayOfChar, 0, i);
    }
    if (l > 2147483647L) {
      return -1;
    }
    return (int)l;
  }
  
  static void deleteFile(File paramFile)
  {
    try
    {
      boolean bool = paramFile.delete();
      if (!bool)
      {
        paramFile.deleteOnExit();
        return;
      }
    }
    catch (Exception paramFile)
    {
      Logger.warn("Failed to delete file", paramFile);
    }
  }
}
