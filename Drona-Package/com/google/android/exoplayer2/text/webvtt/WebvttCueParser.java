package com.google.android.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttCueParser
{
  private static final char CHAR_AMPERSAND = '&';
  private static final char CHAR_GREATER_THAN = '>';
  private static final char CHAR_LESS_THAN = '<';
  private static final char CHAR_SEMI_COLON = ';';
  private static final char CHAR_SLASH = '/';
  private static final char CHAR_SPACE = ' ';
  public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
  private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
  private static final String ENTITY_AMPERSAND = "amp";
  private static final String ENTITY_GREATER_THAN = "gt";
  private static final String ENTITY_LESS_THAN = "lt";
  private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
  private static final int STYLE_BOLD = 1;
  private static final int STYLE_ITALIC = 2;
  private static final String TAG = "WebvttCueParser";
  private static final String TAG_BOLD = "b";
  private static final String TAG_CLASS = "c";
  private static final String TAG_ITALIC = "i";
  private static final String TAG_LANG = "lang";
  private static final String TAG_UNDERLINE = "u";
  private static final String TAG_VOICE = "v";
  private final StringBuilder textBuilder = new StringBuilder();
  
  public WebvttCueParser() {}
  
  private static void applyEntity(String paramString, SpannableStringBuilder paramSpannableStringBuilder)
  {
    int i = paramString.hashCode();
    if (i != 3309)
    {
      if (i != 3464)
      {
        if (i != 96708)
        {
          if ((i == 3374865) && (paramString.equals("nbsp")))
          {
            i = 2;
            break label92;
          }
        }
        else if (paramString.equals("amp"))
        {
          i = 3;
          break label92;
        }
      }
      else if (paramString.equals("lt"))
      {
        i = 0;
        break label92;
      }
    }
    else if (paramString.equals("gt"))
    {
      i = 1;
      break label92;
    }
    i = -1;
    switch (i)
    {
    default: 
      paramSpannableStringBuilder = new StringBuilder();
      paramSpannableStringBuilder.append("ignoring unsupported entity: '&");
      paramSpannableStringBuilder.append(paramString);
      paramSpannableStringBuilder.append(";'");
      Log.w("WebvttCueParser", paramSpannableStringBuilder.toString());
      return;
    case 3: 
      paramSpannableStringBuilder.append('&');
      return;
    case 2: 
      paramSpannableStringBuilder.append(' ');
      return;
    case 1: 
      label92:
      paramSpannableStringBuilder.append('>');
      return;
    }
    paramSpannableStringBuilder.append('<');
  }
  
  private static void applySpansForTag(String paramString, StartTag paramStartTag, SpannableStringBuilder paramSpannableStringBuilder, List paramList1, List paramList2)
  {
    int k = position;
    int m = paramSpannableStringBuilder.length();
    String str = name;
    int i = str.hashCode();
    int j = 0;
    switch (i)
    {
    default: 
      break;
    case 3314158: 
      if (str.equals("lang")) {
        i = 4;
      }
      break;
    case 118: 
      if (str.equals("v")) {
        i = 5;
      }
      break;
    case 117: 
      if (str.equals("u")) {
        i = 2;
      }
      break;
    case 105: 
      if (str.equals("i")) {
        i = 1;
      }
      break;
    case 99: 
      if (str.equals("c")) {
        i = 3;
      }
      break;
    case 98: 
      if (str.equals("b")) {
        i = 0;
      }
      break;
    case 0: 
      if (str.equals("")) {
        i = 6;
      }
      break;
    }
    i = -1;
    switch (i)
    {
    default: 
      return;
    case 2: 
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), k, m, 33);
      break;
    case 1: 
      paramSpannableStringBuilder.setSpan(new StyleSpan(2), k, m, 33);
      break;
    case 0: 
      paramSpannableStringBuilder.setSpan(new StyleSpan(1), k, m, 33);
    }
    paramList2.clear();
    getApplicableStyles(paramList1, paramString, paramStartTag, paramList2);
    int n = paramList2.size();
    i = j;
    while (i < n)
    {
      applyStyleToText(paramSpannableStringBuilder, getstyle, k, m);
      i += 1;
    }
  }
  
  private static void applyStyleToText(SpannableStringBuilder paramSpannableStringBuilder, WebvttCssStyle paramWebvttCssStyle, int paramInt1, int paramInt2)
  {
    if (paramWebvttCssStyle == null) {
      return;
    }
    if (paramWebvttCssStyle.getStyle() != -1) {
      paramSpannableStringBuilder.setSpan(new StyleSpan(paramWebvttCssStyle.getStyle()), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.isLinethrough()) {
      paramSpannableStringBuilder.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.isUnderline()) {
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.hasFontColor()) {
      paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramWebvttCssStyle.getFontColor()), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.hasBackgroundColor()) {
      paramSpannableStringBuilder.setSpan(new BackgroundColorSpan(paramWebvttCssStyle.getBackgroundColor()), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.getFontFamily() != null) {
      paramSpannableStringBuilder.setSpan(new TypefaceSpan(paramWebvttCssStyle.getFontFamily()), paramInt1, paramInt2, 33);
    }
    if (paramWebvttCssStyle.getTextAlign() != null) {
      paramSpannableStringBuilder.setSpan(new AlignmentSpan.Standard(paramWebvttCssStyle.getTextAlign()), paramInt1, paramInt2, 33);
    }
    switch (paramWebvttCssStyle.getFontSizeUnit())
    {
    default: 
      return;
    case 3: 
      paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize() / 100.0F), paramInt1, paramInt2, 33);
      return;
    case 2: 
      paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize()), paramInt1, paramInt2, 33);
      return;
    }
    paramSpannableStringBuilder.setSpan(new AbsoluteSizeSpan((int)paramWebvttCssStyle.getFontSize(), true), paramInt1, paramInt2, 33);
  }
  
  private static int findEndOfTag(String paramString, int paramInt)
  {
    paramInt = paramString.indexOf('>', paramInt);
    if (paramInt == -1) {
      return paramString.length();
    }
    return paramInt + 1;
  }
  
  private static void getApplicableStyles(List paramList1, String paramString, StartTag paramStartTag, List paramList2)
  {
    int j = paramList1.size();
    int i = 0;
    while (i < j)
    {
      WebvttCssStyle localWebvttCssStyle = (WebvttCssStyle)paramList1.get(i);
      int k = localWebvttCssStyle.getSpecificityScore(paramString, name, classes, voice);
      if (k > 0) {
        paramList2.add(new StyleMatch(k, localWebvttCssStyle));
      }
      i += 1;
    }
    Collections.sort(paramList2);
  }
  
  private static String getTagName(String paramString)
  {
    paramString = paramString.trim();
    if (paramString.isEmpty()) {
      return null;
    }
    return Util.splitAtFirst(paramString, "[ \\.]")[0];
  }
  
  private static boolean isSupportedTag(String paramString)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 3314158: 
      if (paramString.equals("lang")) {
        i = 3;
      }
      break;
    case 118: 
      if (paramString.equals("v")) {
        i = 5;
      }
      break;
    case 117: 
      if (paramString.equals("u")) {
        i = 4;
      }
      break;
    case 105: 
      if (paramString.equals("i")) {
        i = 2;
      }
      break;
    case 99: 
      if (paramString.equals("c")) {
        i = 1;
      }
      break;
    case 98: 
      if (paramString.equals("b")) {
        i = 0;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean parseCue(String paramString, Matcher paramMatcher, ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, StringBuilder paramStringBuilder, List paramList)
  {
    try
    {
      paramBuilder.setStartTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(2)));
      parseCueSettingsList(paramMatcher.group(3), paramBuilder);
      paramStringBuilder.setLength(0);
      for (;;)
      {
        paramMatcher = paramParsableByteArray.readLine();
        if (TextUtils.isEmpty(paramMatcher)) {
          break;
        }
        if (paramStringBuilder.length() > 0) {
          paramStringBuilder.append("\n");
        }
        paramStringBuilder.append(paramMatcher.trim());
      }
      parseCueText(paramString, paramStringBuilder.toString(), paramBuilder, paramList);
      return true;
    }
    catch (NumberFormatException paramString)
    {
      for (;;) {}
    }
    paramString = new StringBuilder();
    paramString.append("Skipping cue with bad header: ");
    paramString.append(paramMatcher.group());
    Log.w("WebvttCueParser", paramString.toString());
    return false;
  }
  
  static void parseCueSettingsList(String paramString, WebvttCue.Builder paramBuilder)
  {
    paramString = CUE_SETTING_PATTERN.matcher(paramString);
    while (paramString.find())
    {
      Object localObject = paramString.group(1);
      String str = paramString.group(2);
      try
      {
        boolean bool = "line".equals(localObject);
        if (bool)
        {
          parseLineAttribute(str, paramBuilder);
          continue;
        }
        bool = "align".equals(localObject);
        if (bool)
        {
          paramBuilder.setTextAlignment(parseTextAlignment(str));
          continue;
        }
        bool = "position".equals(localObject);
        if (bool)
        {
          parsePositionAttribute(str, paramBuilder);
          continue;
        }
        bool = "size".equals(localObject);
        if (bool)
        {
          paramBuilder.setWidth(WebvttParserUtil.parsePercentage(str));
          continue;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Unknown cue setting ");
        localStringBuilder.append((String)localObject);
        localStringBuilder.append(":");
        localStringBuilder.append(str);
        Log.w("WebvttCueParser", localStringBuilder.toString());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        for (;;) {}
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Skipping bad cue setting: ");
      ((StringBuilder)localObject).append(paramString.group());
      Log.w("WebvttCueParser", ((StringBuilder)localObject).toString());
    }
  }
  
  static void parseCueText(String paramString1, String paramString2, WebvttCue.Builder paramBuilder, List paramList)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    ArrayDeque localArrayDeque = new ArrayDeque();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < paramString2.length())
    {
      char c = paramString2.charAt(i);
      int k;
      int j;
      int m;
      if (c != '&')
      {
        if (c != '<')
        {
          localSpannableStringBuilder.append(c);
          i += 1;
        }
        else
        {
          k = i + 1;
          if (k >= paramString2.length())
          {
            i = k;
          }
          else
          {
            j = paramString2.charAt(k);
            int n = 1;
            if (j == 47) {
              j = 1;
            } else {
              j = 0;
            }
            int i1 = findEndOfTag(paramString2, k);
            k = i1;
            int i2 = i1 - 2;
            if (paramString2.charAt(i2) == '/') {
              m = 1;
            } else {
              m = 0;
            }
            if (j != 0) {
              n = 2;
            }
            if (m != 0) {
              i1 = i2;
            } else {
              i1 -= 1;
            }
            Object localObject = paramString2.substring(i + n, i1);
            String str = getTagName((String)localObject);
            i = k;
            if (str != null) {
              if (!isSupportedTag(str))
              {
                i = k;
              }
              else if (j != 0)
              {
                do
                {
                  if (localArrayDeque.isEmpty())
                  {
                    i = k;
                    break;
                  }
                  localObject = (StartTag)localArrayDeque.pop();
                  applySpansForTag(paramString1, (StartTag)localObject, localSpannableStringBuilder, paramList, localArrayList);
                } while (!name.equals(str));
                i = k;
              }
              else
              {
                i = k;
                if (m == 0)
                {
                  localArrayDeque.push(StartTag.buildStartTag((String)localObject, localSpannableStringBuilder.length()));
                  i = k;
                }
              }
            }
          }
        }
      }
      else
      {
        j = i + 1;
        m = paramString2.indexOf(';', j);
        i = m;
        k = paramString2.indexOf(' ', j);
        if (m == -1) {
          i = k;
        } else if (k != -1) {
          i = Math.min(m, k);
        }
        if (i != -1)
        {
          applyEntity(paramString2.substring(j, i), localSpannableStringBuilder);
          if (i == k) {
            localSpannableStringBuilder.append(" ");
          }
          i += 1;
        }
        else
        {
          localSpannableStringBuilder.append(c);
          i = j;
        }
      }
    }
    while (!localArrayDeque.isEmpty()) {
      applySpansForTag(paramString1, (StartTag)localArrayDeque.pop(), localSpannableStringBuilder, paramList, localArrayList);
    }
    applySpansForTag(paramString1, StartTag.buildWholeCueVirtualTag(), localSpannableStringBuilder, paramList, localArrayList);
    paramBuilder.setText(localSpannableStringBuilder);
  }
  
  private static void parseLineAttribute(String paramString, WebvttCue.Builder paramBuilder)
    throws NumberFormatException
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setLineAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    else
    {
      paramBuilder.setLineAnchor(Integer.MIN_VALUE);
    }
    if (paramString.endsWith("%"))
    {
      paramBuilder.setLine(WebvttParserUtil.parsePercentage(paramString)).setLineType(0);
      return;
    }
    int j = Integer.parseInt(paramString);
    i = j;
    if (j < 0) {
      i = j - 1;
    }
    paramBuilder.setLine(i).setLineType(1);
  }
  
  private static int parsePositionAnchor(String paramString)
  {
    int i = paramString.hashCode();
    if (i != -1364013995)
    {
      if (i != -1074341483)
      {
        if (i != 100571)
        {
          if ((i == 109757538) && (paramString.equals("start")))
          {
            i = 0;
            break label98;
          }
        }
        else if (paramString.equals("end"))
        {
          i = 3;
          break label98;
        }
      }
      else if (paramString.equals("middle"))
      {
        i = 2;
        break label98;
      }
    }
    else if (paramString.equals("center"))
    {
      i = 1;
      break label98;
    }
    i = -1;
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid anchor value: ");
      localStringBuilder.append(paramString);
      Log.w("WebvttCueParser", localStringBuilder.toString());
      return Integer.MIN_VALUE;
    case 3: 
      return 2;
    case 1: 
    case 2: 
      label98:
      return 1;
    }
    return 0;
  }
  
  private static void parsePositionAttribute(String paramString, WebvttCue.Builder paramBuilder)
    throws NumberFormatException
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setPositionAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    else
    {
      paramBuilder.setPositionAnchor(Integer.MIN_VALUE);
    }
    paramBuilder.setPosition(WebvttParserUtil.parsePercentage(paramString));
  }
  
  private static Layout.Alignment parseTextAlignment(String paramString)
  {
    switch (paramString.hashCode())
    {
    default: 
      break;
    case 109757538: 
      if (paramString.equals("start")) {
        i = 0;
      }
      break;
    case 108511772: 
      if (paramString.equals("right")) {
        i = 5;
      }
      break;
    case 3317767: 
      if (paramString.equals("left")) {
        i = 1;
      }
      break;
    case 100571: 
      if (paramString.equals("end")) {
        i = 4;
      }
      break;
    case -1074341483: 
      if (paramString.equals("middle")) {
        i = 3;
      }
      break;
    case -1364013995: 
      if (paramString.equals("center")) {
        i = 2;
      }
      break;
    }
    int i = -1;
    switch (i)
    {
    default: 
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Invalid alignment value: ");
      localStringBuilder.append(paramString);
      Log.w("WebvttCueParser", localStringBuilder.toString());
      return null;
    case 4: 
    case 5: 
      return Layout.Alignment.ALIGN_OPPOSITE;
    case 2: 
    case 3: 
      return Layout.Alignment.ALIGN_CENTER;
    }
    return Layout.Alignment.ALIGN_NORMAL;
  }
  
  public boolean parseCue(ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, List paramList)
  {
    String str = paramParsableByteArray.readLine();
    if (str == null) {
      return false;
    }
    Object localObject = CUE_HEADER_PATTERN.matcher(str);
    if (((Matcher)localObject).matches()) {
      return parseCue(null, (Matcher)localObject, paramParsableByteArray, paramBuilder, textBuilder, paramList);
    }
    localObject = paramParsableByteArray.readLine();
    if (localObject == null) {
      return false;
    }
    localObject = CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
    if (((Matcher)localObject).matches()) {
      return parseCue(str.trim(), (Matcher)localObject, paramParsableByteArray, paramBuilder, textBuilder, paramList);
    }
    return false;
  }
  
  private static final class StartTag
  {
    private static final String[] NO_CLASSES = new String[0];
    public final String[] classes;
    public final String name;
    public final int position;
    public final String voice;
    
    private StartTag(String paramString1, int paramInt, String paramString2, String[] paramArrayOfString)
    {
      position = paramInt;
      name = paramString1;
      voice = paramString2;
      classes = paramArrayOfString;
    }
    
    public static StartTag buildStartTag(String paramString, int paramInt)
    {
      Object localObject = paramString.trim();
      paramString = (String)localObject;
      if (((String)localObject).isEmpty()) {
        return null;
      }
      int i = ((String)localObject).indexOf(" ");
      if (i == -1)
      {
        str = "";
        localObject = paramString;
        paramString = str;
      }
      else
      {
        paramString = ((String)localObject).substring(i).trim();
        localObject = ((String)localObject).substring(0, i);
      }
      localObject = Util.split((String)localObject, "\\.");
      String str = localObject[0];
      if (localObject.length > 1) {
        localObject = (String[])Arrays.copyOfRange((Object[])localObject, 1, localObject.length);
      } else {
        localObject = NO_CLASSES;
      }
      return new StartTag(str, paramInt, paramString, (String[])localObject);
    }
    
    public static StartTag buildWholeCueVirtualTag()
    {
      return new StartTag("", 0, "", new String[0]);
    }
  }
  
  private static final class StyleMatch
    implements Comparable<StyleMatch>
  {
    public final int score;
    public final WebvttCssStyle style;
    
    public StyleMatch(int paramInt, WebvttCssStyle paramWebvttCssStyle)
    {
      score = paramInt;
      style = paramWebvttCssStyle;
    }
    
    public int compareTo(StyleMatch paramStyleMatch)
    {
      return score - score;
    }
  }
}
