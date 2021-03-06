package com.google.android.exoplayer2.text.ClickListeners;

import android.text.Layout.Alignment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Cea608Decoder
  extends CeaDecoder
{
  private static final int[] BASIC_CHARACTER_SET = { 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, 250, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632 };
  private static final int CC_FIELD_FLAG = 1;
  private static final byte CC_IMPLICIT_DATA_HEADER = -4;
  private static final int CC_MODE_PAINT_ON = 3;
  private static final int CC_MODE_POP_ON = 2;
  private static final int CC_MODE_ROLL_UP = 1;
  private static final int CC_MODE_UNKNOWN = 0;
  private static final int CC_TYPE_FLAG = 2;
  private static final int CC_VALID_608_ID = 4;
  private static final int CC_VALID_FLAG = 4;
  private static final int[] COLUMN_INDICES;
  private static final byte CTRL_BACKSPACE = 33;
  private static final byte CTRL_CARRIAGE_RETURN = 45;
  private static final byte CTRL_DELETE_TO_END_OF_ROW = 36;
  private static final byte CTRL_END_OF_CAPTION = 47;
  private static final byte CTRL_ERASE_DISPLAYED_MEMORY = 44;
  private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = 46;
  private static final byte CTRL_RESUME_CAPTION_LOADING = 32;
  private static final byte CTRL_RESUME_DIRECT_CAPTIONING = 41;
  private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = 37;
  private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = 38;
  private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = 39;
  private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
  private static final int NTSC_CC_FIELD_1 = 0;
  private static final int NTSC_CC_FIELD_2 = 1;
  private static final int[] ROW_INDICES = { 11, 1, 3, 12, 14, 5, 7, 9 };
  private static final int[] SPECIAL_CHARACTER_SET = { 174, 176, 189, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251 };
  private static final int[] SPECIAL_ES_FR_CHARACTER_SET = { 193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, 192, 194, 199, 200, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187 };
  private static final int[] SPECIAL_PT_DE_CHARACTER_SET = { 195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496 };
  private static final int[] STYLE_COLORS;
  private static final int STYLE_ITALICS = 7;
  private static final int STYLE_UNCHANGED = 8;
  private int captionMode;
  private int captionRowCount;
  private final ParsableByteArray ccData = new ParsableByteArray();
  private final ArrayList<com.google.android.exoplayer2.text.cea.Cea608Decoder.CueBuilder> cueBuilders = new ArrayList();
  private List<Cue> cues;
  private CueBuilder currentCueBuilder = new CueBuilder(0, 4);
  private List<Cue> lastCues;
  private final int packetLength;
  private byte repeatableControlCc1;
  private byte repeatableControlCc2;
  private boolean repeatableControlSet;
  private final int selectedField;
  
  static
  {
    COLUMN_INDICES = new int[] { 0, 4, 8, 12, 16, 20, 24, 28 };
    STYLE_COLORS = new int[] { -1, -16711936, -16776961, -16711681, -65536, 65280, -65281 };
  }
  
  public Cea608Decoder(String paramString, int paramInt)
  {
    int i;
    if ("application/x-mp4-cea-608".equals(paramString)) {
      i = 2;
    } else {
      i = 3;
    }
    packetLength = i;
    switch (paramInt)
    {
    default: 
      selectedField = 1;
      break;
    case 3: 
    case 4: 
      selectedField = 2;
    }
    setCaptionMode(0);
    resetCueBuilders();
  }
  
  private static char getChar(byte paramByte)
  {
    return (char)BASIC_CHARACTER_SET[((paramByte & 0x7F) - 32)];
  }
  
  private List getDisplayCues()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < cueBuilders.size())
    {
      Cue localCue = ((CueBuilder)cueBuilders.get(i)).build();
      if (localCue != null) {
        localArrayList.add(localCue);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  private static char getExtendedEsFrChar(byte paramByte)
  {
    return (char)SPECIAL_ES_FR_CHARACTER_SET[(paramByte & 0x1F)];
  }
  
  private static char getExtendedPtDeChar(byte paramByte)
  {
    return (char)SPECIAL_PT_DE_CHARACTER_SET[(paramByte & 0x1F)];
  }
  
  private static char getSpecialChar(byte paramByte)
  {
    return (char)SPECIAL_CHARACTER_SET[(paramByte & 0xF)];
  }
  
  private boolean handleCtrl(byte paramByte1, byte paramByte2)
  {
    boolean bool = isRepeatable(paramByte1);
    if (bool)
    {
      if ((repeatableControlSet) && (repeatableControlCc1 == paramByte1) && (repeatableControlCc2 == paramByte2))
      {
        repeatableControlSet = false;
        return true;
      }
      repeatableControlSet = true;
      repeatableControlCc1 = paramByte1;
      repeatableControlCc2 = paramByte2;
    }
    if (isMidrowCtrlCode(paramByte1, paramByte2))
    {
      handleMidrowCtrl(paramByte2);
      return bool;
    }
    if (isPreambleAddressCode(paramByte1, paramByte2))
    {
      handlePreambleAddressCode(paramByte1, paramByte2);
      return bool;
    }
    if (isTabCtrlCode(paramByte1, paramByte2))
    {
      currentCueBuilder.setTab(paramByte2 - 32);
      return bool;
    }
    if (isMiscCode(paramByte1, paramByte2)) {
      handleMiscCode(paramByte2);
    }
    return bool;
  }
  
  private void handleMidrowCtrl(byte paramByte)
  {
    currentCueBuilder.append(' ');
    boolean bool;
    if ((paramByte & 0x1) == 1) {
      bool = true;
    } else {
      bool = false;
    }
    currentCueBuilder.setStyle(paramByte >> 1 & 0x7, bool);
  }
  
  private void handleMiscCode(byte paramByte)
  {
    if (paramByte != 32)
    {
      if (paramByte != 41) {
        switch (paramByte)
        {
        default: 
          if (captionMode == 0) {
            return;
          }
          if (paramByte != 33)
          {
            if (paramByte == 36) {
              break;
            }
            switch (paramByte)
            {
            default: 
              return;
            case 47: 
              cues = getDisplayCues();
              resetCueBuilders();
              return;
            case 46: 
              resetCueBuilders();
              return;
            case 45: 
              if ((captionMode != 1) || (currentCueBuilder.isEmpty())) {
                break;
              }
              currentCueBuilder.rollUp();
              return;
            case 44: 
              cues = Collections.emptyList();
              if ((captionMode != 1) && (captionMode != 3)) {
                break;
              }
              resetCueBuilders();
              return;
            }
          }
          currentCueBuilder.backspace();
          return;
        case 39: 
          setCaptionMode(1);
          setCaptionRowCount(4);
          return;
        case 38: 
          setCaptionMode(1);
          setCaptionRowCount(3);
          return;
        case 37: 
          setCaptionMode(1);
          setCaptionRowCount(2);
          return;
        }
      } else {
        setCaptionMode(3);
      }
    }
    else {
      setCaptionMode(2);
    }
  }
  
  private void handlePreambleAddressCode(byte paramByte1, byte paramByte2)
  {
    byte b2 = ROW_INDICES[(paramByte1 & 0x7)];
    boolean bool = false;
    if ((paramByte2 & 0x20) != 0) {
      paramByte1 = 1;
    } else {
      paramByte1 = 0;
    }
    byte b1 = b2;
    if (paramByte1 != 0) {
      b1 = b2 + 1;
    }
    if (b1 != currentCueBuilder.getRow())
    {
      if ((captionMode != 1) && (!currentCueBuilder.isEmpty()))
      {
        currentCueBuilder = new CueBuilder(captionMode, captionRowCount);
        cueBuilders.add(currentCueBuilder);
      }
      currentCueBuilder.setRow(b1);
    }
    if ((paramByte2 & 0x10) == 16) {
      paramByte1 = 1;
    } else {
      paramByte1 = 0;
    }
    if ((paramByte2 & 0x1) == 1) {
      bool = true;
    }
    b1 = paramByte2 >> 1 & 0x7;
    CueBuilder localCueBuilder = currentCueBuilder;
    if (paramByte1 != 0) {
      paramByte2 = 8;
    } else {
      paramByte2 = b1;
    }
    localCueBuilder.setStyle(paramByte2, bool);
    if (paramByte1 != 0) {
      currentCueBuilder.setIndent(COLUMN_INDICES[b1]);
    }
  }
  
  private static boolean isMidrowCtrlCode(byte paramByte1, byte paramByte2)
  {
    return ((paramByte1 & 0xF7) == 17) && ((paramByte2 & 0xF0) == 32);
  }
  
  private static boolean isMiscCode(byte paramByte1, byte paramByte2)
  {
    return ((paramByte1 & 0xF7) == 20) && ((paramByte2 & 0xF0) == 32);
  }
  
  private static boolean isPreambleAddressCode(byte paramByte1, byte paramByte2)
  {
    return ((paramByte1 & 0xF0) == 16) && ((paramByte2 & 0xC0) == 64);
  }
  
  private static boolean isRepeatable(byte paramByte)
  {
    return (paramByte & 0xF0) == 16;
  }
  
  private static boolean isTabCtrlCode(byte paramByte1, byte paramByte2)
  {
    return ((paramByte1 & 0xF7) == 23) && (paramByte2 >= 33) && (paramByte2 <= 35);
  }
  
  private void resetCueBuilders()
  {
    currentCueBuilder.reset(captionMode);
    cueBuilders.clear();
    cueBuilders.add(currentCueBuilder);
  }
  
  private void setCaptionMode(int paramInt)
  {
    if (captionMode == paramInt) {
      return;
    }
    int i = captionMode;
    captionMode = paramInt;
    resetCueBuilders();
    if ((i == 3) || (paramInt == 1) || (paramInt == 0)) {
      cues = Collections.emptyList();
    }
  }
  
  private void setCaptionRowCount(int paramInt)
  {
    captionRowCount = paramInt;
    currentCueBuilder.setCaptionRowCount(paramInt);
  }
  
  protected Subtitle createSubtitle()
  {
    lastCues = cues;
    return new CeaSubtitle(cues);
  }
  
  protected void decode(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    ccData.reset(data.array(), data.limit());
    int k = 0;
    label27:
    boolean bool2;
    for (boolean bool1 = false; ccData.bytesLeft() >= packetLength; bool1 = bool2)
    {
      int m;
      if (packetLength == 2) {
        m = -4;
      } else {
        m = (byte)ccData.readUnsignedByte();
      }
      int i = (byte)(ccData.readUnsignedByte() & 0x7F);
      int j = (byte)(ccData.readUnsignedByte() & 0x7F);
      if (((m & 0x6) != 4) || ((selectedField == 1) && ((m & 0x1) != 0)) || ((selectedField == 2) && ((m & 0x1) != 1)) || ((i == 0) && (j == 0))) {
        break label27;
      }
      if (((i & 0xF7) == 17) && ((j & 0xF0) == 48))
      {
        currentCueBuilder.append(getSpecialChar(j));
        bool2 = bool1;
      }
      else if (((i & 0xF6) == 18) && ((j & 0xE0) == 32))
      {
        currentCueBuilder.backspace();
        if ((i & 0x1) == 0)
        {
          currentCueBuilder.append(getExtendedEsFrChar(j));
          bool2 = bool1;
        }
        else
        {
          currentCueBuilder.append(getExtendedPtDeChar(j));
          bool2 = bool1;
        }
      }
      else if ((i & 0xE0) == 0)
      {
        bool2 = handleCtrl(i, j);
      }
      else
      {
        currentCueBuilder.append(getChar(i));
        bool2 = bool1;
        if ((j & 0xE0) != 0)
        {
          currentCueBuilder.append(getChar(j));
          bool2 = bool1;
        }
      }
      k = 1;
    }
    if (k != 0)
    {
      if (!bool1) {
        repeatableControlSet = false;
      }
      if ((captionMode == 1) || (captionMode == 3)) {
        cues = getDisplayCues();
      }
    }
  }
  
  public void flush()
  {
    super.flush();
    cues = null;
    lastCues = null;
    setCaptionMode(0);
    setCaptionRowCount(4);
    resetCueBuilders();
    repeatableControlSet = false;
    repeatableControlCc1 = 0;
    repeatableControlCc2 = 0;
  }
  
  public String getName()
  {
    return "Cea608Decoder";
  }
  
  protected boolean isNewSubtitleDataAvailable()
  {
    return cues != lastCues;
  }
  
  public void release() {}
  
  class CueBuilder
  {
    private static final int BASE_ROW = 15;
    private static final int SCREEN_CHARWIDTH = 32;
    private int captionMode;
    private int captionRowCount;
    private final StringBuilder captionStringBuilder = new StringBuilder();
    private final List<com.google.android.exoplayer2.text.cea.Cea608Decoder.CueBuilder.CueStyle> cueStyles = new ArrayList();
    private int indent;
    private final List<SpannableString> rolledUpCaptions = new ArrayList();
    private int tabOffset;
    private int width;
    
    public CueBuilder(int paramInt)
    {
      reset(this$1);
      setCaptionRowCount(paramInt);
    }
    
    private static void setColorSpan(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt3 == -1) {
        return;
      }
      paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramInt3), paramInt1, paramInt2, 33);
    }
    
    private static void setItalicSpan(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2)
    {
      paramSpannableStringBuilder.setSpan(new StyleSpan(2), paramInt1, paramInt2, 33);
    }
    
    private static void setUnderlineSpan(SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2)
    {
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
    }
    
    public void append(char paramChar)
    {
      captionStringBuilder.append(paramChar);
    }
    
    public void backspace()
    {
      int j = captionStringBuilder.length();
      if (j > 0)
      {
        captionStringBuilder.delete(j - 1, j);
        int i = cueStyles.size() - 1;
        while (i >= 0)
        {
          CueStyle localCueStyle = (CueStyle)cueStyles.get(i);
          if (start != j) {
            break;
          }
          start -= 1;
          i -= 1;
        }
      }
    }
    
    public Cue build()
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      int i = 0;
      while (i < rolledUpCaptions.size())
      {
        localSpannableStringBuilder.append((CharSequence)rolledUpCaptions.get(i));
        localSpannableStringBuilder.append('\n');
        i += 1;
      }
      localSpannableStringBuilder.append(buildSpannableString());
      if (localSpannableStringBuilder.length() == 0) {
        return null;
      }
      i = indent + tabOffset;
      int j = 32 - i - localSpannableStringBuilder.length();
      int k = i - j;
      float f;
      if ((captionMode == 2) && ((Math.abs(k) < 3) || (j < 0)))
      {
        f = 0.5F;
        i = 1;
      }
      else if ((captionMode == 2) && (k > 0))
      {
        f = (32 - j) / 32.0F * 0.8F + 0.1F;
        i = 2;
      }
      else
      {
        f = i / 32.0F * 0.8F + 0.1F;
        i = 0;
      }
      if ((captionMode != 1) && (width <= 7))
      {
        j = width;
        k = 0;
      }
      else
      {
        j = width - 15 - 2;
        k = 2;
      }
      return new Cue(localSpannableStringBuilder, Layout.Alignment.ALIGN_NORMAL, j, 1, k, f, i, Float.MIN_VALUE);
    }
    
    public SpannableString buildSpannableString()
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(captionStringBuilder);
      int i8 = localSpannableStringBuilder.length();
      int i6 = 0;
      int i3 = -1;
      int i2 = -1;
      int n = 0;
      int i1 = -1;
      int i5 = -1;
      int i7 = 0;
      while (i6 < cueStyles.size())
      {
        CueStyle localCueStyle = (CueStyle)cueStyles.get(i6);
        boolean bool = underline;
        int k = style;
        int i = i5;
        int i4 = i7;
        if (k != 8)
        {
          if (k == 7) {
            j = 1;
          } else {
            j = 0;
          }
          if (k == 7)
          {
            i = i5;
            i4 = j;
          }
          else
          {
            i = Cea608Decoder.STYLE_COLORS[k];
            i4 = j;
          }
        }
        int j = start;
        int i9 = i6 + 1;
        if (i9 < cueStyles.size()) {
          k = cueStyles.get(i9)).start;
        } else {
          k = i8;
        }
        if (j == k)
        {
          i6 = i9;
          i5 = i;
          i7 = i4;
        }
        else
        {
          if ((i3 != -1) && (!bool))
          {
            setUnderlineSpan(localSpannableStringBuilder, i3, j);
            k = -1;
          }
          else
          {
            k = i3;
            if (i3 == -1)
            {
              k = i3;
              if (bool) {
                k = j;
              }
            }
          }
          int m;
          if ((i2 != -1) && (i4 == 0))
          {
            setItalicSpan(localSpannableStringBuilder, i2, j);
            m = -1;
          }
          else
          {
            m = i2;
            if (i2 == -1)
            {
              m = i2;
              if (i4 != 0) {
                m = j;
              }
            }
          }
          i6 = i9;
          i3 = k;
          i2 = m;
          i5 = i;
          i7 = i4;
          if (i != i1)
          {
            setColorSpan(localSpannableStringBuilder, n, j, i1);
            i1 = i;
            i6 = i9;
            i3 = k;
            i2 = m;
            n = j;
            i5 = i;
            i7 = i4;
          }
        }
      }
      if ((i3 != -1) && (i3 != i8)) {
        setUnderlineSpan(localSpannableStringBuilder, i3, i8);
      }
      if ((i2 != -1) && (i2 != i8)) {
        setItalicSpan(localSpannableStringBuilder, i2, i8);
      }
      if (n != i8) {
        setColorSpan(localSpannableStringBuilder, n, i8, i1);
      }
      return new SpannableString(localSpannableStringBuilder);
    }
    
    public int getRow()
    {
      return width;
    }
    
    public boolean isEmpty()
    {
      return (cueStyles.isEmpty()) && (rolledUpCaptions.isEmpty()) && (captionStringBuilder.length() == 0);
    }
    
    public void reset(int paramInt)
    {
      captionMode = paramInt;
      cueStyles.clear();
      rolledUpCaptions.clear();
      captionStringBuilder.setLength(0);
      width = 15;
      indent = 0;
      tabOffset = 0;
    }
    
    public void rollUp()
    {
      rolledUpCaptions.add(buildSpannableString());
      captionStringBuilder.setLength(0);
      cueStyles.clear();
      int i = Math.min(captionRowCount, width);
      while (rolledUpCaptions.size() >= i) {
        rolledUpCaptions.remove(0);
      }
    }
    
    public void setCaptionRowCount(int paramInt)
    {
      captionRowCount = paramInt;
    }
    
    public void setIndent(int paramInt)
    {
      indent = paramInt;
    }
    
    public void setRow(int paramInt)
    {
      width = paramInt;
    }
    
    public void setStyle(int paramInt, boolean paramBoolean)
    {
      cueStyles.add(new CueStyle(paramInt, paramBoolean, captionStringBuilder.length()));
    }
    
    public void setTab(int paramInt)
    {
      tabOffset = paramInt;
    }
    
    public String toString()
    {
      return captionStringBuilder.toString();
    }
    
    class CueStyle
    {
      public int start;
      public final int style;
      public final boolean underline;
      
      public CueStyle(boolean paramBoolean, int paramInt)
      {
        style = this$1;
        underline = paramBoolean;
        start = paramInt;
      }
    }
  }
}
