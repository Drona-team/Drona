package com.bugsnag.android;

public abstract interface BeforeNotify
{
  public abstract boolean isUnchanged(Error paramError);
}
