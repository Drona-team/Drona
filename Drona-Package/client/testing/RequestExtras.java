package client.testing;

import ai.api.model.AIContext;
import ai.api.model.Entity;
import client.testing.model.AIRequest;
import client.testing.model.Location;
import client.testing.model.QuestionMetadata;
import java.util.List;
import java.util.Map;

public class RequestExtras
{
  private Map<String, String> additionalHeaders;
  private List<AIContext> contexts;
  private List<Entity> entities;
  private Location location;
  private Boolean resetContexts;
  
  public RequestExtras()
  {
    contexts = null;
    entities = null;
  }
  
  public RequestExtras(List paramList1, List paramList2)
  {
    contexts = paramList1;
    entities = paramList2;
  }
  
  public void copyTo(AIRequest paramAIRequest)
  {
    if (hasContexts()) {
      paramAIRequest.setContexts(getContexts());
    }
    if (hasEntities()) {
      paramAIRequest.setEntities(getEntities());
    }
    if (getResetContexts() != null) {
      paramAIRequest.setResetContexts(getResetContexts());
    }
  }
  
  public Map getAdditionalHeaders()
  {
    return additionalHeaders;
  }
  
  public List getContexts()
  {
    return contexts;
  }
  
  public List getEntities()
  {
    return entities;
  }
  
  public Location getLocation()
  {
    return location;
  }
  
  public Boolean getResetContexts()
  {
    return resetContexts;
  }
  
  public boolean hasAdditionalHeaders()
  {
    return (additionalHeaders != null) && (!additionalHeaders.isEmpty());
  }
  
  public boolean hasContexts()
  {
    return (contexts != null) && (!contexts.isEmpty());
  }
  
  public boolean hasEntities()
  {
    return (entities != null) && (!entities.isEmpty());
  }
  
  public void setAdditionalHeaders(Map paramMap)
  {
    additionalHeaders = paramMap;
  }
  
  public void setContexts(List paramList)
  {
    contexts = paramList;
  }
  
  public void setEntities(List paramList)
  {
    entities = paramList;
  }
  
  public void setLocation(Location paramLocation)
  {
    location = paramLocation;
  }
  
  public void setResetContexts(boolean paramBoolean)
  {
    resetContexts = Boolean.valueOf(paramBoolean);
  }
}
