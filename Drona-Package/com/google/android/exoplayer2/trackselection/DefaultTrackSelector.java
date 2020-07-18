package com.google.android.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.RendererConfiguration;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultTrackSelector
  extends MappingTrackSelector
{
  private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98F;
  private static final int[] NO_TRACKS = new int[0];
  private static final int WITHIN_RENDERER_CAPABILITIES_BONUS = 1000;
  private final TrackSelection.Factory adaptiveTrackSelectionFactory;
  private final AtomicReference<Parameters> parametersReference;
  
  public DefaultTrackSelector()
  {
    this(new AdaptiveTrackSelection.Factory());
  }
  
  public DefaultTrackSelector(TrackSelection.Factory paramFactory)
  {
    adaptiveTrackSelectionFactory = paramFactory;
    parametersReference = new AtomicReference(Parameters.DEFAULT);
  }
  
  public DefaultTrackSelector(BandwidthMeter paramBandwidthMeter)
  {
    this(new AdaptiveTrackSelection.Factory(paramBandwidthMeter));
  }
  
  private static int compareFormatValues(int paramInt1, int paramInt2)
  {
    if (paramInt1 == -1)
    {
      if (paramInt2 == -1) {
        return 0;
      }
    }
    else
    {
      if (paramInt2 == -1) {
        return 1;
      }
      return paramInt1 - paramInt2;
    }
    return -1;
  }
  
  private static int compareInts(int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2) {
      return 1;
    }
    if (paramInt2 > paramInt1) {
      return -1;
    }
    return 0;
  }
  
  private static void filterAdaptiveVideoTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      int j = ((Integer)paramList.get(i)).intValue();
      if (!isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(j), paramString, paramArrayOfInt[j], paramInt1, paramInt2, paramInt3, paramInt4, paramInt5)) {
        paramList.remove(i);
      }
      i -= 1;
    }
  }
  
  protected static boolean formatHasLanguage(Format paramFormat, String paramString)
  {
    return (paramString != null) && (TextUtils.equals(paramString, Util.normalizeLanguageCode(language)));
  }
  
  protected static boolean formatHasNoLanguage(Format paramFormat)
  {
    return (TextUtils.isEmpty(language)) || (formatHasLanguage(paramFormat, "und"));
  }
  
  private static int getAdaptiveAudioTrackCount(TrackGroup paramTrackGroup, int[] paramArrayOfInt, AudioConfigurationTuple paramAudioConfigurationTuple)
  {
    int i = 0;
    int k;
    for (int j = 0; i < length; j = k)
    {
      k = j;
      if (isSupportedAdaptiveAudioTrack(paramTrackGroup.getFormat(i), paramArrayOfInt[i], paramAudioConfigurationTuple)) {
        k = j + 1;
      }
      i += 1;
    }
    return j;
  }
  
  private static int[] getAdaptiveAudioTracks(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean)
  {
    HashSet localHashSet = new HashSet();
    int m = 0;
    Object localObject1 = null;
    int i = 0;
    Object localObject2;
    int k;
    for (int j = 0; i < length; j = k)
    {
      localObject2 = paramTrackGroup.getFormat(i);
      k = channelCount;
      int n = sampleRate;
      if (paramBoolean) {
        localObject2 = null;
      } else {
        localObject2 = sampleMimeType;
      }
      AudioConfigurationTuple localAudioConfigurationTuple = new AudioConfigurationTuple(k, n, (String)localObject2);
      localObject2 = localObject1;
      k = j;
      if (localHashSet.add(localAudioConfigurationTuple))
      {
        n = getAdaptiveAudioTrackCount(paramTrackGroup, paramArrayOfInt, localAudioConfigurationTuple);
        localObject2 = localObject1;
        k = j;
        if (n > j)
        {
          k = n;
          localObject2 = localAudioConfigurationTuple;
        }
      }
      i += 1;
      localObject1 = localObject2;
    }
    if (j > 1)
    {
      localObject2 = new int[j];
      j = 0;
      i = m;
      while (i < length)
      {
        k = j;
        if (isSupportedAdaptiveAudioTrack(paramTrackGroup.getFormat(i), paramArrayOfInt[i], (AudioConfigurationTuple)Assertions.checkNotNull(localObject1)))
        {
          localObject2[j] = i;
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      return localObject2;
    }
    return NO_TRACKS;
  }
  
  private static int getAdaptiveVideoTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List paramList)
  {
    int i = 0;
    int k;
    for (int j = 0; i < paramList.size(); j = k)
    {
      int m = ((Integer)paramList.get(i)).intValue();
      k = j;
      if (isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(m), paramString, paramArrayOfInt[m], paramInt1, paramInt2, paramInt3, paramInt4, paramInt5)) {
        k = j + 1;
      }
      i += 1;
    }
    return j;
  }
  
  private static int[] getAdaptiveVideoTracksForGroup(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean2)
  {
    if (length < 2) {
      return NO_TRACKS;
    }
    List localList = getViewportFilteredTrackIndices(paramTrackGroup, paramInt6, paramInt7, paramBoolean2);
    if (localList.size() < 2) {
      return NO_TRACKS;
    }
    Object localObject1;
    if (!paramBoolean1)
    {
      HashSet localHashSet = new HashSet();
      localObject1 = null;
      paramInt6 = 0;
      int i;
      for (paramInt7 = 0; paramInt6 < localList.size(); paramInt7 = i)
      {
        String str = getFormatgetintValuesampleMimeType;
        Object localObject2 = localObject1;
        i = paramInt7;
        if (localHashSet.add(str))
        {
          int j = getAdaptiveVideoTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, str, paramInt2, paramInt3, paramInt4, paramInt5, localList);
          localObject2 = localObject1;
          i = paramInt7;
          if (j > paramInt7)
          {
            i = j;
            localObject2 = str;
          }
        }
        paramInt6 += 1;
        localObject1 = localObject2;
      }
    }
    else
    {
      localObject1 = null;
    }
    filterAdaptiveVideoTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, localObject1, paramInt2, paramInt3, paramInt4, paramInt5, localList);
    if (localList.size() < 2) {
      return NO_TRACKS;
    }
    return Util.toArray(localList);
  }
  
  private static Point getMaxVideoSizeInViewport(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramBoolean)
    {
      j = 0;
      if (paramInt3 > paramInt4) {
        i = 1;
      } else {
        i = 0;
      }
      if (paramInt1 > paramInt2) {
        j = 1;
      }
      if (i != j) {}
    }
    else
    {
      i = paramInt2;
      paramInt2 = paramInt1;
      paramInt1 = i;
    }
    int i = paramInt3 * paramInt1;
    int j = paramInt4 * paramInt2;
    if (i >= j) {
      return new Point(paramInt2, Util.ceilDivide(j, paramInt3));
    }
    return new Point(Util.ceilDivide(i, paramInt4), paramInt1);
  }
  
  private static List getViewportFilteredTrackIndices(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList(length);
    int j = 0;
    int i = 0;
    while (i < length)
    {
      localArrayList.add(Integer.valueOf(i));
      i += 1;
    }
    if (paramInt1 != Integer.MAX_VALUE)
    {
      if (paramInt2 == Integer.MAX_VALUE) {
        return localArrayList;
      }
      int k;
      for (i = Integer.MAX_VALUE; j < length; i = k)
      {
        Format localFormat = paramTrackGroup.getFormat(j);
        k = i;
        if (width > 0)
        {
          k = i;
          if (height > 0)
          {
            Point localPoint = getMaxVideoSizeInViewport(paramBoolean, paramInt1, paramInt2, width, height);
            int m = width * height;
            k = i;
            if (width >= (int)(x * 0.98F))
            {
              k = i;
              if (height >= (int)(y * 0.98F))
              {
                k = i;
                if (m < i) {
                  k = m;
                }
              }
            }
          }
        }
        j += 1;
      }
      if (i != Integer.MAX_VALUE)
      {
        paramInt1 = localArrayList.size() - 1;
        while (paramInt1 >= 0)
        {
          paramInt2 = paramTrackGroup.getFormat(((Integer)localArrayList.get(paramInt1)).intValue()).getPixelCount();
          if ((paramInt2 == -1) || (paramInt2 > i)) {
            localArrayList.remove(paramInt1);
          }
          paramInt1 -= 1;
        }
      }
    }
    return localArrayList;
  }
  
  protected static boolean isSupported(int paramInt, boolean paramBoolean)
  {
    paramInt &= 0x7;
    return (paramInt == 4) || ((paramBoolean) && (paramInt == 3));
  }
  
  private static boolean isSupportedAdaptiveAudioTrack(Format paramFormat, int paramInt, AudioConfigurationTuple paramAudioConfigurationTuple)
  {
    return (isSupported(paramInt, false)) && (channelCount == channelCount) && (sampleRate == sampleRate) && ((mimeType == null) || (TextUtils.equals(mimeType, sampleMimeType)));
  }
  
  private static boolean isSupportedAdaptiveVideoTrack(Format paramFormat, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
  {
    return (isSupported(paramInt1, false)) && ((paramInt1 & paramInt2) != 0) && ((paramString == null) || (Util.areEqual(sampleMimeType, paramString))) && ((width == -1) || (width <= paramInt3)) && ((height == -1) || (height <= paramInt4)) && ((frameRate == -1.0F) || (frameRate <= paramInt5)) && ((bitrate == -1) || (bitrate <= paramInt6));
  }
  
  private static void maybeConfigureRenderersForTunneling(MappingTrackSelector.MappedTrackInfo paramMappedTrackInfo, int[][][] paramArrayOfInt, RendererConfiguration[] paramArrayOfRendererConfiguration, TrackSelection[] paramArrayOfTrackSelection, int paramInt)
  {
    if (paramInt == 0) {
      return;
    }
    int i1 = 0;
    int i = 0;
    int k = -1;
    int n;
    for (int j = -1; i < paramMappedTrackInfo.getRendererCount(); j = n)
    {
      int i2 = paramMappedTrackInfo.getRendererType(i);
      TrackSelection localTrackSelection = paramArrayOfTrackSelection[i];
      if (i2 != 1)
      {
        m = k;
        n = j;
        if (i2 != 2) {}
      }
      else
      {
        m = k;
        n = j;
        if (localTrackSelection != null)
        {
          m = k;
          n = j;
          if (rendererSupportsTunneling(paramArrayOfInt[i], paramMappedTrackInfo.getTrackGroups(i), localTrackSelection))
          {
            if (i2 == 1) {
              if (k == -1) {}
            }
            while (j != -1)
            {
              i = 0;
              break label166;
              m = i;
              n = j;
              break;
            }
            n = i;
            m = k;
          }
        }
      }
      i += 1;
      k = m;
    }
    i = 1;
    label166:
    int m = i1;
    if (k != -1)
    {
      m = i1;
      if (j != -1) {
        m = 1;
      }
    }
    if ((i & m) != 0)
    {
      paramMappedTrackInfo = new RendererConfiguration(paramInt);
      paramArrayOfRendererConfiguration[k] = paramMappedTrackInfo;
      paramArrayOfRendererConfiguration[j] = paramMappedTrackInfo;
    }
  }
  
  private static boolean rendererSupportsTunneling(int[][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray, TrackSelection paramTrackSelection)
  {
    if (paramTrackSelection == null) {
      return false;
    }
    int j = paramTrackGroupArray.indexOf(paramTrackSelection.getTrackGroup());
    int i = 0;
    while (i < paramTrackSelection.length())
    {
      if ((paramArrayOfInt[j][paramTrackSelection.getIndexInTrackGroup(i)] & 0x20) != 32) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  private static TrackSelection selectAdaptiveVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt, Parameters paramParameters, TrackSelection.Factory paramFactory, BandwidthMeter paramBandwidthMeter)
    throws ExoPlaybackException
  {
    int i;
    if (allowNonSeamlessAdaptiveness) {
      i = 24;
    } else {
      i = 16;
    }
    boolean bool;
    if ((allowMixedMimeAdaptiveness) && ((paramInt & i) != 0)) {
      bool = true;
    } else {
      bool = false;
    }
    paramInt = 0;
    for (;;)
    {
      Object localObject = paramTrackGroupArray;
      if (paramInt >= length) {
        break;
      }
      localObject = ((TrackGroupArray)localObject).context(paramInt);
      int[] arrayOfInt = getAdaptiveVideoTracksForGroup((TrackGroup)localObject, paramArrayOfInt[paramInt], bool, i, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, viewportWidth, viewportHeight, viewportOrientationMayChange);
      if (arrayOfInt.length > 0) {
        return ((TrackSelection.Factory)Assertions.checkNotNull(paramFactory)).createTrackSelection((TrackGroup)localObject, paramBandwidthMeter, arrayOfInt);
      }
      paramInt += 1;
    }
    return null;
  }
  
  private static TrackSelection selectFixedVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
  {
    int n = 0;
    Object localObject1 = null;
    int i = 0;
    int i3 = 0;
    int i2 = -1;
    int i1 = -1;
    for (;;)
    {
      Object localObject2 = paramTrackGroupArray;
      if (n >= length) {
        break;
      }
      TrackGroup localTrackGroup = ((TrackGroupArray)localObject2).context(n);
      List localList = getViewportFilteredTrackIndices(localTrackGroup, viewportWidth, viewportHeight, viewportOrientationMayChange);
      int[] arrayOfInt = paramArrayOfInt[n];
      int j = 0;
      while (j < length)
      {
        localObject2 = localObject1;
        int i4 = i3;
        int i5 = i2;
        int i6 = i1;
        int i7 = i;
        if (isSupported(arrayOfInt[j], exceedRendererCapabilitiesIfNecessary))
        {
          Format localFormat = localTrackGroup.getFormat(j);
          if ((localList.contains(Integer.valueOf(j))) && ((width == -1) || (width <= maxVideoWidth)) && ((height == -1) || (height <= maxVideoHeight)) && ((frameRate == -1.0F) || (frameRate <= maxVideoFrameRate)) && ((bitrate == -1) || (bitrate <= maxVideoBitrate))) {
            i4 = 1;
          } else {
            i4 = 0;
          }
          if ((i4 == 0) && (!exceedVideoConstraintsIfNecessary))
          {
            localObject2 = localObject1;
            i4 = i3;
            i5 = i2;
            i6 = i1;
            i7 = i;
          }
          else
          {
            int m;
            if (i4 != 0) {
              m = 2;
            } else {
              m = 1;
            }
            boolean bool = isSupported(arrayOfInt[j], false);
            int k = m;
            if (bool) {
              k = m + 1000;
            }
            if (k > i3) {
              m = 1;
            } else {
              m = 0;
            }
            if (k == i3)
            {
              if (forceLowestBitrate) {
                if (compareFormatValues(bitrate, i2) >= 0) {}
              }
              for (;;)
              {
                m = 1;
                break;
                do
                {
                  do
                  {
                    m = 0;
                    break label427;
                    m = localFormat.getPixelCount();
                    if (m != i1) {
                      m = compareFormatValues(m, i1);
                    } else {
                      m = compareFormatValues(bitrate, i2);
                    }
                    if ((!bool) || (i4 == 0)) {
                      break;
                    }
                  } while (m <= 0);
                  break;
                } while (m >= 0);
              }
            }
            label427:
            localObject2 = localObject1;
            i4 = i3;
            i5 = i2;
            i6 = i1;
            i7 = i;
            if (m != 0)
            {
              i5 = bitrate;
              i6 = localFormat.getPixelCount();
              i7 = j;
              localObject2 = localTrackGroup;
              i4 = k;
            }
          }
        }
        j += 1;
        localObject1 = localObject2;
        i3 = i4;
        i2 = i5;
        i1 = i6;
        i = i7;
      }
      n += 1;
    }
    if (localObject1 == null) {
      return null;
    }
    return new FixedTrackSelection(localObject1, i);
  }
  
  public ParametersBuilder buildUponParameters()
  {
    return getParameters().buildUpon();
  }
  
  public final void clearSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    setParameters(buildUponParameters().clearSelectionOverride(paramInt, paramTrackGroupArray));
  }
  
  public final void clearSelectionOverrides()
  {
    setParameters(buildUponParameters().clearSelectionOverrides());
  }
  
  public final void clearSelectionOverrides(int paramInt)
  {
    setParameters(buildUponParameters().clearSelectionOverrides(paramInt));
  }
  
  public Parameters getParameters()
  {
    return (Parameters)parametersReference.get();
  }
  
  public final boolean getRendererDisabled(int paramInt)
  {
    return getParameters().getRendererDisabled(paramInt);
  }
  
  public final SelectionOverride getSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    return getParameters().getSelectionOverride(paramInt, paramTrackGroupArray);
  }
  
  public final boolean hasSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    return getParameters().hasSelectionOverride(paramInt, paramTrackGroupArray);
  }
  
  protected TrackSelection[] selectAllTracks(MappingTrackSelector.MappedTrackInfo paramMappedTrackInfo, int[][][] paramArrayOfInt, int[] paramArrayOfInt1, Parameters paramParameters)
    throws ExoPlaybackException
  {
    int i2 = paramMappedTrackInfo.getRendererCount();
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[i2];
    int i1 = 0;
    int j = 0;
    int m = 0;
    for (int k = 0; m < i2; k = n)
    {
      i = j;
      n = k;
      if (2 == paramMappedTrackInfo.getRendererType(m))
      {
        n = 1;
        i = j;
        if (j == 0)
        {
          arrayOfTrackSelection[m] = selectVideoTrack(paramMappedTrackInfo.getTrackGroups(m), paramArrayOfInt[m], paramArrayOfInt1[m], paramParameters, adaptiveTrackSelectionFactory);
          if (arrayOfTrackSelection[m] != null) {
            i = 1;
          } else {
            i = 0;
          }
        }
        if (getTrackGroupslength > 0) {
          j = n;
        } else {
          j = 0;
        }
        n = k | j;
      }
      m += 1;
      j = i;
    }
    AudioTrackScore localAudioTrackScore = null;
    m = -1;
    int n = -1;
    j = Integer.MIN_VALUE;
    int i = i1;
    while (i < i2)
    {
      i1 = paramMappedTrackInfo.getRendererType(i);
      Object localObject;
      switch (i1)
      {
      default: 
        arrayOfTrackSelection[i] = selectOtherTrack(i1, paramMappedTrackInfo.getTrackGroups(i), paramArrayOfInt[i], paramParameters);
        break;
      case 3: 
        localObject = selectTextTrack(paramMappedTrackInfo.getTrackGroups(i), paramArrayOfInt[i], paramParameters);
        if ((localObject != null) && (((Integer)second).intValue() > j))
        {
          if (n != -1) {
            arrayOfTrackSelection[n] = null;
          }
          arrayOfTrackSelection[i] = ((TrackSelection)first);
          j = ((Integer)second).intValue();
          n = i;
        }
        break;
      }
      for (;;)
      {
        break;
        TrackGroupArray localTrackGroupArray = paramMappedTrackInfo.getTrackGroups(i);
        int[][] arrayOfInt = paramArrayOfInt[i];
        i1 = paramArrayOfInt1[i];
        if (k != 0) {
          localObject = null;
        } else {
          localObject = adaptiveTrackSelectionFactory;
        }
        localObject = selectAudioTrack(localTrackGroupArray, arrayOfInt, i1, paramParameters, (TrackSelection.Factory)localObject);
        if ((localObject != null) && ((localAudioTrackScore == null) || (((AudioTrackScore)second).compareTo(localAudioTrackScore) > 0)))
        {
          if (m != -1) {
            arrayOfTrackSelection[m] = null;
          }
          arrayOfTrackSelection[i] = ((TrackSelection)first);
          localAudioTrackScore = (AudioTrackScore)second;
          m = i;
        }
      }
      i += 1;
    }
    return arrayOfTrackSelection;
  }
  
  protected Pair selectAudioTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt, Parameters paramParameters, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a36 = a35\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  protected TrackSelection selectOtherTrack(int paramInt, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
    throws ExoPlaybackException
  {
    Object localObject1 = null;
    int k = 0;
    int n = 0;
    int m = 0;
    while (k < length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.context(k);
      int[] arrayOfInt = paramArrayOfInt[k];
      paramInt = 0;
      while (paramInt < length)
      {
        Object localObject2 = localObject1;
        int i1 = n;
        int j = m;
        if (isSupported(arrayOfInt[paramInt], exceedRendererCapabilitiesIfNecessary))
        {
          int i = getFormatselectionFlags;
          j = 1;
          if ((i & 0x1) != 0) {
            i = 1;
          } else {
            i = 0;
          }
          if (i != 0) {
            j = 2;
          }
          i = j;
          if (isSupported(arrayOfInt[paramInt], false)) {
            i = j + 1000;
          }
          localObject2 = localObject1;
          i1 = n;
          j = m;
          if (i > m)
          {
            i1 = paramInt;
            localObject2 = localTrackGroup;
            j = i;
          }
        }
        paramInt += 1;
        localObject1 = localObject2;
        n = i1;
        m = j;
      }
      k += 1;
    }
    if (localObject1 == null) {
      return null;
    }
    return new FixedTrackSelection(localObject1, n);
  }
  
  protected Pair selectTextTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, Parameters paramParameters)
    throws ExoPlaybackException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  protected final Pair selectTracks(MappingTrackSelector.MappedTrackInfo paramMappedTrackInfo, int[][][] paramArrayOfInt, int[] paramArrayOfInt1)
    throws ExoPlaybackException
  {
    Parameters localParameters = (Parameters)parametersReference.get();
    int k = paramMappedTrackInfo.getRendererCount();
    TrackSelection[] arrayOfTrackSelection = selectAllTracks(paramMappedTrackInfo, paramArrayOfInt, paramArrayOfInt1, localParameters);
    int i = 0;
    while (i < k)
    {
      if (localParameters.getRendererDisabled(i))
      {
        arrayOfTrackSelection[i] = null;
      }
      else
      {
        paramArrayOfInt1 = paramMappedTrackInfo.getTrackGroups(i);
        if (localParameters.hasSelectionOverride(i, paramArrayOfInt1))
        {
          localObject = localParameters.getSelectionOverride(i, paramArrayOfInt1);
          if (localObject == null) {
            arrayOfTrackSelection[i] = null;
          } else if (length == 1) {
            arrayOfTrackSelection[i] = new FixedTrackSelection(paramArrayOfInt1.context(groupIndex), tracks[0]);
          } else {
            arrayOfTrackSelection[i] = ((TrackSelection.Factory)Assertions.checkNotNull(adaptiveTrackSelectionFactory)).createTrackSelection(paramArrayOfInt1.context(groupIndex), getBandwidthMeter(), tracks);
          }
        }
      }
      i += 1;
    }
    Object localObject = new RendererConfiguration[k];
    i = 0;
    while (i < k)
    {
      int j;
      if ((!localParameters.getRendererDisabled(i)) && ((paramMappedTrackInfo.getRendererType(i) == 6) || (arrayOfTrackSelection[i] != null))) {
        j = 1;
      } else {
        j = 0;
      }
      if (j != 0) {
        paramArrayOfInt1 = RendererConfiguration.DEFAULT;
      } else {
        paramArrayOfInt1 = null;
      }
      localObject[i] = paramArrayOfInt1;
      i += 1;
    }
    maybeConfigureRenderersForTunneling(paramMappedTrackInfo, paramArrayOfInt, (RendererConfiguration[])localObject, arrayOfTrackSelection, tunnelingAudioSessionId);
    return Pair.create(localObject, arrayOfTrackSelection);
  }
  
  protected TrackSelection selectVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt, Parameters paramParameters, TrackSelection.Factory paramFactory)
    throws ExoPlaybackException
  {
    if ((!forceHighestSupportedBitrate) && (!forceLowestBitrate) && (paramFactory != null)) {
      paramFactory = selectAdaptiveVideoTrack(paramTrackGroupArray, paramArrayOfInt, paramInt, paramParameters, paramFactory, getBandwidthMeter());
    } else {
      paramFactory = null;
    }
    Object localObject = paramFactory;
    if (paramFactory == null) {
      localObject = selectFixedVideoTrack(paramTrackGroupArray, paramArrayOfInt, paramParameters);
    }
    return localObject;
  }
  
  public void setParameters(Parameters paramParameters)
  {
    Assertions.checkNotNull(paramParameters);
    if (!((Parameters)parametersReference.getAndSet(paramParameters)).equals(paramParameters)) {
      invalidate();
    }
  }
  
  public void setParameters(ParametersBuilder paramParametersBuilder)
  {
    setParameters(paramParametersBuilder.build());
  }
  
  public final void setRendererDisabled(int paramInt, boolean paramBoolean)
  {
    setParameters(buildUponParameters().setRendererDisabled(paramInt, paramBoolean));
  }
  
  public final void setSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray, SelectionOverride paramSelectionOverride)
  {
    setParameters(buildUponParameters().setSelectionOverride(paramInt, paramTrackGroupArray, paramSelectionOverride));
  }
  
  public void setTunnelingAudioSessionId(int paramInt)
  {
    setParameters(buildUponParameters().setTunnelingAudioSessionId(paramInt));
  }
  
  private static final class AudioConfigurationTuple
  {
    public final int channelCount;
    @Nullable
    public final String mimeType;
    public final int sampleRate;
    
    public AudioConfigurationTuple(int paramInt1, int paramInt2, String paramString)
    {
      channelCount = paramInt1;
      sampleRate = paramInt2;
      mimeType = paramString;
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
        paramObject = (AudioConfigurationTuple)paramObject;
        return (channelCount == channelCount) && (sampleRate == sampleRate) && (TextUtils.equals(mimeType, mimeType));
      }
      return false;
    }
    
    public int hashCode()
    {
      int j = channelCount;
      int k = sampleRate;
      int i;
      if (mimeType != null) {
        i = mimeType.hashCode();
      } else {
        i = 0;
      }
      return (j * 31 + k) * 31 + i;
    }
  }
  
  protected static final class AudioTrackScore
    implements Comparable<AudioTrackScore>
  {
    private final int bitrate;
    private final int channelCount;
    private final int defaultSelectionFlagScore;
    private final int matchLanguageScore;
    private final DefaultTrackSelector.Parameters parameters;
    private final int sampleRate;
    private final int withinRendererCapabilitiesScore;
    
    public AudioTrackScore(Format paramFormat, DefaultTrackSelector.Parameters paramParameters, int paramInt) {}
    
    public int compareTo(AudioTrackScore paramAudioTrackScore)
    {
      if (withinRendererCapabilitiesScore != withinRendererCapabilitiesScore) {
        return DefaultTrackSelector.compareInts(withinRendererCapabilitiesScore, withinRendererCapabilitiesScore);
      }
      if (matchLanguageScore != matchLanguageScore) {
        return DefaultTrackSelector.compareInts(matchLanguageScore, matchLanguageScore);
      }
      if (defaultSelectionFlagScore != defaultSelectionFlagScore) {
        return DefaultTrackSelector.compareInts(defaultSelectionFlagScore, defaultSelectionFlagScore);
      }
      if (parameters.forceLowestBitrate) {
        return DefaultTrackSelector.compareInts(bitrate, bitrate);
      }
      int j = withinRendererCapabilitiesScore;
      int i = 1;
      if (j != 1) {
        i = -1;
      }
      if (channelCount != channelCount) {
        return i * DefaultTrackSelector.compareInts(channelCount, channelCount);
      }
      if (sampleRate != sampleRate) {
        return i * DefaultTrackSelector.compareInts(sampleRate, sampleRate);
      }
      return i * DefaultTrackSelector.compareInts(bitrate, bitrate);
    }
  }
  
  public static final class Parameters
    implements Parcelable
  {
    public static final Parcelable.Creator<Parameters> CREATOR = new Parcelable.Creator()
    {
      public DefaultTrackSelector.Parameters createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DefaultTrackSelector.Parameters(paramAnonymousParcel);
      }
      
      public DefaultTrackSelector.Parameters[] newArray(int paramAnonymousInt)
      {
        return new DefaultTrackSelector.Parameters[paramAnonymousInt];
      }
    };
    public static final Parameters DEFAULT = new Parameters();
    public final boolean allowMixedMimeAdaptiveness;
    public final boolean allowNonSeamlessAdaptiveness;
    public final int disabledTextTrackSelectionFlags;
    public final boolean exceedRendererCapabilitiesIfNecessary;
    public final boolean exceedVideoConstraintsIfNecessary;
    public final boolean forceHighestSupportedBitrate;
    public final boolean forceLowestBitrate;
    public final int maxVideoBitrate;
    public final int maxVideoFrameRate;
    public final int maxVideoHeight;
    public final int maxVideoWidth;
    @Nullable
    public final String preferredAudioLanguage;
    @Nullable
    public final String preferredTextLanguage;
    private final SparseBooleanArray rendererDisabledFlags;
    public final boolean selectUndeterminedTextLanguage;
    private final SparseArray<Map<TrackGroupArray, DefaultTrackSelector.SelectionOverride>> selectionOverrides;
    public final int tunnelingAudioSessionId;
    public final int viewportHeight;
    public final boolean viewportOrientationMayChange;
    public final int viewportWidth;
    
    private Parameters()
    {
      this(new SparseArray(), new SparseBooleanArray(), null, null, false, 0, false, false, false, true, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, true, true, Integer.MAX_VALUE, Integer.MAX_VALUE, true, 0);
    }
    
    Parameters(Parcel paramParcel)
    {
      selectionOverrides = readSelectionOverrides(paramParcel);
      rendererDisabledFlags = paramParcel.readSparseBooleanArray();
      preferredAudioLanguage = paramParcel.readString();
      preferredTextLanguage = paramParcel.readString();
      selectUndeterminedTextLanguage = Util.readBoolean(paramParcel);
      disabledTextTrackSelectionFlags = paramParcel.readInt();
      forceLowestBitrate = Util.readBoolean(paramParcel);
      forceHighestSupportedBitrate = Util.readBoolean(paramParcel);
      allowMixedMimeAdaptiveness = Util.readBoolean(paramParcel);
      allowNonSeamlessAdaptiveness = Util.readBoolean(paramParcel);
      maxVideoWidth = paramParcel.readInt();
      maxVideoHeight = paramParcel.readInt();
      maxVideoFrameRate = paramParcel.readInt();
      maxVideoBitrate = paramParcel.readInt();
      exceedVideoConstraintsIfNecessary = Util.readBoolean(paramParcel);
      exceedRendererCapabilitiesIfNecessary = Util.readBoolean(paramParcel);
      viewportWidth = paramParcel.readInt();
      viewportHeight = paramParcel.readInt();
      viewportOrientationMayChange = Util.readBoolean(paramParcel);
      tunnelingAudioSessionId = paramParcel.readInt();
    }
    
    Parameters(SparseArray paramSparseArray, SparseBooleanArray paramSparseBooleanArray, String paramString1, String paramString2, boolean paramBoolean1, int paramInt1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean6, boolean paramBoolean7, int paramInt6, int paramInt7, boolean paramBoolean8, int paramInt8)
    {
      selectionOverrides = paramSparseArray;
      rendererDisabledFlags = paramSparseBooleanArray;
      preferredAudioLanguage = Util.normalizeLanguageCode(paramString1);
      preferredTextLanguage = Util.normalizeLanguageCode(paramString2);
      selectUndeterminedTextLanguage = paramBoolean1;
      disabledTextTrackSelectionFlags = paramInt1;
      forceLowestBitrate = paramBoolean2;
      forceHighestSupportedBitrate = paramBoolean3;
      allowMixedMimeAdaptiveness = paramBoolean4;
      allowNonSeamlessAdaptiveness = paramBoolean5;
      maxVideoWidth = paramInt2;
      maxVideoHeight = paramInt3;
      maxVideoFrameRate = paramInt4;
      maxVideoBitrate = paramInt5;
      exceedVideoConstraintsIfNecessary = paramBoolean6;
      exceedRendererCapabilitiesIfNecessary = paramBoolean7;
      viewportWidth = paramInt6;
      viewportHeight = paramInt7;
      viewportOrientationMayChange = paramBoolean8;
      tunnelingAudioSessionId = paramInt8;
    }
    
    private static boolean areRendererDisabledFlagsEqual(SparseBooleanArray paramSparseBooleanArray1, SparseBooleanArray paramSparseBooleanArray2)
    {
      int j = paramSparseBooleanArray1.size();
      if (paramSparseBooleanArray2.size() != j) {
        return false;
      }
      int i = 0;
      while (i < j)
      {
        if (paramSparseBooleanArray2.indexOfKey(paramSparseBooleanArray1.keyAt(i)) < 0) {
          return false;
        }
        i += 1;
      }
      return true;
    }
    
    private static boolean areSelectionOverridesEqual(SparseArray paramSparseArray1, SparseArray paramSparseArray2)
    {
      int j = paramSparseArray1.size();
      if (paramSparseArray2.size() != j) {
        return false;
      }
      int i = 0;
      while (i < j)
      {
        int k = paramSparseArray2.indexOfKey(paramSparseArray1.keyAt(i));
        if (k >= 0)
        {
          if (!areSelectionOverridesEqual((Map)paramSparseArray1.valueAt(i), (Map)paramSparseArray2.valueAt(k))) {
            return false;
          }
          i += 1;
        }
        else
        {
          return false;
        }
      }
      return true;
    }
    
    private static boolean areSelectionOverridesEqual(Map paramMap1, Map paramMap2)
    {
      int i = paramMap1.size();
      if (paramMap2.size() != i) {
        return false;
      }
      paramMap1 = paramMap1.entrySet().iterator();
      while (paramMap1.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap1.next();
        TrackGroupArray localTrackGroupArray = (TrackGroupArray)localEntry.getKey();
        if (!paramMap2.containsKey(localTrackGroupArray)) {
          break label96;
        }
        if (!Util.areEqual(localEntry.getValue(), paramMap2.get(localTrackGroupArray))) {
          return false;
        }
      }
      return true;
      label96:
      return false;
    }
    
    private static SparseArray readSelectionOverrides(Parcel paramParcel)
    {
      int k = paramParcel.readInt();
      SparseArray localSparseArray = new SparseArray(k);
      int i = 0;
      while (i < k)
      {
        int m = paramParcel.readInt();
        int n = paramParcel.readInt();
        HashMap localHashMap = new HashMap(n);
        int j = 0;
        while (j < n)
        {
          localHashMap.put((TrackGroupArray)paramParcel.readParcelable(TrackGroupArray.class.getClassLoader()), (DefaultTrackSelector.SelectionOverride)paramParcel.readParcelable(DefaultTrackSelector.SelectionOverride.class.getClassLoader()));
          j += 1;
        }
        localSparseArray.put(m, localHashMap);
        i += 1;
      }
      return localSparseArray;
    }
    
    private static void writeSelectionOverridesToParcel(Parcel paramParcel, SparseArray paramSparseArray)
    {
      int j = paramSparseArray.size();
      paramParcel.writeInt(j);
      int i = 0;
      while (i < j)
      {
        int k = paramSparseArray.keyAt(i);
        Object localObject = (Map)paramSparseArray.valueAt(i);
        int m = ((Map)localObject).size();
        paramParcel.writeInt(k);
        paramParcel.writeInt(m);
        localObject = ((Map)localObject).entrySet().iterator();
        while (((Iterator)localObject).hasNext())
        {
          Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
          paramParcel.writeParcelable((Parcelable)localEntry.getKey(), 0);
          paramParcel.writeParcelable((Parcelable)localEntry.getValue(), 0);
        }
        i += 1;
      }
    }
    
    public DefaultTrackSelector.ParametersBuilder buildUpon()
    {
      return new DefaultTrackSelector.ParametersBuilder(this, null);
    }
    
    public int describeContents()
    {
      return 0;
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
        paramObject = (Parameters)paramObject;
        return (selectUndeterminedTextLanguage == selectUndeterminedTextLanguage) && (disabledTextTrackSelectionFlags == disabledTextTrackSelectionFlags) && (forceLowestBitrate == forceLowestBitrate) && (forceHighestSupportedBitrate == forceHighestSupportedBitrate) && (allowMixedMimeAdaptiveness == allowMixedMimeAdaptiveness) && (allowNonSeamlessAdaptiveness == allowNonSeamlessAdaptiveness) && (maxVideoWidth == maxVideoWidth) && (maxVideoHeight == maxVideoHeight) && (maxVideoFrameRate == maxVideoFrameRate) && (exceedVideoConstraintsIfNecessary == exceedVideoConstraintsIfNecessary) && (exceedRendererCapabilitiesIfNecessary == exceedRendererCapabilitiesIfNecessary) && (viewportOrientationMayChange == viewportOrientationMayChange) && (viewportWidth == viewportWidth) && (viewportHeight == viewportHeight) && (maxVideoBitrate == maxVideoBitrate) && (tunnelingAudioSessionId == tunnelingAudioSessionId) && (TextUtils.equals(preferredAudioLanguage, preferredAudioLanguage)) && (TextUtils.equals(preferredTextLanguage, preferredTextLanguage)) && (areRendererDisabledFlagsEqual(rendererDisabledFlags, rendererDisabledFlags)) && (areSelectionOverridesEqual(selectionOverrides, selectionOverrides));
      }
      return false;
    }
    
    public final boolean getRendererDisabled(int paramInt)
    {
      return rendererDisabledFlags.get(paramInt);
    }
    
    public final DefaultTrackSelector.SelectionOverride getSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
    {
      Map localMap = (Map)selectionOverrides.get(paramInt);
      if (localMap != null) {
        return (DefaultTrackSelector.SelectionOverride)localMap.get(paramTrackGroupArray);
      }
      return null;
    }
    
    public final boolean hasSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
    {
      Map localMap = (Map)selectionOverrides.get(paramInt);
      return (localMap != null) && (localMap.containsKey(paramTrackGroupArray));
    }
    
    public int hashCode()
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      writeSelectionOverridesToParcel(paramParcel, selectionOverrides);
      paramParcel.writeSparseBooleanArray(rendererDisabledFlags);
      paramParcel.writeString(preferredAudioLanguage);
      paramParcel.writeString(preferredTextLanguage);
      Util.writeBoolean(paramParcel, selectUndeterminedTextLanguage);
      paramParcel.writeInt(disabledTextTrackSelectionFlags);
      Util.writeBoolean(paramParcel, forceLowestBitrate);
      Util.writeBoolean(paramParcel, forceHighestSupportedBitrate);
      Util.writeBoolean(paramParcel, allowMixedMimeAdaptiveness);
      Util.writeBoolean(paramParcel, allowNonSeamlessAdaptiveness);
      paramParcel.writeInt(maxVideoWidth);
      paramParcel.writeInt(maxVideoHeight);
      paramParcel.writeInt(maxVideoFrameRate);
      paramParcel.writeInt(maxVideoBitrate);
      Util.writeBoolean(paramParcel, exceedVideoConstraintsIfNecessary);
      Util.writeBoolean(paramParcel, exceedRendererCapabilitiesIfNecessary);
      paramParcel.writeInt(viewportWidth);
      paramParcel.writeInt(viewportHeight);
      Util.writeBoolean(paramParcel, viewportOrientationMayChange);
      paramParcel.writeInt(tunnelingAudioSessionId);
    }
  }
  
  public static final class ParametersBuilder
  {
    private boolean allowMixedMimeAdaptiveness;
    private boolean allowNonSeamlessAdaptiveness;
    private int disabledTextTrackSelectionFlags;
    private boolean exceedRendererCapabilitiesIfNecessary;
    private boolean exceedVideoConstraintsIfNecessary;
    private boolean forceHighestSupportedBitrate;
    private boolean forceLowestBitrate;
    private int maxVideoBitrate;
    private int maxVideoFrameRate;
    private int maxVideoHeight;
    private int maxVideoWidth;
    @Nullable
    private String preferredAudioLanguage;
    @Nullable
    private String preferredTextLanguage;
    private final SparseBooleanArray rendererDisabledFlags;
    private boolean selectUndeterminedTextLanguage;
    private final SparseArray<Map<TrackGroupArray, DefaultTrackSelector.SelectionOverride>> selectionOverrides;
    private int tunnelingAudioSessionId;
    private int viewportHeight;
    private boolean viewportOrientationMayChange;
    private int viewportWidth;
    
    public ParametersBuilder()
    {
      this(DefaultTrackSelector.Parameters.DEFAULT);
    }
    
    private ParametersBuilder(DefaultTrackSelector.Parameters paramParameters)
    {
      selectionOverrides = cloneSelectionOverrides(selectionOverrides);
      rendererDisabledFlags = rendererDisabledFlags.clone();
      preferredAudioLanguage = preferredAudioLanguage;
      preferredTextLanguage = preferredTextLanguage;
      selectUndeterminedTextLanguage = selectUndeterminedTextLanguage;
      disabledTextTrackSelectionFlags = disabledTextTrackSelectionFlags;
      forceLowestBitrate = forceLowestBitrate;
      forceHighestSupportedBitrate = forceHighestSupportedBitrate;
      allowMixedMimeAdaptiveness = allowMixedMimeAdaptiveness;
      allowNonSeamlessAdaptiveness = allowNonSeamlessAdaptiveness;
      maxVideoWidth = maxVideoWidth;
      maxVideoHeight = maxVideoHeight;
      maxVideoFrameRate = maxVideoFrameRate;
      maxVideoBitrate = maxVideoBitrate;
      exceedVideoConstraintsIfNecessary = exceedVideoConstraintsIfNecessary;
      exceedRendererCapabilitiesIfNecessary = exceedRendererCapabilitiesIfNecessary;
      viewportWidth = viewportWidth;
      viewportHeight = viewportHeight;
      viewportOrientationMayChange = viewportOrientationMayChange;
      tunnelingAudioSessionId = tunnelingAudioSessionId;
    }
    
    private static SparseArray cloneSelectionOverrides(SparseArray paramSparseArray)
    {
      SparseArray localSparseArray = new SparseArray();
      int i = 0;
      while (i < paramSparseArray.size())
      {
        localSparseArray.put(paramSparseArray.keyAt(i), new HashMap((Map)paramSparseArray.valueAt(i)));
        i += 1;
      }
      return localSparseArray;
    }
    
    public DefaultTrackSelector.Parameters build()
    {
      return new DefaultTrackSelector.Parameters(selectionOverrides, rendererDisabledFlags, preferredAudioLanguage, preferredTextLanguage, selectUndeterminedTextLanguage, disabledTextTrackSelectionFlags, forceLowestBitrate, forceHighestSupportedBitrate, allowMixedMimeAdaptiveness, allowNonSeamlessAdaptiveness, maxVideoWidth, maxVideoHeight, maxVideoFrameRate, maxVideoBitrate, exceedVideoConstraintsIfNecessary, exceedRendererCapabilitiesIfNecessary, viewportWidth, viewportHeight, viewportOrientationMayChange, tunnelingAudioSessionId);
    }
    
    public final ParametersBuilder clearSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
    {
      Map localMap = (Map)selectionOverrides.get(paramInt);
      if (localMap != null)
      {
        if (!localMap.containsKey(paramTrackGroupArray)) {
          return this;
        }
        localMap.remove(paramTrackGroupArray);
        if (localMap.isEmpty()) {
          selectionOverrides.remove(paramInt);
        }
      }
      return this;
    }
    
    public final ParametersBuilder clearSelectionOverrides()
    {
      if (selectionOverrides.size() == 0) {
        return this;
      }
      selectionOverrides.clear();
      return this;
    }
    
    public final ParametersBuilder clearSelectionOverrides(int paramInt)
    {
      Map localMap = (Map)selectionOverrides.get(paramInt);
      if (localMap != null)
      {
        if (localMap.isEmpty()) {
          return this;
        }
        selectionOverrides.remove(paramInt);
      }
      return this;
    }
    
    public ParametersBuilder clearVideoSizeConstraints()
    {
      return setMaxVideoSize(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
    
    public ParametersBuilder clearViewportSizeConstraints()
    {
      return setViewportSize(Integer.MAX_VALUE, Integer.MAX_VALUE, true);
    }
    
    public ParametersBuilder setAllowMixedMimeAdaptiveness(boolean paramBoolean)
    {
      allowMixedMimeAdaptiveness = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setAllowNonSeamlessAdaptiveness(boolean paramBoolean)
    {
      allowNonSeamlessAdaptiveness = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setDisabledTextTrackSelectionFlags(int paramInt)
    {
      disabledTextTrackSelectionFlags = paramInt;
      return this;
    }
    
    public ParametersBuilder setExceedRendererCapabilitiesIfNecessary(boolean paramBoolean)
    {
      exceedRendererCapabilitiesIfNecessary = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setExceedVideoConstraintsIfNecessary(boolean paramBoolean)
    {
      exceedVideoConstraintsIfNecessary = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setForceHighestSupportedBitrate(boolean paramBoolean)
    {
      forceHighestSupportedBitrate = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setForceLowestBitrate(boolean paramBoolean)
    {
      forceLowestBitrate = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setMaxVideoBitrate(int paramInt)
    {
      maxVideoBitrate = paramInt;
      return this;
    }
    
    public ParametersBuilder setMaxVideoFrameRate(int paramInt)
    {
      maxVideoFrameRate = paramInt;
      return this;
    }
    
    public ParametersBuilder setMaxVideoSize(int paramInt1, int paramInt2)
    {
      maxVideoWidth = paramInt1;
      maxVideoHeight = paramInt2;
      return this;
    }
    
    public ParametersBuilder setMaxVideoSizeSd()
    {
      return setMaxVideoSize(1279, 719);
    }
    
    public ParametersBuilder setPreferredAudioLanguage(String paramString)
    {
      preferredAudioLanguage = paramString;
      return this;
    }
    
    public ParametersBuilder setPreferredTextLanguage(String paramString)
    {
      preferredTextLanguage = paramString;
      return this;
    }
    
    public final ParametersBuilder setRendererDisabled(int paramInt, boolean paramBoolean)
    {
      if (rendererDisabledFlags.get(paramInt) == paramBoolean) {
        return this;
      }
      if (paramBoolean)
      {
        rendererDisabledFlags.put(paramInt, true);
        return this;
      }
      rendererDisabledFlags.delete(paramInt);
      return this;
    }
    
    public ParametersBuilder setSelectUndeterminedTextLanguage(boolean paramBoolean)
    {
      selectUndeterminedTextLanguage = paramBoolean;
      return this;
    }
    
    public final ParametersBuilder setSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray, DefaultTrackSelector.SelectionOverride paramSelectionOverride)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a6 = a5\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
    }
    
    public ParametersBuilder setTunnelingAudioSessionId(int paramInt)
    {
      if (tunnelingAudioSessionId != paramInt) {
        tunnelingAudioSessionId = paramInt;
      }
      return this;
    }
    
    public ParametersBuilder setViewportSize(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      viewportWidth = paramInt1;
      viewportHeight = paramInt2;
      viewportOrientationMayChange = paramBoolean;
      return this;
    }
    
    public ParametersBuilder setViewportSizeToPhysicalDisplaySize(Context paramContext, boolean paramBoolean)
    {
      paramContext = Util.getPhysicalDisplaySize(paramContext);
      return setViewportSize(x, y, paramBoolean);
    }
  }
  
  public static final class SelectionOverride
    implements Parcelable
  {
    public static final Parcelable.Creator<SelectionOverride> CREATOR = new Parcelable.Creator()
    {
      public DefaultTrackSelector.SelectionOverride createFromParcel(Parcel paramAnonymousParcel)
      {
        return new DefaultTrackSelector.SelectionOverride(paramAnonymousParcel);
      }
      
      public DefaultTrackSelector.SelectionOverride[] newArray(int paramAnonymousInt)
      {
        return new DefaultTrackSelector.SelectionOverride[paramAnonymousInt];
      }
    };
    public final int groupIndex;
    public final int length;
    public final int[] tracks;
    
    public SelectionOverride(int paramInt, int... paramVarArgs)
    {
      groupIndex = paramInt;
      tracks = Arrays.copyOf(paramVarArgs, paramVarArgs.length);
      length = paramVarArgs.length;
      Arrays.sort(tracks);
    }
    
    SelectionOverride(Parcel paramParcel)
    {
      groupIndex = paramParcel.readInt();
      length = paramParcel.readByte();
      tracks = new int[length];
      paramParcel.readIntArray(tracks);
    }
    
    public boolean containsTrack(int paramInt)
    {
      int[] arrayOfInt = tracks;
      int j = arrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfInt[i] == paramInt) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public int describeContents()
    {
      return 0;
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
        paramObject = (SelectionOverride)paramObject;
        return (groupIndex == groupIndex) && (Arrays.equals(tracks, tracks));
      }
      return false;
    }
    
    public int hashCode()
    {
      return groupIndex * 31 + Arrays.hashCode(tracks);
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(groupIndex);
      paramParcel.writeInt(tracks.length);
      paramParcel.writeIntArray(tracks);
    }
  }
}
