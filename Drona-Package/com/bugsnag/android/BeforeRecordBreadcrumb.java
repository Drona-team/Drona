package com.bugsnag.android;

public abstract interface BeforeRecordBreadcrumb
{
  public abstract boolean shouldRecord(Breadcrumb paramBreadcrumb);
}
