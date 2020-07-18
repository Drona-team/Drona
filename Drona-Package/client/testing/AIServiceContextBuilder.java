package client.testing;

import java.util.TimeZone;
import java.util.UUID;

public class AIServiceContextBuilder
{
  private String sessionId;
  private TimeZone timeZone;
  
  public AIServiceContextBuilder() {}
  
  public static AIServiceContext buildFromSessionId(String paramString)
  {
    return new AIServiceContextBuilder().setSessionId(paramString).build();
  }
  
  private static String createRandomSessionId()
  {
    return UUID.randomUUID().toString();
  }
  
  public AIServiceContext build()
  {
    if (sessionId != null) {
      return new PlainAIServiceContext(sessionId, timeZone);
    }
    throw new IllegalStateException("Session id is undefined");
  }
  
  public AIServiceContextBuilder generateSessionId()
  {
    sessionId = createRandomSessionId();
    return this;
  }
  
  public String getSessionId()
  {
    return sessionId;
  }
  
  public TimeZone getTimeZone()
  {
    return timeZone;
  }
  
  public AIServiceContextBuilder setSessionId(String paramString)
  {
    if (paramString != null)
    {
      sessionId = paramString;
      return this;
    }
    throw new IllegalArgumentException("sessionId cannot be null");
  }
  
  public AIServiceContextBuilder setSessionId(TimeZone paramTimeZone)
  {
    timeZone = paramTimeZone;
    return this;
  }
  
  class PlainAIServiceContext
    implements AIServiceContext
  {
    private final TimeZone timeZone;
    
    public PlainAIServiceContext(TimeZone paramTimeZone)
    {
      timeZone = paramTimeZone;
    }
    
    public String getSessionId()
    {
      return AIServiceContextBuilder.this;
    }
    
    public TimeZone getTimeZone()
    {
      return timeZone;
    }
  }
}
