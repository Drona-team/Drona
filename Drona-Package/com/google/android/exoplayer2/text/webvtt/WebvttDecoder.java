package com.google.android.exoplayer2.text.webvtt;

import android.text.TextUtils;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.ArrayList;
import java.util.List;

public final class WebvttDecoder
  extends SimpleSubtitleDecoder
{
  private static final String COMMENT_START = "NOTE";
  private static final int EVENT_COMMENT = 1;
  private static final int EVENT_CUE = 3;
  private static final int EVENT_END_OF_FILE = 0;
  private static final int EVENT_NONE = -1;
  private static final int EVENT_STYLE_BLOCK = 2;
  private static final String STYLE_START = "STYLE";
  private final CssParser cssParser = new CssParser();
  private final WebvttCueParser cueParser = new WebvttCueParser();
  private final List<WebvttCssStyle> definedStyles = new ArrayList();
  private final ParsableByteArray parsableWebvttData = new ParsableByteArray();
  private final WebvttCue.Builder webvttCueBuilder = new WebvttCue.Builder();
  
  public WebvttDecoder()
  {
    super("WebvttDecoder");
  }
  
  private static int getNextEvent(ParsableByteArray paramParsableByteArray)
  {
    int i = -1;
    int j = 0;
    while (i == -1)
    {
      j = paramParsableByteArray.getPosition();
      String str = paramParsableByteArray.readLine();
      if (str == null) {
        i = 0;
      } else if ("STYLE".equals(str)) {
        i = 2;
      } else if (str.startsWith("NOTE")) {
        i = 1;
      } else {
        i = 3;
      }
    }
    paramParsableByteArray.setPosition(j);
    return i;
  }
  
  private static void skipComment(ParsableByteArray paramParsableByteArray)
  {
    while (!TextUtils.isEmpty(paramParsableByteArray.readLine())) {}
  }
  
  protected WebvttSubtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException
  {
    parsableWebvttData.reset(paramArrayOfByte, paramInt);
    webvttCueBuilder.reset();
    definedStyles.clear();
    paramArrayOfByte = parsableWebvttData;
    try
    {
      WebvttParserUtil.validateWebvttHeaderLine(paramArrayOfByte);
      while (!TextUtils.isEmpty(parsableWebvttData.readLine())) {}
      paramArrayOfByte = new ArrayList();
      for (;;)
      {
        paramInt = getNextEvent(parsableWebvttData);
        if (paramInt == 0) {
          break;
        }
        if (paramInt == 1)
        {
          skipComment(parsableWebvttData);
        }
        else if (paramInt == 2)
        {
          if (paramArrayOfByte.isEmpty())
          {
            parsableWebvttData.readLine();
            WebvttCssStyle localWebvttCssStyle = cssParser.parseBlock(parsableWebvttData);
            if (localWebvttCssStyle != null) {
              definedStyles.add(localWebvttCssStyle);
            }
          }
          else
          {
            throw new SubtitleDecoderException("A style block was found after the first cue.");
          }
        }
        else if ((paramInt == 3) && (cueParser.parseCue(parsableWebvttData, webvttCueBuilder, definedStyles)))
        {
          paramArrayOfByte.add(webvttCueBuilder.build());
          webvttCueBuilder.reset();
        }
      }
      return new WebvttSubtitle(paramArrayOfByte);
    }
    catch (ParserException paramArrayOfByte)
    {
      throw new SubtitleDecoderException(paramArrayOfByte);
    }
  }
}
