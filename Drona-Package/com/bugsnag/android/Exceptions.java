package com.bugsnag.android;

import java.io.IOException;

class Exceptions
  implements JsonStream.Streamable
{
  private final BugsnagException exception;
  private String exceptionType;
  private String[] projectPackages;
  
  Exceptions(Configuration paramConfiguration, BugsnagException paramBugsnagException)
  {
    exception = paramBugsnagException;
    exceptionType = paramBugsnagException.getType();
    projectPackages = paramConfiguration.getProjectPackages();
  }
  
  private void exceptionToStream(JsonStream paramJsonStream, String paramString1, String paramString2, StackTraceElement[] paramArrayOfStackTraceElement)
    throws IOException
  {
    paramJsonStream.beginObject();
    paramJsonStream.name("errorClass").value(paramString1);
    paramJsonStream.name("message").value(paramString2);
    paramJsonStream.name("type").value(exceptionType);
    paramString1 = new Stacktrace(paramArrayOfStackTraceElement, projectPackages);
    paramJsonStream.name("stacktrace").value(paramString1);
    paramJsonStream.endObject();
  }
  
  BugsnagException getException()
  {
    return exception;
  }
  
  String getExceptionType()
  {
    return exceptionType;
  }
  
  String[] getProjectPackages()
  {
    return projectPackages;
  }
  
  void setExceptionType(String paramString)
  {
    exceptionType = paramString;
    exception.setType(exceptionType);
  }
  
  void setProjectPackages(String[] paramArrayOfString)
  {
    projectPackages = paramArrayOfString;
    exception.setProjectPackages(paramArrayOfString);
  }
  
  public void toStream(JsonStream paramJsonStream)
    throws IOException
  {
    paramJsonStream.beginArray();
    for (Object localObject = exception; localObject != null; localObject = ((Throwable)localObject).getCause()) {
      if ((localObject instanceof JsonStream.Streamable)) {
        ((JsonStream.Streamable)localObject).toStream(paramJsonStream);
      } else {
        exceptionToStream(paramJsonStream, localObject.getClass().getName(), ((Throwable)localObject).getLocalizedMessage(), ((Throwable)localObject).getStackTrace());
      }
    }
    paramJsonStream.endArray();
  }
}
