package com.bugsnag.android;

import java.io.IOException;
import java.util.List;

class CachedThread
  implements JsonStream.Streamable
{
  private final boolean isErrorReportingThread;
  private final String name;
  private Stacktrace stacktrace;
  private final String type;
  private final long value;
  
  private CachedThread(long paramLong, String paramString1, String paramString2, boolean paramBoolean, Stacktrace paramStacktrace)
  {
    value = paramLong;
    name = paramString1;
    type = paramString2;
    isErrorReportingThread = paramBoolean;
    stacktrace = paramStacktrace;
  }
  
  CachedThread(long paramLong, String paramString1, String paramString2, boolean paramBoolean, List paramList)
  {
    this(paramLong, paramString1, paramString2, paramBoolean, new Stacktrace(paramList));
  }
  
  CachedThread(Configuration paramConfiguration, long paramLong, String paramString1, String paramString2, boolean paramBoolean, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    this(paramLong, paramString1, paramString2, paramBoolean, new Stacktrace(paramArrayOfStackTraceElement, paramConfiguration.getProjectPackages()));
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("id").value(value);
    paramJsonStream.name("name").value(name);
    paramJsonStream.name("type").value(type);
    paramJsonStream.name("stacktrace").value(stacktrace);
    if (isErrorReportingThread) {
      paramJsonStream.name("errorReportingThread").value(true);
    }
    paramJsonStream.endObject();
  }
}
