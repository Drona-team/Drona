package com.bugsnag.android;

import com.bugsnag.RuntimeVersions;

public class InternalHooks
{
  public InternalHooks() {}
  
  public static void configureClient(Client paramClient)
  {
    paramClient.getConfig().addBeforeSendSession(new BeforeSendSession()
    {
      public void beforeSendSession(SessionTrackingPayload paramAnonymousSessionTrackingPayload)
      {
        RuntimeVersions.addRuntimeVersions(paramAnonymousSessionTrackingPayload.getDevice());
      }
    });
    paramClient.getConfig().beforeSend(new BeforeSend()
    {
      public boolean canExecute(Report paramAnonymousReport)
      {
        RuntimeVersions.addRuntimeVersions(paramAnonymousReport.getError().getDeviceData());
        return true;
      }
    });
  }
}
