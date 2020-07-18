package client.testing.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Map;

public class AIOutputContext
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("lifespan")
  private Integer lifespan;
  @SerializedName("name")
  private String name;
  @SerializedName("parameters")
  private Map<String, JsonElement> parameters;
  
  public AIOutputContext() {}
  
  public Integer getLifespan()
  {
    return lifespan;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Map getParameters()
  {
    return parameters;
  }
  
  public void setLifespan(Integer paramInteger)
  {
    lifespan = paramInteger;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public void setParameters(Map paramMap)
  {
    parameters = paramMap;
  }
}
