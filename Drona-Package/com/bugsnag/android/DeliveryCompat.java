package com.bugsnag.android;

class DeliveryCompat
  implements Delivery
{
  volatile ErrorReportApiClient errorReportApiClient;
  volatile SessionTrackingApiClient sessionTrackingApiClient;
  
  DeliveryCompat() {}
  
  public void deliver(Report paramReport, Configuration paramConfiguration)
    throws DeliveryFailureException
  {
    if (errorReportApiClient != null) {
      try
      {
        errorReportApiClient.postReport(paramConfiguration.getEndpoint(), paramReport, paramConfiguration.getErrorApiHeaders());
        return;
      }
      catch (Throwable paramReport)
      {
        handleException(paramReport);
      }
    }
  }
  
  public void deliver(SessionTrackingPayload paramSessionTrackingPayload, Configuration paramConfiguration)
    throws DeliveryFailureException
  {
    if (sessionTrackingApiClient != null) {
      try
      {
        sessionTrackingApiClient.postSessionTrackingPayload(paramConfiguration.getSessionEndpoint(), paramSessionTrackingPayload, paramConfiguration.getSessionApiHeaders());
        return;
      }
      catch (Throwable paramSessionTrackingPayload)
      {
        handleException(paramSessionTrackingPayload);
      }
    }
  }
  
  void handleException(Throwable paramThrowable)
    throws DeliveryFailureException
  {
    if (!(paramThrowable instanceof NetworkException))
    {
      Logger.warn("Ignoring Exception, this API is deprecated", paramThrowable);
      return;
    }
    throw new DeliveryFailureException(paramThrowable.getMessage(), paramThrowable);
  }
}
