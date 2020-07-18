package com.google.android.exoplayer2.source.smoothstreaming.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ClickListeners.PsshAtomUtil;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class SsManifestParser
  implements ParsingLoadable.Parser<SsManifest>
{
  private final XmlPullParserFactory xmlParserFactory;
  
  public SsManifestParser()
  {
    try
    {
      XmlPullParserFactory localXmlPullParserFactory = XmlPullParserFactory.newInstance();
      xmlParserFactory = localXmlPullParserFactory;
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", localXmlPullParserException);
    }
  }
  
  public SsManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    Object localObject = xmlParserFactory;
    try
    {
      localObject = ((XmlPullParserFactory)localObject).newPullParser();
      ((XmlPullParser)localObject).setInput(paramInputStream, null);
      paramUri = new SmoothStreamingMediaParser(null, paramUri.toString()).parse((XmlPullParser)localObject);
      return (SsManifest)paramUri;
    }
    catch (XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
  }
  
  private static abstract class ElementParser
  {
    private final String baseUri;
    private final List<Pair<String, Object>> normalizedAttributes;
    private final ElementParser parent;
    private final String tag;
    
    public ElementParser(ElementParser paramElementParser, String paramString1, String paramString2)
    {
      parent = paramElementParser;
      baseUri = paramString1;
      tag = paramString2;
      normalizedAttributes = new LinkedList();
    }
    
    private ElementParser newChildParser(ElementParser paramElementParser, String paramString1, String paramString2)
    {
      if ("QualityLevel".equals(paramString1)) {
        return new SsManifestParser.QualityLevelParser(paramElementParser, paramString2);
      }
      if ("Protection".equals(paramString1)) {
        return new SsManifestParser.ProtectionParser(paramElementParser, paramString2);
      }
      if ("StreamIndex".equals(paramString1)) {
        return new SsManifestParser.StreamIndexParser(paramElementParser, paramString2);
      }
      return null;
    }
    
    protected void addChild(Object paramObject) {}
    
    protected abstract Object build();
    
    protected final Object getNormalizedAttribute(String paramString)
    {
      int i = 0;
      while (i < normalizedAttributes.size())
      {
        Pair localPair = (Pair)normalizedAttributes.get(i);
        if (((String)first).equals(paramString)) {
          return second;
        }
        i += 1;
      }
      if (parent == null) {
        return null;
      }
      return parent.getNormalizedAttribute(paramString);
    }
    
    protected boolean handleChildInline(String paramString)
    {
      return false;
    }
    
    public final Object parse(XmlPullParser paramXmlPullParser)
      throws XmlPullParserException, IOException
    {
      int j = 0;
      int i;
      for (int m = 0;; m = i)
      {
        int k;
        Object localObject;
        switch (paramXmlPullParser.getEventType())
        {
        default: 
          k = j;
          i = m;
          break;
        case 4: 
          k = j;
          i = m;
          if (j != 0)
          {
            k = j;
            i = m;
            if (m == 0)
            {
              parseText(paramXmlPullParser);
              k = j;
              i = m;
            }
          }
          break;
        case 3: 
          k = j;
          i = m;
          if (j != 0) {
            if (m > 0)
            {
              i = m - 1;
              k = j;
            }
            else
            {
              localObject = paramXmlPullParser.getName();
              parseEndTag(paramXmlPullParser);
              k = j;
              i = m;
              if (!handleChildInline((String)localObject)) {
                return build();
              }
            }
          }
          break;
        case 2: 
          localObject = paramXmlPullParser.getName();
          if (tag.equals(localObject))
          {
            parseStartTag(paramXmlPullParser);
            k = 1;
            i = m;
          }
          else
          {
            k = j;
            i = m;
            if (j != 0) {
              if (m > 0)
              {
                i = m + 1;
                k = j;
              }
              else if (handleChildInline((String)localObject))
              {
                parseStartTag(paramXmlPullParser);
                k = j;
                i = m;
              }
              else
              {
                localObject = newChildParser(this, (String)localObject, baseUri);
                if (localObject == null)
                {
                  i = 1;
                  k = j;
                }
                else
                {
                  addChild(((ElementParser)localObject).parse(paramXmlPullParser));
                  k = j;
                  i = m;
                }
              }
            }
          }
          break;
        case 1: 
          return null;
        }
        paramXmlPullParser.next();
        j = k;
      }
    }
    
    protected final boolean parseBoolean(XmlPullParser paramXmlPullParser, String paramString, boolean paramBoolean)
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        paramBoolean = Boolean.parseBoolean(paramXmlPullParser);
      }
      return paramBoolean;
    }
    
    protected void parseEndTag(XmlPullParser paramXmlPullParser) {}
    
    protected final int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          paramInt = Integer.parseInt(paramXmlPullParser);
          return paramInt;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      return paramInt;
    }
    
    protected final long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          paramLong = Long.parseLong(paramXmlPullParser);
          return paramLong;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      return paramLong;
    }
    
    protected final int parseRequiredInt(XmlPullParser paramXmlPullParser, String paramString)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          int i = Integer.parseInt(paramXmlPullParser);
          return i;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected final long parseRequiredLong(XmlPullParser paramXmlPullParser, String paramString)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        try
        {
          long l = Long.parseLong(paramXmlPullParser);
          return l;
        }
        catch (NumberFormatException paramXmlPullParser)
        {
          throw new ParserException(paramXmlPullParser);
        }
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected final String parseRequiredString(XmlPullParser paramXmlPullParser, String paramString)
      throws SsManifestParser.MissingFieldException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
      if (paramXmlPullParser != null) {
        return paramXmlPullParser;
      }
      throw new SsManifestParser.MissingFieldException(paramString);
    }
    
    protected void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {}
    
    protected void parseText(XmlPullParser paramXmlPullParser) {}
    
    protected final void putNormalizedAttribute(String paramString, Object paramObject)
    {
      normalizedAttributes.add(Pair.create(paramString, paramObject));
    }
  }
  
  public static class MissingFieldException
    extends ParserException
  {
    public MissingFieldException(String paramString)
    {
      super();
    }
  }
  
  private static class ProtectionParser
    extends SsManifestParser.ElementParser
  {
    public static final String KEY_SYSTEM_ID = "SystemID";
    public static final String TAG = "Protection";
    public static final String TAG_PROTECTION_HEADER = "ProtectionHeader";
    private boolean inProtectionHeader;
    private byte[] initData;
    private UUID uuid;
    
    public ProtectionParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "Protection");
    }
    
    private static String stripCurlyBraces(String paramString)
    {
      String str = paramString;
      if (paramString.charAt(0) == '{')
      {
        str = paramString;
        if (paramString.charAt(paramString.length() - 1) == '}') {
          str = paramString.substring(1, paramString.length() - 1);
        }
      }
      return str;
    }
    
    public Object build()
    {
      return new SsManifest.ProtectionElement(uuid, PsshAtomUtil.buildPsshAtom(uuid, initData));
    }
    
    public boolean handleChildInline(String paramString)
    {
      return "ProtectionHeader".equals(paramString);
    }
    
    public void parseEndTag(XmlPullParser paramXmlPullParser)
    {
      if ("ProtectionHeader".equals(paramXmlPullParser.getName())) {
        inProtectionHeader = false;
      }
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
    {
      if ("ProtectionHeader".equals(paramXmlPullParser.getName()))
      {
        inProtectionHeader = true;
        uuid = UUID.fromString(stripCurlyBraces(paramXmlPullParser.getAttributeValue(null, "SystemID")));
      }
    }
    
    public void parseText(XmlPullParser paramXmlPullParser)
    {
      if (inProtectionHeader) {
        initData = Base64.decode(paramXmlPullParser.getText(), 0);
      }
    }
  }
  
  private static class QualityLevelParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_BITRATE = "Bitrate";
    private static final String KEY_CHANNELS = "Channels";
    private static final String KEY_CODEC_PRIVATE_DATA = "CodecPrivateData";
    private static final String KEY_FOUR_CC = "FourCC";
    private static final String KEY_INDEX = "Index";
    private static final String KEY_LANGUAGE = "Language";
    private static final String KEY_MAX_HEIGHT = "MaxHeight";
    private static final String KEY_MAX_WIDTH = "MaxWidth";
    private static final String KEY_NAME = "Name";
    private static final String KEY_SAMPLING_RATE = "SamplingRate";
    private static final String KEY_TYPE = "Type";
    public static final String TAG = "QualityLevel";
    private Format format;
    
    public QualityLevelParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "QualityLevel");
    }
    
    private static List buildCodecSpecificData(String paramString)
    {
      ArrayList localArrayList = new ArrayList();
      if (!TextUtils.isEmpty(paramString))
      {
        paramString = Util.getBytesFromHexString(paramString);
        byte[][] arrayOfByte = CodecSpecificDataUtil.splitNalUnits(paramString);
        if (arrayOfByte == null)
        {
          localArrayList.add(paramString);
          return localArrayList;
        }
        Collections.addAll(localArrayList, arrayOfByte);
      }
      return localArrayList;
    }
    
    private static String fourCCToMimeType(String paramString)
    {
      if ((!paramString.equalsIgnoreCase("H264")) && (!paramString.equalsIgnoreCase("X264")) && (!paramString.equalsIgnoreCase("AVC1")) && (!paramString.equalsIgnoreCase("DAVC")))
      {
        if ((!paramString.equalsIgnoreCase("AAC")) && (!paramString.equalsIgnoreCase("AACL")) && (!paramString.equalsIgnoreCase("AACH")) && (!paramString.equalsIgnoreCase("AACP")))
        {
          if ((!paramString.equalsIgnoreCase("TTML")) && (!paramString.equalsIgnoreCase("DFXP")))
          {
            if ((!paramString.equalsIgnoreCase("ac-3")) && (!paramString.equalsIgnoreCase("dac3")))
            {
              if ((!paramString.equalsIgnoreCase("ec-3")) && (!paramString.equalsIgnoreCase("dec3")))
              {
                if (paramString.equalsIgnoreCase("dtsc")) {
                  return "audio/vnd.dts";
                }
                if ((!paramString.equalsIgnoreCase("dtsh")) && (!paramString.equalsIgnoreCase("dtsl")))
                {
                  if (paramString.equalsIgnoreCase("dtse")) {
                    return "audio/vnd.dts.hd;profile=lbr";
                  }
                  if (paramString.equalsIgnoreCase("opus")) {
                    return "audio/opus";
                  }
                  return null;
                }
                return "audio/vnd.dts.hd";
              }
              return "audio/eac3";
            }
            return "audio/ac3";
          }
          return "application/ttml+xml";
        }
        return "audio/mp4a-latm";
      }
      return "video/avc";
    }
    
    public Object build()
    {
      return format;
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      int j = ((Integer)getNormalizedAttribute("Type")).intValue();
      String str1 = paramXmlPullParser.getAttributeValue(null, "Index");
      String str2 = (String)getNormalizedAttribute("Name");
      int i = parseRequiredInt(paramXmlPullParser, "Bitrate");
      Object localObject2 = fourCCToMimeType(parseRequiredString(paramXmlPullParser, "FourCC"));
      Object localObject1 = localObject2;
      if (j == 2)
      {
        format = Format.createVideoContainerFormat(str1, str2, "video/mp4", (String)localObject2, null, i, parseRequiredInt(paramXmlPullParser, "MaxWidth"), parseRequiredInt(paramXmlPullParser, "MaxHeight"), -1.0F, buildCodecSpecificData(paramXmlPullParser.getAttributeValue(null, "CodecPrivateData")), 0);
        return;
      }
      if (j == 1)
      {
        if (localObject2 == null) {
          localObject1 = "audio/mp4a-latm";
        }
        j = parseRequiredInt(paramXmlPullParser, "Channels");
        int k = parseRequiredInt(paramXmlPullParser, "SamplingRate");
        List localList = buildCodecSpecificData(paramXmlPullParser.getAttributeValue(null, "CodecPrivateData"));
        paramXmlPullParser = localList;
        localObject2 = paramXmlPullParser;
        if (localList.isEmpty())
        {
          localObject2 = paramXmlPullParser;
          if ("audio/mp4a-latm".equals(localObject1)) {
            localObject2 = Collections.singletonList(CodecSpecificDataUtil.buildAacLcAudioSpecificConfig(k, j));
          }
        }
        format = Format.createAudioContainerFormat(str1, str2, "audio/mp4", (String)localObject1, null, i, j, k, (List)localObject2, 0, (String)getNormalizedAttribute("Language"));
        return;
      }
      if (j == 3)
      {
        format = Format.createTextContainerFormat(str1, str2, "application/mp4", (String)localObject2, null, i, 0, (String)getNormalizedAttribute("Language"));
        return;
      }
      format = Format.createContainerFormat(str1, str2, "application/mp4", (String)localObject2, null, i, 0, null);
    }
  }
  
  private static class SmoothStreamingMediaParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_DURATION = "Duration";
    private static final String KEY_DVR_WINDOW_LENGTH = "DVRWindowLength";
    private static final String KEY_IS_LIVE = "IsLive";
    private static final String KEY_LOOKAHEAD_COUNT = "LookaheadCount";
    private static final String KEY_MAJOR_VERSION = "MajorVersion";
    private static final String KEY_MINOR_VERSION = "MinorVersion";
    private static final String KEY_TIME_SCALE = "TimeScale";
    public static final String TAG = "SmoothStreamingMedia";
    private long duration;
    private long dvrWindowLength;
    private boolean isLive;
    private int lookAheadCount = -1;
    private int majorVersion;
    private int minorVersion;
    private SsManifest.ProtectionElement protectionElement = null;
    private final List<SsManifest.StreamElement> streamElements = new LinkedList();
    private long timescale;
    
    public SmoothStreamingMediaParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "SmoothStreamingMedia");
    }
    
    public void addChild(Object paramObject)
    {
      if ((paramObject instanceof SsManifest.StreamElement))
      {
        streamElements.add((SsManifest.StreamElement)paramObject);
        return;
      }
      if ((paramObject instanceof SsManifest.ProtectionElement))
      {
        boolean bool;
        if (protectionElement == null) {
          bool = true;
        } else {
          bool = false;
        }
        Assertions.checkState(bool);
        protectionElement = ((SsManifest.ProtectionElement)paramObject);
      }
    }
    
    public Object build()
    {
      SsManifest.StreamElement[] arrayOfStreamElement = new SsManifest.StreamElement[streamElements.size()];
      streamElements.toArray(arrayOfStreamElement);
      if (protectionElement != null)
      {
        DrmInitData localDrmInitData = new DrmInitData(new DrmInitData.SchemeData[] { new DrmInitData.SchemeData(protectionElement.uuid, "video/mp4", protectionElement.data) });
        int k = arrayOfStreamElement.length;
        int i = 0;
        while (i < k)
        {
          Object localObject = arrayOfStreamElement[i];
          int j = type;
          if ((j == 2) || (j == 1))
          {
            localObject = formats;
            j = 0;
            while (j < localObject.length)
            {
              localObject[j] = localObject[j].copyWithDrmInitData(localDrmInitData);
              j += 1;
            }
          }
          i += 1;
        }
      }
      return new SsManifest(majorVersion, minorVersion, timescale, duration, dvrWindowLength, lookAheadCount, isLive, protectionElement, arrayOfStreamElement);
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      majorVersion = parseRequiredInt(paramXmlPullParser, "MajorVersion");
      minorVersion = parseRequiredInt(paramXmlPullParser, "MinorVersion");
      timescale = parseLong(paramXmlPullParser, "TimeScale", 10000000L);
      duration = parseRequiredLong(paramXmlPullParser, "Duration");
      dvrWindowLength = parseLong(paramXmlPullParser, "DVRWindowLength", 0L);
      lookAheadCount = parseInt(paramXmlPullParser, "LookaheadCount", -1);
      isLive = parseBoolean(paramXmlPullParser, "IsLive", false);
      putNormalizedAttribute("TimeScale", Long.valueOf(timescale));
    }
  }
  
  private static class StreamIndexParser
    extends SsManifestParser.ElementParser
  {
    private static final String KEY_DISPLAY_HEIGHT = "DisplayHeight";
    private static final String KEY_DISPLAY_WIDTH = "DisplayWidth";
    private static final String KEY_FRAGMENT_DURATION = "d";
    private static final String KEY_FRAGMENT_REPEAT_COUNT = "r";
    private static final String KEY_FRAGMENT_START_TIME = "t";
    private static final String KEY_LANGUAGE = "Language";
    private static final String KEY_MAX_HEIGHT = "MaxHeight";
    private static final String KEY_MAX_WIDTH = "MaxWidth";
    private static final String KEY_NAME = "Name";
    private static final String KEY_SUB_TYPE = "Subtype";
    private static final String KEY_TIME_SCALE = "TimeScale";
    private static final String KEY_TYPE = "Type";
    private static final String KEY_TYPE_AUDIO = "audio";
    private static final String KEY_TYPE_TEXT = "text";
    private static final String KEY_TYPE_VIDEO = "video";
    private static final String KEY_URL = "Url";
    public static final String TAG = "StreamIndex";
    private static final String TAG_STREAM_FRAGMENT = "c";
    private final String baseUri;
    private int displayHeight;
    private int displayWidth;
    private final List<Format> formats;
    private String language;
    private long lastChunkDuration;
    private int maxHeight;
    private int maxWidth;
    private String name;
    private ArrayList<Long> startTimes;
    private String subType;
    private long timescale;
    private int type;
    private String url;
    
    public StreamIndexParser(SsManifestParser.ElementParser paramElementParser, String paramString)
    {
      super(paramString, "StreamIndex");
      baseUri = paramString;
      formats = new LinkedList();
    }
    
    private void parseStreamElementStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      type = parseType(paramXmlPullParser);
      putNormalizedAttribute("Type", Integer.valueOf(type));
      if (type == 3) {
        subType = parseRequiredString(paramXmlPullParser, "Subtype");
      } else {
        subType = paramXmlPullParser.getAttributeValue(null, "Subtype");
      }
      name = paramXmlPullParser.getAttributeValue(null, "Name");
      url = parseRequiredString(paramXmlPullParser, "Url");
      maxWidth = parseInt(paramXmlPullParser, "MaxWidth", -1);
      maxHeight = parseInt(paramXmlPullParser, "MaxHeight", -1);
      displayWidth = parseInt(paramXmlPullParser, "DisplayWidth", -1);
      displayHeight = parseInt(paramXmlPullParser, "DisplayHeight", -1);
      language = paramXmlPullParser.getAttributeValue(null, "Language");
      putNormalizedAttribute("Language", language);
      timescale = parseInt(paramXmlPullParser, "TimeScale", -1);
      if (timescale == -1L) {
        timescale = ((Long)getNormalizedAttribute("TimeScale")).longValue();
      }
      startTimes = new ArrayList();
    }
    
    private void parseStreamFragmentStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      int i = startTimes.size();
      long l2 = parseLong(paramXmlPullParser, "t", -9223372036854775807L);
      long l1 = l2;
      int j = 1;
      if (l2 == -9223372036854775807L) {
        if (i == 0) {
          l1 = 0L;
        } else if (lastChunkDuration != -1L) {
          l1 = ((Long)startTimes.get(i - 1)).longValue() + lastChunkDuration;
        } else {
          throw new ParserException("Unable to infer start time");
        }
      }
      startTimes.add(Long.valueOf(l1));
      lastChunkDuration = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
      l2 = parseLong(paramXmlPullParser, "r", 1L);
      i = j;
      if (l2 > 1L) {
        if (lastChunkDuration != -9223372036854775807L) {
          i = j;
        } else {
          throw new ParserException("Repeated chunk with unspecified duration");
        }
      }
      for (;;)
      {
        long l3 = i;
        if (l3 >= l2) {
          break;
        }
        startTimes.add(Long.valueOf(lastChunkDuration * l3 + l1));
        i += 1;
      }
    }
    
    private int parseType(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "Type");
      if (paramXmlPullParser != null)
      {
        if ("audio".equalsIgnoreCase(paramXmlPullParser)) {
          return 1;
        }
        if ("video".equalsIgnoreCase(paramXmlPullParser)) {
          return 2;
        }
        if ("text".equalsIgnoreCase(paramXmlPullParser)) {
          return 3;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Invalid key value[");
        localStringBuilder.append(paramXmlPullParser);
        localStringBuilder.append("]");
        throw new ParserException(localStringBuilder.toString());
      }
      throw new SsManifestParser.MissingFieldException("Type");
    }
    
    public void addChild(Object paramObject)
    {
      if ((paramObject instanceof Format)) {
        formats.add((Format)paramObject);
      }
    }
    
    public Object build()
    {
      Format[] arrayOfFormat = new Format[formats.size()];
      formats.toArray(arrayOfFormat);
      return new SsManifest.StreamElement(baseUri, url, type, subType, timescale, name, maxWidth, maxHeight, displayWidth, displayHeight, language, arrayOfFormat, startTimes, lastChunkDuration);
    }
    
    public boolean handleChildInline(String paramString)
    {
      return "c".equals(paramString);
    }
    
    public void parseStartTag(XmlPullParser paramXmlPullParser)
      throws ParserException
    {
      if ("c".equals(paramXmlPullParser.getName()))
      {
        parseStreamFragmentStartTag(paramXmlPullParser);
        return;
      }
      parseStreamElementStartTag(paramXmlPullParser);
    }
  }
}
