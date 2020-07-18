package com.facebook.common.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Closeables
{
  @VisibleForTesting
  static final Logger logger = Logger.getLogger(Closeables.class.getName());
  
  private Closeables() {}
  
  public static void close(Closeable paramCloseable, boolean paramBoolean)
    throws IOException
  {
    if (paramCloseable == null) {
      return;
    }
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable)
    {
      if (paramBoolean)
      {
        logger.log(Level.WARNING, "IOException thrown while closing Closeable.", paramCloseable);
        return;
      }
      throw paramCloseable;
    }
  }
  
  public static void closeQuietly(InputStream paramInputStream)
  {
    try
    {
      close(paramInputStream, true);
      return;
    }
    catch (IOException paramInputStream)
    {
      throw new AssertionError(paramInputStream);
    }
  }
  
  public static void closeQuietly(Reader paramReader)
  {
    try
    {
      close(paramReader, true);
      return;
    }
    catch (IOException paramReader)
    {
      throw new AssertionError(paramReader);
    }
  }
}
