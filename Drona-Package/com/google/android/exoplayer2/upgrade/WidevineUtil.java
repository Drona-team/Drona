package com.google.android.exoplayer2.upgrade;

import android.util.Pair;
import java.util.Map;

public final class WidevineUtil
{
  public static final String PROPERTY_LICENSE_DURATION_REMAINING = "LicenseDurationRemaining";
  public static final String PROPERTY_PLAYBACK_DURATION_REMAINING = "PlaybackDurationRemaining";
  
  private WidevineUtil() {}
  
  private static long getDurationRemainingSec(Map paramMap, String paramString)
  {
    if (paramMap != null) {}
    try
    {
      paramMap = paramMap.get(paramString);
      paramMap = (String)paramMap;
      if (paramMap != null)
      {
        long l = Long.parseLong(paramMap);
        return l;
      }
    }
    catch (NumberFormatException paramMap)
    {
      for (;;) {}
    }
    return -9223372036854775807L;
  }
  
  public static Pair getLicenseDurationRemainingSec(DrmSession paramDrmSession)
  {
    paramDrmSession = paramDrmSession.queryKeyStatus();
    if (paramDrmSession == null) {
      return null;
    }
    return new Pair(Long.valueOf(getDurationRemainingSec(paramDrmSession, "LicenseDurationRemaining")), Long.valueOf(getDurationRemainingSec(paramDrmSession, "PlaybackDurationRemaining")));
  }
}
