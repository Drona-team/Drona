package com.bugsnag.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class Stacktrace
  implements JsonStream.Streamable
{
  private static final int STACKTRACE_TRIM_LENGTH = 200;
  private final List<Map<String, Object>> trace;
  
  Stacktrace(List paramList)
  {
    if (paramList.size() >= 200)
    {
      trace = paramList.subList(0, 200);
      return;
    }
    trace = paramList;
  }
  
  Stacktrace(StackTraceElement[] paramArrayOfStackTraceElement, String[] paramArrayOfString)
  {
    trace = serializeStacktrace(paramArrayOfStackTraceElement, sanitiseProjectPackages(paramArrayOfString));
  }
  
  private static boolean inProject(String paramString, List paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      String str = (String)paramList.next();
      if ((str != null) && (paramString.startsWith(str))) {
        return true;
      }
    }
    return false;
  }
  
  static boolean inProject(String paramString, String[] paramArrayOfString)
  {
    return inProject(paramString, sanitiseProjectPackages(paramArrayOfString));
  }
  
  private static List sanitiseProjectPackages(String[] paramArrayOfString)
  {
    if (paramArrayOfString != null) {
      return Arrays.asList(paramArrayOfString);
    }
    return Collections.emptyList();
  }
  
  private Map serializeStackframe(StackTraceElement paramStackTraceElement, List paramList)
  {
    HashMap localHashMap = new HashMap();
    try
    {
      int i = paramStackTraceElement.getClassName().length();
      if (i > 0)
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append(paramStackTraceElement.getClassName());
        ((StringBuilder)localObject).append(".");
        ((StringBuilder)localObject).append(paramStackTraceElement.getMethodName());
        localObject = ((StringBuilder)localObject).toString();
      }
      else
      {
        localObject = paramStackTraceElement.getMethodName();
      }
      localHashMap.put("method", localObject);
      Object localObject = paramStackTraceElement.getFileName();
      if (localObject == null) {
        localObject = "Unknown";
      } else {
        localObject = paramStackTraceElement.getFileName();
      }
      localHashMap.put("file", localObject);
      localHashMap.put("lineNumber", Integer.valueOf(paramStackTraceElement.getLineNumber()));
      boolean bool = inProject(paramStackTraceElement.getClassName(), paramList);
      if (bool)
      {
        localHashMap.put("inProject", Boolean.valueOf(true));
        return localHashMap;
      }
    }
    catch (Exception paramStackTraceElement)
    {
      Logger.warn("Failed to serialize stacktrace", paramStackTraceElement);
      return null;
    }
    return localHashMap;
  }
  
  private List serializeStacktrace(StackTraceElement[] paramArrayOfStackTraceElement, List paramList)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while ((i < paramArrayOfStackTraceElement.length) && (i < 200))
    {
      Map localMap = serializeStackframe(paramArrayOfStackTraceElement[i], paramList);
      if (localMap != null) {
        localArrayList.add(localMap);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginArray();
    Iterator localIterator = trace.iterator();
    while (localIterator.hasNext()) {
      paramJsonStream.value((Map)localIterator.next());
    }
    paramJsonStream.endArray();
  }
}
