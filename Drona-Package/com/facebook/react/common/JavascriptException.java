package com.facebook.react.common;

import javax.annotation.Nullable;

public class JavascriptException
  extends RuntimeException
  implements HasJavascriptExceptionMetadata
{
  @Nullable
  private String extraDataAsJson;
  
  public JavascriptException(String paramString)
  {
    super(paramString);
  }
  
  public String getExtraDataAsJson()
  {
    return extraDataAsJson;
  }
  
  public JavascriptException setExtraDataAsJson(String paramString)
  {
    extraDataAsJson = paramString;
    return this;
  }
}
