package client.testing.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public class IOUtils
{
  private static final int DEFAULT_BUFFER_SIZE = 4096;
  private static final String DEFAULT_CHARSET = "UTF-8";
  
  public IOUtils() {}
  
  private static long copy(Reader paramReader, Writer paramWriter)
    throws IOException
  {
    return copy(paramReader, paramWriter, new char['?']);
  }
  
  private static long copy(Reader paramReader, Writer paramWriter, char[] paramArrayOfChar)
    throws IOException
  {
    long l = 0L;
    for (int i = paramReader.read(paramArrayOfChar); i > 0; i = paramReader.read(paramArrayOfChar))
    {
      paramWriter.write(paramArrayOfChar, 0, i);
      l += i;
    }
    return l;
  }
  
  public static String readAll(InputStream paramInputStream)
    throws IOException
  {
    return readAll(paramInputStream, "UTF-8");
  }
  
  public static String readAll(InputStream paramInputStream, String paramString)
    throws IOException
  {
    paramInputStream = new InputStreamReader(paramInputStream, paramString);
    try
    {
      paramString = readAll(paramInputStream);
      paramInputStream.close();
      return paramString;
    }
    catch (Throwable paramString)
    {
      try
      {
        throw paramString;
      }
      catch (Throwable localThrowable)
      {
        if (paramString != null) {
          try
          {
            paramInputStream.close();
          }
          catch (Throwable paramInputStream)
          {
            paramString.addSuppressed(paramInputStream);
          }
        } else {
          paramInputStream.close();
        }
        throw localThrowable;
      }
    }
  }
  
  public static String readAll(InputStream paramInputStream, Charset paramCharset)
    throws IOException
  {
    return readAll(paramInputStream, paramCharset.name());
  }
  
  public static String readAll(InputStreamReader paramInputStreamReader)
    throws IOException
  {
    StringWriter localStringWriter = new StringWriter();
    copy(paramInputStreamReader, localStringWriter);
    return localStringWriter.toString();
  }
  
  public static void writeAll(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    writeAll(paramString, paramOutputStream, "UTF-8");
  }
  
  public static void writeAll(String paramString1, OutputStream paramOutputStream, String paramString2)
    throws IOException
  {
    if ((paramString1 != null) && (paramString1.length() > 0)) {
      paramOutputStream.write(paramString1.getBytes(paramString2));
    }
  }
  
  public static void writeAll(String paramString, OutputStream paramOutputStream, Charset paramCharset)
    throws IOException
  {
    writeAll(paramString, paramOutputStream, paramCharset.name());
  }
}
