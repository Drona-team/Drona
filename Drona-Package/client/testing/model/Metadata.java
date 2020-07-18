package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Metadata
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("intentId")
  private String intentId;
  @SerializedName("intentName")
  private String intentName;
  @SerializedName("webhookUsed")
  private String webhookUsed;
  
  public Metadata() {}
  
  public String getIntentId()
  {
    return intentId;
  }
  
  public String getIntentName()
  {
    return intentName;
  }
  
  public boolean isWebhookUsed()
  {
    if (webhookUsed != null) {
      return Boolean.valueOf(webhookUsed).booleanValue();
    }
    return false;
  }
  
  public void setIntentId(String paramString)
  {
    intentId = paramString;
  }
  
  public void setIntentName(String paramString)
  {
    intentName = paramString;
  }
  
  public void setWebhookUsed(boolean paramBoolean)
  {
    webhookUsed = Boolean.toString(paramBoolean);
  }
}
