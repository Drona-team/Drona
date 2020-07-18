package com.google.android.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Pair;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo
{
  public static final int MAX_SUPPORTED_INSTANCES_UNKNOWN = -1;
  public static final String TAG = "MediaCodecInfo";
  public final boolean adaptive;
  @Nullable
  public final MediaCodecInfo.CodecCapabilities capabilities;
  private final boolean isVideo;
  @Nullable
  public final String mimeType;
  public final String name;
  public final boolean passthrough;
  public final boolean secure;
  public final boolean tunneling;
  
  private MediaCodecInfo(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    name = ((String)Assertions.checkNotNull(paramString1));
    mimeType = paramString2;
    capabilities = paramCodecCapabilities;
    passthrough = paramBoolean1;
    boolean bool = true;
    if ((!paramBoolean2) && (paramCodecCapabilities != null) && (isAdaptive(paramCodecCapabilities))) {
      paramBoolean1 = true;
    } else {
      paramBoolean1 = false;
    }
    adaptive = paramBoolean1;
    if ((paramCodecCapabilities != null) && (isTunneling(paramCodecCapabilities))) {
      paramBoolean1 = true;
    } else {
      paramBoolean1 = false;
    }
    tunneling = paramBoolean1;
    paramBoolean1 = bool;
    if (!paramBoolean3) {
      if ((paramCodecCapabilities != null) && (isSecure(paramCodecCapabilities))) {
        paramBoolean1 = bool;
      } else {
        paramBoolean1 = false;
      }
    }
    secure = paramBoolean1;
    isVideo = MimeTypes.isVideo(paramString2);
  }
  
  private static int adjustMaxInputChannelCount(String paramString1, String paramString2, int paramInt)
  {
    if (paramInt <= 1)
    {
      if ((Util.SDK_INT >= 26) && (paramInt > 0)) {
        return paramInt;
      }
      if ((!"audio/mpeg".equals(paramString2)) && (!"audio/3gpp".equals(paramString2)) && (!"audio/amr-wb".equals(paramString2)) && (!"audio/mp4a-latm".equals(paramString2)) && (!"audio/vorbis".equals(paramString2)) && (!"audio/opus".equals(paramString2)) && (!"audio/raw".equals(paramString2)) && (!"audio/flac".equals(paramString2)) && (!"audio/g711-alaw".equals(paramString2)) && (!"audio/g711-mlaw".equals(paramString2)))
      {
        if ("audio/gsm".equals(paramString2)) {
          return paramInt;
        }
        int i;
        if ("audio/ac3".equals(paramString2)) {
          i = 6;
        } else if ("audio/eac3".equals(paramString2)) {
          i = 16;
        } else {
          i = 30;
        }
        paramString2 = new StringBuilder();
        paramString2.append("AssumedMaxChannelAdjustment: ");
        paramString2.append(paramString1);
        paramString2.append(", [");
        paramString2.append(paramInt);
        paramString2.append(" to ");
        paramString2.append(i);
        paramString2.append("]");
        Log.w("MediaCodecInfo", paramString2.toString());
        return i;
      }
    }
    return paramInt;
  }
  
  private static boolean areSizeAndRateSupportedV21(MediaCodecInfo.VideoCapabilities paramVideoCapabilities, int paramInt1, int paramInt2, double paramDouble)
  {
    if ((paramDouble != -1.0D) && (paramDouble > 0.0D)) {
      return paramVideoCapabilities.areSizeAndRateSupported(paramInt1, paramInt2, paramDouble);
    }
    return paramVideoCapabilities.isSizeSupported(paramInt1, paramInt2);
  }
  
  private static int getMaxSupportedInstancesV23(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.getMaxSupportedInstances();
  }
  
  private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 19) && (isAdaptiveV19(paramCodecCapabilities));
  }
  
  private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("adaptive-playback");
  }
  
  private static boolean isSecure(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 21) && (isSecureV21(paramCodecCapabilities));
  }
  
  private static boolean isSecureV21(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("secure-playback");
  }
  
  private static boolean isTunneling(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 21) && (isTunnelingV21(paramCodecCapabilities));
  }
  
  private static boolean isTunnelingV21(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("tunneled-playback");
  }
  
  private void logAssumedSupport(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AssumedSupport [");
    localStringBuilder.append(paramString);
    localStringBuilder.append("] [");
    localStringBuilder.append(name);
    localStringBuilder.append(", ");
    localStringBuilder.append(mimeType);
    localStringBuilder.append("] [");
    localStringBuilder.append(Util.DEVICE_DEBUG_INFO);
    localStringBuilder.append("]");
    Log.d("MediaCodecInfo", localStringBuilder.toString());
  }
  
  private void logNoSupport(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("NoSupport [");
    localStringBuilder.append(paramString);
    localStringBuilder.append("] [");
    localStringBuilder.append(name);
    localStringBuilder.append(", ");
    localStringBuilder.append(mimeType);
    localStringBuilder.append("] [");
    localStringBuilder.append(Util.DEVICE_DEBUG_INFO);
    localStringBuilder.append("]");
    Log.d("MediaCodecInfo", localStringBuilder.toString());
  }
  
  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities, false, false, false);
  }
  
  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities, false, paramBoolean1, paramBoolean2);
  }
  
  public static MediaCodecInfo newPassthroughInstance(String paramString)
  {
    return new MediaCodecInfo(paramString, null, null, true, false, false);
  }
  
  public Point alignVideoSizeV21(int paramInt1, int paramInt2)
  {
    if (capabilities == null)
    {
      logNoSupport("align.caps");
      return null;
    }
    MediaCodecInfo.VideoCapabilities localVideoCapabilities = capabilities.getVideoCapabilities();
    if (localVideoCapabilities == null)
    {
      logNoSupport("align.vCaps");
      return null;
    }
    int i = localVideoCapabilities.getWidthAlignment();
    int j = localVideoCapabilities.getHeightAlignment();
    return new Point(Util.ceilDivide(paramInt1, i) * i, Util.ceilDivide(paramInt2, j) * j);
  }
  
  public int getMaxSupportedInstances()
  {
    if ((Util.SDK_INT >= 23) && (capabilities != null)) {
      return getMaxSupportedInstancesV23(capabilities);
    }
    return -1;
  }
  
  public MediaCodecInfo.CodecProfileLevel[] getProfileLevels()
  {
    if ((capabilities != null) && (capabilities.profileLevels != null)) {
      return capabilities.profileLevels;
    }
    return new MediaCodecInfo.CodecProfileLevel[0];
  }
  
  public boolean isAudioChannelCountSupportedV21(int paramInt)
  {
    if (capabilities == null)
    {
      logNoSupport("channelCount.caps");
      return false;
    }
    Object localObject = capabilities.getAudioCapabilities();
    if (localObject == null)
    {
      logNoSupport("channelCount.aCaps");
      return false;
    }
    if (adjustMaxInputChannelCount(name, mimeType, ((MediaCodecInfo.AudioCapabilities)localObject).getMaxInputChannelCount()) < paramInt)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("channelCount.support, ");
      ((StringBuilder)localObject).append(paramInt);
      logNoSupport(((StringBuilder)localObject).toString());
      return false;
    }
    return true;
  }
  
  public boolean isAudioSampleRateSupportedV21(int paramInt)
  {
    if (capabilities == null)
    {
      logNoSupport("sampleRate.caps");
      return false;
    }
    Object localObject = capabilities.getAudioCapabilities();
    if (localObject == null)
    {
      logNoSupport("sampleRate.aCaps");
      return false;
    }
    if (!((MediaCodecInfo.AudioCapabilities)localObject).isSampleRateSupported(paramInt))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("sampleRate.support, ");
      ((StringBuilder)localObject).append(paramInt);
      logNoSupport(((StringBuilder)localObject).toString());
      return false;
    }
    return true;
  }
  
  public boolean isCodecSupported(String paramString)
  {
    if (paramString != null)
    {
      if (mimeType == null) {
        return true;
      }
      String str = MimeTypes.getMediaMimeType(paramString);
      if (str == null) {
        return true;
      }
      if (!mimeType.equals(str))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("codec.mime ");
        ((StringBuilder)localObject).append(paramString);
        ((StringBuilder)localObject).append(", ");
        ((StringBuilder)localObject).append(str);
        logNoSupport(((StringBuilder)localObject).toString());
        return false;
      }
      Object localObject = MediaCodecUtil.getCodecProfileAndLevel(paramString);
      if (localObject == null) {
        return true;
      }
      MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = getProfileLevels();
      int j = arrayOfCodecProfileLevel.length;
      int i = 0;
      while (i < j)
      {
        MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = arrayOfCodecProfileLevel[i];
        if ((profile == ((Integer)first).intValue()) && (level >= ((Integer)second).intValue())) {
          return true;
        }
        i += 1;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("codec.profileLevel, ");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(", ");
      ((StringBuilder)localObject).append(str);
      logNoSupport(((StringBuilder)localObject).toString());
      return false;
    }
    return true;
  }
  
  public boolean isFormatSupported(Format paramFormat)
    throws MediaCodecUtil.DecoderQueryException
  {
    boolean bool2 = isCodecSupported(codecs);
    boolean bool1 = false;
    if (!bool2) {
      return false;
    }
    if (isVideo)
    {
      if (width > 0)
      {
        if (height <= 0) {
          return true;
        }
        if (Util.SDK_INT >= 21) {
          return isVideoSizeAndRateSupportedV21(width, height, frameRate);
        }
        if (width * height <= MediaCodecUtil.maxH264DecodableFrameSize()) {
          bool1 = true;
        }
        if (!bool1)
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("legacyFrameSize, ");
          localStringBuilder.append(width);
          localStringBuilder.append("x");
          localStringBuilder.append(height);
          logNoSupport(localStringBuilder.toString());
          return bool1;
        }
      }
      else
      {
        return true;
      }
    }
    else
    {
      if ((Util.SDK_INT >= 21) && (((sampleRate != -1) && (!isAudioSampleRateSupportedV21(sampleRate))) || ((channelCount != -1) && (!isAudioChannelCountSupportedV21(channelCount))))) {
        break label197;
      }
      return true;
    }
    return bool1;
    label197:
    return false;
  }
  
  public boolean isSeamlessAdaptationSupported(Format paramFormat)
  {
    if (isVideo) {
      return adaptive;
    }
    paramFormat = MediaCodecUtil.getCodecProfileAndLevel(codecs);
    return (paramFormat != null) && (((Integer)first).intValue() == 42);
  }
  
  public boolean isSeamlessAdaptationSupported(Format paramFormat1, Format paramFormat2, boolean paramBoolean)
  {
    if (isVideo)
    {
      if ((sampleMimeType.equals(sampleMimeType)) && (rotationDegrees == rotationDegrees) && ((adaptive) || ((width == width) && (height == height))))
      {
        if ((!paramBoolean) && (colorInfo == null)) {
          break label210;
        }
        if (Util.areEqual(colorInfo, colorInfo)) {
          return true;
        }
      }
      return false;
    }
    else
    {
      if ("audio/mp4a-latm".equals(mimeType))
      {
        if ((!sampleMimeType.equals(sampleMimeType)) || (channelCount != channelCount)) {
          break label212;
        }
        if (sampleRate != sampleRate) {
          return false;
        }
        paramFormat1 = MediaCodecUtil.getCodecProfileAndLevel(codecs);
        paramFormat2 = MediaCodecUtil.getCodecProfileAndLevel(codecs);
        if (paramFormat1 != null)
        {
          if (paramFormat2 == null) {
            return false;
          }
          int i = ((Integer)first).intValue();
          int j = ((Integer)first).intValue();
          return (i == 42) && (j == 42);
        }
      }
      return false;
    }
    label210:
    return true;
    label212:
    return false;
  }
  
  public boolean isVideoSizeAndRateSupportedV21(int paramInt1, int paramInt2, double paramDouble)
  {
    if (capabilities == null)
    {
      logNoSupport("sizeAndRate.caps");
      return false;
    }
    Object localObject = capabilities.getVideoCapabilities();
    if (localObject == null)
    {
      logNoSupport("sizeAndRate.vCaps");
      return false;
    }
    if (!areSizeAndRateSupportedV21((MediaCodecInfo.VideoCapabilities)localObject, paramInt1, paramInt2, paramDouble)) {
      if ((paramInt1 < paramInt2) && (areSizeAndRateSupportedV21((MediaCodecInfo.VideoCapabilities)localObject, paramInt2, paramInt1, paramDouble)))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("sizeAndRate.rotated, ");
        ((StringBuilder)localObject).append(paramInt1);
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(paramInt2);
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(paramDouble);
        logAssumedSupport(((StringBuilder)localObject).toString());
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("sizeAndRate.support, ");
        ((StringBuilder)localObject).append(paramInt1);
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(paramInt2);
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(paramDouble);
        logNoSupport(((StringBuilder)localObject).toString());
        return false;
      }
    }
    return true;
  }
  
  public String toString()
  {
    return name;
  }
}
