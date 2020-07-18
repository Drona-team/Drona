package com.bugsnag.android;

public abstract interface BeforeSend
{
  public abstract boolean canExecute(Report paramReport);
}
