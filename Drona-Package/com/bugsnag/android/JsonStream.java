package com.bugsnag.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

public class JsonStream
  extends JsonWriter
{
  private final ObjectJsonStreamer objectJsonStreamer;
  private final Writer out;
  
  public JsonStream(Writer paramWriter)
  {
    super(paramWriter);
    setSerializeNulls(false);
    out = paramWriter;
    objectJsonStreamer = new ObjectJsonStreamer();
  }
  
  public JsonStream name(String paramString)
    throws IOException
  {
    super.name(paramString);
    return this;
  }
  
  public void value(Streamable paramStreamable)
    throws IOException
  {
    if (paramStreamable == null)
    {
      nullValue();
      return;
    }
    paramStreamable.toStream(this);
  }
  
  public void value(File paramFile)
    throws IOException
  {
    if (paramFile != null)
    {
      if (paramFile.length() <= 0L) {
        return;
      }
      super.flush();
      beforeValue();
      Object localObject = null;
      try
      {
        paramFile = new BufferedReader(new InputStreamReader(new FileInputStream(paramFile), "UTF-8"));
        try
        {
          IOUtils.copy(paramFile, out);
          IOUtils.closeQuietly(paramFile);
          out.flush();
          return;
        }
        catch (Throwable localThrowable1) {}
        IOUtils.closeQuietly(paramFile);
      }
      catch (Throwable localThrowable2)
      {
        paramFile = localObject;
      }
      throw localThrowable2;
    }
  }
  
  public void value(Object paramObject)
    throws IOException
  {
    objectJsonStreamer.objectToStream(paramObject, this);
  }
  
  public static abstract interface Streamable
  {
    public abstract void toStream(JsonStream paramJsonStream)
      throws IOException;
  }
}
