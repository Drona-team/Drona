package com.google.android.exoplayer2.text.subrip;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.LongArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SubripDecoder
  extends SimpleSubtitleDecoder
{
  private static final String ALIGN_BOTTOM_LEFT = "{\\an1}";
  private static final String ALIGN_BOTTOM_MID = "{\\an2}";
  private static final String ALIGN_BOTTOM_RIGHT = "{\\an3}";
  private static final String ALIGN_MID_LEFT = "{\\an4}";
  private static final String ALIGN_MID_MID = "{\\an5}";
  private static final String ALIGN_MID_RIGHT = "{\\an6}";
  private static final String ALIGN_TOP_LEFT = "{\\an7}";
  private static final String ALIGN_TOP_MID = "{\\an8}";
  private static final String ALIGN_TOP_RIGHT = "{\\an9}";
  static final float END_FRACTION = 0.92F;
  static final float MID_FRACTION = 0.5F;
  private static final String PAGE_KEY = "SubripDecoder";
  static final float START_FRACTION = 0.08F;
  private static final String SUBRIP_ALIGNMENT_TAG = "\\{\\\\an[1-9]\\}";
  private static final Pattern SUBRIP_TAG_PATTERN = Pattern.compile("\\{\\\\.*?\\}");
  private static final String SUBRIP_TIMECODE = "(?:(\\d+):)?(\\d+):(\\d+),(\\d+)";
  private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))\\s*-->\\s*((?:(\\d+):)?(\\d+):(\\d+),(\\d+))?\\s*");
  private final ArrayList<String> tags = new ArrayList();
  private final StringBuilder textBuilder = new StringBuilder();
  
  public SubripDecoder()
  {
    super("SubripDecoder");
  }
  
  private Cue buildCue(Spanned paramSpanned, String paramString)
  {
    if (paramString == null) {
      return new Cue(paramSpanned);
    }
    int i = paramString.hashCode();
    int k = 5;
    switch (i)
    {
    default: 
      break;
    case -685620462: 
      if (paramString.equals("{\\an9}")) {
        i = 5;
      }
      break;
    case -685620493: 
      if (paramString.equals("{\\an8}")) {
        i = 8;
      }
      break;
    case -685620524: 
      if (paramString.equals("{\\an7}")) {
        i = 2;
      }
      break;
    case -685620555: 
      if (paramString.equals("{\\an6}")) {
        i = 4;
      }
      break;
    case -685620586: 
      if (paramString.equals("{\\an5}")) {
        i = 7;
      }
      break;
    case -685620617: 
      if (paramString.equals("{\\an4}")) {
        i = 1;
      }
      break;
    case -685620648: 
      if (paramString.equals("{\\an3}")) {
        i = 3;
      }
      break;
    case -685620679: 
      if (paramString.equals("{\\an2}")) {
        i = 6;
      }
      break;
    case -685620710: 
      if (paramString.equals("{\\an1}")) {
        i = 0;
      }
      break;
    }
    i = -1;
    int j;
    switch (i)
    {
    default: 
      j = 1;
      break;
    case 3: 
    case 4: 
    case 5: 
      j = 2;
      break;
    case 0: 
    case 1: 
    case 2: 
      j = 0;
    }
    switch (paramString.hashCode())
    {
    default: 
      break;
    case -685620462: 
      if (paramString.equals("{\\an9}")) {
        i = k;
      }
      break;
    case -685620493: 
      if (paramString.equals("{\\an8}")) {
        i = 4;
      }
      break;
    case -685620524: 
      if (paramString.equals("{\\an7}")) {
        i = 3;
      }
      break;
    case -685620555: 
      if (paramString.equals("{\\an6}")) {
        i = 8;
      }
      break;
    case -685620586: 
      if (paramString.equals("{\\an5}")) {
        i = 7;
      }
      break;
    case -685620617: 
      if (paramString.equals("{\\an4}")) {
        i = 6;
      }
      break;
    case -685620648: 
      if (paramString.equals("{\\an3}")) {
        i = 2;
      }
      break;
    case -685620679: 
      if (paramString.equals("{\\an2}")) {
        i = 1;
      }
      break;
    case -685620710: 
      if (paramString.equals("{\\an1}")) {
        i = 0;
      }
      break;
    }
    i = -1;
    switch (i)
    {
    default: 
      i = 1;
      break;
    case 3: 
    case 4: 
    case 5: 
      i = 0;
      break;
    case 0: 
    case 1: 
    case 2: 
      i = 2;
    }
    return new Cue(paramSpanned, null, getFractionalPositionForAnchorType(i), 0, i, getFractionalPositionForAnchorType(j), j, Float.MIN_VALUE);
  }
  
  static float getFractionalPositionForAnchorType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 0.92F;
    case 1: 
      return 0.5F;
    }
    return 0.08F;
  }
  
  private static long parseTimecode(Matcher paramMatcher, int paramInt)
  {
    return (Long.parseLong(paramMatcher.group(paramInt + 1)) * 60L * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 2)) * 60L * 1000L + Long.parseLong(paramMatcher.group(paramInt + 3)) * 1000L + Long.parseLong(paramMatcher.group(paramInt + 4))) * 1000L;
  }
  
  private String processLine(String paramString, ArrayList paramArrayList)
  {
    Object localObject = paramString.trim();
    paramString = new StringBuilder((String)localObject);
    localObject = SUBRIP_TAG_PATTERN.matcher((CharSequence)localObject);
    int i = 0;
    while (((Matcher)localObject).find())
    {
      String str = ((Matcher)localObject).group();
      paramArrayList.add(str);
      int j = ((Matcher)localObject).start() - i;
      int k = str.length();
      paramString.replace(j, j + k, "");
      i += k;
    }
    return paramString.toString();
  }
  
  protected SubripSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(paramArrayOfByte, paramInt);
    for (;;)
    {
      paramArrayOfByte = localParsableByteArray.readLine();
      if ((paramArrayOfByte == null) || (paramArrayOfByte.length() != 0)) {}
      try
      {
        Integer.parseInt(paramArrayOfByte);
        paramArrayOfByte = localParsableByteArray.readLine();
        if (paramArrayOfByte == null)
        {
          Log.w("SubripDecoder", "Unexpected end");
          break;
        }
        localObject = SUBRIP_TIMING_LINE.matcher(paramArrayOfByte);
        if (((Matcher)localObject).matches())
        {
          paramInt = 1;
          localLongArray.add(parseTimecode((Matcher)localObject, 1));
          paramBoolean = TextUtils.isEmpty(((Matcher)localObject).group(6));
          int i = 0;
          if (!paramBoolean) {
            localLongArray.add(parseTimecode((Matcher)localObject, 6));
          } else {
            paramInt = 0;
          }
          textBuilder.setLength(0);
          tags.clear();
          for (;;)
          {
            paramArrayOfByte = localParsableByteArray.readLine();
            if (TextUtils.isEmpty(paramArrayOfByte)) {
              break;
            }
            if (textBuilder.length() > 0) {
              textBuilder.append("<br>");
            }
            textBuilder.append(processLine(paramArrayOfByte, tags));
          }
          localObject = Html.fromHtml(textBuilder.toString());
          while (i < tags.size())
          {
            paramArrayOfByte = (String)tags.get(i);
            if (paramArrayOfByte.matches("\\{\\\\an[1-9]\\}")) {
              break label267;
            }
            i += 1;
          }
          paramArrayOfByte = null;
          label267:
          localArrayList.add(buildCue((Spanned)localObject, paramArrayOfByte));
          if (paramInt == 0) {
            continue;
          }
          localArrayList.add(null);
          continue;
        }
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Skipping invalid timing: ");
        ((StringBuilder)localObject).append(paramArrayOfByte);
        Log.w("SubripDecoder", ((StringBuilder)localObject).toString());
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Object localObject;
        for (;;) {}
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Skipping invalid index: ");
      ((StringBuilder)localObject).append(paramArrayOfByte);
      Log.w("SubripDecoder", ((StringBuilder)localObject).toString());
    }
    paramArrayOfByte = new Cue[localArrayList.size()];
    localArrayList.toArray(paramArrayOfByte);
    return new SubripSubtitle(paramArrayOfByte, localLongArray.toArray());
  }
}
