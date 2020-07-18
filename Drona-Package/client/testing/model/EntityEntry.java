package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class EntityEntry
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("synonyms")
  private List<String> synonyms;
  @SerializedName("value")
  private String value;
  
  public EntityEntry() {}
  
  public EntityEntry(String paramString)
  {
    value = paramString;
  }
  
  public EntityEntry(String paramString, List paramList)
  {
    value = paramString;
    synonyms = paramList;
  }
  
  public EntityEntry(String paramString, String[] paramArrayOfString)
  {
    value = paramString;
    synonyms = Arrays.asList(paramArrayOfString);
  }
  
  public List getSynonyms()
  {
    return synonyms;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setSynonyms(List paramList)
  {
    synonyms = paramList;
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
}
