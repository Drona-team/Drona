package com.bugsnag.android;

import java.util.Map;

@Deprecated
public abstract interface ErrorReportApiClient
{
  public abstract void postReport(String paramString, Report paramReport, Map paramMap)
    throws NetworkException, BadResponseException;
}
