package com.google.android.exoplayer2.util;

import android.text.TextUtils;
import java.util.ArrayList;

public final class MimeTypes
{
  public static final String APPLICATION_CAMERA_MOTION = "application/x-camera-motion";
  public static final String APPLICATION_CEA608 = "application/cea-608";
  public static final String APPLICATION_CEA708 = "application/cea-708";
  public static final String APPLICATION_DVBSUBS = "application/dvbsubs";
  public static final String APPLICATION_EMSG = "application/x-emsg";
  public static final String APPLICATION_EXIF = "application/x-exif";
  public static final String APPLICATION_ID3 = "application/id3";
  public static final String APPLICATION_M3U8 = "application/x-mpegURL";
  public static final String APPLICATION_MP4 = "application/mp4";
  public static final String APPLICATION_MP4CEA608 = "application/x-mp4-cea-608";
  public static final String APPLICATION_MP4VTT = "application/x-mp4-vtt";
  public static final String APPLICATION_MPD = "application/dash+xml";
  public static final String APPLICATION_PGS = "application/pgs";
  public static final String APPLICATION_RAWCC = "application/x-rawcc";
  public static final String APPLICATION_SCTE35 = "application/x-scte35";
  public static final String APPLICATION_SS = "application/vnd.ms-sstr+xml";
  public static final String APPLICATION_SUBRIP = "application/x-subrip";
  public static final String APPLICATION_TTML = "application/ttml+xml";
  public static final String APPLICATION_TX3G = "application/x-quicktime-tx3g";
  public static final String APPLICATION_VOBSUB = "application/vobsub";
  public static final String APPLICATION_WEBM = "application/webm";
  public static final String AUDIO_AAC = "audio/mp4a-latm";
  public static final String AUDIO_AC3 = "audio/ac3";
  public static final String AUDIO_ALAC = "audio/alac";
  public static final String AUDIO_ALAW = "audio/g711-alaw";
  public static final String AUDIO_AMR_NB = "audio/3gpp";
  public static final String AUDIO_AMR_WB = "audio/amr-wb";
  public static final String AUDIO_DTS = "audio/vnd.dts";
  public static final String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
  public static final String AUDIO_DTS_HD = "audio/vnd.dts.hd";
  public static final String AUDIO_E_AC3 = "audio/eac3";
  public static final String AUDIO_E_AC3_JOC = "audio/eac3-joc";
  public static final String AUDIO_FLAC = "audio/flac";
  public static final String AUDIO_MLAW = "audio/g711-mlaw";
  public static final String AUDIO_MP4 = "audio/mp4";
  public static final String AUDIO_MPEG = "audio/mpeg";
  public static final String AUDIO_MPEG_L1 = "audio/mpeg-L1";
  public static final String AUDIO_MPEG_L2 = "audio/mpeg-L2";
  public static final String AUDIO_MSGSM = "audio/gsm";
  public static final String AUDIO_OPUS = "audio/opus";
  public static final String AUDIO_RAW = "audio/raw";
  public static final String AUDIO_TRUEHD = "audio/true-hd";
  public static final String AUDIO_UNKNOWN = "audio/x-unknown";
  public static final String AUDIO_VORBIS = "audio/vorbis";
  public static final String AUDIO_WEBM = "audio/webm";
  public static final String BASE_TYPE_APPLICATION = "application";
  public static final String BASE_TYPE_AUDIO = "audio";
  public static final String BASE_TYPE_TEXT = "text";
  public static final String BASE_TYPE_VIDEO = "video";
  public static final String TEXT_SSA = "text/x-ssa";
  public static final String TEXT_VTT = "text/vtt";
  public static final String VIDEO_H263 = "video/3gpp";
  public static final String VIDEO_H264 = "video/avc";
  public static final String VIDEO_H265 = "video/hevc";
  public static final String VIDEO_MP4 = "video/mp4";
  public static final String VIDEO_MP4V = "video/mp4v-es";
  public static final String VIDEO_MPEG = "video/mpeg";
  public static final String VIDEO_MPEG2 = "video/mpeg2";
  public static final String VIDEO_UNKNOWN = "video/x-unknown";
  public static final String VIDEO_VC1 = "video/wvc1";
  public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
  public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
  public static final String VIDEO_WEBM = "video/webm";
  private static final ArrayList<CustomMimeType> customMimeTypes = new ArrayList();
  
