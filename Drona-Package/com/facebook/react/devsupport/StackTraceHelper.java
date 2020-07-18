package com.facebook.react.devsupport;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.devsupport.interfaces.StackFrame;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StackTraceHelper
{
  public static final String COLUMN_KEY = "column";
  public static final String LINE_NUMBER_KEY = "lineNumber";
  private static final Pattern STACK_FRAME_PATTERN1 = Pattern.compile("^(?:(.*?)@)?(.*?)\\:([0-9]+)\\:([0-9]+)$");
  private static final Pattern STACK_FRAME_PATTERN2 = Pattern.compile("\\s*(?:at)\\s*(.+?)\\s*[@(](.*):([0-9]+):([0-9]+)[)]$");
  
  public StackTraceHelper() {}
  
  public static StackFrame[] convertJavaStackTrace(Throwable paramThrowable)
  {
    paramThrowable = paramThrowable.getStackTrace();
    StackFrame[] arrayOfStackFrame = new StackFrame[paramThrowable.length];
    int i = 0;
    while (i < paramThrowable.length)
    {
      arrayOfStackFrame[i] = new StackFrameImpl(paramThrowable[i].getClassName(), paramThrowable[i].getFileName(), paramThrowable[i].getMethodName(), paramThrowable[i].getLineNumber(), -1, null);
      i += 1;
    }
    return arrayOfStackFrame;
  }
  
  public static StackFrame[] convertJsStackTrace(ReadableArray paramReadableArray)
  {
    int j = 0;
    int i;
    if (paramReadableArray != null) {
      i = paramReadableArray.size();
    } else {
      i = 0;
    }
    StackFrame[] arrayOfStackFrame = new StackFrame[i];
    while (j < i)
    {
      Object localObject = paramReadableArray.getType(j);
      if (localObject == ReadableType.GIF)
      {
        localObject = paramReadableArray.getMap(j);
        String str1 = ((ReadableMap)localObject).getString("methodName");
        String str2 = ((ReadableMap)localObject).getString("file");
        int k;
        if ((((ReadableMap)localObject).hasKey("lineNumber")) && (!((ReadableMap)localObject).isNull("lineNumber"))) {
          k = ((ReadableMap)localObject).getInt("lineNumber");
        } else {
          k = -1;
        }
        int m;
        if ((((ReadableMap)localObject).hasKey("column")) && (!((ReadableMap)localObject).isNull("column"))) {
          m = ((ReadableMap)localObject).getInt("column");
        } else {
          m = -1;
        }
        arrayOfStackFrame[j] = new StackFrameImpl(str2, str1, k, m, null);
      }
      else if (localObject == ReadableType.String)
      {
        arrayOfStackFrame[j] = new StackFrameImpl(null, paramReadableArray.getString(j), -1, -1, null);
      }
      j += 1;
    }
    return arrayOfStackFrame;
  }
  
  public static StackFrame[] convertJsStackTrace(String paramString)
  {
    String[] arrayOfString = paramString.split("\n");
    StackFrame[] arrayOfStackFrame = new StackFrame[arrayOfString.length];
    int i = 0;
    while (i < arrayOfString.length)
    {
      Object localObject1 = STACK_FRAME_PATTERN1.matcher(arrayOfString[i]);
      paramString = (String)localObject1;
      Object localObject2 = STACK_FRAME_PATTERN2.matcher(arrayOfString[i]);
      if (((Matcher)localObject2).find()) {
        paramString = (String)localObject2;
      } else {
        if (!((Matcher)localObject1).find()) {
          break label130;
        }
      }
      localObject2 = paramString.group(2);
      if (paramString.group(1) == null) {}
      for (localObject1 = "(unknown)";; localObject1 = paramString.group(1)) {
        break;
      }
      arrayOfStackFrame[i] = new StackFrameImpl((String)localObject2, (String)localObject1, Integer.parseInt(paramString.group(3)), Integer.parseInt(paramString.group(4)), null);
      break label149;
      label130:
      arrayOfStackFrame[i] = new StackFrameImpl(null, arrayOfString[i], -1, -1, null);
      label149:
      i += 1;
    }
    return arrayOfStackFrame;
  }
  
  public static StackFrame[] convertJsStackTrace(JSONArray paramJSONArray)
  {
    int j = 0;
    int i;
    if (paramJSONArray != null) {
      i = paramJSONArray.length();
    } else {
      i = 0;
    }
    StackFrame[] arrayOfStackFrame = new StackFrame[i];
    while (j < i) {
      try
      {
        Object localObject = paramJSONArray.getJSONObject(j);
        String str1 = ((JSONObject)localObject).getString("methodName");
        String str2 = ((JSONObject)localObject).getString("file");
        boolean bool = ((JSONObject)localObject).has("lineNumber");
        if (bool)
        {
          bool = ((JSONObject)localObject).isNull("lineNumber");
          if (!bool)
          {
            k = ((JSONObject)localObject).getInt("lineNumber");
            break label93;
          }
        }
        int k = -1;
        label93:
        bool = ((JSONObject)localObject).has("column");
        if (bool)
        {
          bool = ((JSONObject)localObject).isNull("column");
          if (!bool)
          {
            m = ((JSONObject)localObject).getInt("column");
            break label136;
          }
        }
        int m = -1;
        label136:
        localObject = new StackFrameImpl(str2, str1, k, m, null);
        arrayOfStackFrame[j] = localObject;
        j += 1;
      }
      catch (JSONException paramJSONArray)
      {
        throw new RuntimeException(paramJSONArray);
      }
    }
    return arrayOfStackFrame;
  }
  
  public static String formatFrameSource(StackFrame paramStackFrame)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramStackFrame.getFileName());
    int i = paramStackFrame.getLine();
    if (i > 0)
    {
      localStringBuilder.append(":");
      localStringBuilder.append(i);
      i = paramStackFrame.getColumn();
      if (i > 0)
      {
        localStringBuilder.append(":");
        localStringBuilder.append(i);
      }
    }
    return localStringBuilder.toString();
  }
  
  public static String formatStackTrace(String paramString, StackFrame[] paramArrayOfStackFrame)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("\n");
    int j = paramArrayOfStackFrame.length;
    int i = 0;
    while (i < j)
    {
      paramString = paramArrayOfStackFrame[i];
      localStringBuilder.append(paramString.getMethod());
      localStringBuilder.append("\n");
      localStringBuilder.append("    ");
      localStringBuilder.append(formatFrameSource(paramString));
      localStringBuilder.append("\n");
      i += 1;
    }
    return localStringBuilder.toString();
  }
  
  public static class StackFrameImpl
    implements StackFrame
  {
    private final int mColumn;
    private final String mFile;
    private final String mFileName;
    private final int mLine;
    private final String mMethod;
    
    private StackFrameImpl(String paramString1, String paramString2, int paramInt1, int paramInt2)
    {
      mFile = paramString1;
      mMethod = paramString2;
      mLine = paramInt1;
      mColumn = paramInt2;
      if (paramString1 != null) {
        paramString1 = new File(paramString1).getName();
      } else {
        paramString1 = "";
      }
      mFileName = paramString1;
    }
    
    private StackFrameImpl(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2)
    {
      mFile = paramString1;
      mFileName = paramString2;
      mMethod = paramString3;
      mLine = paramInt1;
      mColumn = paramInt2;
    }
    
    public int getColumn()
    {
      return mColumn;
    }
    
    public String getFile()
    {
      return mFile;
    }
    
    public String getFileName()
    {
      return mFileName;
    }
    
    public int getLine()
    {
      return mLine;
    }
    
    public String getMethod()
    {
      return mMethod;
    }
    
    public JSONObject toJSON()
    {
      return new JSONObject(MapBuilder.get("file", getFile(), "methodName", getMethod(), "lineNumber", Integer.valueOf(getLine()), "column", Integer.valueOf(getColumn())));
    }
  }
}