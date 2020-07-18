package com.bumptech.glide.disklrucache;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

class StrictLineReader
  implements Closeable
{
  private static final byte CR = 13;
  private static final byte LF = 10;
  private byte[] buf;
  private final Charset charset;
  private int end;
  private final InputStream in;
  private int pos;
  
  public StrictLineReader(InputStream paramInputStream, int paramInt, Charset paramCharset)
  {
    if ((paramInputStream != null) && (paramCharset != null))
    {
      if (paramInt >= 0)
      {
        if (paramCharset.equals(Util.US_ASCII))
        {
          in = paramInputStream;
          charset = paramCharset;
          buf = new byte[paramInt];
          return;
        }
        throw new IllegalArgumentException("Unsupported encoding");
      }
      throw new IllegalArgumentException("capacity <= 0");
    }
    throw new NullPointerException();
  }
  
  public StrictLineReader(InputStream paramInputStream, Charset paramCharset)
  {
    this(paramInputStream, 8192, paramCharset);
  }
  
  private void fillBuf()
    throws IOException
  {
    int i = in.read(buf, 0, buf.length);
    if (i != -1)
    {
      pos = 0;
      end = i;
      return;
    }
    throw new EOFException();
  }
  
  public void close()
    throws IOException
  {
    InputStream localInputStream = in;
    try
    {
      if (buf != null)
      {
        buf = null;
        in.close();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean hasUnterminatedLine()
  {
    return end == -1;
  }
  
  public String readLine()
    throws IOException
  {
    InputStream localInputStream = in;
    for (;;)
    {
      int i;
      try
      {
        if (buf != null)
        {
          if (pos >= end) {
            fillBuf();
          }
          i = pos;
          Object localObject;
          if (i != end)
          {
            if (buf[i] == 10)
            {
              if (i != pos)
              {
                localObject = buf;
                j = i - 1;
                if (localObject[j] == 13)
                {
                  localObject = new String(buf, pos, j - pos, charset.name());
                  pos = (i + 1);
                  return localObject;
                }
              }
            }
            else {
              i += 1;
            }
          }
          else
          {
            localObject = new ByteArrayOutputStream(end - pos + 80)
            {
              public String toString()
              {
                int i;
                if ((count > 0) && (buf[(count - 1)] == 13)) {
                  i = count - 1;
                } else {
                  i = count;
                }
                Object localObject = buf;
                StrictLineReader localStrictLineReader = StrictLineReader.this;
                try
                {
                  localObject = new String((byte[])localObject, 0, i, charset.name());
                  return localObject;
                }
                catch (UnsupportedEncodingException localUnsupportedEncodingException)
                {
                  throw new AssertionError(localUnsupportedEncodingException);
                }
              }
            };
            ((ByteArrayOutputStream)localObject).write(buf, pos, end - pos);
            end = -1;
            fillBuf();
            i = pos;
            if (i != end)
            {
              if (buf[i] == 10)
              {
                if (i != pos) {
                  ((ByteArrayOutputStream)localObject).write(buf, pos, i - pos);
                }
                pos = (i + 1);
                localObject = ((ByteArrayOutputStream)localObject).toString();
                return localObject;
              }
              i += 1;
              continue;
            }
            continue;
          }
        }
        else
        {
          throw new IOException("LineReader is closed");
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      int j = i;
    }
  }
}
