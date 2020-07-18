package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("entries")
  private List<ai.api.model.EntityEntry> entries;
  @SerializedName("extend")
  private Boolean extend;
  @SerializedName("isEnum")
  private Boolean isEnum;
  @SerializedName("name")
  private String name;
  
  public Entity() {}
  
  public Entity(String paramString)
  {
    name = paramString;
  }
  
  public void addEntry(EntityEntry paramEntityEntry)
  {
    if (entries == null) {
      entries = new ArrayList();
    }
    entries.add(paramEntityEntry);
  }
  
  public List getEntries()
  {
    return entries;
  }
  
  public Boolean getExtend()
  {
    return extend;
  }
  
  public String getName()
  {
    return name;
  }
  
  public Boolean isEnum()
  {
    return isEnum;
  }
  
  public void setEntries(List paramList)
  {
    entries = paramList;
  }
  
  public void setExtend(boolean paramBoolean)
  {
    extend = Boolean.valueOf(paramBoolean);
  }
  
  public void setIsEnum(Boolean paramBoolean)
  {
    isEnum = paramBoolean;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
}
