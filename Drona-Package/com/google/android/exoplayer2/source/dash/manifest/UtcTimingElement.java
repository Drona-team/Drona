package com.google.android.exoplayer2.source.dash.manifest;

public final class UtcTimingElement
{
  public final String schemeIdUri;
  public final String value;
  
  public UtcTimingElement(String paramString1, String paramString2)
  {
    schemeIdUri = paramString1;
    value = paramString2;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(schemeIdUri);
    localStringBuilder.append(", ");
    localStringBuilder.append(value);
    return localStringBuilder.toString();
  }
}
