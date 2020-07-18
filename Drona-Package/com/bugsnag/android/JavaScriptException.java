package com.bugsnag.android;

import com.bugsnag.BugsnagReactNative;
import java.io.IOException;
import java.util.logging.Logger;

public class JavaScriptException
  extends BugsnagException
  implements JsonStream.Streamable
{
  private static final String EXCEPTION_TYPE = "browserjs";
  private static final long serialVersionUID = 1175784680140218622L;
  private final String rawStacktrace;
  
  public JavaScriptException(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1, paramString2, new StackTraceElement[0]);
    super.setType("browserjs");
    rawStacktrace = paramString3;
  }
  
  private Integer parseIntSafe(String paramString)
  {
    try
    {
      int i = Integer.parseInt(paramString);
      return Integer.valueOf(i);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    BugsnagReactNative.logger.info(String.format("Expected an integer, got: '%s'", new Object[] { paramString }));
    return null;
  }
  
  private void serialiseHermesFrame(JsonStream paramJsonStream, String paramString)
    throws IOException
  {
    int k = Math.max(paramString.lastIndexOf(" "), paramString.lastIndexOf("("));
    int m = paramString.lastIndexOf(")");
    int j = 0;
    int i;
    if ((k > -1) && (k < m)) {
      i = 1;
    } else {
      i = 0;
    }
    int n = "at ".length();
    int i1 = paramString.indexOf(" (");
    if (n < i1) {
      j = 1;
    }
    if ((i != 0) || (j != 0))
    {
      paramJsonStream.beginObject();
      paramJsonStream.name("method").value(paramString.substring(n, i1));
      if (i != 0)
      {
        paramString = paramString.substring(k + 1, m);
        Object localObject = paramString.replaceFirst(":\\d+:\\d+$", "");
        paramJsonStream.name("file").value((String)localObject);
        localObject = paramString.split(":");
        if (localObject.length >= 2)
        {
          paramString = parseIntSafe(localObject[(localObject.length - 2)]);
          localObject = parseIntSafe(localObject[(localObject.length - 1)]);
          if (paramString != null) {
            paramJsonStream.name("lineNumber").value(paramString);
          }
          if (localObject != null) {
            paramJsonStream.name("columnNumber").value((Number)localObject);
          }
        }
      }
      paramJsonStream.endObject();
    }
  }
  
  private void serialiseJsCoreFrame(JsonStream paramJsonStream, String paramString)
    throws IOException
  {
    paramJsonStream.beginObject();
    Object localObject = paramString.split("@", 2);
    paramString = localObject[0];
    if (localObject.length == 2)
    {
      paramJsonStream.name("method").value(localObject[0]);
      paramString = localObject[1];
    }
    int i = paramString.lastIndexOf(":");
    localObject = paramString;
    if (i != -1)
    {
      localObject = parseIntSafe(paramString.substring(i + 1));
      if (localObject != null) {
        paramJsonStream.name("columnNumber").value((Number)localObject);
      }
      localObject = paramString.substring(0, i);
    }
    i = ((String)localObject).lastIndexOf(":");
    paramString = (String)localObject;
    if (i != -1)
    {
      paramString = parseIntSafe(((String)localObject).substring(i + 1));
      if (paramString != null) {
        paramJsonStream.name("lineNumber").value(paramString);
      }
      paramString = ((String)localObject).substring(0, i);
    }
    paramJsonStream.name("file").value(paramString);
    paramJsonStream.endObject();
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("errorClass").value(getName());
    paramJsonStream.name("message").value(getMessage());
    paramJsonStream.name("type").value(getType());
    paramJsonStream.name("stacktrace");
    paramJsonStream.beginArray();
    boolean bool = rawStacktrace.matches("(?s).*\\sat .* \\(.*\\d+:\\d+\\)\\s.*");
    String[] arrayOfString = rawStacktrace.split("\\n");
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = arrayOfString[i];
      if (bool) {
        serialiseHermesFrame(paramJsonStream, str.trim());
      } else {
        serialiseJsCoreFrame(paramJsonStream, str.trim());
      }
      i += 1;
    }
    paramJsonStream.endArray();
    paramJsonStream.endObject();
  }
}
