package com.google.android.exoplayer2.source.ClickListeners;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

public final class AdPlaybackState
{
  public static final int AD_STATE_AVAILABLE = 1;
  public static final int AD_STATE_ERROR = 4;
  public static final int AD_STATE_PLAYED = 3;
  public static final int AD_STATE_SKIPPED = 2;
  public static final int AD_STATE_UNAVAILABLE = 0;
  public static final AdPlaybackState NONE = new AdPlaybackState(new long[0]);
  public final int adGroupCount;
  public final long[] adGroupTimesUs;
  public final AdGroup[] adGroups;
  public final long adResumePositionUs;
  public final long contentDurationUs;
  
  public AdPlaybackState(long... paramVarArgs)
  {
    int j = paramVarArgs.length;
    adGroupCount = j;
    adGroupTimesUs = Arrays.copyOf(paramVarArgs, j);
    adGroups = new AdGroup[j];
    int i = 0;
    while (i < j)
    {
      adGroups[i] = new AdGroup();
      i += 1;
    }
    adResumePositionUs = 0L;
    contentDurationUs = -9223372036854775807L;
  }
  
  private AdPlaybackState(long[] paramArrayOfLong, AdGroup[] paramArrayOfAdGroup, long paramLong1, long paramLong2)
  {
    adGroupCount = paramArrayOfAdGroup.length;
    adGroupTimesUs = paramArrayOfLong;
    adGroups = paramArrayOfAdGroup;
    adResumePositionUs = paramLong1;
    contentDurationUs = paramLong2;
  }
  
  private boolean isPositionBeforeAdGroup(long paramLong, int paramInt)
  {
    long l = adGroupTimesUs[paramInt];
    if (l == Long.MIN_VALUE)
    {
      if ((contentDurationUs == -9223372036854775807L) || (paramLong < contentDurationUs)) {
        return true;
      }
    }
    else if (paramLong < l) {
      return true;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (AdPlaybackState)paramObject;
      return (adGroupCount == adGroupCount) && (adResumePositionUs == adResumePositionUs) && (contentDurationUs == contentDurationUs) && (Arrays.equals(adGroupTimesUs, adGroupTimesUs)) && (Arrays.equals(adGroups, adGroups));
    }
    return false;
  }
  
  public int getAdGroupIndexAfterPositionUs(long paramLong)
  {
    int i = 0;
    while ((i < adGroupTimesUs.length) && (adGroupTimesUs[i] != Long.MIN_VALUE) && ((paramLong >= adGroupTimesUs[i]) || (!adGroups[i].hasUnplayedAds()))) {
      i += 1;
    }
    if (i < adGroupTimesUs.length) {
      return i;
    }
    return -1;
  }
  
  public int getAdGroupIndexForPositionUs(long paramLong)
  {
    int i = adGroupTimesUs.length - 1;
    while ((i >= 0) && (isPositionBeforeAdGroup(paramLong, i))) {
      i -= 1;
    }
    if ((i >= 0) && (adGroups[i].hasUnplayedAds())) {
      return i;
    }
    return -1;
  }
  
  public int hashCode()
  {
    return (((adGroupCount * 31 + (int)adResumePositionUs) * 31 + (int)contentDurationUs) * 31 + Arrays.hashCode(adGroupTimesUs)) * 31 + Arrays.hashCode(adGroups);
  }
  
