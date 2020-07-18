package email.apptailor.googlesignin;

import com.facebook.react.bridge.WritableMap;

public class PendingAuthRecovery
{
  private WritableMap userProperties;
  
  public PendingAuthRecovery(WritableMap paramWritableMap)
  {
    userProperties = paramWritableMap;
  }
  
  public WritableMap getUserProperties()
  {
    return userProperties;
  }
}
