package com.facebook.react.util;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSStackTrace
{
  private static final String COLUMN_KEY = "column";
  private static final Pattern FILE_ID_PATTERN = Pattern.compile("\\b((?:seg-\\d+(?:_\\d+)?|\\d+)\\.js)");
  private static final String FILE_KEY = "file";
  private static final String LINE_NUMBER_KEY = "lineNumber";
  private static final String METHOD_NAME_KEY = "methodName";
  
  public JSStackTrace() {}
  
  public static String format(String paramString, ReadableArray paramReadableArray)
  {
    paramString = new StringBuilder(paramString);
    paramString.append(", stack:\n");
    int i = 0;
    while (i < paramReadableArray.size())
    {
      ReadableMap localReadableMap = paramReadableArray.getMap(i);
      paramString.append(localReadableMap.getString("methodName"));
      paramString.append("@");
      paramString.append(parseFileId(localReadableMap));
      if ((localReadableMap.hasKey("lineNumber")) && (!localReadableMap.isNull("lineNumber")) && (localReadableMap.getType("lineNumber") == ReadableType.Number)) {
        paramString.append(localReadableMap.getInt("lineNumber"));
      } else {
        paramString.append(-1);
      }
      if ((localReadableMap.hasKey("column")) && (!localReadableMap.isNull("column")) && (localReadableMap.getType("column") == ReadableType.Number))
      {
        paramString.append(":");
        paramString.append(localReadableMap.getInt("column"));
      }
      paramString.append("\n");
      i += 1;
    }
    return paramString.toString();
  }
  
  private static String parseFileId(ReadableMap paramReadableMap)
  {
    if ((paramReadableMap.hasKey("file")) && (!paramReadableMap.isNull("file")) && (paramReadableMap.getType("file") == ReadableType.String))
    {
      paramReadableMap = paramReadableMap.getString("file");
      if (paramReadableMap != null)
      {
        paramReadableMap = FILE_ID_PATTERN.matcher(paramReadableMap);
        if (paramReadableMap.find())
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append(paramReadableMap.group(1));
          localStringBuilder.append(":");
          return localStringBuilder.toString();
        }
      }
    }
    return "";
  }
}
