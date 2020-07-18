package com.google.android.exoplayer2.text.ttml;

import android.text.Layout.Alignment;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ColorParser;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.util.XmlPullParserUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public final class TtmlDecoder
  extends SimpleSubtitleDecoder
{
  private static final String ACTION_UPDATE_ALL = "http://www.w3.org/ns/ttml#parameter";
  private static final String ATTR_BEGIN = "begin";
  private static final String ATTR_DURATION = "dur";
  private static final String ATTR_END = "end";
  private static final String ATTR_REGION = "region";
  private static final String ATTR_STYLE = "style";
  private static final Pattern CELL_RESOLUTION;
  private static final Pattern CLOCK_TIME = Pattern.compile("^([0-9][0-9]+):([0-9][0-9]):([0-9][0-9])(?:(\\.[0-9]+)|:([0-9][0-9])(?:\\.([0-9]+))?)?$");
  private static final CellResolution DEFAULT_CELL_RESOLUTION = new CellResolution(32, 15);
  private static final FrameAndTickRate DEFAULT_FRAME_AND_TICK_RATE;
  private static final int DEFAULT_FRAME_RATE = 30;
  private static final Pattern FONT_SIZE;
  private static final Pattern OFFSET_TIME = Pattern.compile("^([0-9]+(?:\\.[0-9]+)?)(h|m|s|ms|f|t)$");
  private static final String PAGE_KEY = "TtmlDecoder";
  private static final Pattern PERCENTAGE_COORDINATES;
  private final XmlPullParserFactory xmlParserFactory;
  
  static
  {
    FONT_SIZE = Pattern.compile("^(([0-9]*.)?[0-9]+)(px|em|%)$");
    PERCENTAGE_COORDINATES = Pattern.compile("^(\\d+\\.?\\d*?)% (\\d+\\.?\\d*?)%$");
    CELL_RESOLUTION = Pattern.compile("^(\\d+) (\\d+)$");
    DEFAULT_FRAME_AND_TICK_RATE = new FrameAndTickRate(30.0F, 1, 1);
  }
  
  public TtmlDecoder()
  {
    super("TtmlDecoder");
    try
    {
      XmlPullParserFactory localXmlPullParserFactory = XmlPullParserFactory.newInstance();
      xmlParserFactory = localXmlPullParserFactory;
      localXmlPullParserFactory = xmlParserFactory;
      localXmlPullParserFactory.setNamespaceAware(true);
      return;
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      throw new RuntimeException("Couldn't create XmlPullParserFactory instance", localXmlPullParserException);
    }
  }
  
  private TtmlStyle createIfNull(TtmlStyle paramTtmlStyle)
  {
    TtmlStyle localTtmlStyle = paramTtmlStyle;
    if (paramTtmlStyle == null) {
      localTtmlStyle = new TtmlStyle();
    }
    return localTtmlStyle;
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    return (paramString.equals("tt")) || (paramString.equals("head")) || (paramString.equals("body")) || (paramString.equals("div")) || (paramString.equals("p")) || (paramString.equals("span")) || (paramString.equals("br")) || (paramString.equals("style")) || (paramString.equals("styling")) || (paramString.equals("layout")) || (paramString.equals("region")) || (paramString.equals("metadata")) || (paramString.equals("smpte:image")) || (paramString.equals("smpte:data")) || (paramString.equals("smpte:information"));
  }
  
  private CellResolution parseCellResolution(XmlPullParser paramXmlPullParser, CellResolution paramCellResolution)
    throws SubtitleDecoderException
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "cellResolution");
    if (paramXmlPullParser == null) {
      return paramCellResolution;
    }
    Object localObject = CELL_RESOLUTION.matcher(paramXmlPullParser);
    if (!((Matcher)localObject).matches())
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Ignoring malformed cell resolution: ");
      ((StringBuilder)localObject).append(paramXmlPullParser);
      Log.w("TtmlDecoder", ((StringBuilder)localObject).toString());
      return paramCellResolution;
    }
    try
    {
      int i = Integer.parseInt(((Matcher)localObject).group(1));
      int j = Integer.parseInt(((Matcher)localObject).group(2));
      if ((i != 0) && (j != 0))
      {
        localObject = new CellResolution(i, j);
        return localObject;
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Invalid cell resolution ");
      ((StringBuilder)localObject).append(i);
      ((StringBuilder)localObject).append(" ");
      ((StringBuilder)localObject).append(j);
      localObject = new SubtitleDecoderException(((StringBuilder)localObject).toString());
      throw ((Throwable)localObject);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      for (;;) {}
    }
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Ignoring malformed cell resolution: ");
    ((StringBuilder)localObject).append(paramXmlPullParser);
    Log.w("TtmlDecoder", ((StringBuilder)localObject).toString());
    return paramCellResolution;
  }
  
  private static void parseFontSize(String paramString, TtmlStyle paramTtmlStyle)
    throws SubtitleDecoderException
  {
    Object localObject = Util.split(paramString, "\\s+");
    if (localObject.length == 1)
    {
      localObject = FONT_SIZE.matcher(paramString);
    }
    else
    {
      if (localObject.length != 2) {
        break label298;
      }
      localObject = FONT_SIZE.matcher(localObject[1]);
      Log.w("TtmlDecoder", "Multiple values in fontSize attribute. Picking the second value for vertical font size and ignoring the first.");
    }
    if (((Matcher)localObject).matches())
    {
      paramString = ((Matcher)localObject).group(3);
      int i = -1;
      int j = paramString.hashCode();
      if (j != 37)
      {
        if (j != 3240)
        {
          if ((j == 3592) && (paramString.equals("px"))) {
            i = 0;
          }
        }
        else if (paramString.equals("em")) {
          i = 1;
        }
      }
      else if (paramString.equals("%")) {
        i = 2;
      }
      switch (i)
      {
      default: 
        paramTtmlStyle = new StringBuilder();
        paramTtmlStyle.append("Invalid unit for fontSize: '");
        paramTtmlStyle.append(paramString);
        paramTtmlStyle.append("'.");
        throw new SubtitleDecoderException(paramTtmlStyle.toString());
      case 2: 
        paramTtmlStyle.setFontSizeUnit(3);
        break;
      case 1: 
        paramTtmlStyle.setFontSizeUnit(2);
        break;
      case 0: 
        paramTtmlStyle.setFontSizeUnit(1);
      }
      paramTtmlStyle.setFontSize(Float.valueOf(((Matcher)localObject).group(1)).floatValue());
      return;
    }
    paramTtmlStyle = new StringBuilder();
    paramTtmlStyle.append("Invalid expression for fontSize: '");
    paramTtmlStyle.append(paramString);
    paramTtmlStyle.append("'.");
    throw new SubtitleDecoderException(paramTtmlStyle.toString());
    label298:
    paramString = new StringBuilder();
    paramString.append("Invalid number of entries for fontSize: ");
    paramString.append(localObject.length);
    paramString.append(".");
    throw new SubtitleDecoderException(paramString.toString());
  }
  
  private FrameAndTickRate parseFrameAndTickRates(XmlPullParser paramXmlPullParser)
    throws SubtitleDecoderException
  {
    Object localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRate");
    int i;
    if (localObject != null) {
      i = Integer.parseInt((String)localObject);
    } else {
      i = 30;
    }
    float f = 1.0F;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "frameRateMultiplier");
    if (localObject != null)
    {
      localObject = Util.split((String)localObject, " ");
      if (localObject.length == 2) {
        f = Integer.parseInt(localObject[0]) / Integer.parseInt(localObject[1]);
      } else {
        throw new SubtitleDecoderException("frameRateMultiplier doesn't have 2 parts");
      }
    }
    int j = DEFAULT_FRAME_AND_TICK_RATEsubFrameRate;
    localObject = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "subFrameRate");
    if (localObject != null) {
      j = Integer.parseInt((String)localObject);
    }
    int k = DEFAULT_FRAME_AND_TICK_RATEtickRate;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue("http://www.w3.org/ns/ttml#parameter", "tickRate");
    if (paramXmlPullParser != null) {
      k = Integer.parseInt(paramXmlPullParser);
    }
    return new FrameAndTickRate(i * f, j, k);
  }
  
  private Map parseHeader(XmlPullParser paramXmlPullParser, Map paramMap1, Map paramMap2, CellResolution paramCellResolution)
    throws IOException, XmlPullParserException
  {
    do
    {
      paramXmlPullParser.next();
      Object localObject1;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "style"))
      {
        Object localObject2 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "style");
        localObject1 = parseStyleAttributes(paramXmlPullParser, new TtmlStyle());
        if (localObject2 != null)
        {
          localObject2 = parseStyleIds((String)localObject2);
          int j = localObject2.length;
          int i = 0;
          while (i < j)
          {
            ((TtmlStyle)localObject1).chain((TtmlStyle)paramMap1.get(localObject2[i]));
            i += 1;
          }
        }
        if (((TtmlStyle)localObject1).getId() != null) {
          paramMap1.put(((TtmlStyle)localObject1).getId(), localObject1);
        }
      }
      else if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "region"))
      {
        localObject1 = parseRegionAttributes(paramXmlPullParser, paramCellResolution);
        if (localObject1 != null) {
          paramMap2.put(value, localObject1);
        }
      }
    } while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "head"));
    return paramMap1;
  }
  
  private TtmlNode parseNode(XmlPullParser paramXmlPullParser, TtmlNode paramTtmlNode, Map paramMap, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    int k = paramXmlPullParser.getAttributeCount();
    TtmlStyle localTtmlStyle = parseStyleAttributes(paramXmlPullParser, null);
    Object localObject2 = null;
    long l1 = -9223372036854775807L;
    long l2 = -9223372036854775807L;
    long l5 = -9223372036854775807L;
    Object localObject1 = "";
    int j = 0;
    long l3;
    while (j < k)
    {
      Object localObject3 = paramXmlPullParser.getAttributeName(j);
      Object localObject5 = paramXmlPullParser.getAttributeValue(j);
      switch (((String)localObject3).hashCode())
      {
      default: 
        break;
      case 109780401: 
        if (((String)localObject3).equals("style")) {
          i = 3;
        }
        break;
      case 93616297: 
        if (((String)localObject3).equals("begin")) {
          i = 0;
        }
        break;
      case 100571: 
        if (((String)localObject3).equals("end")) {
          i = 1;
        }
        break;
      case 99841: 
        if (((String)localObject3).equals("dur")) {
          i = 2;
        }
        break;
      case -934795532: 
        if (((String)localObject3).equals("region")) {
          i = 4;
        }
        break;
      }
      int i = -1;
      Object localObject4;
      switch (i)
      {
      default: 
        localObject3 = localObject2;
        l3 = l1;
        l4 = l2;
        l6 = l5;
        localObject4 = localObject1;
        break;
      case 4: 
        localObject3 = localObject2;
        l3 = l1;
        l4 = l2;
        l6 = l5;
        localObject4 = localObject1;
        if (paramMap.containsKey(localObject5))
        {
          localObject4 = localObject5;
          localObject3 = localObject2;
          l3 = l1;
          l4 = l2;
          l6 = l5;
        }
        break;
      case 3: 
        localObject5 = parseStyleIds((String)localObject5);
        localObject3 = localObject2;
        l3 = l1;
        l4 = l2;
        l6 = l5;
        localObject4 = localObject1;
        if (localObject5.length > 0)
        {
          localObject3 = localObject5;
          l3 = l1;
          l4 = l2;
          l6 = l5;
          localObject4 = localObject1;
        }
        break;
      case 2: 
        l6 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        localObject3 = localObject2;
        l3 = l1;
        l4 = l2;
        localObject4 = localObject1;
        break;
      case 1: 
        l4 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        localObject3 = localObject2;
        l3 = l1;
        l6 = l5;
        localObject4 = localObject1;
        break;
      case 0: 
        l3 = parseTimeExpression((String)localObject5, paramFrameAndTickRate);
        localObject4 = localObject1;
        l6 = l5;
        l4 = l2;
        localObject3 = localObject2;
      }
      j += 1;
      localObject2 = localObject3;
      l1 = l3;
      l2 = l4;
      l5 = l6;
      localObject1 = localObject4;
    }
    long l6 = l1;
    long l4 = l2;
    if (paramTtmlNode != null)
    {
      l6 = l1;
      l4 = l2;
      if (startTimeUs != -9223372036854775807L)
      {
        l3 = l1;
        if (l1 != -9223372036854775807L) {
          l3 = l1 + startTimeUs;
        }
        l6 = l3;
        l4 = l2;
        if (l2 != -9223372036854775807L)
        {
          l4 = l2 + startTimeUs;
          l6 = l3;
        }
      }
    }
    l1 = l4;
    if (l4 == -9223372036854775807L) {
      if (l5 != -9223372036854775807L)
      {
        l1 = l5 + l6;
      }
      else
      {
        l1 = l4;
        if (paramTtmlNode != null)
        {
          l1 = l4;
          if (endTimeUs != -9223372036854775807L) {
            l1 = endTimeUs;
          }
        }
      }
    }
    return TtmlNode.buildNode(paramXmlPullParser.getName(), l6, l1, localTtmlStyle, localObject2, (String)localObject1);
  }
  
  private TtmlRegion parseRegionAttributes(XmlPullParser paramXmlPullParser, CellResolution paramCellResolution)
  {
    String str2 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "id");
    if (str2 == null) {
      return null;
    }
    String str1 = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "origin");
    Object localObject;
    if (str1 != null)
    {
      localObject = PERCENTAGE_COORDINATES.matcher(str1);
      if (!((Matcher)localObject).matches()) {}
    }
    for (;;)
    {
      try
      {
        f1 = Float.parseFloat(((Matcher)localObject).group(1));
        f2 = f1 / 100.0F;
        f1 = Float.parseFloat(((Matcher)localObject).group(2));
        f1 /= 100.0F;
        localObject = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "extent");
        if (localObject != null)
        {
          localObject = PERCENTAGE_COORDINATES.matcher((CharSequence)localObject);
          if (!((Matcher)localObject).matches()) {}
        }
      }
      catch (NumberFormatException paramXmlPullParser)
      {
        float f1;
        float f2;
        float f3;
        float f4;
        int i;
        continue;
      }
      try
      {
        f3 = Float.parseFloat(((Matcher)localObject).group(1));
        f3 /= 100.0F;
        f4 = Float.parseFloat(((Matcher)localObject).group(2));
        f4 /= 100.0F;
        paramXmlPullParser = XmlPullParserUtil.getAttributeValue(paramXmlPullParser, "displayAlign");
        if (paramXmlPullParser != null)
        {
          paramXmlPullParser = Util.toLowerInvariant(paramXmlPullParser);
          i = -1;
          int j = paramXmlPullParser.hashCode();
          if (j != -1364013995)
          {
            if ((j == 92734940) && (paramXmlPullParser.equals("after"))) {
              i = 1;
            }
          }
          else if (paramXmlPullParser.equals("center")) {
            i = 0;
          }
          switch (i)
          {
          default: 
            break;
          case 1: 
            f1 += f4;
            i = 2;
            break;
          case 0: 
            f1 += f4 / 2.0F;
            i = 1;
            break;
          }
        }
        i = 0;
        return new TtmlRegion(str2, f2, f1, 0, i, f3, 1, 1.0F / rows);
      }
      catch (NumberFormatException paramXmlPullParser) {}
    }
    paramXmlPullParser = new StringBuilder();
    paramXmlPullParser.append("Ignoring region with malformed extent: ");
    paramXmlPullParser.append(str1);
    Log.w("TtmlDecoder", paramXmlPullParser.toString());
    return null;
    paramXmlPullParser = new StringBuilder();
    paramXmlPullParser.append("Ignoring region with unsupported extent: ");
    paramXmlPullParser.append(str1);
    Log.w("TtmlDecoder", paramXmlPullParser.toString());
    return null;
    Log.w("TtmlDecoder", "Ignoring region without an extent");
    return null;
    paramXmlPullParser = new StringBuilder();
    paramXmlPullParser.append("Ignoring region with malformed origin: ");
    paramXmlPullParser.append(str1);
    Log.w("TtmlDecoder", paramXmlPullParser.toString());
    return null;
    paramXmlPullParser = new StringBuilder();
    paramXmlPullParser.append("Ignoring region with unsupported origin: ");
    paramXmlPullParser.append(str1);
    Log.w("TtmlDecoder", paramXmlPullParser.toString());
    return null;
    Log.w("TtmlDecoder", "Ignoring region without an origin");
    return null;
  }
  
  private TtmlStyle parseStyleAttributes(XmlPullParser paramXmlPullParser, TtmlStyle paramTtmlStyle)
  {
    int n = paramXmlPullParser.getAttributeCount();
    int j = 0;
    for (localObject = paramTtmlStyle; j < n; localObject = paramTtmlStyle)
    {
      String str = paramXmlPullParser.getAttributeValue(j);
      paramTtmlStyle = paramXmlPullParser.getAttributeName(j);
      int i = paramTtmlStyle.hashCode();
      int m = 4;
      int k = 2;
      switch (i)
      {
      default: 
        break;
      case 1287124693: 
        if (paramTtmlStyle.equals("backgroundColor")) {
          i = 1;
        }
        break;
      case 365601008: 
        if (paramTtmlStyle.equals("fontSize")) {
          i = 4;
        }
        break;
      case 94842723: 
        if (paramTtmlStyle.equals("color")) {
          i = 2;
        }
        break;
      case 3355: 
        if (paramTtmlStyle.equals("id")) {
          i = 0;
        }
        break;
      case -734428249: 
        if (paramTtmlStyle.equals("fontWeight")) {
          i = 5;
        }
        break;
      case -879295043: 
        if (paramTtmlStyle.equals("textDecoration")) {
          i = 8;
        }
        break;
      case -1065511464: 
        if (paramTtmlStyle.equals("textAlign")) {
          i = 7;
        }
        break;
      case -1224696685: 
        if (paramTtmlStyle.equals("fontFamily")) {
          i = 3;
        }
        break;
      case -1550943582: 
        if (paramTtmlStyle.equals("fontStyle")) {
          i = 6;
        }
        break;
      }
      i = -1;
      switch (i)
      {
      default: 
        paramTtmlStyle = (TtmlStyle)localObject;
        break;
      case 8: 
        paramTtmlStyle = Util.toLowerInvariant(str);
        i = paramTtmlStyle.hashCode();
        if (i != -1461280213)
        {
          if (i != -1026963764)
          {
            if (i != 913457136)
            {
              if ((i == 1679736913) && (paramTtmlStyle.equals("linethrough")))
              {
                i = 0;
                break label478;
              }
            }
            else if (paramTtmlStyle.equals("nolinethrough"))
            {
              i = 1;
              break label478;
            }
          }
          else if (paramTtmlStyle.equals("underline"))
          {
            i = k;
            break label478;
          }
        }
        else if (paramTtmlStyle.equals("nounderline"))
        {
          i = 3;
          break label478;
        }
        i = -1;
        switch (i)
        {
        default: 
          paramTtmlStyle = (TtmlStyle)localObject;
          break;
        case 3: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setUnderline(false);
          break;
        case 2: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setUnderline(true);
          break;
        case 1: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setLinethrough(false);
          break;
        case 0: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setLinethrough(true);
        }
        break;
      case 7: 
        paramTtmlStyle = Util.toLowerInvariant(str);
        switch (paramTtmlStyle.hashCode())
        {
        default: 
          break;
        case 109757538: 
          if (paramTtmlStyle.equals("start")) {
            i = 1;
          }
          break;
        case 108511772: 
          if (paramTtmlStyle.equals("right")) {
            i = 2;
          }
          break;
        case 3317767: 
          if (paramTtmlStyle.equals("left")) {
            i = 0;
          }
          break;
        case 100571: 
          if (paramTtmlStyle.equals("end")) {
            i = 3;
          }
          break;
        case -1364013995: 
          if (paramTtmlStyle.equals("center")) {
            i = m;
          }
          break;
        }
        i = -1;
        switch (i)
        {
        default: 
          paramTtmlStyle = (TtmlStyle)localObject;
          break;
        case 4: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setTextAlign(Layout.Alignment.ALIGN_CENTER);
          break;
        case 3: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          break;
        case 2: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
          break;
        case 1: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
          break;
        case 0: 
          paramTtmlStyle = createIfNull((TtmlStyle)localObject).setTextAlign(Layout.Alignment.ALIGN_NORMAL);
        }
        break;
      case 6: 
        paramTtmlStyle = createIfNull((TtmlStyle)localObject).setItalic("italic".equalsIgnoreCase(str));
        break;
      case 5: 
        label478:
        paramTtmlStyle = createIfNull((TtmlStyle)localObject).setBold("bold".equalsIgnoreCase(str));
        break;
      }
      for (;;)
      {
        try
        {
          paramTtmlStyle = createIfNull((TtmlStyle)localObject);
        }
        catch (SubtitleDecoderException paramTtmlStyle)
        {
          paramTtmlStyle = (TtmlStyle)localObject;
          continue;
        }
        try
        {
          parseFontSize(str, paramTtmlStyle);
        }
        catch (SubtitleDecoderException localSubtitleDecoderException) {}
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Failed parsing fontSize value: ");
      ((StringBuilder)localObject).append(str);
      Log.w("TtmlDecoder", ((StringBuilder)localObject).toString());
      break label1138;
      paramTtmlStyle = createIfNull((TtmlStyle)localObject).setFontFamily(str);
      break label1138;
      localObject = createIfNull((TtmlStyle)localObject);
      paramTtmlStyle = (TtmlStyle)localObject;
      try
      {
        ((TtmlStyle)localObject).setFontColor(ColorParser.parseTtmlColor(str));
      }
      catch (IllegalArgumentException localIllegalArgumentException1)
      {
        for (;;) {}
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Failed parsing color value: ");
      ((StringBuilder)localObject).append(str);
      Log.w("TtmlDecoder", ((StringBuilder)localObject).toString());
      break label1138;
      localObject = createIfNull((TtmlStyle)localObject);
      paramTtmlStyle = (TtmlStyle)localObject;
      try
      {
        ((TtmlStyle)localObject).setBackgroundColor(ColorParser.parseTtmlColor(str));
      }
      catch (IllegalArgumentException localIllegalArgumentException2)
      {
        label1138:
        for (;;) {}
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Failed parsing background value: ");
      ((StringBuilder)localObject).append(str);
      Log.w("TtmlDecoder", ((StringBuilder)localObject).toString());
      break label1138;
      paramTtmlStyle = (TtmlStyle)localObject;
      if ("style".equals(paramXmlPullParser.getName())) {
        paramTtmlStyle = createIfNull((TtmlStyle)localObject).setId(str);
      }
      j += 1;
    }
    return localObject;
  }
  
  private String[] parseStyleIds(String paramString)
  {
    paramString = paramString.trim();
    if (paramString.isEmpty()) {
      return new String[0];
    }
    return Util.split(paramString, "\\s+");
  }
  
  private static long parseTimeExpression(String paramString, FrameAndTickRate paramFrameAndTickRate)
    throws SubtitleDecoderException
  {
    Matcher localMatcher = CLOCK_TIME.matcher(paramString);
    boolean bool = localMatcher.matches();
    int i = 5;
    double d3;
    double d1;
    double d2;
    if (bool)
    {
      double d4 = Long.parseLong(localMatcher.group(1)) * 3600L;
      double d5 = Long.parseLong(localMatcher.group(2)) * 60L;
      double d6 = Long.parseLong(localMatcher.group(3));
      paramString = localMatcher.group(4);
      d3 = 0.0D;
      if (paramString != null) {
        d1 = Double.parseDouble(paramString);
      } else {
        d1 = 0.0D;
      }
      paramString = localMatcher.group(5);
      if (paramString != null) {
        d2 = (float)Long.parseLong(paramString) / effectiveFrameRate;
      } else {
        d2 = 0.0D;
      }
      paramString = localMatcher.group(6);
      if (paramString != null) {
        d3 = Long.parseLong(paramString) / subFrameRate / effectiveFrameRate;
      }
      return ((d4 + d5 + d6 + d1 + d2 + d3) * 1000000.0D);
    }
    localMatcher = OFFSET_TIME.matcher(paramString);
    if (localMatcher.matches())
    {
      d3 = Double.parseDouble(localMatcher.group(1));
      d2 = d3;
      paramString = localMatcher.group(2);
      int j = paramString.hashCode();
      if (j != 102)
      {
        if (j != 104)
        {
          if (j != 109)
          {
            if (j != 3494)
            {
              switch (j)
              {
              default: 
                break;
              case 116: 
                if (!paramString.equals("t")) {
                  break;
                }
                break;
              case 115: 
                if (!paramString.equals("s")) {
                  break;
                }
                i = 2;
                break;
              }
            }
            else if (paramString.equals("ms"))
            {
              i = 3;
              break label378;
            }
          }
          else if (paramString.equals("m"))
          {
            i = 1;
            break label378;
          }
        }
        else if (paramString.equals("h"))
        {
          i = 0;
          break label378;
        }
      }
      else if (paramString.equals("f"))
      {
        i = 4;
        break label378;
      }
      i = -1;
      label378:
      d1 = d2;
      switch (i)
      {
      default: 
        d1 = d2;
        break;
      case 5: 
        d1 = d3 / tickRate;
        break;
      case 4: 
        d1 = d3 / effectiveFrameRate;
        break;
      case 3: 
        d1 = d3 / 1000.0D;
        break;
      case 1: 
        d1 = d3 * 60.0D;
        break;
      case 0: 
        d1 = d3 * 3600.0D;
      }
      return (d1 * 1000000.0D);
    }
    paramFrameAndTickRate = new StringBuilder();
    paramFrameAndTickRate.append("Malformed time expression: ");
    paramFrameAndTickRate.append(paramString);
    throw new SubtitleDecoderException(paramFrameAndTickRate.toString());
  }
  
  protected TtmlSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException
  {
    Object localObject1 = xmlParserFactory;
    try
    {
      XmlPullParser localXmlPullParser = ((XmlPullParserFactory)localObject1).newPullParser();
      HashMap localHashMap1 = new HashMap();
      HashMap localHashMap2 = new HashMap();
      Object localObject3 = null;
      localHashMap2.put("", new TtmlRegion(null));
      int i = 0;
      localXmlPullParser.setInput(new ByteArrayInputStream(paramArrayOfByte, 0, paramInt), null);
      ArrayDeque localArrayDeque = new ArrayDeque();
      int j = localXmlPullParser.getEventType();
      Object localObject2 = DEFAULT_FRAME_AND_TICK_RATE;
      localObject1 = DEFAULT_CELL_RESOLUTION;
      paramArrayOfByte = (byte[])localObject3;
      while (j != 1)
      {
        localObject3 = localArrayDeque.peek();
        TtmlNode localTtmlNode1 = (TtmlNode)localObject3;
        Object localObject4;
        Object localObject5;
        byte[] arrayOfByte;
        if (i == 0)
        {
          localObject3 = localXmlPullParser.getName();
          if (j == 2)
          {
            paramBoolean = "tt".equals(localObject3);
            if (paramBoolean)
            {
              localObject2 = parseFrameAndTickRates(localXmlPullParser);
              localObject1 = DEFAULT_CELL_RESOLUTION;
              localObject1 = parseCellResolution(localXmlPullParser, (CellResolution)localObject1);
            }
            paramBoolean = isSupportedTag((String)localObject3);
            if (!paramBoolean)
            {
              localObject3 = new StringBuilder();
              ((StringBuilder)localObject3).append("Ignoring unsupported tag: ");
              ((StringBuilder)localObject3).append(localXmlPullParser.getName());
              Log.log("TtmlDecoder", ((StringBuilder)localObject3).toString());
              paramInt = i + 1;
              localObject3 = paramArrayOfByte;
              localObject4 = localObject2;
              localObject5 = localObject1;
            }
            else
            {
              paramBoolean = "head".equals(localObject3);
              if (paramBoolean)
              {
                parseHeader(localXmlPullParser, localHashMap1, localHashMap2, (CellResolution)localObject1);
                localObject3 = paramArrayOfByte;
                paramInt = i;
                localObject4 = localObject2;
                localObject5 = localObject1;
              }
              else
              {
                try
                {
                  TtmlNode localTtmlNode2 = parseNode(localXmlPullParser, localTtmlNode1, localHashMap2, (FrameAndTickRate)localObject2);
                  localArrayDeque.push(localTtmlNode2);
                  localObject3 = paramArrayOfByte;
                  paramInt = i;
                  localObject4 = localObject2;
                  localObject5 = localObject1;
                  if (localTtmlNode1 == null) {
                    break label570;
                  }
                  localTtmlNode1.addChild(localTtmlNode2);
                  localObject3 = paramArrayOfByte;
                  paramInt = i;
                  localObject4 = localObject2;
                  localObject5 = localObject1;
                }
                catch (SubtitleDecoderException localSubtitleDecoderException)
                {
                  Log.w("TtmlDecoder", "Suppressing parser error", localSubtitleDecoderException);
                  paramInt = i + 1;
                  arrayOfByte = paramArrayOfByte;
                  localObject4 = localObject2;
                  localObject5 = localObject1;
                }
              }
            }
          }
          else if (j == 4)
          {
            localTtmlNode1.addChild(TtmlNode.buildTextNode(localXmlPullParser.getText()));
            arrayOfByte = paramArrayOfByte;
            paramInt = i;
            localObject4 = localObject2;
            localObject5 = localObject1;
          }
          else
          {
            arrayOfByte = paramArrayOfByte;
            paramInt = i;
            localObject4 = localObject2;
            localObject5 = localObject1;
            if (j == 3)
            {
              paramBoolean = localXmlPullParser.getName().equals("tt");
              if (paramBoolean)
              {
                paramArrayOfByte = localArrayDeque.peek();
                paramArrayOfByte = (TtmlNode)paramArrayOfByte;
                paramArrayOfByte = new TtmlSubtitle(paramArrayOfByte, localHashMap1, localHashMap2);
              }
              localArrayDeque.pop();
              arrayOfByte = paramArrayOfByte;
              paramInt = i;
              localObject4 = localObject2;
              localObject5 = localObject1;
            }
          }
        }
        else if (j == 2)
        {
          paramInt = i + 1;
          arrayOfByte = paramArrayOfByte;
          localObject4 = localObject2;
          localObject5 = localObject1;
        }
        else
        {
          arrayOfByte = paramArrayOfByte;
          paramInt = i;
          localObject4 = localObject2;
          localObject5 = localObject1;
          if (j == 3)
          {
            paramInt = i - 1;
            localObject5 = localObject1;
            localObject4 = localObject2;
            arrayOfByte = paramArrayOfByte;
          }
        }
        label570:
        localXmlPullParser.next();
        j = localXmlPullParser.getEventType();
        paramArrayOfByte = arrayOfByte;
        i = paramInt;
        localObject2 = localObject4;
        localObject1 = localObject5;
      }
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      throw new IllegalStateException("Unexpected error when reading input.", paramArrayOfByte);
    }
    catch (XmlPullParserException paramArrayOfByte)
    {
      throw new SubtitleDecoderException("Unable to decode source", paramArrayOfByte);
    }
  }
  
  private static final class CellResolution
  {
    final int columns;
    final int rows;
    
    CellResolution(int paramInt1, int paramInt2)
    {
      columns = paramInt1;
      rows = paramInt2;
    }
  }
  
  private static final class FrameAndTickRate
  {
    final float effectiveFrameRate;
    final int subFrameRate;
    final int tickRate;
    
    FrameAndTickRate(float paramFloat, int paramInt1, int paramInt2)
    {
      effectiveFrameRate = paramFloat;
      subFrameRate = paramInt1;
      tickRate = paramInt2;
    }
  }
}