  public AdPlaybackState withAdCount(int paramInt1, int paramInt2)
  {
    boolean bool;
    if (paramInt2 > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    if (adGroups[paramInt1].count == paramInt2) {
      return this;
    }
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt1] = adGroups[paramInt1].withAdCount(paramInt2);
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withAdDurationsUs(long[][] paramArrayOfLong)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    int i = 0;
    while (i < adGroupCount)
    {
      arrayOfAdGroup[i] = arrayOfAdGroup[i].withAdDurationsUs(paramArrayOfLong[i]);
      i += 1;
    }
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withAdLoadError(int paramInt1, int paramInt2)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdState(4, paramInt2);
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withAdResumePositionUs(long paramLong)
  {
    if (adResumePositionUs == paramLong) {
      return this;
    }
    return new AdPlaybackState(adGroupTimesUs, adGroups, paramLong, contentDurationUs);
  }
  
  public AdPlaybackState withAdUri(int paramInt1, int paramInt2, Uri paramUri)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdUri(paramUri, paramInt2);
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withContentDurationUs(long paramLong)
  {
    if (contentDurationUs == paramLong) {
      return this;
    }
    return new AdPlaybackState(adGroupTimesUs, adGroups, adResumePositionUs, paramLong);
  }
  
  public AdPlaybackState withPlayedAd(int paramInt1, int paramInt2)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdState(3, paramInt2);
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withSkippedAd(int paramInt1, int paramInt2)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt1] = arrayOfAdGroup[paramInt1].withAdState(2, paramInt2);
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public AdPlaybackState withSkippedAdGroup(int paramInt)
  {
    AdGroup[] arrayOfAdGroup = (AdGroup[])Arrays.copyOf(adGroups, adGroups.length);
    arrayOfAdGroup[paramInt] = arrayOfAdGroup[paramInt].withAllAdsSkipped();
    return new AdPlaybackState(adGroupTimesUs, arrayOfAdGroup, adResumePositionUs, contentDurationUs);
  }
  
  public final class AdGroup
  {
    public final int count;
    public final long[] durationsUs;
    public final int[] states;
    public final Uri[] uris;
    
    public AdGroup()
    {
      this(new int[0], new Uri[0], new long[0]);
    }
    
    private AdGroup(int[] paramArrayOfInt, Uri[] paramArrayOfUri, long[] paramArrayOfLong)
    {
      boolean bool;
      if (paramArrayOfInt.length == paramArrayOfUri.length) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool);
      count = this$1;
      states = paramArrayOfInt;
      uris = paramArrayOfUri;
      durationsUs = paramArrayOfLong;
    }
    
    private static long[] copyDurationsUsWithSpaceForAdCount(long[] paramArrayOfLong, int paramInt)
    {
      int i = paramArrayOfLong.length;
      paramInt = Math.max(paramInt, i);
      paramArrayOfLong = Arrays.copyOf(paramArrayOfLong, paramInt);
      Arrays.fill(paramArrayOfLong, i, paramInt, -9223372036854775807L);
      return paramArrayOfLong;
    }
    
    private static int[] copyStatesWithSpaceForAdCount(int[] paramArrayOfInt, int paramInt)
    {
      int i = paramArrayOfInt.length;
      paramInt = Math.max(paramInt, i);
      paramArrayOfInt = Arrays.copyOf(paramArrayOfInt, paramInt);
      Arrays.fill(paramArrayOfInt, i, paramInt, 0);
      return paramArrayOfInt;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {
        return true;
      }
      if (paramObject != null)
      {
        if (getClass() != paramObject.getClass()) {
          return false;
        }
        paramObject = (AdGroup)paramObject;
        return (count == count) && (Arrays.equals(uris, uris)) && (Arrays.equals(states, states)) && (Arrays.equals(durationsUs, durationsUs));
      }
      return false;
    }
    
    public int getFirstAdIndexToPlay()
    {
      return getNextAdIndexToPlay(-1);
    }
    
    public int getNextAdIndexToPlay(int paramInt)
    {
      paramInt += 1;
      while ((paramInt < states.length) && (states[paramInt] != 0))
      {
        if (states[paramInt] == 1) {
          return paramInt;
        }
        paramInt += 1;
      }
      return paramInt;
    }
    
    public boolean hasUnplayedAds()
    {
      return (count == -1) || (getFirstAdIndexToPlay() < count);
    }
    
    public int hashCode()
    {
      return ((count * 31 + Arrays.hashCode(uris)) * 31 + Arrays.hashCode(states)) * 31 + Arrays.hashCode(durationsUs);
    }
    
