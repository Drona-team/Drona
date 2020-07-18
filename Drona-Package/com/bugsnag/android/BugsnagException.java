package com.bugsnag.android;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class BugsnagException
  extends Throwable
  implements JsonStream.Streamable
{
  private static final long serialVersionUID = 5068182621179433346L;
  private final List<Map<String, Object>> customStackframes;
  private String message;
  private String name;
  private String[] projectPackages;
  private JsonStream.Streamable streamable;
  private String type = "android";
  
  BugsnagException(String paramString1, String paramString2, List paramList)
  {
    super(paramString2);
    setStackTrace(new StackTraceElement[0]);
    name = paramString1;
    customStackframes = paramList;
  }
  
  public BugsnagException(String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement)
  {
    super(paramString2);
    setStackTrace(paramArrayOfStackTraceElement);
    name = paramString1;
    customStackframes = null;
  }
  
  BugsnagException(Throwable paramThrowable)
  {
    super(paramThrowable.getMessage());
    if ((paramThrowable instanceof JsonStream.Streamable))
    {
      streamable = ((JsonStream.Streamable)paramThrowable);
      name = "";
    }
    else
    {
      name = paramThrowable.getClass().getName();
    }
    setStackTrace(paramThrowable.getStackTrace());
    initCause(paramThrowable.getCause());
    customStackframes = null;
  }
  
  public String getMessage()
  {
    if (message != null) {
      return message;
    }
    return super.getMessage();
  }
  
  public String getName()
  {
    return name;
  }
  
  String getType()
  {
    return type;
  }
  
  public void setMessage(String paramString)
  {
    message = paramString;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  void setProjectPackages(String[] paramArrayOfString)
  {
    projectPackages = paramArrayOfString;
  }
  
  void setType(String paramString)
  {
    type = paramString;
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    if (streamable != null)
    {
      streamable.toStream(paramJsonStream);
      return;
    }
    Object localObject = customStackframes;
    if (localObject != null) {
      localObject = new Stacktrace((List)localObject);
    } else {
      localObject = new Stacktrace(getStackTrace(), projectPackages);
    }
    paramJsonStream.beginObject();
    paramJsonStream.name("errorClass").value(getName());
    paramJsonStream.name("message").value(getLocalizedMessage());
    paramJsonStream.name("type").value(type);
    paramJsonStream.name("stacktrace").value((JsonStream.Streamable)localObject);
    paramJsonStream.endObject();
  }
}
