package client.testing.model;

import client.testing.AIServiceException;
import java.io.Serializable;

public class AIError
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private final AIResponse aiResponse;
  private AIServiceException exception;
  private final String message;
  
  public AIError(AIServiceException paramAIServiceException)
  {
    aiResponse = paramAIServiceException.getResponse();
    message = paramAIServiceException.getMessage();
    exception = paramAIServiceException;
  }
  
  public AIError(AIResponse paramAIResponse)
  {
    aiResponse = paramAIResponse;
    if (paramAIResponse == null)
    {
      message = "API.AI service returns empty result";
      return;
    }
    if (paramAIResponse.getStatus() != null)
    {
      message = paramAIResponse.getStatus().getErrorDetails();
      return;
    }
    message = "API.AI service returns error code with empty status";
  }
  
  public AIError(String paramString)
  {
    aiResponse = null;
    message = paramString;
  }
  
  public String getMessage()
  {
    return message;
  }
  
  public String toString()
  {
    if (exception != null) {
      return exception.toString();
    }
    return message;
  }
}
