package com.airbnb.lottie.model;

public class Marker
{
  private static String CARRIAGE_RETURN;
  public final float durationFrames;
  private final String name;
  public final float startFrame;
  
  public Marker(String paramString, float paramFloat1, float paramFloat2)
  {
    name = paramString;
    durationFrames = paramFloat2;
    startFrame = paramFloat1;
  }
  
  public boolean matchesName(String paramString)
  {
    if (name.equalsIgnoreCase(paramString)) {
      return true;
    }
    return (name.endsWith(CARRIAGE_RETURN)) && (name.substring(0, name.length() - 1).equalsIgnoreCase(paramString));
  }
}
