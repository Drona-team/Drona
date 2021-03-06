package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AIEvent
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("data")
  private Map<String, String> data;
  @SerializedName("name")
  private String name;
  
  public AIEvent() {}
  
  public AIEvent(String paramString)
  {
    name = paramString;
  }
  
  public void addDataField(String paramString1, String paramString2)
  {
    if (data == null) {
      setData(new HashMap());
    }
    data.put(paramString1, paramString2);
  }
  
  public void addDataField(Map paramMap)
  {
    if (data == null) {
      setData(new HashMap());
    }
    data.putAll(paramMap);
  }
  
  public Map getData()
  {
    return data;
  }
  
  public String getDataField(String paramString)
  {
    return getDataField(paramString, "");
  }
  
  public String getDataField(String paramString1, String paramString2)
  {
    if (data.containsKey(paramString1)) {
      return (String)data.get(paramString1);
    }
    return paramString2;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setData(Map paramMap)
  {
    data = paramMap;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
}
