package client.testing.model;

import client.testing.util.StringUtils;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AIRequest
  extends QuestionMetadata
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("confidence")
  private float[] confidence;
  @SerializedName("contexts")
  private List<ai.api.model.AIContext> contexts;
  @SerializedName("event")
  private AIEvent event;
  @SerializedName("query")
  private String[] query;
  @SerializedName("resetContexts")
  private Boolean resetContexts;
  
  public AIRequest() {}
  
  public AIRequest(String paramString)
  {
    setQuery(paramString);
  }
  
  public void addContext(AIContext paramAIContext)
  {
    if (contexts == null) {
      contexts = new ArrayList(1);
    }
    contexts.add(paramAIContext);
  }
  
  public float[] getConfidence()
  {
    return confidence;
  }
  
  public Boolean getResetContexts()
  {
    return resetContexts;
  }
  
  public void setConfidence(float[] paramArrayOfFloat)
  {
    confidence = paramArrayOfFloat;
  }
  
  public void setContexts(List paramList)
  {
    contexts = paramList;
  }
  
  public void setEvent(AIEvent paramAIEvent)
  {
    event = paramAIEvent;
  }
  
  public void setQuery(String paramString)
  {
    if (!StringUtils.isEmpty(paramString))
    {
      query = new String[] { paramString };
      confidence = null;
      return;
    }
    throw new IllegalStateException("Query must not be empty");
  }
  
  public void setQuery(String[] paramArrayOfString, float[] paramArrayOfFloat)
  {
    if (paramArrayOfString != null)
    {
      if ((paramArrayOfFloat == null) && (paramArrayOfString.length > 1)) {
        throw new IllegalStateException("Then confidences array is null, query must be one or zero item length");
      }
      if ((paramArrayOfFloat != null) && (paramArrayOfString.length != paramArrayOfFloat.length)) {
        throw new IllegalStateException("Query and confidence arrays must be equals size");
      }
      query = paramArrayOfString;
      confidence = paramArrayOfFloat;
      return;
    }
    throw new IllegalStateException("Query array must not be null");
  }
  
  public void setResetContexts(Boolean paramBoolean)
  {
    resetContexts = paramBoolean;
  }
  
  public String toString()
  {
    return String.format("AIRequest{query=%s, resetContexts=%s, language='%s', timezone='%s'}", new Object[] { Arrays.toString(query), resetContexts, getLanguage(), getTimezone() });
  }
}
