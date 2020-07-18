package com.bugsnag.android;

import java.io.IOException;

@Deprecated
public class NetworkException
  extends IOException
{
  private static final long serialVersionUID = -4370366096145029322L;
  
  public NetworkException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
