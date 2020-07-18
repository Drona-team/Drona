package client.testing.model;

import client.testing.util.StringUtils;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QuestionMetadata
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("entities")
  private List<ai.api.model.Entity> entities;
  @SerializedName("lang")
  private String language;
  @SerializedName("location")
  private Location location;
  @SerializedName("sessionId")
  private String sessionId;
  @SerializedName("timezone")
  private String timezone;
  
  public QuestionMetadata() {}
  
  public void addEntity(Entity paramEntity)
  {
    if (entities == null) {
      entities = new ArrayList();
    }
    entities.add(paramEntity);
  }
  
  public List getEntities()
  {
    return entities;
  }
  
  public String getLanguage()
  {
    return language;
  }
  
  public Location getLocation()
  {
    return location;
  }
  
  public String getSessionId()
  {
    return sessionId;
  }
  
  public String getTimezone()
  {
    return timezone;
  }
  
  public void setEntities(List paramList)
  {
    entities = paramList;
  }
  
  public void setLanguage(String paramString)
  {
    if (!StringUtils.isEmpty(paramString))
    {
      language = paramString;
      return;
    }
    throw new IllegalArgumentException("language must not be null");
  }
  
  public void setLocation(Location paramLocation)
  {
    location = paramLocation;
  }
  
  public void setSessionId(String paramString)
  {
    sessionId = paramString;
  }
  
  public void setTimezone(String paramString)
  {
    timezone = paramString;
  }
  
  public String toString()
  {
    return String.format("QuestionMetadata{language='%s', timezone='%s'}", new Object[] { language, timezone });
  }
}