  private MimeTypes() {}
  
  public static String getAudioMediaMimeType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = Util.splitCodecs(paramString);
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      String str = getMediaMimeType(paramString[i]);
      if ((str != null) && (isAudio(str))) {
        return str;
      }
      i += 1;
    }
    return null;
  }
  
  private static String getCustomMimeTypeForCodec(String paramString)
  {
    int j = customMimeTypes.size();
    int i = 0;
    while (i < j)
    {
      CustomMimeType localCustomMimeType = (CustomMimeType)customMimeTypes.get(i);
      if (paramString.startsWith(codecPrefix)) {
        return mimeType;
      }
      i += 1;
    }
    return null;
  }
  
  public static int getEncoding(String paramString)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 1556697186: 
      if (paramString.equals("audio/true-hd")) {
        i = 5;
      }
      break;
    case 1505942594: 
      if (paramString.equals("audio/vnd.dts.hd")) {
        i = 4;
      }
      break;
    case 1504578661: 
      if (paramString.equals("audio/eac3")) {
        i = 1;
      }
      break;
    case 187078296: 
      if (paramString.equals("audio/ac3")) {
        i = 0;
      }
      break;
    case -1095064472: 
      if (paramString.equals("audio/vnd.dts")) {
        i = 3;
      }
      break;
    case -2123537834: 
      if (paramString.equals("audio/eac3-joc")) {
        i = 2;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      return 0;
    case 5: 
      return 14;
    case 4: 
      return 8;
    case 3: 
      return 7;
    case 1: 
    case 2: 
      return 6;
    }
    return 5;
  }
  
  public static String getMediaMimeType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = Util.toLowerInvariant(paramString.trim());
    if ((!paramString.startsWith("avc1")) && (!paramString.startsWith("avc3"))) {
      if ((!paramString.startsWith("hev1")) && (!paramString.startsWith("hvc1"))) {
        if ((!paramString.startsWith("vp9")) && (!paramString.startsWith("vp09"))) {
          if ((!paramString.startsWith("vp8")) && (!paramString.startsWith("vp08"))) {
            if (paramString.startsWith("mp4a")) {
              if (paramString.startsWith("mp4a."))
              {
                paramString = paramString.substring(5);
                if (paramString.length() < 2) {}
              }
            }
          }
        }
      }
    }
    try
    {
      paramString = getMimeTypeFromMp4ObjectType(Integer.parseInt(Util.toUpperInvariant(paramString.substring(0, 2)), 16));
    }
    catch (NumberFormatException paramString)
    {
      for (;;) {}
    }
    paramString = null;
    if (paramString == null)
    {
      return "audio/mp4a-latm";
      if ((!paramString.startsWith("ac-3")) && (!paramString.startsWith("dac3")))
      {
        if ((!paramString.startsWith("ec-3")) && (!paramString.startsWith("dec3")))
        {
          if (paramString.startsWith("ec+3")) {
            return "audio/eac3-joc";
          }
          if ((!paramString.startsWith("dtsc")) && (!paramString.startsWith("dtse")))
          {
            if ((!paramString.startsWith("dtsh")) && (!paramString.startsWith("dtsl")))
            {
              if (paramString.startsWith("opus")) {
                return "audio/opus";
              }
              if (paramString.startsWith("vorbis")) {
                return "audio/vorbis";
              }
              if (paramString.startsWith("flac")) {
                return "audio/flac";
              }
              return getCustomMimeTypeForCodec(paramString);
            }
            return "audio/vnd.dts.hd";
          }
          return "audio/vnd.dts";
        }
        return "audio/eac3";
      }
      return "audio/ac3";
      return "video/x-vnd.on2.vp8";
      return "video/x-vnd.on2.vp9";
      return "video/hevc";
      return "video/avc";
    }
    return paramString;
  }
  
  public static String getMimeTypeFromMp4ObjectType(int paramInt)
  {
    if (paramInt != 35)
    {
      if (paramInt != 64)
      {
        if (paramInt != 163)
        {
          if (paramInt != 177)
          {
            switch (paramInt)
            {
            default: 
              switch (paramInt)
              {
              default: 
                switch (paramInt)
                {
                default: 
                  switch (paramInt)
                  {
                  default: 
                    return null;
                  case 173: 
                    return "audio/opus";
                  case 170: 
                  case 171: 
                    return "audio/vnd.dts.hd";
                  }
                  return "audio/vnd.dts";
                case 166: 
                  return "audio/eac3";
                }
                return "audio/ac3";
              case 106: 
                return "video/mpeg";
              case 105: 
              case 107: 
                return "audio/mpeg";
              case 96: 
              case 97: 
              case 98: 
              case 99: 
              case 100: 
              case 101: 
                return "video/mpeg2";
              }
            case 33: 
              return "video/avc";
            }
            return "video/mp4v-es";
          }
          return "video/x-vnd.on2.vp9";
        }
        return "video/wvc1";
      }
      return "audio/mp4a-latm";
    }
    return "video/hevc";
  }
  
  private static String getTopLevelType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    int i = paramString.indexOf('/');
    if (i != -1) {
      return paramString.substring(0, i);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid mime type: ");
    localStringBuilder.append(paramString);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static int getTrackType(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return -1;
    }
    if (isAudio(paramString)) {
      return 1;
    }
    if (isVideo(paramString)) {
      return 2;
    }
    if ((!isText(paramString)) && (!"application/cea-608".equals(paramString)) && (!"application/cea-708".equals(paramString)) && (!"application/x-mp4-cea-608".equals(paramString)) && (!"application/x-subrip".equals(paramString)) && (!"application/ttml+xml".equals(paramString)) && (!"application/x-quicktime-tx3g".equals(paramString)) && (!"application/x-mp4-vtt".equals(paramString)) && (!"application/x-rawcc".equals(paramString)) && (!"application/vobsub".equals(paramString)) && (!"application/pgs".equals(paramString)) && (!"application/dvbsubs".equals(paramString)))
    {
      if ((!"application/id3".equals(paramString)) && (!"application/x-emsg".equals(paramString)) && (!"application/x-scte35".equals(paramString)))
      {
        if ("application/x-camera-motion".equals(paramString)) {
          return 5;
        }
        return getTrackTypeForCustomMimeType(paramString);
      }
      return 4;
    }
    return 3;
  }
  
  private static int getTrackTypeForCustomMimeType(String paramString)
  {
    int j = customMimeTypes.size();
    int i = 0;
    while (i < j)
    {
      CustomMimeType localCustomMimeType = (CustomMimeType)customMimeTypes.get(i);
      if (paramString.equals(mimeType)) {
        return trackType;
      }
      i += 1;
    }
    return -1;
  }
  
  public static int getTrackTypeOfCodec(String paramString)
  {
    return getTrackType(getMediaMimeType(paramString));
  }
  
  public static String getVideoMediaMimeType(String paramString)
  {
    if (paramString == null) {
      return null;
    }
    paramString = Util.splitCodecs(paramString);
    int j = paramString.length;
    int i = 0;
    while (i < j)
    {
      String str = getMediaMimeType(paramString[i]);
      if ((str != null) && (isVideo(str))) {
        return str;
      }
      i += 1;
    }
    return null;
  }
  
  public static boolean isApplication(String paramString)
  {
    return "application".equals(getTopLevelType(paramString));
  }
  
  public static boolean isAudio(String paramString)
  {
    return "audio".equals(getTopLevelType(paramString));
  }
  
  public static boolean isText(String paramString)
  {
    return "text".equals(getTopLevelType(paramString));
  }
  
  public static boolean isVideo(String paramString)
  {
    return "video".equals(getTopLevelType(paramString));
  }
  
  public static void registerCustomMimeType(String paramString1, String paramString2, int paramInt)
  {
    paramString2 = new CustomMimeType(paramString1, paramString2, paramInt);
    int i = customMimeTypes.size();
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramString1.equals(customMimeTypesgetmimeType))
      {
        customMimeTypes.remove(paramInt);
        break;
      }
      paramInt += 1;
    }
    customMimeTypes.add(paramString2);
  }
  
  private static final class CustomMimeType
  {
    public final String codecPrefix;
    public final String mimeType;
    public final int trackType;
    
    public CustomMimeType(String paramString1, String paramString2, int paramInt)
    {
      mimeType = paramString1;
      codecPrefix = paramString2;
      trackType = paramInt;
    }
  }
}
