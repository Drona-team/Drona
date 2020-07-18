package com.bumptech.glide.load;

import java.io.IOException;

public final class HttpException
  extends IOException
{
  public static final int UNKNOWN = -1;
  private static final long serialVersionUID = 1L;
  private final int statusCode;
  
  public HttpException(int paramInt)
  {
    this(localStringBuilder.toString(), paramInt);
  }
  
  public HttpException(String paramString)
  {
    this(paramString, -1);
  }
  
  public HttpException(String paramString, int paramInt)
  {
    this(paramString, paramInt, null);
  }
  
  public HttpException(String paramString, int paramInt, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    statusCode = paramInt;
  }
  
  public int getStatusCode()
  {
    return statusCode;
  }
}
