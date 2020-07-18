package com.bugsnag.android;

import java.util.Locale;

@Deprecated
public class BadResponseException
  extends Exception
{
  private static final long serialVersionUID = -870190454845379171L;
  
  public BadResponseException(String paramString, int paramInt)
  {
    super(String.format(Locale.US, "%s (%d)", new Object[] { paramString, Integer.valueOf(paramInt) }));
  }
}
