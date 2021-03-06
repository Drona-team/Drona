package com.google.android.exoplayer2.source.dash.manifest;

import com.google.android.exoplayer2.metadata.emsg.EventMessage;

public final class EventStream
{
  public final EventMessage[] events;
  public final long[] presentationTimesUs;
  public final String schemeIdUri;
  public final long timescale;
  public final String value;
  
  public EventStream(String paramString1, String paramString2, long paramLong, long[] paramArrayOfLong, EventMessage[] paramArrayOfEventMessage)
  {
    schemeIdUri = paramString1;
    value = paramString2;
    timescale = paramLong;
    presentationTimesUs = paramArrayOfLong;
    events = paramArrayOfEventMessage;
  }
  
  public String resolve()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(schemeIdUri);
    localStringBuilder.append("/");
    localStringBuilder.append(value);
    return localStringBuilder.toString();
  }
}
