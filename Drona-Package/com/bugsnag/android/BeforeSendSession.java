package com.bugsnag.android;

abstract interface BeforeSendSession
{
  public abstract void beforeSendSession(SessionTrackingPayload paramSessionTrackingPayload);
}
