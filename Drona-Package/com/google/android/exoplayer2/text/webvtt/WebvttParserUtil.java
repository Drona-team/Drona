package com.google.android.exoplayer2.text.webvtt;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class WebvttParserUtil
{
  private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
  private static final String WEBVTT_HEADER = "WEBVTT";
  
  private WebvttParserUtil() {}
  
  public static Matcher findNextCueHeader(ParsableByteArray paramParsableByteArray)
  {
    Object localObject;
    do
    {
      localObject = paramParsableByteArray.readLine();
      if (localObject == null) {
        break;
      }
      if (COMMENT.matcher((CharSequence)localObject).matches()) {
        for (;;)
        {
          localObject = paramParsableByteArray.readLine();
          if ((localObject == null) || (((String)localObject).isEmpty())) {
            break;
          }
        }
      }
      localObject = WebvttCueParser.CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
    } while (!((Matcher)localObject).matches());
    return localObject;
    return null;
  }
  
  public static boolean isWebvttHeaderLine(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray = paramParsableByteArray.readLine();
    return (paramParsableByteArray != null) && (paramParsableByteArray.startsWith("WEBVTT"));
  }
  
  public static float parsePercentage(String paramString)
    throws NumberFormatException
  {
    if (paramString.endsWith("%")) {
      return Float.parseFloat(paramString.substring(0, paramString.length() - 1)) / 100.0F;
    }
    throw new NumberFormatException("Percentages must end with %");
  }
  
  public static long parseTimestampUs(String paramString)
    throws NumberFormatException
  {
    paramString = Util.splitAtFirst(paramString, "\\.");
    int i = 0;
    String[] arrayOfString = Util.split(paramString[0], ":");
    int j = arrayOfString.length;
    long l1 = 0L;
    while (i < j)
    {
      l1 = l1 * 60L + Long.parseLong(arrayOfString[i]);
      i += 1;
    }
    long l2 = l1 * 1000L;
    l1 = l2;
    if (paramString.length == 2) {
      l1 = l2 + Long.parseLong(paramString[1]);
    }
    return l1 * 1000L;
  }
  
  public static void validateWebvttHeaderLine(ParsableByteArray paramParsableByteArray)
    throws ParserException
  {
    int i = paramParsableByteArray.getPosition();
    if (isWebvttHeaderLine(paramParsableByteArray)) {
      return;
    }
    paramParsableByteArray.setPosition(i);
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Expected WEBVTT. Got ");
    localStringBuilder.append(paramParsableByteArray.readLine());
    throw new ParserException(localStringBuilder.toString());
  }
}
