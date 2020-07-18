package com.bugsnag.android;

public abstract interface Delivery
{
  public abstract void deliver(Report paramReport, Configuration paramConfiguration)
    throws DeliveryFailureException;
  
  public abstract void deliver(SessionTrackingPayload paramSessionTrackingPayload, Configuration paramConfiguration)
    throws DeliveryFailureException;
}
