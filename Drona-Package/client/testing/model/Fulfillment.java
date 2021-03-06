package client.testing.model;

import client.testing.util.StringUtils;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Fulfillment
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("contextOut")
  private List<ai.api.model.AIOutputContext> contextOut;
  @SerializedName("data")
  private Map<String, JsonElement> data;
  @SerializedName("displayText")
  private String displayText;
  @SerializedName("messages")
  private List<ai.api.model.ResponseMessage> messages;
  @SerializedName("source")
  private String source;
  @SerializedName("speech")
  private String speech;
  
  public Fulfillment() {}
  
  public AIOutputContext getContext(String paramString)
  {
    if (!StringUtils.isEmpty(paramString))
    {
      if (contextOut == null) {
        return null;
      }
      Iterator localIterator = contextOut.iterator();
      while (localIterator.hasNext())
      {
        AIOutputContext localAIOutputContext = (AIOutputContext)localIterator.next();
        if (paramString.equalsIgnoreCase(localAIOutputContext.getName())) {
          return localAIOutputContext;
        }
      }
      return null;
    }
    throw new IllegalArgumentException("name argument must be not empty");
  }
  
  public List getContextOut()
  {
    return contextOut;
  }
  
  public Map getData()
  {
    return data;
  }
  
  public String getDisplayText()
  {
    return displayText;
  }
  
  public List getMessages()
  {
    return messages;
  }
  
  public void getMessages(List paramList)
  {
    setMessages(paramList);
  }
  
  public String getSource()
  {
    return source;
  }
  
  public String getSpeech()
  {
    return speech;
  }
  
  public void setContextOut(List paramList)
  {
    contextOut = paramList;
  }
  
  public void setContextOut(AIOutputContext... paramVarArgs)
  {
    setContextOut(Arrays.asList(paramVarArgs));
  }
  
  public void setData(Map paramMap)
  {
    data = paramMap;
  }
  
  public void setDisplayText(String paramString)
  {
    displayText = paramString;
  }
  
  public void setMessages(List paramList)
  {
    messages = paramList;
  }
  
  public void setMessages(ResponseMessage... paramVarArgs)
  {
    setMessages(Arrays.asList(paramVarArgs));
  }
  
  public void setSource(String paramString)
  {
    source = paramString;
  }
  
  public void setSpeech(String paramString)
  {
    speech = paramString;
  }
}
