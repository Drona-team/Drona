package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;

public class AIResponse
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  @SerializedName("id")
  private String id;
  @SerializedName("lang")
  private String lang;
  @SerializedName("result")
  private Result result;
  @SerializedName("sessionId")
  private String sessionId;
  @SerializedName("status")
  private Status status;
  @SerializedName("timestamp")
  private Date timestamp;
  
  public AIResponse() {}
  
  public void cleanup()
  {
    if (result != null) {
      result.trimParameters();
    }
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getLang()
  {
    return lang;
  }
  
  public Result getResult()
  {
    return result;
  }
  
  public String getSessionId()
  {
    return sessionId;
  }
  
  public Status getStatus()
  {
    return status;
  }
  
  public Date getTimestamp()
  {
    return timestamp;
  }
  
  public boolean isError()
  {
    return (status != null) && (status.getCode() != null) && (status.getCode().intValue() >= 400);
  }
  
  public void setId(String paramString)
  {
    id = paramString;
  }
  
  public void setLang(String paramString)
  {
    lang = paramString;
  }
  
  public void setResult(Result paramResult)
  {
    result = paramResult;
  }
  
  public void setSessionId(String paramString)
  {
    sessionId = paramString;
  }
  
  public void setStatus(Status paramStatus)
  {
    status = paramStatus;
  }
  
  public void setTimestamp(Date paramDate)
  {
    timestamp = paramDate;
  }
  
  public String toString()
  {
    return String.format("AIResponse{id='%s', timestamp=%s, result=%s, status=%s, sessionId=%s}", new Object[] { id, timestamp, result, status, sessionId });
  }
}
