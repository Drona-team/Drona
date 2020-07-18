package com.bugsnag.android;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

class ObjectJsonStreamer
{
  private static final String FILTERED_PLACEHOLDER = "[FILTERED]";
  private static final String OBJECT_PLACEHOLDER = "[OBJECT]";
  String[] filters = { "password" };
  
  ObjectJsonStreamer() {}
  
  private boolean shouldFilter(String paramString)
  {
    if (filters != null)
    {
      if (paramString == null) {
        return false;
      }
      String[] arrayOfString = filters;
      int j = arrayOfString.length;
      int i = 0;
      while (i < j)
      {
        if (paramString.contains(arrayOfString[i])) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  void objectToStream(Object paramObject, JsonStream paramJsonStream)
    throws IOException
  {
    if (paramObject == null)
    {
      paramJsonStream.nullValue();
      return;
    }
    if ((paramObject instanceof String))
    {
      paramJsonStream.value((String)paramObject);
      return;
    }
    if ((paramObject instanceof Number))
    {
      paramJsonStream.value((Number)paramObject);
      return;
    }
    if ((paramObject instanceof Boolean))
    {
      paramJsonStream.value((Boolean)paramObject);
      return;
    }
    if ((paramObject instanceof Map))
    {
      paramJsonStream.beginObject();
      paramObject = ((Map)paramObject).entrySet().iterator();
      while (paramObject.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramObject.next();
        Object localObject = localEntry.getKey();
        if ((localObject instanceof String))
        {
          localObject = (String)localObject;
          paramJsonStream.name((String)localObject);
          if (shouldFilter((String)localObject)) {
            paramJsonStream.value("[FILTERED]");
          } else {
            objectToStream(localEntry.getValue(), paramJsonStream);
          }
        }
      }
      paramJsonStream.endObject();
      return;
    }
    if ((paramObject instanceof Collection))
    {
      paramJsonStream.beginArray();
      paramObject = ((Collection)paramObject).iterator();
      while (paramObject.hasNext()) {
        objectToStream(paramObject.next(), paramJsonStream);
      }
      paramJsonStream.endArray();
      return;
    }
    if (paramObject.getClass().isArray())
    {
      paramJsonStream.beginArray();
      int j = Array.getLength(paramObject);
      int i = 0;
      while (i < j)
      {
        objectToStream(Array.get(paramObject, i), paramJsonStream);
        i += 1;
      }
      paramJsonStream.endArray();
      return;
    }
    paramJsonStream.value("[OBJECT]");
  }
}
