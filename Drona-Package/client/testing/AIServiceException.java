package client.testing;

import client.testing.model.AIResponse;
import client.testing.model.Status;
import client.testing.util.StringUtils;

public class AIServiceException
  extends Exception
{
  private static final long serialVersionUID = 1L;
  private final AIResponse aiResponse;
  
  public AIServiceException()
  {
    aiResponse = null;
  }
  
  public AIServiceException(AIResponse paramAIResponse)
  {
    aiResponse = paramAIResponse;
  }
  
  public AIServiceException(String paramString)
  {
    super(paramString);
    aiResponse = null;
  }
  
  public AIServiceException(String paramString, Throwable paramThrowable)
  {
    super(paramString, paramThrowable);
    aiResponse = null;
  }
  
  public String getMessage()
  {
    if ((aiResponse != null) && (aiResponse.getStatus() != null))
    {
      String str = aiResponse.getStatus().getErrorDetails();
      if (!StringUtils.isEmpty(str)) {
        return str;
      }
    }
    return super.getMessage();
  }
  
  public AIResponse getResponse()
  {
    return aiResponse;
  }
}
