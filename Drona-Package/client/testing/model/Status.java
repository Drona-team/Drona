package client.testing.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Status
  implements Serializable
{
  private static final Map<Integer, String> errorDescriptions = new HashMap();
  private static final Map<Integer, String> errorTypes = new HashMap();
  private static final long serialVersionUID = 1L;
  @SerializedName("code")
  private Integer code;
  @SerializedName("errorDetails")
  private String errorDetails;
  @SerializedName("errorID")
  private String errorID;
  @SerializedName("errorType")
  private String errorType;
  
  public Status()
  {
    errorDescriptions.put(Integer.valueOf(400), "Some required parameter is missing or has wrong value. Details will be in the errorDetails field.");
    errorTypes.put(Integer.valueOf(400), "bad_request");
    errorDescriptions.put(Integer.valueOf(401), "Authorization failed. Please check your access keys.");
    errorTypes.put(Integer.valueOf(401), "unauthorized");
    errorDescriptions.put(Integer.valueOf(404), "Uri is not found or some resource with provided id is not found.");
    errorTypes.put(Integer.valueOf(404), "not_found");
    errorDescriptions.put(Integer.valueOf(405), "Attempting to use POST with a GET-only endpoint, or vice-versa.");
    errorTypes.put(Integer.valueOf(405), "not_allowed");
    errorDescriptions.put(Integer.valueOf(406), "Uploaded files have some problems with it.");
    errorTypes.put(Integer.valueOf(406), "not_acceptable");
    errorDescriptions.put(Integer.valueOf(409), "The request could not be completed due to a conflict with the current state of the resource. This code is only allowed in situations where it is expected that the user might be able to resolve the conflict and resubmit the request.");
    errorTypes.put(Integer.valueOf(409), "conflict");
  }
  
  public static Status fromResponseCode(int paramInt)
  {
    Status localStatus = new Status();
    localStatus.setCode(Integer.valueOf(paramInt));
    if (errorTypes.containsKey(Integer.valueOf(paramInt))) {
      localStatus.setErrorType((String)errorTypes.get(Integer.valueOf(paramInt)));
    }
    if (errorDescriptions.containsKey(Integer.valueOf(paramInt))) {
      localStatus.setErrorDetails((String)errorDescriptions.get(Integer.valueOf(paramInt)));
    }
    return localStatus;
  }
  
  public Integer getCode()
  {
    return code;
  }
  
  public String getErrorDetails()
  {
    if ((code != null) && (errorDescriptions.containsKey(code))) {
      return (String)errorDescriptions.get(code);
    }
    return errorDetails;
  }
  
  public String getErrorID()
  {
    return errorID;
  }
  
  public String getErrorType()
  {
    return errorType;
  }
  
  public void setCode(Integer paramInteger)
  {
    code = paramInteger;
  }
  
  public void setErrorDetails(String paramString)
  {
    errorDetails = paramString;
  }
  
  public void setErrorID(String paramString)
  {
    errorID = paramString;
  }
  
  public void setErrorType(String paramString)
  {
    errorType = paramString;
  }
  
  public String toString()
  {
    return String.format("Status{code=%d, errorType='%s', errorDetails='%s'}", new Object[] { code, errorType, errorDetails });
  }
}
