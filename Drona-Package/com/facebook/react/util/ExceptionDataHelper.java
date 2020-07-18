package com.facebook.react.util;

import android.util.JsonWriter;
import com.facebook.react.bridge.JsonWriterHelper;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class ExceptionDataHelper
{
  static final String EXTRA_DATA_FIELD = "extraData";
  
  public ExceptionDataHelper() {}
  
  public static String getExtraDataAsJson(ReadableMap paramReadableMap)
  {
    if (paramReadableMap != null) {
      if (paramReadableMap.getType("extraData") == ReadableType.Null) {
        return null;
      }
    }
    try
    {
      StringWriter localStringWriter = new StringWriter();
      JsonWriter localJsonWriter = new JsonWriter(localStringWriter);
      JsonWriterHelper.value(localJsonWriter, paramReadableMap.getDynamic("extraData"));
      localJsonWriter.close();
      localStringWriter.close();
      paramReadableMap = localStringWriter.toString();
      return paramReadableMap;
    }
    catch (IOException paramReadableMap) {}
    return null;
    return null;
  }
}
