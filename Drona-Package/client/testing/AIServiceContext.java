package client.testing;

import java.util.TimeZone;

public abstract interface AIServiceContext
{
  public abstract String getSessionId();
  
  public abstract TimeZone getTimeZone();
}
