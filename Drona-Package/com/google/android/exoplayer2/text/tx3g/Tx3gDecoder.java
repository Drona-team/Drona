package com.google.android.exoplayer2.text.tx3g;

import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.SimpleSubtitleDecoder;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleDecoderException;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.nio.charset.Charset;
import java.util.List;

public final class Tx3gDecoder
  extends SimpleSubtitleDecoder
{
  private static final char BOM_UTF16_BE = '?';
  private static final char BOM_UTF16_LE = '?';
  private static final int DEFAULT_COLOR = -1;
  private static final int DEFAULT_FONT_FACE = 0;
  private static final String DEFAULT_FONT_FAMILY = "sans-serif";
  private static final float DEFAULT_VERTICAL_PLACEMENT = 0.85F;
  private static final int FONT_FACE_BOLD = 1;
  private static final int FONT_FACE_ITALIC = 2;
  private static final int FONT_FACE_UNDERLINE = 4;
  private static final int SIZE_ATOM_HEADER = 8;
  private static final int SIZE_BOM_UTF16 = 2;
  private static final int SIZE_SHORT = 2;
  private static final int SIZE_STYLE_RECORD = 12;
  private static final int SPAN_PRIORITY_HIGH = 0;
  private static final int SPAN_PRIORITY_LOW = 16711680;
  private static final String TX3G_SERIF = "Serif";
  private static final int TYPE_STYL = Util.getIntegerCodeForString("styl");
  private static final int TYPE_TBOX = Util.getIntegerCodeForString("tbox");
  private int calculatedVideoTrackHeight;
  private boolean customVerticalPlacement;
  private int defaultColorRgba;
  private int defaultFontFace;
  private String defaultFontFamily;
  private float defaultVerticalPlacement;
  private final ParsableByteArray parsableByteArray = new ParsableByteArray();
  
  public Tx3gDecoder(List paramList)
  {
    super("Tx3gDecoder");
    decodeInitializationData(paramList);
  }
  
  private void applyStyleRecord(ParsableByteArray paramParsableByteArray, SpannableStringBuilder paramSpannableStringBuilder)
    throws SubtitleDecoderException
  {
    boolean bool;
    if (paramParsableByteArray.bytesLeft() >= 12) {
      bool = true;
    } else {
      bool = false;
    }
    assertTrue(bool);
    int i = paramParsableByteArray.readUnsignedShort();
    int j = paramParsableByteArray.readUnsignedShort();
    paramParsableByteArray.skipBytes(2);
    int k = paramParsableByteArray.readUnsignedByte();
    paramParsableByteArray.skipBytes(1);
    int m = paramParsableByteArray.readInt();
    attachFontFace(paramSpannableStringBuilder, k, defaultFontFace, i, j, 0);
    attachColor(paramSpannableStringBuilder, m, defaultColorRgba, i, j, 0);
  }
  
  private static void assertTrue(boolean paramBoolean)
    throws SubtitleDecoderException
  {
    if (paramBoolean) {
      return;
    }
    throw new SubtitleDecoderException("Unexpected subtitle format.");
  }
  
  private static void attachColor(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt1 != paramInt2) {
      paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramInt1 >>> 8 | (paramInt1 & 0xFF) << 24), paramInt3, paramInt4, paramInt5 | 0x21);
    }
  }
  
  private static void attachFontFace(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt1 != paramInt2)
    {
      int j = paramInt5 | 0x21;
      int i = 1;
      if ((paramInt1 & 0x1) != 0) {
        paramInt2 = 1;
      } else {
        paramInt2 = 0;
      }
      if ((paramInt1 & 0x2) != 0) {
        paramInt5 = 1;
      } else {
        paramInt5 = 0;
      }
      if (paramInt2 != 0)
      {
        if (paramInt5 != 0) {
          paramSpannableStringBuilder.setSpan(new StyleSpan(3), paramInt3, paramInt4, j);
        } else {
          paramSpannableStringBuilder.setSpan(new StyleSpan(1), paramInt3, paramInt4, j);
        }
      }
      else if (paramInt5 != 0) {
        paramSpannableStringBuilder.setSpan(new StyleSpan(2), paramInt3, paramInt4, j);
      }
      if ((paramInt1 & 0x4) != 0) {
        paramInt1 = i;
      } else {
        paramInt1 = 0;
      }
      if (paramInt1 != 0) {
        paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt3, paramInt4, j);
      }
      if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt5 == 0)) {
        paramSpannableStringBuilder.setSpan(new StyleSpan(0), paramInt3, paramInt4, j);
      }
    }
  }
  
  private static void attachFontFamily(SpannableStringBuilder paramSpannableStringBuilder, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramString1 != paramString2) {
      paramSpannableStringBuilder.setSpan(new TypefaceSpan(paramString1), paramInt1, paramInt2, paramInt3 | 0x21);
    }
  }
  
  private void decodeInitializationData(List paramList)
  {
    boolean bool = false;
    if ((paramList != null) && (paramList.size() == 1) && ((((byte[])paramList.get(0)).length == 48) || (((byte[])paramList.get(0)).length == 53)))
    {
      byte[] arrayOfByte = (byte[])paramList.get(0);
      defaultFontFace = arrayOfByte[24];
      defaultColorRgba = ((arrayOfByte[26] & 0xFF) << 24 | (arrayOfByte[27] & 0xFF) << 16 | (arrayOfByte[28] & 0xFF) << 8 | arrayOfByte[29] & 0xFF);
      if ("Serif".equals(Util.fromUtf8Bytes(arrayOfByte, 43, arrayOfByte.length - 43))) {
        paramList = "serif";
      } else {
        paramList = "sans-serif";
      }
      defaultFontFamily = paramList;
      calculatedVideoTrackHeight = (arrayOfByte[25] * 20);
      if ((arrayOfByte[0] & 0x20) != 0) {
        bool = true;
      }
      customVerticalPlacement = bool;
      if (customVerticalPlacement)
      {
        int i = arrayOfByte[10];
        defaultVerticalPlacement = ((arrayOfByte[11] & 0xFF | (i & 0xFF) << 8) / calculatedVideoTrackHeight);
        defaultVerticalPlacement = Util.constrainValue(defaultVerticalPlacement, 0.0F, 0.95F);
        return;
      }
      defaultVerticalPlacement = 0.85F;
      return;
    }
    defaultFontFace = 0;
    defaultColorRgba = -1;
    defaultFontFamily = "sans-serif";
    customVerticalPlacement = false;
    defaultVerticalPlacement = 0.85F;
  }
  
  private static String readSubtitleText(ParsableByteArray paramParsableByteArray)
    throws SubtitleDecoderException
  {
    boolean bool;
    if (paramParsableByteArray.bytesLeft() >= 2) {
      bool = true;
    } else {
      bool = false;
    }
    assertTrue(bool);
    int i = paramParsableByteArray.readUnsignedShort();
    if (i == 0) {
      return "";
    }
    if (paramParsableByteArray.bytesLeft() >= 2)
    {
      int j = paramParsableByteArray.peekChar();
      if ((j == 65279) || (j == 65534)) {
        return paramParsableByteArray.readString(i, Charset.forName("UTF-16"));
      }
    }
    return paramParsableByteArray.readString(i, Charset.forName("UTF-8"));
  }
  
  protected Subtitle decode(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
    throws SubtitleDecoderException
  {
    parsableByteArray.reset(paramArrayOfByte, paramInt);
    paramArrayOfByte = readSubtitleText(parsableByteArray);
    if (paramArrayOfByte.isEmpty()) {
      return Tx3gSubtitle.EMPTY;
    }
    paramArrayOfByte = new SpannableStringBuilder(paramArrayOfByte);
    attachFontFace(paramArrayOfByte, defaultFontFace, 0, 0, paramArrayOfByte.length(), 16711680);
    attachColor(paramArrayOfByte, defaultColorRgba, -1, 0, paramArrayOfByte.length(), 16711680);
    attachFontFamily(paramArrayOfByte, defaultFontFamily, "sans-serif", 0, paramArrayOfByte.length(), 16711680);
    float f2;
    for (float f1 = defaultVerticalPlacement; parsableByteArray.bytesLeft() >= 8; f1 = f2)
    {
      int i = parsableByteArray.getPosition();
      int j = parsableByteArray.readInt();
      int k = parsableByteArray.readInt();
      int m = TYPE_STYL;
      boolean bool = false;
      paramInt = 0;
      paramBoolean = true;
      if (k == m)
      {
        if (parsableByteArray.bytesLeft() < 2) {
          paramBoolean = false;
        }
        assertTrue(paramBoolean);
        k = parsableByteArray.readUnsignedShort();
        for (;;)
        {
          f2 = f1;
          if (paramInt >= k) {
            break;
          }
          applyStyleRecord(parsableByteArray, paramArrayOfByte);
          paramInt += 1;
        }
      }
      f2 = f1;
      if (k == TYPE_TBOX)
      {
        f2 = f1;
        if (customVerticalPlacement)
        {
          paramBoolean = bool;
          if (parsableByteArray.bytesLeft() >= 2) {
            paramBoolean = true;
          }
          assertTrue(paramBoolean);
          f2 = Util.constrainValue(parsableByteArray.readUnsignedShort() / calculatedVideoTrackHeight, 0.0F, 0.95F);
        }
      }
      parsableByteArray.setPosition(i + j);
    }
    return new Tx3gSubtitle(new Cue(paramArrayOfByte, null, f1, 0, 0, Float.MIN_VALUE, Integer.MIN_VALUE, Float.MIN_VALUE));
  }
}
