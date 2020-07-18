package com.google.android.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.Xml;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.IpAddress;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ClickListeners.PsshAtomUtil;
import com.google.android.exoplayer2.metadata.emsg.EventMessage;
import com.google.android.exoplayer2.upgrade.DrmInitData;
import com.google.android.exoplayer2.upstream.ParsingLoadable.Parser;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.UriUtil;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

public class DashManifestParser
  extends DefaultHandler
  implements ParsingLoadable.Parser<DashManifest>
{
  private static final Pattern CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
  private static final Pattern CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
  private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
  private static final String PAGE_KEY = "MpdParser";
  private final String contentId;
  private final XmlPullParserFactory xmlParserFactory;
  
  public DashManifestParser()
  {
    this(null);
  }
  
  public DashManifestParser(String paramString)
  {
    contentId = paramString;
    try
    {
      paramString = XmlPullParserFactory.newInstance();
      xmlParserFactory = paramString;
      return;
    }
    catch (XmlPullParserException paramString)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", paramString);
    }
  }
  
  private static int checkContentTypeConsistency(int paramInt1, int paramInt2)
  {
    if (paramInt1 == -1) {
      return paramInt2;
    }
    if (paramInt2 == -1) {
      return paramInt1;
    }
    boolean bool;
    if (paramInt1 == paramInt2) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    return paramInt1;
  }
  
  private static String checkLanguageConsistency(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      return paramString2;
    }
    if (paramString2 == null) {
      return paramString1;
    }
    Assertions.checkState(paramString1.equals(paramString2));
    return paramString1;
  }
  
  private static void filterRedundantIncompleteSchemeDatas(ArrayList paramArrayList)
  {
    int i = paramArrayList.size() - 1;
    while (i >= 0)
    {
      com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData localSchemeData = (com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData)paramArrayList.get(i);
      if (!localSchemeData.hasData())
      {
        int j = 0;
        while (j < paramArrayList.size())
        {
          if (((com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData)paramArrayList.get(j)).canReplace(localSchemeData))
          {
            paramArrayList.remove(i);
            break;
          }
          j += 1;
        }
      }
      i -= 1;
    }
  }
  
  private static String getSampleMimeType(String paramString1, String paramString2)
  {
    if (MimeTypes.isAudio(paramString1)) {
      return MimeTypes.getAudioMediaMimeType(paramString2);
    }
    if (MimeTypes.isVideo(paramString1)) {
      return MimeTypes.getVideoMediaMimeType(paramString2);
    }
    if (mimeTypeIsRawText(paramString1)) {
      return paramString1;
    }
    if ("application/mp4".equals(paramString1))
    {
      if (paramString2 != null)
      {
        if (paramString2.startsWith("stpp")) {
          return "application/ttml+xml";
        }
        if (paramString2.startsWith("wvtt")) {
          return "application/x-mp4-vtt";
        }
      }
    }
    else if (("application/x-rawcc".equals(paramString1)) && (paramString2 != null))
    {
      if (paramString2.contains("cea708")) {
        return "application/cea-708";
      }
      if ((paramString2.contains("eia608")) || (paramString2.contains("cea608"))) {
        return "application/cea-608";
      }
    }
    return null;
  }
  
  public static void maybeSkipTag(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    if (!XmlPullParserUtil.isStartTag(paramXmlPullParser)) {
      return;
    }
    int i = 1;
    while (i != 0)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser)) {
        i += 1;
      } else if (XmlPullParserUtil.isEndTag(paramXmlPullParser)) {
        i -= 1;
      }
    }
  }
  
  private static boolean mimeTypeIsRawText(String paramString)
  {
    return (MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString)) || ("application/x-mp4-vtt".equals(paramString)) || ("application/cea-708".equals(paramString)) || ("application/cea-608".equals(paramString));
  }
  
  protected static String parseBaseUrl(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    paramXmlPullParser.next();
    return UriUtil.resolve(paramString, paramXmlPullParser.getText());
  }
  
  protected static int parseCea608AccessibilityChannel(List paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      Descriptor localDescriptor = (Descriptor)paramList.get(i);
      if (("urn:scte:dash:cc:cea-608:2015".equals(schemeIdUri)) && (value != null))
      {
        Object localObject = CEA_608_ACCESSIBILITY_PATTERN.matcher(value);
        if (((Matcher)localObject).matches()) {
          return Integer.parseInt(((Matcher)localObject).group(1));
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Unable to parse CEA-608 channel number from: ");
        ((StringBuilder)localObject).append(value);
        Log.w("MpdParser", ((StringBuilder)localObject).toString());
      }
      i += 1;
    }
    return -1;
  }
  
  protected static int parseCea708AccessibilityChannel(List paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      Descriptor localDescriptor = (Descriptor)paramList.get(i);
      if (("urn:scte:dash:cc:cea-708:2015".equals(schemeIdUri)) && (value != null))
      {
        Object localObject = CEA_708_ACCESSIBILITY_PATTERN.matcher(value);
        if (((Matcher)localObject).matches()) {
          return Integer.parseInt(((Matcher)localObject).group(1));
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Unable to parse CEA-708 service block number from: ");
        ((StringBuilder)localObject).append(value);
        Log.w("MpdParser", ((StringBuilder)localObject).toString());
      }
      i += 1;
    }
    return -1;
  }
  
  protected static long parseDateTime(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws ParserException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDateTime(paramXmlPullParser);
  }
  
  protected static Descriptor parseDescriptor(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", "");
    String str2 = parseString(paramXmlPullParser, "value", null);
    String str3 = parseString(paramXmlPullParser, "id", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, paramString));
    return new Descriptor(str1, str2, str3);
  }
  
  protected static int parseDolbyChannelConfiguration(XmlPullParser paramXmlPullParser)
  {
    paramXmlPullParser = Util.toLowerInvariant(paramXmlPullParser.getAttributeValue(null, "value"));
    if (paramXmlPullParser == null) {
      return -1;
    }
    int i = paramXmlPullParser.hashCode();
    if (i != 1596796)
    {
      if (i != 2937391)
      {
        if (i != 3094035)
        {
          if ((i == 3133436) && (paramXmlPullParser.equals("fa01")))
          {
            i = 3;
            break label118;
          }
        }
        else if (paramXmlPullParser.equals("f801"))
        {
          i = 2;
          break label118;
        }
      }
      else if (paramXmlPullParser.equals("a000"))
      {
        i = 1;
        break label118;
      }
    }
    else if (paramXmlPullParser.equals("4000"))
    {
      i = 0;
      break label118;
    }
    i = -1;
    switch (i)
    {
    default: 
      return -1;
    case 3: 
      return 8;
    case 2: 
      return 6;
    case 1: 
      label118:
      return 2;
    }
    return 1;
  }
  
  protected static long parseDuration(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Util.parseXsDuration(paramXmlPullParser);
  }
  
  protected static String parseEac3SupplementalProperties(List paramList)
  {
    int i = 0;
    while (i < paramList.size())
    {
      Descriptor localDescriptor = (Descriptor)paramList.get(i);
      if (("tag:dolby.com,2014:dash:DolbyDigitalPlusExtensionType:2014".equals(schemeIdUri)) && ("ec+3".equals(value))) {
        return "audio/eac3-joc";
      }
      i += 1;
    }
    return "audio/eac3";
  }
  
  protected static float parseFrameRate(XmlPullParser paramXmlPullParser, float paramFloat)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "frameRate");
    float f = paramFloat;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = FRAME_RATE_PATTERN.matcher(paramXmlPullParser);
      f = paramFloat;
      if (paramXmlPullParser.matches())
      {
        int i = Integer.parseInt(paramXmlPullParser.group(1));
        paramXmlPullParser = paramXmlPullParser.group(2);
        if (!TextUtils.isEmpty(paramXmlPullParser)) {
          return i / Integer.parseInt(paramXmlPullParser);
        }
        f = i;
      }
    }
    return f;
  }
  
  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramInt;
    }
    return Integer.parseInt(paramXmlPullParser);
  }
  
  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null) {
      return paramLong;
    }
    return Long.parseLong(paramXmlPullParser);
  }
  
  protected static String parseString(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (paramXmlPullParser == null) {
      return paramString2;
    }
    return paramXmlPullParser;
  }
  
  protected AdaptationSet buildAdaptationSet(int paramInt1, int paramInt2, List paramList1, List paramList2, List paramList3)
  {
    return new AdaptationSet(paramInt1, paramInt2, paramList1, paramList2, paramList3);
  }
  
  protected EventMessage buildEvent(String paramString1, String paramString2, long paramLong1, long paramLong2, byte[] paramArrayOfByte, long paramLong3)
  {
    return new EventMessage(paramString1, paramString2, paramLong2, paramLong1, paramArrayOfByte, paramLong3);
  }
  
  protected EventStream buildEventStream(String paramString1, String paramString2, long paramLong, long[] paramArrayOfLong, EventMessage[] paramArrayOfEventMessage)
  {
    return new EventStream(paramString1, paramString2, paramLong, paramArrayOfLong, paramArrayOfEventMessage);
  }
  
  protected Format buildFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, String paramString4, int paramInt6, List paramList1, String paramString5, List paramList2)
  {
    String str2 = getSampleMimeType(paramString3, paramString5);
    String str1 = str2;
    Object localObject = str2;
    if (str2 != null)
    {
      if ("audio/eac3".equals(str2)) {
        str1 = parseEac3SupplementalProperties(paramList2);
      }
      paramList2 = str1;
      if (MimeTypes.isVideo(str1)) {
        return Format.createVideoContainerFormat(paramString1, paramString2, paramString3, str1, paramString5, paramInt5, paramInt1, paramInt2, paramFloat, null, paramInt6);
      }
      if (MimeTypes.isAudio(str1)) {
        return Format.createAudioContainerFormat(paramString1, paramString2, paramString3, str1, paramString5, paramInt5, paramInt3, paramInt4, null, paramInt6, paramString4);
      }
      localObject = paramList2;
      if (mimeTypeIsRawText(str1))
      {
        if ("application/cea-608".equals(str1)) {
          paramInt1 = parseCea608AccessibilityChannel(paramList1);
        }
        for (;;)
        {
          break;
          if ("application/cea-708".equals(str1)) {
            paramInt1 = parseCea708AccessibilityChannel(paramList1);
          } else {
            paramInt1 = -1;
          }
        }
        return Format.createTextContainerFormat(paramString1, paramString2, paramString3, paramList2, paramString5, paramInt5, paramInt6, paramString4, paramInt1);
      }
    }
    return Format.createContainerFormat(paramString1, paramString2, paramString3, (String)localObject, paramString5, paramInt5, paramInt6, paramString4);
  }
  
  protected DashManifest buildMediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, long paramLong7, ProgramInformation paramProgramInformation, UtcTimingElement paramUtcTimingElement, Uri paramUri, List paramList)
  {
    return new DashManifest(paramLong1, paramLong2, paramLong3, paramBoolean, paramLong4, paramLong5, paramLong6, paramLong7, paramProgramInformation, paramUtcTimingElement, paramUri, paramList);
  }
  
  protected Period buildPeriod(String paramString, long paramLong, List paramList1, List paramList2)
  {
    return new Period(paramString, paramLong, paramList1, paramList2);
  }
  
  protected RangedUri buildRangedUri(String paramString, long paramLong1, long paramLong2)
  {
    return new RangedUri(paramString, paramLong1, paramLong2);
  }
  
  protected Representation buildRepresentation(RepresentationInfo paramRepresentationInfo, String paramString1, String paramString2, ArrayList paramArrayList1, ArrayList paramArrayList2)
  {
    Format localFormat = format;
    if (drmSchemeType != null) {
      paramString2 = drmSchemeType;
    }
    ArrayList localArrayList = drmSchemeDatas;
    localArrayList.addAll(paramArrayList1);
    paramArrayList1 = localFormat;
    if (!localArrayList.isEmpty())
    {
      filterRedundantIncompleteSchemeDatas(localArrayList);
      paramArrayList1 = localFormat.copyWithDrmInitData(new DrmInitData(paramString2, localArrayList));
    }
    paramString2 = inbandEventStreams;
    paramString2.addAll(paramArrayList2);
    return Representation.newInstance(paramString1, revisionId, paramArrayList1, baseUrl, segmentBase, paramString2);
  }
  
  protected SegmentBase.SegmentList buildSegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List paramList1, List paramList2)
  {
    return new SegmentBase.SegmentList(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4, paramList1, paramList2);
  }
  
  protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4, List paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
  {
    return new SegmentBase.SegmentTemplate(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4, paramList, paramUrlTemplate1, paramUrlTemplate2);
  }
  
  protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long paramLong1, long paramLong2)
  {
    return new SegmentBase.SegmentTimelineElement(paramLong1, paramLong2);
  }
  
  protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    return new SegmentBase.SingleSegmentBase(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4);
  }
  
  protected UtcTimingElement buildUtcTimingElement(String paramString1, String paramString2)
  {
    return new UtcTimingElement(paramString1, paramString2);
  }
  
  protected int getContentType(Format paramFormat)
  {
    paramFormat = sampleMimeType;
    if (TextUtils.isEmpty(paramFormat)) {
      return -1;
    }
    if (MimeTypes.isVideo(paramFormat)) {
      return 2;
    }
    if (MimeTypes.isAudio(paramFormat)) {
      return 1;
    }
    if (mimeTypeIsRawText(paramFormat)) {
      return 3;
    }
    return -1;
  }
  
  public DashManifest parse(Uri paramUri, InputStream paramInputStream)
    throws IOException
  {
    Object localObject = xmlParserFactory;
    try
    {
      localObject = ((XmlPullParserFactory)localObject).newPullParser();
      ((XmlPullParser)localObject).setInput(paramInputStream, null);
      int i = ((XmlPullParser)localObject).next();
      if (i == 2)
      {
        boolean bool = "MPD".equals(((XmlPullParser)localObject).getName());
        if (bool)
        {
          paramUri = parseMediaPresentationDescription((XmlPullParser)localObject, paramUri.toString());
          return paramUri;
        }
      }
      paramUri = new ParserException("inputStream does not contain a valid media presentation description");
      throw paramUri;
    }
    catch (XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
  }
  
  protected AdaptationSet parseAdaptationSet(XmlPullParser paramXmlPullParser, String paramString, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    Object localObject3 = paramXmlPullParser;
    int i4 = parseInt(paramXmlPullParser, "id", -1);
    int i1 = parseContentType(paramXmlPullParser);
    String str2 = paramXmlPullParser.getAttributeValue(null, "mimeType");
    String str3 = paramXmlPullParser.getAttributeValue(null, "codecs");
    int i5 = parseInt(paramXmlPullParser, "width", -1);
    int i6 = parseInt(paramXmlPullParser, "height", -1);
    float f = parseFrameRate(paramXmlPullParser, -1.0F);
    int i7 = parseInt(paramXmlPullParser, "audioSamplingRate", -1);
    Object localObject2 = paramXmlPullParser.getAttributeValue(null, "lang");
    String str4 = paramXmlPullParser.getAttributeValue(null, "label");
    ArrayList localArrayList4 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList5 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Object localObject1 = null;
    int k = 0;
    int j = 0;
    int i = -1;
    Object localObject4 = paramSegmentBase;
    paramSegmentBase = (SegmentBase)localObject2;
    for (;;)
    {
      paramXmlPullParser.next();
      int n;
      int m;
      int i2;
      int i3;
      Object localObject5;
      if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "BaseURL")) {
        if (k == 0)
        {
          paramString = parseBaseUrl((XmlPullParser)localObject3, paramString);
          k = 1;
          n = j;
          m = i1;
          i2 = k;
          i3 = i;
          localObject5 = localObject1;
          localObject2 = localObject4;
          break label883;
        }
      }
      for (;;)
      {
        n = j;
        m = i1;
        i2 = k;
        i3 = i;
        localObject5 = localObject1;
        localObject2 = localObject4;
        break label883;
        if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "ContentProtection"))
        {
          localObject5 = parseContentProtection(paramXmlPullParser);
          localObject2 = localObject1;
          if (first != null) {
            localObject2 = (String)first;
          }
          n = j;
          m = i;
          localObject1 = localObject2;
          if (second != null)
          {
            localArrayList4.add(second);
            localObject1 = localObject2;
            m = i;
            n = j;
          }
        }
        for (;;)
        {
          j = n;
          i = m;
          break;
          if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "ContentComponent"))
          {
            paramSegmentBase = checkLanguageConsistency(paramSegmentBase, ((XmlPullParser)localObject3).getAttributeValue(null, "lang"));
            m = parseContentType(paramXmlPullParser);
            m = checkContentTypeConsistency(i1, m);
            n = j;
            i2 = k;
            i3 = i;
            localObject5 = localObject1;
            localObject2 = localObject4;
            break label883;
          }
          if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "Role"))
          {
            n = j | parseRole(paramXmlPullParser);
            m = i;
          }
          else
          {
            if (!XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "AudioChannelConfiguration")) {
              break label470;
            }
            m = parseAudioChannelConfiguration(paramXmlPullParser);
            n = j;
          }
        }
        label470:
        if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "Accessibility"))
        {
          localArrayList3.add(parseDescriptor((XmlPullParser)localObject3, "Accessibility"));
        }
        else
        {
          if (!XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "SupplementalProperty")) {
            break label526;
          }
          localArrayList5.add(parseDescriptor((XmlPullParser)localObject3, "SupplementalProperty"));
        }
      }
      label526:
      if (XmlPullParserUtil.isStartTag((XmlPullParser)localObject3, "Representation"))
      {
        localObject2 = paramSegmentBase;
        localObject5 = paramString;
        paramString = parseRepresentation(paramXmlPullParser, paramString, str4, str2, str3, i5, i6, f, i, i7, paramSegmentBase, j, localArrayList3, (SegmentBase)localObject4);
        m = checkContentTypeConsistency(i1, getContentType(format));
        localArrayList2.add(paramString);
        localObject3 = paramXmlPullParser;
        n = j;
        i2 = k;
        i3 = i;
        paramString = (String)localObject5;
        paramSegmentBase = (SegmentBase)localObject2;
        localObject5 = localObject1;
        localObject2 = localObject4;
      }
      else
      {
        SegmentBase localSegmentBase = paramSegmentBase;
        String str1 = paramString;
        XmlPullParser localXmlPullParser = paramXmlPullParser;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase")) {
          localObject2 = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)localObject4);
        }
        for (;;)
        {
          localObject3 = localXmlPullParser;
          n = j;
          m = i1;
          i2 = k;
          i3 = i;
          paramString = str1;
          paramSegmentBase = localSegmentBase;
          localObject5 = localObject1;
          break label883;
          if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
          {
            localObject2 = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)localObject4);
          }
          else
          {
            if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate")) {
              break;
            }
            localObject2 = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)localObject4);
          }
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream"))
        {
          localArrayList1.add(parseDescriptor(paramXmlPullParser, "InbandEventStream"));
          localObject3 = localXmlPullParser;
          n = j;
          m = i1;
          i2 = k;
          i3 = i;
          paramString = str1;
          paramSegmentBase = localSegmentBase;
          localObject5 = localObject1;
          localObject2 = localObject4;
        }
        else
        {
          localObject3 = localXmlPullParser;
          n = j;
          m = i1;
          i2 = k;
          i3 = i;
          paramString = str1;
          paramSegmentBase = localSegmentBase;
          localObject5 = localObject1;
          localObject2 = localObject4;
          if (XmlPullParserUtil.isStartTag(paramXmlPullParser))
          {
            parseAdaptationSetChild(paramXmlPullParser);
            localObject2 = localObject4;
            localObject5 = localObject1;
            paramSegmentBase = localSegmentBase;
            paramString = str1;
            i3 = i;
            i2 = k;
            m = i1;
            n = j;
            localObject3 = localXmlPullParser;
          }
        }
      }
      label883:
      if (XmlPullParserUtil.isEndTag((XmlPullParser)localObject3, "AdaptationSet"))
      {
        paramXmlPullParser = new ArrayList(localArrayList2.size());
        i = 0;
        while (i < localArrayList2.size())
        {
          paramXmlPullParser.add(buildRepresentation((RepresentationInfo)localArrayList2.get(i), contentId, (String)localObject5, localArrayList4, localArrayList1));
          i += 1;
        }
        return buildAdaptationSet(i4, m, paramXmlPullParser, localArrayList3, localArrayList5);
      }
      j = n;
      i1 = m;
      k = i2;
      i = i3;
      localObject1 = localObject5;
      localObject4 = localObject2;
    }
  }
  
  protected void parseAdaptationSetChild(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    maybeSkipTag(paramXmlPullParser);
  }
  
  protected int parseAudioChannelConfiguration(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str = parseString(paramXmlPullParser, "schemeIdUri", null);
    boolean bool = "urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(str);
    int i = -1;
    if (bool) {
      i = parseInt(paramXmlPullParser, "value", -1);
    } else if ("tag:dolby.com,2014:dash:audio_channel_configuration:2011".equals(str)) {
      i = parseDolbyChannelConfiguration(paramXmlPullParser);
    }
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "AudioChannelConfiguration"));
    return i;
  }
  
  protected Pair parseContentProtection(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    Object localObject1 = paramXmlPullParser.getAttributeValue(null, "schemeIdUri");
    label104:
    Object localObject2;
    if (localObject1 != null)
    {
      localObject1 = Util.toLowerInvariant((String)localObject1);
      int i = ((String)localObject1).hashCode();
      if (i != 489446379)
      {
        if (i != 755418770)
        {
          if ((i == 1812765994) && (((String)localObject1).equals("urn:mpeg:dash:mp4protection:2011")))
          {
            i = 0;
            break label104;
          }
        }
        else if (((String)localObject1).equals("urn:uuid:edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"))
        {
          i = 2;
          break label104;
        }
      }
      else if (((String)localObject1).equals("urn:uuid:9a04f079-9840-4286-ab92-e65be0885f95"))
      {
        i = 1;
        break label104;
      }
      i = -1;
      switch (i)
      {
      default: 
        break;
      case 2: 
        localObject2 = IpAddress.WIDEVINE_UUID;
        break;
      case 1: 
        localObject2 = IpAddress.PLAYREADY_UUID;
        break;
      }
      localObject3 = paramXmlPullParser.getAttributeValue(null, "value");
      localObject1 = XmlPullParserUtil.getAttributeValueIgnorePrefix(paramXmlPullParser, "default_KID");
      if ((!TextUtils.isEmpty((CharSequence)localObject1)) && (!"00000000-0000-0000-0000-000000000000".equals(localObject1)))
      {
        localObject1 = ((String)localObject1).split("\\s+");
        localObject2 = new UUID[localObject1.length];
        i = 0;
        while (i < localObject1.length)
        {
          localObject2[i] = UUID.fromString(localObject1[i]);
          i += 1;
        }
        arrayOfByte = PsshAtomUtil.buildPsshAtom(IpAddress.COMMON_PSSH_UUID, (UUID[])localObject2, null);
        localObject1 = null;
        bool = false;
        localObject2 = IpAddress.COMMON_PSSH_UUID;
        break label303;
      }
      localObject2 = null;
      localObject1 = localObject3;
    }
    else
    {
      localObject2 = null;
      localObject1 = null;
    }
    byte[] arrayOfByte = null;
    String str = null;
    boolean bool = false;
    Object localObject3 = localObject1;
    localObject1 = str;
    for (;;)
    {
      label303:
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ms:laurl")) {
        localObject1 = paramXmlPullParser.getAttributeValue(null, "licenseUrl");
      }
      for (;;)
      {
        break;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "widevine:license"))
        {
          str = paramXmlPullParser.getAttributeValue(null, "robustness_level");
          if ((str != null) && (str.startsWith("HW"))) {
            bool = true;
          } else {
            bool = false;
          }
        }
        else if ((arrayOfByte == null) && (XmlPullParserUtil.isStartTagIgnorePrefix(paramXmlPullParser, "pssh")) && (paramXmlPullParser.next() == 4))
        {
          arrayOfByte = Base64.decode(paramXmlPullParser.getText(), 0);
          localObject2 = PsshAtomUtil.parseUuid(arrayOfByte);
          if (localObject2 == null)
          {
            Log.w("MpdParser", "Skipping malformed cenc:pssh data");
            arrayOfByte = null;
          }
        }
        else if ((arrayOfByte == null) && (IpAddress.PLAYREADY_UUID.equals(localObject2)) && (XmlPullParserUtil.isStartTag(paramXmlPullParser, "mspr:pro")) && (paramXmlPullParser.next() == 4))
        {
          arrayOfByte = PsshAtomUtil.buildPsshAtom(IpAddress.PLAYREADY_UUID, Base64.decode(paramXmlPullParser.getText(), 0));
        }
        else
        {
          maybeSkipTag(paramXmlPullParser);
        }
      }
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "ContentProtection"))
      {
        if (localObject2 != null) {
          paramXmlPullParser = new com.google.android.exoplayer2.upgrade.DrmInitData.SchemeData((UUID)localObject2, (String)localObject1, "video/mp4", arrayOfByte, bool);
        } else {
          paramXmlPullParser = null;
        }
        return Pair.create(localObject3, paramXmlPullParser);
      }
    }
  }
  
  protected int parseContentType(XmlPullParser paramXmlPullParser)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "contentType");
    if (TextUtils.isEmpty(paramXmlPullParser)) {
      return -1;
    }
    if ("audio".equals(paramXmlPullParser)) {
      return 1;
    }
    if ("video".equals(paramXmlPullParser)) {
      return 2;
    }
    if ("text".equals(paramXmlPullParser)) {
      return 3;
    }
    return -1;
  }
  
  protected EventMessage parseEvent(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, long paramLong, ByteArrayOutputStream paramByteArrayOutputStream)
    throws IOException, XmlPullParserException
  {
    long l1 = parseLong(paramXmlPullParser, "id", 0L);
    long l3 = parseLong(paramXmlPullParser, "duration", -9223372036854775807L);
    long l2 = parseLong(paramXmlPullParser, "presentationTime", 0L);
    l3 = Util.scaleLargeTimestamp(l3, 1000L, paramLong);
    paramLong = Util.scaleLargeTimestamp(l2, 1000000L, paramLong);
    String str = parseString(paramXmlPullParser, "messageData", null);
    paramXmlPullParser = parseEventObject(paramXmlPullParser, paramByteArrayOutputStream);
    if (str != null) {
      for (;;)
      {
        paramXmlPullParser = Util.getUtf8Bytes(str);
      }
    }
    return buildEvent(paramString1, paramString2, l1, l3, paramXmlPullParser, paramLong);
  }
  
  protected byte[] parseEventObject(XmlPullParser paramXmlPullParser, ByteArrayOutputStream paramByteArrayOutputStream)
    throws XmlPullParserException, IOException
  {
    paramByteArrayOutputStream.reset();
    XmlSerializer localXmlSerializer = Xml.newSerializer();
    localXmlSerializer.setOutput(paramByteArrayOutputStream, "UTF-8");
    paramXmlPullParser.nextToken();
    while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Event"))
    {
      int j = paramXmlPullParser.getEventType();
      int i = 0;
      switch (j)
      {
      default: 
        break;
      case 10: 
        localXmlSerializer.docdecl(paramXmlPullParser.getText());
        break;
      case 9: 
        localXmlSerializer.comment(paramXmlPullParser.getText());
        break;
      case 8: 
        localXmlSerializer.processingInstruction(paramXmlPullParser.getText());
        break;
      case 7: 
        localXmlSerializer.ignorableWhitespace(paramXmlPullParser.getText());
        break;
      case 6: 
        localXmlSerializer.entityRef(paramXmlPullParser.getText());
        break;
      case 5: 
        localXmlSerializer.cdsect(paramXmlPullParser.getText());
        break;
      case 4: 
        localXmlSerializer.text(paramXmlPullParser.getText());
        break;
      case 3: 
        localXmlSerializer.endTag(paramXmlPullParser.getNamespace(), paramXmlPullParser.getName());
        break;
      case 2: 
        localXmlSerializer.startTag(paramXmlPullParser.getNamespace(), paramXmlPullParser.getName());
      case 1: 
      case 0: 
        while (i < paramXmlPullParser.getAttributeCount())
        {
          localXmlSerializer.attribute(paramXmlPullParser.getAttributeNamespace(i), paramXmlPullParser.getAttributeName(i), paramXmlPullParser.getAttributeValue(i));
          i += 1;
          continue;
          localXmlSerializer.endDocument();
          break;
          localXmlSerializer.startDocument(null, Boolean.valueOf(false));
        }
      }
      paramXmlPullParser.nextToken();
    }
    localXmlSerializer.flush();
    return paramByteArrayOutputStream.toByteArray();
  }
  
  protected EventStream parseEventStream(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", "");
    String str2 = parseString(paramXmlPullParser, "value", "");
    long l = parseLong(paramXmlPullParser, "timescale", 1L);
    ArrayList localArrayList = new ArrayList();
    Object localObject = new ByteArrayOutputStream(512);
    do
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Event")) {
        localArrayList.add(parseEvent(paramXmlPullParser, str1, str2, l, (ByteArrayOutputStream)localObject));
      } else {
        maybeSkipTag(paramXmlPullParser);
      }
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "EventStream"));
    paramXmlPullParser = new long[localArrayList.size()];
    localObject = new EventMessage[localArrayList.size()];
    int i = 0;
    while (i < localArrayList.size())
    {
      EventMessage localEventMessage = (EventMessage)localArrayList.get(i);
      paramXmlPullParser[i] = presentationTimeUs;
      localObject[i] = localEventMessage;
      i += 1;
    }
    return buildEventStream(str1, str2, l, paramXmlPullParser, (EventMessage[])localObject);
  }
  
  protected RangedUri parseInitialization(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "sourceURL", "range");
  }
  
  protected DashManifest parseMediaPresentationDescription(XmlPullParser paramXmlPullParser, String paramString)
    throws XmlPullParserException, IOException
  {
    long l8 = parseDateTime(paramXmlPullParser, "availabilityStartTime", -9223372036854775807L);
    long l7 = parseDuration(paramXmlPullParser, "mediaPresentationDuration", -9223372036854775807L);
    long l2 = l7;
    long l9 = parseDuration(paramXmlPullParser, "minBufferTime", -9223372036854775807L);
    Object localObject = paramXmlPullParser.getAttributeValue(null, "type");
    int j = 0;
    boolean bool;
    if ((localObject != null) && ("dynamic".equals(localObject))) {
      bool = true;
    } else {
      bool = false;
    }
    long l3;
    if (bool) {
      l3 = parseDuration(paramXmlPullParser, "minimumUpdatePeriod", -9223372036854775807L);
    } else {
      l3 = -9223372036854775807L;
    }
    long l4;
    if (bool) {
      l4 = parseDuration(paramXmlPullParser, "timeShiftBufferDepth", -9223372036854775807L);
    } else {
      l4 = -9223372036854775807L;
    }
    long l5;
    if (bool) {
      l5 = parseDuration(paramXmlPullParser, "suggestedPresentationDelay", -9223372036854775807L);
    } else {
      l5 = -9223372036854775807L;
    }
    long l10 = parseDateTime(paramXmlPullParser, "publishTime", -9223372036854775807L);
    ArrayList localArrayList = new ArrayList();
    long l1;
    if (bool) {
      l1 = -9223372036854775807L;
    } else {
      l1 = 0L;
    }
    int i = 0;
    UtcTimingElement localUtcTimingElement = null;
    Uri localUri = null;
    localObject = null;
    for (;;)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        if (j == 0)
        {
          paramString = parseBaseUrl(paramXmlPullParser, paramString);
          j = 1;
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ProgramInformation"))
      {
        localObject = parseProgramInformation(paramXmlPullParser);
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "UTCTiming"))
      {
        localUtcTimingElement = parseUtcTiming(paramXmlPullParser);
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Location"))
      {
        localUri = Uri.parse(paramXmlPullParser.nextText());
      }
      else if ((XmlPullParserUtil.isStartTag(paramXmlPullParser, "Period")) && (i == 0))
      {
        Pair localPair = parsePeriod(paramXmlPullParser, paramString, l1);
        Period localPeriod = (Period)first;
        if (startMs == -9223372036854775807L)
        {
          if (bool)
          {
            i = 1;
          }
          else
          {
            paramXmlPullParser = new StringBuilder();
            paramXmlPullParser.append("Unable to determine start of period ");
            paramXmlPullParser.append(localArrayList.size());
            throw new ParserException(paramXmlPullParser.toString());
          }
        }
        else
        {
          l1 = ((Long)second).longValue();
          if (l1 == -9223372036854775807L) {
            l1 = -9223372036854775807L;
          } else {
            l1 += startMs;
          }
          localArrayList.add(localPeriod);
        }
      }
      else
      {
        maybeSkipTag(paramXmlPullParser);
      }
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "MPD"))
      {
        long l6 = l2;
        if (l7 == -9223372036854775807L) {
          if (l1 != -9223372036854775807L) {
            l6 = l1;
          } else if (bool) {
            l6 = l2;
          } else {
            throw new ParserException("Unable to determine duration of static manifest.");
          }
        }
        if (!localArrayList.isEmpty()) {
          return buildMediaPresentationDescription(l8, l6, l9, bool, l3, l4, l5, l10, (ProgramInformation)localObject, localUtcTimingElement, localUri, localArrayList);
        }
        throw new ParserException("No periods found.");
      }
    }
  }
  
  protected Pair parsePeriod(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
    throws XmlPullParserException, IOException
  {
    String str = paramXmlPullParser.getAttributeValue(null, "id");
    paramLong = parseDuration(paramXmlPullParser, "start", paramLong);
    long l = parseDuration(paramXmlPullParser, "duration", -9223372036854775807L);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    Object localObject2 = null;
    Object localObject1 = paramString;
    do
    {
      paramXmlPullParser.next();
      int j;
      Object localObject3;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        j = i;
        paramString = (String)localObject2;
        localObject3 = localObject1;
        if (i == 0)
        {
          localObject3 = parseBaseUrl(paramXmlPullParser, (String)localObject1);
          j = 1;
          paramString = (String)localObject2;
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AdaptationSet"))
      {
        localArrayList1.add(parseAdaptationSet(paramXmlPullParser, (String)localObject1, (SegmentBase)localObject2));
        j = i;
        paramString = (String)localObject2;
        localObject3 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "EventStream"))
      {
        localArrayList2.add(parseEventStream(paramXmlPullParser));
        j = i;
        paramString = (String)localObject2;
        localObject3 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, null);
        j = i;
        localObject3 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, null);
        j = i;
        localObject3 = localObject1;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
      {
        paramString = parseSegmentTemplate(paramXmlPullParser, null);
        j = i;
        localObject3 = localObject1;
      }
      else
      {
        maybeSkipTag(paramXmlPullParser);
        localObject3 = localObject1;
        paramString = (String)localObject2;
        j = i;
      }
      i = j;
      localObject2 = paramString;
      localObject1 = localObject3;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Period"));
    return Pair.create(buildPeriod(str, paramLong, localArrayList1, localArrayList2), Long.valueOf(l));
  }
  
  protected ProgramInformation parseProgramInformation(XmlPullParser paramXmlPullParser)
    throws IOException, XmlPullParserException
  {
    String str3 = null;
    String str4 = parseString(paramXmlPullParser, "moreInformationURL", null);
    String str5 = parseString(paramXmlPullParser, "lang", null);
    String str2 = null;
    String str1 = null;
    for (;;)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Title")) {
        str3 = paramXmlPullParser.nextText();
      }
      for (;;)
      {
        break;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Source")) {
          str2 = paramXmlPullParser.nextText();
        } else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Copyright")) {
          str1 = paramXmlPullParser.nextText();
        } else {
          maybeSkipTag(paramXmlPullParser);
        }
      }
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "ProgramInformation")) {
        return new ProgramInformation(str3, str2, str1, str4, str5);
      }
    }
  }
  
  protected RangedUri parseRangedUrl(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramString1 = paramXmlPullParser.getAttributeValue(null, paramString1);
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString2);
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = paramXmlPullParser.split("-");
      l3 = Long.parseLong(paramXmlPullParser[0]);
      l1 = l3;
      l2 = l1;
      if (paramXmlPullParser.length == 2)
      {
        l2 = Long.parseLong(paramXmlPullParser[1]) - l3 + 1L;
        break label84;
      }
    }
    else
    {
      l2 = 0L;
    }
    long l3 = -1L;
    long l1 = l2;
    long l2 = l3;
    label84:
    return buildRangedUri(paramString1, l1, l2);
  }
  
  protected RepresentationInfo parseRepresentation(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, String paramString5, int paramInt5, List paramList, SegmentBase paramSegmentBase)
    throws XmlPullParserException, IOException
  {
    String str4 = paramXmlPullParser.getAttributeValue(null, "id");
    int i = parseInt(paramXmlPullParser, "bandwidth", -1);
    String str5 = parseString(paramXmlPullParser, "mimeType", paramString3);
    String str2 = parseString(paramXmlPullParser, "codecs", paramString4);
    int j = parseInt(paramXmlPullParser, "width", paramInt1);
    int k = parseInt(paramXmlPullParser, "height", paramInt2);
    paramFloat = parseFrameRate(paramXmlPullParser, paramFloat);
    int m = parseInt(paramXmlPullParser, "audioSamplingRate", paramInt4);
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    paramInt1 = 0;
    paramString4 = null;
    paramString3 = paramString1;
    for (;;)
    {
      paramXmlPullParser.next();
      String str1;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        paramInt2 = paramInt1;
        str1 = paramString3;
        paramString1 = paramSegmentBase;
        if (paramInt1 == 0)
        {
          str1 = parseBaseUrl(paramXmlPullParser, paramString3);
          paramInt2 = 1;
          paramString1 = paramSegmentBase;
        }
      }
      String str3;
      for (;;)
      {
        str3 = paramString4;
        paramInt4 = paramInt3;
        break label518;
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
        {
          paramInt4 = parseAudioChannelConfiguration(paramXmlPullParser);
          paramInt2 = paramInt1;
          str1 = paramString3;
          str3 = paramString4;
          paramString1 = paramSegmentBase;
          break label518;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
        {
          paramString1 = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)paramSegmentBase);
          paramInt2 = paramInt1;
          str1 = paramString3;
        }
        else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
        {
          paramString1 = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)paramSegmentBase);
          paramInt2 = paramInt1;
          str1 = paramString3;
        }
        else
        {
          if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate")) {
            break;
          }
          paramString1 = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)paramSegmentBase);
          paramInt2 = paramInt1;
          str1 = paramString3;
        }
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
      {
        Pair localPair = parseContentProtection(paramXmlPullParser);
        if (first != null) {
          paramString4 = (String)first;
        }
        paramInt2 = paramInt1;
        str1 = paramString3;
        str3 = paramString4;
        paramInt4 = paramInt3;
        paramString1 = paramSegmentBase;
        if (second != null)
        {
          localArrayList1.add(second);
          paramInt2 = paramInt1;
          str1 = paramString3;
          str3 = paramString4;
          paramInt4 = paramInt3;
          paramString1 = paramSegmentBase;
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "InbandEventStream"))
      {
        localArrayList2.add(parseDescriptor(paramXmlPullParser, "InbandEventStream"));
        paramInt2 = paramInt1;
        str1 = paramString3;
        str3 = paramString4;
        paramInt4 = paramInt3;
        paramString1 = paramSegmentBase;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SupplementalProperty"))
      {
        localArrayList3.add(parseDescriptor(paramXmlPullParser, "SupplementalProperty"));
        paramInt2 = paramInt1;
        str1 = paramString3;
        str3 = paramString4;
        paramInt4 = paramInt3;
        paramString1 = paramSegmentBase;
      }
      else
      {
        maybeSkipTag(paramXmlPullParser);
        paramString1 = paramSegmentBase;
        paramInt4 = paramInt3;
        str3 = paramString4;
        str1 = paramString3;
        paramInt2 = paramInt1;
      }
      label518:
      paramString3 = paramString1;
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "Representation"))
      {
        paramXmlPullParser = buildFormat(str4, paramString2, str5, j, k, paramFloat, paramInt4, m, i, paramString5, paramInt5, paramList, str2, localArrayList3);
        if (paramString1 == null) {
          paramString3 = new SegmentBase.SingleSegmentBase();
        }
        return new RepresentationInfo(paramXmlPullParser, str1, paramString3, str3, localArrayList1, localArrayList2, -1L);
      }
      paramInt1 = paramInt2;
      paramString3 = str1;
      paramString4 = str3;
      paramInt3 = paramInt4;
      paramSegmentBase = paramString1;
    }
  }
  
  protected int parseRole(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", null);
    String str2 = parseString(paramXmlPullParser, "value", null);
    do
    {
      paramXmlPullParser.next();
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Role"));
    if (("urn:mpeg:dash:role:2011".equals(str1)) && ("main".equals(str2))) {
      return 1;
    }
    return 0;
  }
  
  protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser paramXmlPullParser, SegmentBase.SingleSegmentBase paramSingleSegmentBase)
    throws XmlPullParserException, IOException
  {
    long l1;
    if (paramSingleSegmentBase != null) {
      l1 = timescale;
    } else {
      l1 = 1L;
    }
    long l3 = parseLong(paramXmlPullParser, "timescale", l1);
    long l2 = 0L;
    if (paramSingleSegmentBase != null) {
      l1 = presentationTimeOffset;
    } else {
      l1 = 0L;
    }
    long l4 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
    if (paramSingleSegmentBase != null) {
      l1 = indexStart;
    } else {
      l1 = 0L;
    }
    if (paramSingleSegmentBase != null) {
      l2 = indexLength;
    }
    Object localObject1 = null;
    Object localObject2 = paramXmlPullParser.getAttributeValue(null, "indexRange");
    if (localObject2 != null)
    {
      localObject2 = ((String)localObject2).split("-");
      l2 = Long.parseLong(localObject2[0]);
      l1 = l2;
      l2 = Long.parseLong(localObject2[1]) - l2 + 1L;
    }
    if (paramSingleSegmentBase != null) {
      localObject1 = initialization;
    }
    do
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization"))
      {
        paramSingleSegmentBase = parseInitialization(paramXmlPullParser);
      }
      else
      {
        maybeSkipTag(paramXmlPullParser);
        paramSingleSegmentBase = (SegmentBase.SingleSegmentBase)localObject1;
      }
      localObject1 = paramSingleSegmentBase;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentBase"));
    return buildSingleSegmentBase(paramSingleSegmentBase, l3, l4, l1, l2);
  }
  
  protected SegmentBase.SegmentList parseSegmentList(XmlPullParser paramXmlPullParser, SegmentBase.SegmentList paramSegmentList)
    throws XmlPullParserException, IOException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a28 = a27\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser paramXmlPullParser, SegmentBase.SegmentTemplate paramSegmentTemplate)
    throws XmlPullParserException, IOException
  {
    long l2 = 1L;
    if (paramSegmentTemplate != null) {
      l1 = timescale;
    } else {
      l1 = 1L;
    }
    long l3 = parseLong(paramXmlPullParser, "timescale", l1);
    if (paramSegmentTemplate != null) {
      l1 = presentationTimeOffset;
    } else {
      l1 = 0L;
    }
    long l4 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
    if (paramSegmentTemplate != null) {
      l1 = duration;
    } else {
      l1 = -9223372036854775807L;
    }
    long l5 = parseLong(paramXmlPullParser, "duration", l1);
    long l1 = l2;
    if (paramSegmentTemplate != null) {
      l1 = startNumber;
    }
    l1 = parseLong(paramXmlPullParser, "startNumber", l1);
    Object localObject4 = null;
    Object localObject1;
    if (paramSegmentTemplate != null) {
      localObject1 = mediaTemplate;
    } else {
      localObject1 = null;
    }
    UrlTemplate localUrlTemplate1 = parseUrlTemplate(paramXmlPullParser, "media", (UrlTemplate)localObject1);
    if (paramSegmentTemplate != null) {
      localObject1 = initializationTemplate;
    } else {
      localObject1 = null;
    }
    UrlTemplate localUrlTemplate2 = parseUrlTemplate(paramXmlPullParser, "initialization", (UrlTemplate)localObject1);
    Object localObject3 = null;
    Object localObject2;
    do
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization"))
      {
        localObject1 = parseInitialization(paramXmlPullParser);
        localObject2 = localObject3;
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
      {
        localObject2 = parseSegmentTimeline(paramXmlPullParser);
        localObject1 = localObject4;
      }
      else
      {
        maybeSkipTag(paramXmlPullParser);
        localObject2 = localObject3;
        localObject1 = localObject4;
      }
      localObject4 = localObject1;
      localObject3 = localObject2;
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTemplate"));
    localObject3 = localObject1;
    paramXmlPullParser = (XmlPullParser)localObject2;
    if (paramSegmentTemplate != null)
    {
      if (localObject1 == null) {
        localObject1 = initialization;
      }
      if (localObject2 != null)
      {
        localObject3 = localObject1;
        paramXmlPullParser = (XmlPullParser)localObject2;
      }
      else
      {
        paramXmlPullParser = segmentTimeline;
        localObject3 = localObject1;
      }
    }
    return buildSegmentTemplate(localObject3, l3, l4, l1, l5, paramXmlPullParser, localUrlTemplate2, localUrlTemplate1);
  }
  
  protected List parseSegmentTimeline(XmlPullParser paramXmlPullParser)
    throws XmlPullParserException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    long l2 = 0L;
    do
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "S"))
      {
        long l1 = parseLong(paramXmlPullParser, "t", l2);
        long l3 = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
        int i = 0;
        int j = parseInt(paramXmlPullParser, "r", 0);
        for (;;)
        {
          l2 = l1;
          if (i >= j + 1) {
            break;
          }
          localArrayList.add(buildSegmentTimelineElement(l1, l3));
          l1 += l3;
          i += 1;
        }
      }
      maybeSkipTag(paramXmlPullParser);
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTimeline"));
    return localArrayList;
  }
  
  protected RangedUri parseSegmentUrl(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "media", "mediaRange");
  }
  
  protected UrlTemplate parseUrlTemplate(XmlPullParser paramXmlPullParser, String paramString, UrlTemplate paramUrlTemplate)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser != null) {
      paramUrlTemplate = UrlTemplate.compile(paramXmlPullParser);
    }
    return paramUrlTemplate;
  }
  
  protected UtcTimingElement parseUtcTiming(XmlPullParser paramXmlPullParser)
  {
    return buildUtcTimingElement(paramXmlPullParser.getAttributeValue(null, "schemeIdUri"), paramXmlPullParser.getAttributeValue(null, "value"));
  }
  
  protected static final class RepresentationInfo
  {
    public final String baseUrl;
    public final ArrayList<com.google.android.exoplayer2.drm.DrmInitData.SchemeData> drmSchemeDatas;
    public final String drmSchemeType;
    public final Format format;
    public final ArrayList<Descriptor> inbandEventStreams;
    public final long revisionId;
    public final SegmentBase segmentBase;
    
    public RepresentationInfo(Format paramFormat, String paramString1, SegmentBase paramSegmentBase, String paramString2, ArrayList paramArrayList1, ArrayList paramArrayList2, long paramLong)
    {
      format = paramFormat;
      baseUrl = paramString1;
      segmentBase = paramSegmentBase;
      drmSchemeType = paramString2;
      drmSchemeDatas = paramArrayList1;
      inbandEventStreams = paramArrayList2;
      revisionId = paramLong;
    }
  }
}
