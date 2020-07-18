package com.bugsnag.android;

import java.util.Map;

@Deprecated
public abstract interface SessionTrackingApiClient
{
  public abstract void postSessionTrackingPayload(String paramString, SessionTrackingPayload paramSessionTrackingPayload, Map paramMap)
    throws NetworkException, BadResponseException;
}
