package com.bugsnag.android;

public class DeliveryFailureException
  extends Exception
{
  private static final long serialVersionUID = 1501477209400426470L;
  
  public DeliveryFailureException(String paramString)
  {
    super(paramString);
  }
  
  public DeliveryFailureException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
  }
}