    public AdGroup withAdCount(int paramInt)
    {
      boolean bool;
      if ((count == -1) && (states.length <= paramInt)) {
        bool = true;
      } else {
        bool = false;
      }
      Assertions.checkArgument(bool);
      int[] arrayOfInt = copyStatesWithSpaceForAdCount(states, paramInt);
      long[] arrayOfLong = copyDurationsUsWithSpaceForAdCount(durationsUs, paramInt);
      return new AdGroup(paramInt, arrayOfInt, (Uri[])Arrays.copyOf(uris, paramInt), arrayOfLong);
    }
    
    public AdGroup withAdDurationsUs(long[] paramArrayOfLong)
    {
      boolean bool;
      if ((count != -1) && (paramArrayOfLong.length > uris.length)) {
        bool = false;
      } else {
        bool = true;
      }
      Assertions.checkArgument(bool);
      long[] arrayOfLong = paramArrayOfLong;
      if (paramArrayOfLong.length < uris.length) {
        arrayOfLong = copyDurationsUsWithSpaceForAdCount(paramArrayOfLong, uris.length);
      }
      return new AdGroup(count, states, uris, arrayOfLong);
    }
    
    public AdGroup withAdState(int paramInt1, int paramInt2)
    {
      int i = count;
      boolean bool2 = false;
      boolean bool1;
      if ((i != -1) && (paramInt2 >= count)) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      Assertions.checkArgument(bool1);
      int[] arrayOfInt = copyStatesWithSpaceForAdCount(states, paramInt2 + 1);
      if ((arrayOfInt[paramInt2] != 0) && (arrayOfInt[paramInt2] != 1))
      {
        bool1 = bool2;
        if (arrayOfInt[paramInt2] != paramInt1) {}
      }
      else
      {
        bool1 = true;
      }
      Assertions.checkArgument(bool1);
      long[] arrayOfLong;
      if (durationsUs.length == arrayOfInt.length) {
        arrayOfLong = durationsUs;
      } else {
        arrayOfLong = copyDurationsUsWithSpaceForAdCount(durationsUs, arrayOfInt.length);
      }
      Uri[] arrayOfUri;
      if (uris.length == arrayOfInt.length) {
        arrayOfUri = uris;
      } else {
        arrayOfUri = (Uri[])Arrays.copyOf(uris, arrayOfInt.length);
      }
      arrayOfInt[paramInt2] = paramInt1;
      return new AdGroup(count, arrayOfInt, arrayOfUri, arrayOfLong);
    }
    
    public AdGroup withAdUri(Uri paramUri, int paramInt)
    {
      int i = count;
      boolean bool2 = false;
      if ((i != -1) && (paramInt >= count)) {
        bool1 = false;
      } else {
        bool1 = true;
      }
      Assertions.checkArgument(bool1);
      int[] arrayOfInt = copyStatesWithSpaceForAdCount(states, paramInt + 1);
      boolean bool1 = bool2;
      if (arrayOfInt[paramInt] == 0) {
        bool1 = true;
      }
      Assertions.checkArgument(bool1);
      long[] arrayOfLong;
      if (durationsUs.length == arrayOfInt.length) {
        arrayOfLong = durationsUs;
      } else {
        arrayOfLong = copyDurationsUsWithSpaceForAdCount(durationsUs, arrayOfInt.length);
      }
      Uri[] arrayOfUri = (Uri[])Arrays.copyOf(uris, arrayOfInt.length);
      arrayOfUri[paramInt] = paramUri;
      arrayOfInt[paramInt] = 1;
      return new AdGroup(count, arrayOfInt, arrayOfUri, arrayOfLong);
    }
    
    public AdGroup withAllAdsSkipped()
    {
      int j = count;
      int i = 0;
      if (j == -1) {
        return new AdGroup(0, new int[0], new Uri[0], new long[0]);
      }
      j = states.length;
      int[] arrayOfInt = Arrays.copyOf(states, j);
      while (i < j)
      {
        if ((arrayOfInt[i] == 1) || (arrayOfInt[i] == 0)) {
          arrayOfInt[i] = 2;
        }
        i += 1;
      }
      return new AdGroup(j, arrayOfInt, uris, durationsUs);
    }
  }
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface AdState {}
}
