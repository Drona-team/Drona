package com.google.android.exoplayer2.source.configurations.playlist;

import android.net.Uri;
import android.util.Base64;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ClickListeners.PsshAtomUtil;
import com.google.android.exoplayer2.source.UnrecognizedInputFormatException;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HlsPlaylistParser
  implements ParsingLoadable.Parser<com.google.android.exoplayer2.source.hls.playlist.HlsPlaylist>
{
  private static final String ATTR_CLOSED_CAPTIONS_NONE = "CLOSED-CAPTIONS=NONE";
  private static final String BOOLEAN_FALSE = "NO";
  private static final String BOOLEAN_TRUE = "YES";
  private static final String KEYFORMAT_IDENTITY = "identity";
  private static final String KEYFORMAT_PLAYREADY = "com.microsoft.playready";
  private static final String KEYFORMAT_WIDEVINE_PSSH_BINARY = "urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed";
  private static final String KEYFORMAT_WIDEVINE_PSSH_JSON = "com.widevine";
  private static final String METHOD_AES_128 = "AES-128";
  private static final String METHOD_NONE = "NONE";
  private static final String METHOD_SAMPLE_AES = "SAMPLE-AES";
  private static final String METHOD_SAMPLE_AES_CENC = "SAMPLE-AES-CENC";
  private static final String METHOD_SAMPLE_AES_CTR = "SAMPLE-AES-CTR";
  private static final String PLAYLIST_HEADER = "#EXTM3U";
  private static final Pattern REGEX_ATTR_BYTERANGE;
  private static final Pattern REGEX_AUDIO;
  private static final Pattern REGEX_AUTOSELECT;
  private static final Pattern REGEX_AVERAGE_BANDWIDTH = Pattern.compile("AVERAGE-BANDWIDTH=(\\d+)\\b");
  private static final Pattern REGEX_BANDWIDTH;
  private static final Pattern REGEX_BYTERANGE;
  private static final Pattern REGEX_CODECS;
  private static final Pattern REGEX_DEFAULT;
  private static final Pattern REGEX_FORCED;
  private static final Pattern REGEX_FRAME_RATE;
  private static final Pattern REGEX_GROUP_ID;
  private static final Pattern REGEX_IMPORT = Pattern.compile("IMPORT=\"(.+?)\"");
  private static final Pattern REGEX_INSTREAM_ID;
  private static final Pattern REGEX_IV;
  private static final Pattern REGEX_KEYFORMAT;
  private static final Pattern REGEX_KEYFORMATVERSIONS;
  private static final Pattern REGEX_LANGUAGE;
  private static final Pattern REGEX_MEDIA_DURATION;
  private static final Pattern REGEX_MEDIA_SEQUENCE;
  private static final Pattern REGEX_MEDIA_TITLE;
  private static final Pattern REGEX_METHOD;
  private static final Pattern REGEX_NAME;
  private static final Pattern REGEX_PLAYLIST_TYPE;
  private static final Pattern REGEX_RESOLUTION;
  private static final Pattern REGEX_TARGET_DURATION;
  private static final Pattern REGEX_TIME_OFFSET;
  private static final Pattern REGEX_TYPE;
  private static final Pattern REGEX_URI;
  private static final Pattern REGEX_VALUE;
  private static final Pattern REGEX_VARIABLE_REFERENCE = Pattern.compile("\\{\\$([a-zA-Z0-9\\-_]+)\\}");
  private static final Pattern REGEX_VERSION;
  private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
  private static final String TAG_DEFINE = "#EXT-X-DEFINE";
  private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
  private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
  private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
  private static final String TAG_GAP = "#EXT-X-GAP";
  private static final String TAG_INDEPENDENT_SEGMENTS = "#EXT-X-INDEPENDENT-SEGMENTS";
  private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
  private static final String TAG_KEY = "#EXT-X-KEY";
  private static final String TAG_MEDIA = "#EXT-X-MEDIA";
  private static final String TAG_MEDIA_DURATION = "#EXTINF";
  private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
  private static final String TAG_PLAYLIST_TYPE = "#EXT-X-PLAYLIST-TYPE";
  private static final String TAG_PREFIX = "#EXT";
  private static final String TAG_PROGRAM_DATE_TIME = "#EXT-X-PROGRAM-DATE-TIME";
  private static final String TAG_START = "#EXT-X-START";
  private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
  private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
  private static final String TAG_VERSION = "#EXT-X-VERSION";
  private static final String TYPE_AUDIO = "AUDIO";
  private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
  private static final String TYPE_SUBTITLES = "SUBTITLES";
  private static final String TYPE_VIDEO = "VIDEO";
  private final HlsMasterPlaylist masterPlaylist;
  
  static
  {
    REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    REGEX_BANDWIDTH = Pattern.compile("[^-]BANDWIDTH=(\\d+)\\b");
    REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    REGEX_FRAME_RATE = Pattern.compile("FRAME-RATE=([\\d\\.]+)\\b");
    REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    REGEX_PLAYLIST_TYPE = Pattern.compile("#EXT-X-PLAYLIST-TYPE:(.+)\\b");
    REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    REGEX_MEDIA_TITLE = Pattern.compile("#EXTINF:[\\d\\.]+\\b,(.+)");
    REGEX_TIME_OFFSET = Pattern.compile("TIME-OFFSET=(-?[\\d\\.]+)\\b");
    REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128|SAMPLE-AES|SAMPLE-AES-CENC|SAMPLE-AES-CTR)\\s*(?:,|$)");
    REGEX_KEYFORMAT = Pattern.compile("KEYFORMAT=\"(.+?)\"");
    REGEX_KEYFORMATVERSIONS = Pattern.compile("KEYFORMATVERSIONS=\"(.+?)\"");
    REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
    REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"((?:CC|SERVICE)\\d+)\"");
    REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    REGEX_FORCED = compileBooleanAttrPattern("FORCED");
    REGEX_VALUE = Pattern.compile("VALUE=\"(.+?)\"");
  }
  
  public HlsPlaylistParser()
  {
    this(HlsMasterPlaylist.EMPTY);
  }
  
  public HlsPlaylistParser(HlsMasterPlaylist paramHlsMasterPlaylist)
  {
    masterPlaylist = paramHlsMasterPlaylist;
  }
  
  private static boolean checkPlaylistHeader(BufferedReader paramBufferedReader)
    throws IOException
  {
    int j = paramBufferedReader.read();
    int i = j;
    if (j == 239) {
      if (paramBufferedReader.read() == 187)
      {
        if (paramBufferedReader.read() != 191) {
          return false;
        }
        i = paramBufferedReader.read();
      }
      else
      {
        return false;
      }
    }
    j = skipIgnorableWhitespace(paramBufferedReader, true, i);
    int k = "#EXTM3U".length();
    i = 0;
    while (i < k)
    {
      if (j != "#EXTM3U".charAt(i)) {
        return false;
      }
      j = paramBufferedReader.read();
      i += 1;
    }
    return Util.isLinebreak(skipIgnorableWhitespace(paramBufferedReader, false, j));
  }
  
  private static Pattern compileBooleanAttrPattern(String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramString);
    localStringBuilder.append("=(");
    localStringBuilder.append("NO");
    localStringBuilder.append("|");
    localStringBuilder.append("YES");
    localStringBuilder.append(")");
    return Pattern.compile(localStringBuilder.toString());
  }
  
  private static double parseDoubleAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Double.parseDouble(parseStringAttr(paramString, paramPattern, Collections.emptyMap()));
  }
  
  private static int parseIntAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Integer.parseInt(parseStringAttr(paramString, paramPattern, Collections.emptyMap()));
  }
  
  private static long parseLongAttr(String paramString, Pattern paramPattern)
    throws ParserException
  {
    return Long.parseLong(parseStringAttr(paramString, paramPattern, Collections.emptyMap()));
  }
  
  private static HlsMasterPlaylist parseMasterPlaylist(LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a69 = a68\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private static HlsMediaPlaylist parseMediaPlaylist(HlsMasterPlaylist paramHlsMasterPlaylist, LineIterator paramLineIterator, String paramString)
    throws IOException
  {
    boolean bool4 = hasIndependentSegments;
    HashMap localHashMap = new HashMap();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    TreeMap localTreeMap = new TreeMap();
    long l9 = -9223372036854775807L;
    long l8 = -9223372036854775807L;
    int i = 0;
    Object localObject4 = null;
    long l7 = 0L;
    boolean bool3 = false;
    int m = 0;
    long l6 = 0L;
    int k = 1;
    boolean bool2 = false;
    Object localObject7 = null;
    long l1 = 0L;
    long l3 = 0L;
    Object localObject1 = null;
    int j = 0;
    long l4 = 0L;
    Object localObject2 = null;
    Object localObject3 = null;
    HlsMediaPlaylist.Segment localSegment = null;
    long l2 = -1L;
    boolean bool5 = false;
    String str1 = "";
    long l5 = 0L;
    label119:
    label1307:
    for (;;)
    {
      if (!paramLineIterator.hasNext()) {
        break label1310;
      }
      String str2 = paramLineIterator.next();
      if (str2.startsWith("#EXT")) {
        localArrayList2.add(str2);
      }
      Object localObject5;
      if (str2.startsWith("#EXT-X-PLAYLIST-TYPE"))
      {
        localObject5 = parseStringAttr(str2, REGEX_PLAYLIST_TYPE, localHashMap);
        if ("VOD".equals(localObject5)) {
          i = 1;
        } else if ("EVENT".equals(localObject5)) {
          i = 2;
        }
      }
      else if (str2.startsWith("#EXT-X-START"))
      {
        l9 = (parseDoubleAttr(str2, REGEX_TIME_OFFSET) * 1000000.0D);
      }
      else
      {
        Object localObject6;
        long l10;
        if (str2.startsWith("#EXT-X-MAP"))
        {
          localObject5 = parseStringAttr(str2, REGEX_URI, localHashMap);
          localObject6 = parseOptionalStringAttr(str2, REGEX_ATTR_BYTERANGE, localHashMap);
          l10 = l1;
          if (localObject6 != null)
          {
            localObject6 = ((String)localObject6).split("@");
            long l11 = Long.parseLong(localObject6[0]);
            l10 = l1;
            l2 = l11;
            if (localObject6.length > 1)
            {
              l10 = Long.parseLong(localObject6[1]);
              l2 = l11;
            }
          }
          localSegment = new HlsMediaPlaylist.Segment((String)localObject5, l10, l2);
          l1 = 0L;
          l2 = -1L;
        }
        else if (str2.startsWith("#EXT-X-TARGETDURATION"))
        {
          l8 = 1000000L * parseIntAttr(str2, REGEX_TARGET_DURATION);
        }
        else if (str2.startsWith("#EXT-X-MEDIA-SEQUENCE"))
        {
          l6 = parseLongAttr(str2, REGEX_MEDIA_SEQUENCE);
          l3 = l6;
        }
        else if (str2.startsWith("#EXT-X-VERSION"))
        {
          k = parseIntAttr(str2, REGEX_VERSION);
        }
        else
        {
          if (str2.startsWith("#EXT-X-DEFINE"))
          {
            localObject5 = parseOptionalStringAttr(str2, REGEX_IMPORT, localHashMap);
            if (localObject5 != null)
            {
              localObject6 = (String)variableDefinitions.get(localObject5);
              if (localObject6 != null) {
                localHashMap.put(localObject5, localObject6);
              }
            }
            else
            {
              localHashMap.put(parseStringAttr(str2, REGEX_NAME, localHashMap), parseStringAttr(str2, REGEX_VALUE, localHashMap));
            }
          }
          for (;;)
          {
            break label1307;
            if (str2.startsWith("#EXTINF"))
            {
              double d = parseDoubleAttr(str2, REGEX_MEDIA_DURATION);
              str1 = parseOptionalStringAttr(str2, REGEX_MEDIA_TITLE, "", localHashMap);
              l5 = (d * 1000000.0D);
              break label119;
            }
            Object localObject8;
            if (str2.startsWith("#EXT-X-KEY"))
            {
              String str3 = parseStringAttr(str2, REGEX_METHOD, localHashMap);
              localObject8 = parseOptionalStringAttr(str2, REGEX_KEYFORMAT, "identity", localHashMap);
              if ("NONE".equals(str3))
              {
                localTreeMap.clear();
                localObject1 = null;
                localObject2 = null;
                localObject3 = null;
                break label119;
              }
              localObject6 = parseOptionalStringAttr(str2, REGEX_IV, localHashMap);
              if ("identity".equals(localObject8))
              {
                localObject3 = localObject4;
                localObject5 = localObject1;
                if ("AES-128".equals(str3))
                {
                  localObject2 = parseStringAttr(str2, REGEX_URI, localHashMap);
                  localObject3 = localObject6;
                  break label119;
                }
              }
              for (;;)
              {
                localObject2 = null;
                localObject4 = localObject3;
                localObject1 = localObject5;
                localObject3 = localObject6;
                break;
                localObject2 = localObject4;
                if (localObject4 == null) {
                  if ((!"SAMPLE-AES-CENC".equals(str3)) && (!"SAMPLE-AES-CTR".equals(str3))) {
                    localObject2 = "cbcs";
                  } else {
                    localObject2 = "cenc";
                  }
                }
                if ("com.microsoft.playready".equals(localObject8)) {
                  localObject4 = parsePlayReadySchemeData(str2, localHashMap);
                } else {
                  localObject4 = parseWidevineSchemeData(str2, (String)localObject8, localHashMap);
                }
                localObject3 = localObject2;
                localObject5 = localObject1;
                if (localObject4 != null)
                {
                  localTreeMap.put(localObject8, localObject4);
                  localObject5 = null;
                  localObject3 = localObject2;
                }
              }
            }
            if (str2.startsWith("#EXT-X-BYTERANGE"))
            {
              localObject5 = parseStringAttr(str2, REGEX_BYTERANGE, localHashMap).split("@");
              l10 = Long.parseLong(localObject5[0]);
              l2 = l10;
              if (localObject5.length <= 1) {
                break label119;
              }
              l1 = Long.parseLong(localObject5[1]);
              l2 = l10;
              break label119;
            }
            if (str2.startsWith("#EXT-X-DISCONTINUITY-SEQUENCE"))
            {
              m = Integer.parseInt(str2.substring(str2.indexOf(':') + 1));
              bool3 = true;
              break label119;
            }
            if (str2.equals("#EXT-X-DISCONTINUITY"))
            {
              j += 1;
              break label119;
            }
            if (str2.startsWith("#EXT-X-PROGRAM-DATE-TIME"))
            {
              if (l7 != 0L) {
                continue;
              }
              l7 = IpAddress.msToUs(Util.parseXsDateTime(str2.substring(str2.indexOf(':') + 1))) - l4;
              break label119;
            }
            if (str2.equals("#EXT-X-GAP"))
            {
              bool5 = true;
              break label119;
            }
            if (str2.equals("#EXT-X-INDEPENDENT-SEGMENTS"))
            {
              bool4 = true;
              break label119;
            }
            if (str2.equals("#EXT-X-ENDLIST"))
            {
              bool2 = true;
              break label119;
            }
            if (!str2.startsWith("#"))
            {
              if (localObject2 == null) {
                localObject5 = null;
              } else if (localObject3 != null) {
                localObject5 = localObject3;
              } else {
                localObject5 = Long.toHexString(l3);
              }
              l10 = l3 + 1L;
              boolean bool1 = l2 < -1L;
              l3 = l1;
              if (!bool1) {
                l3 = 0L;
              }
              localObject8 = localObject7;
              localObject6 = localObject1;
              if (localObject1 == null)
              {
                localObject8 = localObject7;
                localObject6 = localObject1;
                if (!localTreeMap.isEmpty())
                {
                  localObject1 = (DrmInitData.SchemeData[])localTreeMap.values().toArray(new DrmInitData.SchemeData[0]);
                  localObject6 = new DrmInitData((String)localObject4, (DrmInitData.SchemeData[])localObject1);
                  if (localObject7 == null)
                  {
                    localObject7 = new DrmInitData.SchemeData[localObject1.length];
                    int n = 0;
                    while (n < localObject1.length)
                    {
                      localObject7[n] = localObject1[n].copyWithData(null);
                      n += 1;
                    }
                    localObject8 = new DrmInitData((String)localObject4, (DrmInitData.SchemeData[])localObject7);
                  }
                  else
                  {
                    localObject8 = localObject7;
                  }
                }
              }
              localArrayList1.add(new HlsMediaPlaylist.Segment(replaceVariableReferences(str2, localHashMap), localSegment, str1, l5, j, l4, (DrmInitData)localObject6, (String)localObject2, (String)localObject5, l3, l2, bool5));
              l4 += l5;
              l1 = l3;
              if (bool1) {
                l1 = l3 + l2;
              }
              localObject7 = localObject8;
              l3 = l10;
              localObject1 = localObject6;
              break;
            }
          }
        }
      }
    }
    label1310:
    if (l7 != 0L) {
      bool5 = true;
    } else {
      bool5 = false;
    }
    return new HlsMediaPlaylist(i, paramString, localArrayList2, l9, l7, bool3, m, l6, k, l8, bool4, bool2, bool5, (DrmInitData)localObject7, localArrayList1);
  }
  
  private static boolean parseOptionalBooleanAttribute(String paramString, Pattern paramPattern, boolean paramBoolean)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find()) {
      paramBoolean = paramString.group(1).equals("YES");
    }
    return paramBoolean;
  }
  
  private static String parseOptionalStringAttr(String paramString1, Pattern paramPattern, String paramString2, Map paramMap)
  {
    paramString1 = paramPattern.matcher(paramString1);
    if (paramString1.find()) {
      paramString2 = paramString1.group(1);
    }
    if (!paramMap.isEmpty())
    {
      if (paramString2 == null) {
        return paramString2;
      }
      return replaceVariableReferences(paramString2, paramMap);
    }
    return paramString2;
  }
  
  private static String parseOptionalStringAttr(String paramString, Pattern paramPattern, Map paramMap)
  {
    return parseOptionalStringAttr(paramString, paramPattern, null, paramMap);
  }
  
  private static DrmInitData.SchemeData parsePlayReadySchemeData(String paramString, Map paramMap)
    throws ParserException
  {
    if (!"1".equals(parseOptionalStringAttr(paramString, REGEX_KEYFORMATVERSIONS, "1", paramMap))) {
      return null;
    }
    paramString = parseStringAttr(paramString, REGEX_URI, paramMap);
    paramString = Base64.decode(paramString.substring(paramString.indexOf(',')), 0);
    paramString = PsshAtomUtil.buildPsshAtom(IpAddress.PLAYREADY_UUID, paramString);
    return new DrmInitData.SchemeData(IpAddress.PLAYREADY_UUID, "video/mp4", paramString);
  }
  
  private static int parseSelectionFlags(String paramString)
  {
    if (parseOptionalBooleanAttribute(paramString, REGEX_DEFAULT, false)) {
      j = 1;
    } else {
      j = 0;
    }
    int i = j;
    if (parseOptionalBooleanAttribute(paramString, REGEX_FORCED, false)) {
      i = j | 0x2;
    }
    int j = i;
    if (parseOptionalBooleanAttribute(paramString, REGEX_AUTOSELECT, false)) {
      j = i | 0x4;
    }
    return j;
  }
  
  private static String parseStringAttr(String paramString, Pattern paramPattern, Map paramMap)
    throws ParserException
  {
    paramMap = parseOptionalStringAttr(paramString, paramPattern, paramMap);
    if (paramMap != null) {
      return paramMap;
    }
    paramMap = new StringBuilder();
    paramMap.append("Couldn't match ");
    paramMap.append(paramPattern.pattern());
    paramMap.append(" in ");
    paramMap.append(paramString);
    throw new ParserException(paramMap.toString());
  }
  
  private static DrmInitData.SchemeData parseWidevineSchemeData(String paramString1, String paramString2, Map paramMap)
    throws ParserException
  {
    if ("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed".equals(paramString2))
    {
      paramString1 = parseStringAttr(paramString1, REGEX_URI, paramMap);
      return new DrmInitData.SchemeData(IpAddress.WIDEVINE_UUID, "video/mp4", Base64.decode(paramString1.substring(paramString1.indexOf(',')), 0));
    }
    if ("com.widevine".equals(paramString2))
    {
      paramString2 = IpAddress.WIDEVINE_UUID;
      try
      {
        paramString1 = new DrmInitData.SchemeData(paramString2, "hls", paramString1.getBytes("UTF-8"));
        return paramString1;
      }
      catch (UnsupportedEncodingException paramString1)
      {
        throw new ParserException(paramString1);
      }
    }
    return null;
  }
  
  private static String replaceVariableReferences(String paramString, Map paramMap)
  {
    paramString = REGEX_VARIABLE_REFERENCE.matcher(paramString);
    StringBuffer localStringBuffer = new StringBuffer();
    while (paramString.find())
    {
      String str = paramString.group(1);
      if (paramMap.containsKey(str)) {
        paramString.appendReplacement(localStringBuffer, Matcher.quoteReplacement((String)paramMap.get(str)));
      }
    }
    paramString.appendTail(localStringBuffer);
    return localStringBuffer.toString();
  }
  
  private static int skipIgnorableWhitespace(BufferedReader paramBufferedReader, boolean paramBoolean, int paramInt)
    throws IOException
  {
    while ((paramInt != -1) && (Character.isWhitespace(paramInt)) && ((paramBoolean) || (!Util.isLinebreak(paramInt)))) {
      paramInt = paramBufferedReader.read();
    }
    return paramInt;
  }
  
  public HlsPlaylist parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream));
    ArrayDeque localArrayDeque = new ArrayDeque();
    try
    {
      boolean bool = checkPlaylistHeader(paramInputStream);
      if (bool)
      {
        String str;
        for (;;)
        {
          str = paramInputStream.readLine();
          if (str == null) {
            break label261;
          }
          str = str.trim();
          bool = str.isEmpty();
          if (!bool)
          {
            bool = str.startsWith("#EXT-X-STREAM-INF");
            if (bool)
            {
              localArrayDeque.add(str);
              paramUri = parseMasterPlaylist(new LineIterator(localArrayDeque, paramInputStream), paramUri.toString());
              Util.closeQuietly(paramInputStream);
              return paramUri;
            }
            bool = str.startsWith("#EXT-X-TARGETDURATION");
            if (bool) {
              break;
            }
            bool = str.startsWith("#EXT-X-MEDIA-SEQUENCE");
            if (bool) {
              break;
            }
            bool = str.startsWith("#EXTINF");
            if (bool) {
              break;
            }
            bool = str.startsWith("#EXT-X-KEY");
            if (bool) {
              break;
            }
            bool = str.startsWith("#EXT-X-BYTERANGE");
            if (bool) {
              break;
            }
            bool = str.equals("#EXT-X-DISCONTINUITY");
            if (bool) {
              break;
            }
            bool = str.equals("#EXT-X-DISCONTINUITY-SEQUENCE");
            if (bool) {
              break;
            }
            bool = str.equals("#EXT-X-ENDLIST");
            if (bool) {
              break;
            }
            localArrayDeque.add(str);
          }
        }
        localArrayDeque.add(str);
        paramUri = parseMediaPlaylist(masterPlaylist, new LineIterator(localArrayDeque, paramInputStream), paramUri.toString());
        Util.closeQuietly(paramInputStream);
        return paramUri;
        label261:
        Util.closeQuietly(paramInputStream);
        throw new ParserException("Failed to parse the playlist, could not identify any tags.");
      }
      throw new UnrecognizedInputFormatException("Input does not start with the #EXTM3U header.", paramUri);
    }
    catch (Throwable paramUri)
    {
      Util.closeQuietly(paramInputStream);
      throw paramUri;
    }
  }
  
  class LineIterator
  {
    private String next;
    private final BufferedReader reader;
    
    public LineIterator(BufferedReader paramBufferedReader)
    {
      reader = paramBufferedReader;
    }
    
    public boolean hasNext()
      throws IOException
    {
      if (next != null) {
        return true;
      }
      if (!isEmpty())
      {
        next = ((String)poll());
        return true;
      }
      do
      {
        String str = reader.readLine();
        next = str;
        if (str == null) {
          break;
        }
        next = next.trim();
      } while (next.isEmpty());
      return true;
      return false;
    }
    
    public String next()
      throws IOException
    {
      if (hasNext())
      {
        String str = next;
        next = null;
        return str;
      }
      return null;
    }
  }
}
