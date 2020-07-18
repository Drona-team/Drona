package com.facebook.react.bridge;

import android.util.JsonWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JsonWriterHelper
{
  public JsonWriterHelper() {}
  
  private static void dynamicValue(JsonWriter paramJsonWriter, Dynamic paramDynamic)
    throws IOException
  {
    switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[paramDynamic.getType().ordinal()])
    {
    default: 
      paramJsonWriter = new StringBuilder();
      paramJsonWriter.append("Unknown data type: ");
      paramJsonWriter.append(paramDynamic.getType());
      throw new IllegalArgumentException(paramJsonWriter.toString());
    case 6: 
      readableArrayValue(paramJsonWriter, paramDynamic.asArray());
      return;
    case 5: 
      readableMapValue(paramJsonWriter, paramDynamic.asMap());
      return;
    case 4: 
      paramJsonWriter.value(paramDynamic.asString());
      return;
    case 3: 
      paramJsonWriter.value(paramDynamic.asDouble());
      return;
    case 2: 
      paramJsonWriter.value(paramDynamic.asBoolean());
      return;
    }
    paramJsonWriter.nullValue();
  }
  
  private static void listValue(JsonWriter paramJsonWriter, List paramList)
    throws IOException
  {
    paramJsonWriter.beginArray();
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      objectValue(paramJsonWriter, paramList.next());
    }
    paramJsonWriter.endArray();
  }
  
  private static void mapValue(JsonWriter paramJsonWriter, Map paramMap)
    throws IOException
  {
    paramJsonWriter.beginObject();
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      paramJsonWriter.name(localEntry.getKey().toString());
      value(paramJsonWriter, localEntry.getValue());
    }
    paramJsonWriter.endObject();
  }
  
  private static void objectValue(JsonWriter paramJsonWriter, Object paramObject)
    throws IOException
  {
    if (paramObject == null)
    {
      paramJsonWriter.nullValue();
      return;
    }
    if ((paramObject instanceof String))
    {
      paramJsonWriter.value((String)paramObject);
      return;
    }
    if ((paramObject instanceof Number))
    {
      paramJsonWriter.value((Number)paramObject);
      return;
    }
    if ((paramObject instanceof Boolean))
    {
      paramJsonWriter.value(((Boolean)paramObject).booleanValue());
      return;
    }
    paramJsonWriter = new StringBuilder();
    paramJsonWriter.append("Unknown value: ");
    paramJsonWriter.append(paramObject);
    throw new IllegalArgumentException(paramJsonWriter.toString());
  }
  
  public static void readableArrayValue(JsonWriter paramJsonWriter, ReadableArray paramReadableArray)
    throws IOException
  {
    paramJsonWriter.beginArray();
    int i = 0;
    try
    {
      for (;;)
      {
        int j = paramReadableArray.size();
        if (i >= j) {
          break label226;
        }
        j = 1.$SwitchMap$com$facebook$react$bridge$ReadableType[paramReadableArray.getType(i).ordinal()];
        switch (j)
        {
        default: 
          break;
        case 6: 
          readableArrayValue(paramJsonWriter, paramReadableArray.getArray(i));
          break;
        case 5: 
          readableMapValue(paramJsonWriter, paramReadableArray.getMap(i));
          break;
        case 4: 
          paramJsonWriter.value(paramReadableArray.getString(i));
          break;
        case 3: 
          paramJsonWriter.value(paramReadableArray.getDouble(i));
          break;
        case 2: 
          paramJsonWriter.value(paramReadableArray.getBoolean(i));
          break;
        case 1: 
          paramJsonWriter.nullValue();
        }
        i += 1;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unknown data type: ");
      localStringBuilder.append(paramReadableArray.getType(i));
      throw new IllegalArgumentException(localStringBuilder.toString());
      label226:
      paramJsonWriter.endArray();
      return;
    }
    catch (Throwable paramReadableArray)
    {
      paramJsonWriter.endArray();
      throw paramReadableArray;
    }
  }
  
  private static void readableMapValue(JsonWriter paramJsonWriter, ReadableMap paramReadableMap)
    throws IOException
  {
    paramJsonWriter.beginObject();
    try
    {
      Object localObject = paramReadableMap.keySetIterator();
      String str;
      for (;;)
      {
        boolean bool = ((ReadableMapKeySetIterator)localObject).hasNextKey();
        if (!bool) {
          break label252;
        }
        str = ((ReadableMapKeySetIterator)localObject).nextKey();
        paramJsonWriter.name(str);
        int i = 1.$SwitchMap$com$facebook$react$bridge$ReadableType[paramReadableMap.getType(str).ordinal()];
        switch (i)
        {
        default: 
          break;
        case 6: 
          readableArrayValue(paramJsonWriter, paramReadableMap.getArray(str));
          break;
        case 5: 
          readableMapValue(paramJsonWriter, paramReadableMap.getMap(str));
          break;
        case 4: 
          paramJsonWriter.value(paramReadableMap.getString(str));
          break;
        case 3: 
          paramJsonWriter.value(paramReadableMap.getDouble(str));
          break;
        case 2: 
          paramJsonWriter.value(paramReadableMap.getBoolean(str));
          break;
        case 1: 
          paramJsonWriter.nullValue();
        }
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Unknown data type: ");
      ((StringBuilder)localObject).append(paramReadableMap.getType(str));
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      label252:
      paramJsonWriter.endObject();
      return;
    }
    catch (Throwable paramReadableMap)
    {
      paramJsonWriter.endObject();
      throw paramReadableMap;
    }
  }
  
  public static void value(JsonWriter paramJsonWriter, Object paramObject)
    throws IOException
  {
    if ((paramObject instanceof Map))
    {
      mapValue(paramJsonWriter, (Map)paramObject);
      return;
    }
    if ((paramObject instanceof List))
    {
      listValue(paramJsonWriter, (List)paramObject);
      return;
    }
    if ((paramObject instanceof ReadableMap))
    {
      readableMapValue(paramJsonWriter, (ReadableMap)paramObject);
      return;
    }
    if ((paramObject instanceof ReadableArray))
    {
      readableArrayValue(paramJsonWriter, (ReadableArray)paramObject);
      return;
    }
    if ((paramObject instanceof Dynamic))
    {
      dynamicValue(paramJsonWriter, (Dynamic)paramObject);
      return;
    }
    objectValue(paramJsonWriter, paramObject);
  }
}
