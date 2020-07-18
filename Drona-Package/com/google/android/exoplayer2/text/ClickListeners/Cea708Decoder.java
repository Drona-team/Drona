package com.google.android.exoplayer2.text.ClickListeners;

import android.graphics.Color;
import android.text.Layout.Alignment;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.Subtitle;
import com.google.android.exoplayer2.text.SubtitleInputBuffer;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Cea708Decoder
  extends CeaDecoder
{
  private static final int CC_VALID_FLAG = 4;
  private static final int CHARACTER_BIG_CARONS = 42;
  private static final int CHARACTER_BIG_OE = 44;
  private static final int CHARACTER_BOLD_BULLET = 53;
  private static final int CHARACTER_CLOSE_DOUBLE_QUOTE = 52;
  private static final int CHARACTER_CLOSE_SINGLE_QUOTE = 50;
  private static final int CHARACTER_DIAERESIS_Y = 63;
  private static final int CHARACTER_ELLIPSIS = 37;
  private static final int CHARACTER_FIVE_EIGHTHS = 120;
  private static final int CHARACTER_HORIZONTAL_BORDER = 125;
  private static final int CHARACTER_LOWER_LEFT_BORDER = 124;
  private static final int CHARACTER_LOWER_RIGHT_BORDER = 126;
  private static final int CHARACTER_MN = 127;
  private static final int CHARACTER_NBTSP = 33;
  private static final int CHARACTER_ONE_EIGHTH = 118;
  private static final int CHARACTER_OPEN_DOUBLE_QUOTE = 51;
  private static final int CHARACTER_OPEN_SINGLE_QUOTE = 49;
  private static final int CHARACTER_SEVEN_EIGHTHS = 121;
  private static final int CHARACTER_SM = 61;
  private static final int CHARACTER_SMALL_CARONS = 58;
  private static final int CHARACTER_SMALL_OE = 60;
  private static final int CHARACTER_SOLID_BLOCK = 48;
  private static final int CHARACTER_THREE_EIGHTHS = 119;
  private static final int CHARACTER_TM = 57;
  private static final int CHARACTER_TSP = 32;
  private static final int CHARACTER_UPPER_LEFT_BORDER = 127;
  private static final int CHARACTER_UPPER_RIGHT_BORDER = 123;
  private static final int CHARACTER_VERTICAL_BORDER = 122;
  private static final int COMMAND_BS = 8;
  private static final int COMMAND_CLW = 136;
  private static final int COMMAND_CR = 13;
  private static final int COMMAND_CW0 = 128;
  private static final int COMMAND_CW1 = 129;
  private static final int COMMAND_CW2 = 130;
  private static final int COMMAND_CW3 = 131;
  private static final int COMMAND_CW4 = 132;
  private static final int COMMAND_CW5 = 133;
  private static final int COMMAND_CW6 = 134;
  private static final int COMMAND_CW7 = 135;
  private static final int COMMAND_DF0 = 152;
  private static final int COMMAND_DF1 = 153;
  private static final int COMMAND_DF2 = 154;
  private static final int COMMAND_DF3 = 155;
  private static final int COMMAND_DF4 = 156;
  private static final int COMMAND_DF5 = 157;
  private static final int COMMAND_DF6 = 158;
  private static final int COMMAND_DF7 = 159;
  private static final int COMMAND_DLC = 142;
  private static final int COMMAND_DLW = 140;
  private static final int COMMAND_DLY = 141;
  private static final int COMMAND_DSW = 137;
  private static final int COMMAND_ETX = 3;
  private static final int COMMAND_EXT1 = 16;
  private static final int COMMAND_EXT1_END = 23;
  private static final int COMMAND_EXT1_START = 17;
  private static final int COMMAND_FF = 12;
  private static final int COMMAND_HCR = 14;
  private static final int COMMAND_HDW = 138;
  private static final int COMMAND_NUL = 0;
  private static final int COMMAND_P16_END = 31;
  private static final int COMMAND_P16_START = 24;
  private static final int COMMAND_RST = 143;
  private static final int COMMAND_SPA = 144;
  private static final int COMMAND_SPC = 145;
  private static final int COMMAND_SPL = 146;
  private static final int COMMAND_SWA = 151;
  private static final int COMMAND_TGW = 139;
  private static final int DTVCC_PACKET_DATA = 2;
  private static final int DTVCC_PACKET_START = 3;
  private static final int GROUP_C0_END = 31;
  private static final int GROUP_C1_END = 159;
  private static final int GROUP_C2_END = 31;
  private static final int GROUP_C3_END = 159;
  private static final int GROUP_G0_END = 127;
  private static final int GROUP_G1_END = 255;
  private static final int GROUP_G2_END = 127;
  private static final int GROUP_G3_END = 255;
  private static final int NUM_WINDOWS = 8;
  private static final String PAGE_KEY = "Cea708Decoder";
  private final ParsableByteArray ccData = new ParsableByteArray();
  private final CueBuilder[] cueBuilders;
  private List<Cue> cues;
  private CueBuilder currentCueBuilder;
  private DtvCcPacket currentDtvCcPacket;
  private int currentWindow;
  private List<Cue> lastCues;
  private final int selectedServiceNumber;
  private final ParsableBitArray serviceBlockPacket = new ParsableBitArray();
  
  public Cea708Decoder(int paramInt, List paramList)
  {
    int i = paramInt;
    if (paramInt == -1) {
      i = 1;
    }
    selectedServiceNumber = i;
    cueBuilders = new CueBuilder[8];
    paramInt = 0;
    while (paramInt < 8)
    {
      cueBuilders[paramInt] = new CueBuilder();
      paramInt += 1;
    }
    currentCueBuilder = cueBuilders[0];
    resetCueBuilders();
  }
  
  private void finalizeCurrentPacket()
  {
    if (currentDtvCcPacket == null) {
      return;
    }
    processCurrentPacket();
    currentDtvCcPacket = null;
  }
  
  private List getDisplayCues()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < 8)
    {
      if ((!cueBuilders[i].isEmpty()) && (cueBuilders[i].isVisible())) {
        localArrayList.add(cueBuilders[i].build());
      }
      i += 1;
    }
    Collections.sort(localArrayList);
    return Collections.unmodifiableList(localArrayList);
  }
  
  private void handleC0Command(int paramInt)
  {
    if (paramInt != 0)
    {
      if (paramInt != 3) {
        if (paramInt == 8) {}
      }
      switch (paramInt)
      {
      default: 
        if ((paramInt >= 17) && (paramInt <= 23))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Currently unsupported COMMAND_EXT1 Command: ");
          localStringBuilder.append(paramInt);
          Log.w("Cea708Decoder", localStringBuilder.toString());
          serviceBlockPacket.skipBits(8);
          return;
        }
        if ((paramInt >= 24) && (paramInt <= 31))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Currently unsupported COMMAND_P16 Command: ");
          localStringBuilder.append(paramInt);
          Log.w("Cea708Decoder", localStringBuilder.toString());
          serviceBlockPacket.skipBits(16);
          return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Invalid C0 command: ");
        localStringBuilder.append(paramInt);
        Log.w("Cea708Decoder", localStringBuilder.toString());
        return;
      case 13: 
        currentCueBuilder.append('\n');
        return;
      case 12: 
        resetCueBuilders();
        return;
        currentCueBuilder.backspace();
        return;
        cues = getDisplayCues();
      }
    }
  }
  
  private void handleC1Command(int paramInt)
  {
    int j = 1;
    int k = 1;
    int i = 1;
    Object localObject;
    switch (paramInt)
    {
    default: 
      switch (paramInt)
      {
      default: 
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Invalid C1 command: ");
        ((StringBuilder)localObject).append(paramInt);
        Log.w("Cea708Decoder", ((StringBuilder)localObject).toString());
        return;
      case 152: 
      case 153: 
      case 154: 
      case 155: 
      case 156: 
      case 157: 
      case 158: 
      case 159: 
        paramInt -= 152;
        handleDefineWindow(paramInt);
        if (currentWindow != paramInt)
        {
          currentWindow = paramInt;
          currentCueBuilder = cueBuilders[paramInt];
          return;
        }
        break;
      case 151: 
        if (!currentCueBuilder.isDefined())
        {
          serviceBlockPacket.skipBits(32);
          return;
        }
        handleSetWindowAttributes();
        return;
      }
      break;
    case 146: 
      if (!currentCueBuilder.isDefined())
      {
        serviceBlockPacket.skipBits(16);
        return;
      }
      handleSetPenLocation();
      return;
    case 145: 
      if (!currentCueBuilder.isDefined())
      {
        serviceBlockPacket.skipBits(24);
        return;
      }
      handleSetPenColor();
      return;
    case 144: 
      if (!currentCueBuilder.isDefined())
      {
        serviceBlockPacket.skipBits(16);
        return;
      }
      handleSetPenAttributes();
      return;
    case 143: 
      resetCueBuilders();
      return;
    case 141: 
      serviceBlockPacket.skipBits(8);
      return;
    case 140: 
    case 139: 
    case 138: 
    case 137: 
    case 136: 
    case 128: 
    case 129: 
    case 130: 
    case 131: 
    case 132: 
    case 133: 
    case 134: 
    case 135: 
      while (i <= 8)
      {
        if (serviceBlockPacket.readBit()) {
          cueBuilders[(8 - i)].reset();
        }
        i += 1;
        continue;
        paramInt = 1;
        while (paramInt <= 8)
        {
          if (serviceBlockPacket.readBit())
          {
            localObject = cueBuilders[(8 - paramInt)];
            ((CueBuilder)localObject).setVisibility(((CueBuilder)localObject).isVisible() ^ true);
          }
          paramInt += 1;
          continue;
          while (j <= 8)
          {
            if (serviceBlockPacket.readBit()) {
              cueBuilders[(8 - j)].setVisibility(false);
            }
            j += 1;
            continue;
            paramInt = 1;
            while (paramInt <= 8)
            {
              if (serviceBlockPacket.readBit()) {
                cueBuilders[(8 - paramInt)].setVisibility(true);
              }
              paramInt += 1;
              continue;
              while (k <= 8)
              {
                if (serviceBlockPacket.readBit()) {
                  cueBuilders[(8 - k)].clear();
                }
                k += 1;
                continue;
                paramInt -= 128;
                if (currentWindow != paramInt)
                {
                  currentWindow = paramInt;
                  currentCueBuilder = cueBuilders[paramInt];
                }
              }
            }
          }
        }
      }
    }
  }
  
  private void handleC2Command(int paramInt)
  {
    if (paramInt <= 7) {
      return;
    }
    if (paramInt <= 15)
    {
      serviceBlockPacket.skipBits(8);
      return;
    }
    if (paramInt <= 23)
    {
      serviceBlockPacket.skipBits(16);
      return;
    }
    if (paramInt <= 31) {
      serviceBlockPacket.skipBits(24);
    }
  }
  
  private void handleC3Command(int paramInt)
  {
    if (paramInt <= 135)
    {
      serviceBlockPacket.skipBits(32);
      return;
    }
    if (paramInt <= 143)
    {
      serviceBlockPacket.skipBits(40);
      return;
    }
    if (paramInt <= 159)
    {
      serviceBlockPacket.skipBits(2);
      paramInt = serviceBlockPacket.readBits(6);
      serviceBlockPacket.skipBits(paramInt * 8);
    }
  }
  
  private void handleDefineWindow(int paramInt)
  {
    CueBuilder localCueBuilder = cueBuilders[paramInt];
    serviceBlockPacket.skipBits(2);
    boolean bool1 = serviceBlockPacket.readBit();
    boolean bool2 = serviceBlockPacket.readBit();
    boolean bool3 = serviceBlockPacket.readBit();
    paramInt = serviceBlockPacket.readBits(3);
    boolean bool4 = serviceBlockPacket.readBit();
    int i = serviceBlockPacket.readBits(7);
    int j = serviceBlockPacket.readBits(8);
    int k = serviceBlockPacket.readBits(4);
    int m = serviceBlockPacket.readBits(4);
    serviceBlockPacket.skipBits(2);
    int n = serviceBlockPacket.readBits(6);
    serviceBlockPacket.skipBits(2);
    localCueBuilder.defineWindow(bool1, bool2, bool3, paramInt, bool4, i, j, m, n, k, serviceBlockPacket.readBits(3), serviceBlockPacket.readBits(3));
  }
  
  private void handleG0Character(int paramInt)
  {
    if (paramInt == 127)
    {
      currentCueBuilder.append('?');
      return;
    }
    currentCueBuilder.append((char)(paramInt & 0xFF));
  }
  
  private void handleG1Character(int paramInt)
  {
    currentCueBuilder.append((char)(paramInt & 0xFF));
  }
  
  private void handleG2Character(int paramInt)
  {
    if (paramInt != 37)
    {
      if (paramInt != 42)
      {
        if (paramInt != 44)
        {
          if (paramInt != 63)
          {
            switch (paramInt)
            {
            default: 
              switch (paramInt)
              {
              default: 
                switch (paramInt)
                {
                default: 
                  switch (paramInt)
                  {
                  default: 
                    switch (paramInt)
                    {
                    default: 
                      StringBuilder localStringBuilder = new StringBuilder();
                      localStringBuilder.append("Invalid G2 character: ");
                      localStringBuilder.append(paramInt);
                      Log.w("Cea708Decoder", localStringBuilder.toString());
                      return;
                    case 127: 
                      currentCueBuilder.append('?');
                      return;
                    case 126: 
                      currentCueBuilder.append('?');
                      return;
                    case 125: 
                      currentCueBuilder.append('?');
                      return;
                    case 124: 
                      currentCueBuilder.append('?');
                      return;
                    case 123: 
                      currentCueBuilder.append('?');
                      return;
                    case 122: 
                      currentCueBuilder.append('?');
                      return;
                    case 121: 
                      currentCueBuilder.append('?');
                      return;
                    case 120: 
                      currentCueBuilder.append('?');
                      return;
                    case 119: 
                      currentCueBuilder.append('?');
                      return;
                    }
                    currentCueBuilder.append('?');
                    return;
                  case 61: 
                    currentCueBuilder.append('?');
                    return;
                  }
                  currentCueBuilder.append('?');
                  return;
                case 58: 
                  currentCueBuilder.append('?');
                  return;
                }
                currentCueBuilder.append('?');
                return;
              case 53: 
                currentCueBuilder.append('?');
                return;
              case 52: 
                currentCueBuilder.append('?');
                return;
              case 51: 
                currentCueBuilder.append('?');
                return;
              case 50: 
                currentCueBuilder.append('?');
                return;
              case 49: 
                currentCueBuilder.append('?');
                return;
              }
              currentCueBuilder.append('?');
              return;
            case 33: 
              currentCueBuilder.append('?');
              return;
            }
            currentCueBuilder.append(' ');
            return;
          }
          currentCueBuilder.append('?');
          return;
        }
        currentCueBuilder.append('?');
        return;
      }
      currentCueBuilder.append('?');
      return;
    }
    currentCueBuilder.append('?');
  }
  
  private void handleG3Character(int paramInt)
  {
    if (paramInt == 160)
    {
      currentCueBuilder.append('?');
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid G3 character: ");
    localStringBuilder.append(paramInt);
    Log.w("Cea708Decoder", localStringBuilder.toString());
    currentCueBuilder.append('_');
  }
  
  private void handleSetPenAttributes()
  {
    int i = serviceBlockPacket.readBits(4);
    int j = serviceBlockPacket.readBits(2);
    int k = serviceBlockPacket.readBits(2);
    boolean bool1 = serviceBlockPacket.readBit();
    boolean bool2 = serviceBlockPacket.readBit();
    int m = serviceBlockPacket.readBits(3);
    int n = serviceBlockPacket.readBits(3);
    currentCueBuilder.setPenAttributes(i, j, k, bool1, bool2, m, n);
  }
  
  private void handleSetPenColor()
  {
    int i = serviceBlockPacket.readBits(2);
    i = CueBuilder.getArgbColorFromCeaColor(serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), i);
    int j = serviceBlockPacket.readBits(2);
    j = CueBuilder.getArgbColorFromCeaColor(serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), j);
    serviceBlockPacket.skipBits(2);
    int k = CueBuilder.getArgbColorFromCeaColor(serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2));
    currentCueBuilder.setPenColor(i, j, k);
  }
  
  private void handleSetPenLocation()
  {
    serviceBlockPacket.skipBits(4);
    int i = serviceBlockPacket.readBits(4);
    serviceBlockPacket.skipBits(2);
    int j = serviceBlockPacket.readBits(6);
    currentCueBuilder.setPenLocation(i, j);
  }
  
  private void handleSetWindowAttributes()
  {
    int i = serviceBlockPacket.readBits(2);
    int k = CueBuilder.getArgbColorFromCeaColor(serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), i);
    int j = serviceBlockPacket.readBits(2);
    i = j;
    int m = CueBuilder.getArgbColorFromCeaColor(serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2), serviceBlockPacket.readBits(2));
    if (serviceBlockPacket.readBit()) {
      i = j | 0x4;
    }
    boolean bool = serviceBlockPacket.readBit();
    j = serviceBlockPacket.readBits(2);
    int n = serviceBlockPacket.readBits(2);
    int i1 = serviceBlockPacket.readBits(2);
    serviceBlockPacket.skipBits(8);
    currentCueBuilder.setWindowAttributes(k, m, bool, i, j, n, i1);
  }
  
  private void processCurrentPacket()
  {
    StringBuilder localStringBuilder;
    if (currentDtvCcPacket.currentIndex != currentDtvCcPacket.packetSize * 2 - 1)
    {
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("DtvCcPacket ended prematurely; size is ");
      localStringBuilder.append(currentDtvCcPacket.packetSize * 2 - 1);
      localStringBuilder.append(", but current index is ");
      localStringBuilder.append(currentDtvCcPacket.currentIndex);
      localStringBuilder.append(" (sequence number ");
      localStringBuilder.append(currentDtvCcPacket.sequenceNumber);
      localStringBuilder.append("); ignoring packet");
      Log.w("Cea708Decoder", localStringBuilder.toString());
      return;
    }
    serviceBlockPacket.reset(currentDtvCcPacket.packetData, currentDtvCcPacket.currentIndex);
    int j = serviceBlockPacket.readBits(3);
    int i = j;
    int m = serviceBlockPacket.readBits(5);
    if (j == 7)
    {
      serviceBlockPacket.skipBits(2);
      int k = serviceBlockPacket.readBits(6);
      j = k;
      i = j;
      if (k < 7)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Invalid extended service number: ");
        localStringBuilder.append(k);
        Log.w("Cea708Decoder", localStringBuilder.toString());
        i = j;
      }
    }
    if (m == 0)
    {
      if (i != 0)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("serviceNumber is non-zero (");
        localStringBuilder.append(i);
        localStringBuilder.append(") when blockSize is 0");
        Log.w("Cea708Decoder", localStringBuilder.toString());
      }
    }
    else
    {
      if (i != selectedServiceNumber) {
        return;
      }
      i = 0;
      while (serviceBlockPacket.bitsLeft() > 0)
      {
        j = serviceBlockPacket.readBits(8);
        if (j != 16)
        {
          if (j <= 31)
          {
            handleC0Command(j);
            continue;
          }
          if (j <= 127) {
            handleG0Character(j);
          }
        }
        for (;;)
        {
          i = 1;
          break;
          if (j <= 159)
          {
            handleC1Command(j);
          }
          else if (j <= 255)
          {
            handleG1Character(j);
          }
          else
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("Invalid base command: ");
            localStringBuilder.append(j);
            Log.w("Cea708Decoder", localStringBuilder.toString());
            break;
            j = serviceBlockPacket.readBits(8);
            if (j <= 31)
            {
              handleC2Command(j);
              break;
            }
            if (j <= 127)
            {
              handleG2Character(j);
            }
            else
            {
              if (j <= 159)
              {
                handleC3Command(j);
                break;
              }
              if (j > 255) {
                break label490;
              }
              handleG3Character(j);
            }
          }
        }
        label490:
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Invalid extended command: ");
        localStringBuilder.append(j);
        Log.w("Cea708Decoder", localStringBuilder.toString());
      }
      if (i != 0) {
        cues = getDisplayCues();
      }
    }
  }
  
  private void resetCueBuilders()
  {
    int i = 0;
    while (i < 8)
    {
      cueBuilders[i].reset();
      i += 1;
    }
  }
  
  protected Subtitle createSubtitle()
  {
    lastCues = cues;
    return new CeaSubtitle(cues);
  }
  
  protected void decode(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    Object localObject = data.array();
    ccData.reset((byte[])localObject, data.limit());
    while (ccData.bytesLeft() >= 3)
    {
      int k = ccData.readUnsignedByte() & 0x7;
      int m = k & 0x3;
      boolean bool = false;
      if ((k & 0x4) == 4) {
        k = 1;
      } else {
        k = 0;
      }
      int i = (byte)ccData.readUnsignedByte();
      int j = (byte)ccData.readUnsignedByte();
      if (((m == 2) || (m == 3)) && (k != 0))
      {
        if (m == 3)
        {
          finalizeCurrentPacket();
          m = i & 0x3F;
          k = m;
          if (m == 0) {
            k = 64;
          }
          currentDtvCcPacket = new DtvCcPacket((i & 0xC0) >> 6, k);
          paramSubtitleInputBuffer = currentDtvCcPacket.packetData;
          localObject = currentDtvCcPacket;
          k = currentIndex;
          currentIndex = (k + 1);
          paramSubtitleInputBuffer[k] = j;
        }
        else
        {
          if (m == 2) {
            bool = true;
          }
          Assertions.checkArgument(bool);
          if (currentDtvCcPacket == null)
          {
            Log.e("Cea708Decoder", "Encountered DTVCC_PACKET_DATA before DTVCC_PACKET_START");
            continue;
          }
          paramSubtitleInputBuffer = currentDtvCcPacket.packetData;
          localObject = currentDtvCcPacket;
          k = currentIndex;
          currentIndex = (k + 1);
          paramSubtitleInputBuffer[k] = i;
          paramSubtitleInputBuffer = currentDtvCcPacket.packetData;
          localObject = currentDtvCcPacket;
          k = currentIndex;
          currentIndex = (k + 1);
          paramSubtitleInputBuffer[k] = j;
        }
        if (currentDtvCcPacket.currentIndex == currentDtvCcPacket.packetSize * 2 - 1) {
          finalizeCurrentPacket();
        }
      }
    }
  }
  
  public void flush()
  {
    super.flush();
    cues = null;
    lastCues = null;
    currentWindow = 0;
    currentCueBuilder = cueBuilders[currentWindow];
    resetCueBuilders();
    currentDtvCcPacket = null;
  }
  
  public String getName()
  {
    return "Cea708Decoder";
  }
  
  protected boolean isNewSubtitleDataAvailable()
  {
    return cues != lastCues;
  }
  
  final class CueBuilder
  {
    private static final int BORDER_AND_EDGE_TYPE_NONE = 0;
    private static final int BORDER_AND_EDGE_TYPE_UNIFORM = 3;
    public static final int COLOR_SOLID_BLACK;
    public static final int COLOR_SOLID_WHITE = getArgbColorFromCeaColor(2, 2, 2, 0);
    public static final int COLOR_TRANSPARENT;
    private static final int DEFAULT_PRIORITY = 4;
    private static final int DIRECTION_BOTTOM_TO_TOP = 3;
    private static final int DIRECTION_LEFT_TO_RIGHT = 0;
    private static final int DIRECTION_RIGHT_TO_LEFT = 1;
    private static final int DIRECTION_TOP_TO_BOTTOM = 2;
    private static final int HORIZONTAL_SIZE = 209;
    private static final int JUSTIFICATION_CENTER = 2;
    private static final int JUSTIFICATION_FULL = 3;
    private static final int JUSTIFICATION_LEFT = 0;
    private static final int JUSTIFICATION_RIGHT = 1;
    private static final int MAXIMUM_ROW_COUNT = 15;
    private static final int PEN_FONT_STYLE_DEFAULT = 0;
    private static final int PEN_FONT_STYLE_MONOSPACED_WITHOUT_SERIFS = 3;
    private static final int PEN_FONT_STYLE_MONOSPACED_WITH_SERIFS = 1;
    private static final int PEN_FONT_STYLE_PROPORTIONALLY_SPACED_WITHOUT_SERIFS = 4;
    private static final int PEN_FONT_STYLE_PROPORTIONALLY_SPACED_WITH_SERIFS = 2;
    private static final int PEN_OFFSET_NORMAL = 1;
    private static final int PEN_SIZE_STANDARD = 1;
    private static final int[] PEN_STYLE_BACKGROUND = { COLOR_SOLID_BLACK, COLOR_SOLID_BLACK, COLOR_SOLID_BLACK, COLOR_SOLID_BLACK, COLOR_SOLID_BLACK, COLOR_TRANSPARENT, COLOR_TRANSPARENT };
    private static final int[] PEN_STYLE_EDGE_TYPE;
    private static final int[] PEN_STYLE_FONT_STYLE;
    private static final int RELATIVE_CUE_SIZE = 99;
    private static final int VERTICAL_SIZE = 74;
    private static final int[] WINDOW_STYLE_FILL;
    private static final int[] WINDOW_STYLE_JUSTIFICATION;
    private static final int[] WINDOW_STYLE_PRINT_DIRECTION;
    private static final int[] WINDOW_STYLE_SCROLL_DIRECTION;
    private static final boolean[] WINDOW_STYLE_WORD_WRAP;
    private int anchorId;
    private int backgroundColor;
    private int backgroundColorStartPosition;
    private final SpannableStringBuilder captionStringBuilder = new SpannableStringBuilder();
    private boolean defined;
    private int foregroundColor;
    private int foregroundColorStartPosition;
    private int fsync;
    private int horizontalAnchor;
    private int italicsStartPosition;
    private int justification;
    private int penStyleId;
    private int priority;
    private boolean relativePositioning;
    private final List<SpannableString> rolledUpCaptions = new ArrayList();
    private int rowCount;
    private boolean rowLock;
    private int underlineStartPosition;
    private int verticalAnchor;
    private boolean visible;
    private int windowFillColor;
    private int windowStyleId;
    
    static
    {
      COLOR_SOLID_BLACK = getArgbColorFromCeaColor(0, 0, 0, 0);
      COLOR_TRANSPARENT = getArgbColorFromCeaColor(0, 0, 0, 3);
      WINDOW_STYLE_JUSTIFICATION = new int[] { 0, 0, 0, 0, 0, 2, 0 };
      WINDOW_STYLE_PRINT_DIRECTION = new int[] { 0, 0, 0, 0, 0, 0, 2 };
      WINDOW_STYLE_SCROLL_DIRECTION = new int[] { 3, 3, 3, 3, 3, 3, 1 };
      WINDOW_STYLE_WORD_WRAP = new boolean[] { 0, 0, 0, 1, 1, 1, 0 };
      WINDOW_STYLE_FILL = new int[] { COLOR_SOLID_BLACK, COLOR_TRANSPARENT, COLOR_SOLID_BLACK, COLOR_SOLID_BLACK, COLOR_TRANSPARENT, COLOR_SOLID_BLACK, COLOR_SOLID_BLACK };
      PEN_STYLE_FONT_STYLE = new int[] { 0, 1, 2, 3, 4, 3, 4 };
      PEN_STYLE_EDGE_TYPE = new int[] { 0, 0, 0, 0, 0, 3, 3 };
    }
    
    public CueBuilder()
    {
      reset();
    }
    
    public static int getArgbColorFromCeaColor(int paramInt1, int paramInt2, int paramInt3)
    {
      return getArgbColorFromCeaColor(paramInt1, paramInt2, paramInt3, 0);
    }
    
    public static int getArgbColorFromCeaColor(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Assertions.checkIndex(paramInt1, 0, 4);
      Assertions.checkIndex(paramInt2, 0, 4);
      Assertions.checkIndex(paramInt3, 0, 4);
      Assertions.checkIndex(paramInt4, 0, 4);
      int i = 255;
      switch (paramInt4)
      {
      default: 
        break;
      case 0: 
      case 1: 
        paramInt4 = 255;
        break;
      case 3: 
        paramInt4 = 0;
        break;
      }
      paramInt4 = 127;
      if (paramInt1 > 1) {
        paramInt1 = 255;
      } else {
        paramInt1 = 0;
      }
      if (paramInt2 > 1) {
        paramInt2 = 255;
      } else {
        paramInt2 = 0;
      }
      if (paramInt3 > 1) {
        paramInt3 = i;
      } else {
        paramInt3 = 0;
      }
      return Color.argb(paramInt4, paramInt1, paramInt2, paramInt3);
    }
    
    public void append(char paramChar)
    {
      if (paramChar == '\n')
      {
        rolledUpCaptions.add(buildSpannableString());
        captionStringBuilder.clear();
        if (italicsStartPosition != -1) {
          italicsStartPosition = 0;
        }
        if (underlineStartPosition != -1) {
          underlineStartPosition = 0;
        }
        if (foregroundColorStartPosition != -1) {
          foregroundColorStartPosition = 0;
        }
        if (backgroundColorStartPosition != -1) {
          backgroundColorStartPosition = 0;
        }
        while (((rowLock) && (rolledUpCaptions.size() >= rowCount)) || (rolledUpCaptions.size() >= 15)) {
          rolledUpCaptions.remove(0);
        }
      }
      captionStringBuilder.append(paramChar);
    }
    
    public void backspace()
    {
      int i = captionStringBuilder.length();
      if (i > 0) {
        captionStringBuilder.delete(i - 1, i);
      }
    }
    
    public Cea708Cue build()
    {
      if (isEmpty()) {
        return null;
      }
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      int i = 0;
      while (i < rolledUpCaptions.size())
      {
        localSpannableStringBuilder.append((CharSequence)rolledUpCaptions.get(i));
        localSpannableStringBuilder.append('\n');
        i += 1;
      }
      localSpannableStringBuilder.append(buildSpannableString());
      Object localObject;
      switch (justification)
      {
      default: 
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Unexpected justification value: ");
        ((StringBuilder)localObject).append(justification);
        throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      case 2: 
        localObject = Layout.Alignment.ALIGN_CENTER;
        break;
      case 1: 
        localObject = Layout.Alignment.ALIGN_OPPOSITE;
        break;
      case 0: 
      case 3: 
        localObject = Layout.Alignment.ALIGN_NORMAL;
      }
      float f1;
      float f2;
      if (relativePositioning)
      {
        f1 = horizontalAnchor / 99.0F;
        f2 = verticalAnchor / 99.0F;
      }
      else
      {
        f1 = horizontalAnchor / 209.0F;
        f2 = verticalAnchor / 74.0F;
      }
      if (anchorId % 3 == 0) {
        i = 0;
      } else if (anchorId % 3 == 1) {
        i = 1;
      } else {
        i = 2;
      }
      int j;
      if (anchorId / 3 == 0) {
        j = 0;
      } else if (anchorId / 3 == 1) {
        j = 1;
      } else {
        j = 2;
      }
      boolean bool;
      if (windowFillColor != COLOR_SOLID_BLACK) {
        bool = true;
      } else {
        bool = false;
      }
      return new Cea708Cue(localSpannableStringBuilder, (Layout.Alignment)localObject, f2 * 0.9F + 0.05F, 0, i, f1 * 0.9F + 0.05F, j, Float.MIN_VALUE, bool, windowFillColor, priority);
    }
    
    public SpannableString buildSpannableString()
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(captionStringBuilder);
      int i = localSpannableStringBuilder.length();
      if (i > 0)
      {
        if (italicsStartPosition != -1) {
          localSpannableStringBuilder.setSpan(new StyleSpan(2), italicsStartPosition, i, 33);
        }
        if (underlineStartPosition != -1) {
          localSpannableStringBuilder.setSpan(new UnderlineSpan(), underlineStartPosition, i, 33);
        }
        if (foregroundColorStartPosition != -1) {
          localSpannableStringBuilder.setSpan(new ForegroundColorSpan(foregroundColor), foregroundColorStartPosition, i, 33);
        }
        if (backgroundColorStartPosition != -1) {
          localSpannableStringBuilder.setSpan(new BackgroundColorSpan(backgroundColor), backgroundColorStartPosition, i, 33);
        }
      }
      return new SpannableString(localSpannableStringBuilder);
    }
    
    public void clear()
    {
      rolledUpCaptions.clear();
      captionStringBuilder.clear();
      italicsStartPosition = -1;
      underlineStartPosition = -1;
      foregroundColorStartPosition = -1;
      backgroundColorStartPosition = -1;
      fsync = 0;
    }
    
    public void defineWindow(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt1, boolean paramBoolean4, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8)
    {
      defined = true;
      visible = paramBoolean1;
      rowLock = paramBoolean2;
      priority = paramInt1;
      relativePositioning = paramBoolean4;
      verticalAnchor = paramInt2;
      horizontalAnchor = paramInt3;
      anchorId = paramInt6;
      paramInt1 = rowCount;
      paramInt2 = paramInt4 + 1;
      if (paramInt1 != paramInt2)
      {
        rowCount = paramInt2;
        while (((paramBoolean2) && (rolledUpCaptions.size() >= rowCount)) || (rolledUpCaptions.size() >= 15)) {
          rolledUpCaptions.remove(0);
        }
      }
      if ((paramInt7 != 0) && (windowStyleId != paramInt7))
      {
        windowStyleId = paramInt7;
        paramInt1 = paramInt7 - 1;
        setWindowAttributes(WINDOW_STYLE_FILL[paramInt1], COLOR_TRANSPARENT, WINDOW_STYLE_WORD_WRAP[paramInt1], 0, WINDOW_STYLE_PRINT_DIRECTION[paramInt1], WINDOW_STYLE_SCROLL_DIRECTION[paramInt1], WINDOW_STYLE_JUSTIFICATION[paramInt1]);
      }
      if ((paramInt8 != 0) && (penStyleId != paramInt8))
      {
        penStyleId = paramInt8;
        paramInt1 = paramInt8 - 1;
        setPenAttributes(0, 1, 1, false, false, PEN_STYLE_EDGE_TYPE[paramInt1], PEN_STYLE_FONT_STYLE[paramInt1]);
        setPenColor(COLOR_SOLID_WHITE, PEN_STYLE_BACKGROUND[paramInt1], COLOR_SOLID_BLACK);
      }
    }
    
    public boolean isDefined()
    {
      return defined;
    }
    
    public boolean isEmpty()
    {
      return (!isDefined()) || ((rolledUpCaptions.isEmpty()) && (captionStringBuilder.length() == 0));
    }
    
    public boolean isVisible()
    {
      return visible;
    }
    
    public void reset()
    {
      clear();
      defined = false;
      visible = false;
      priority = 4;
      relativePositioning = false;
      verticalAnchor = 0;
      horizontalAnchor = 0;
      anchorId = 0;
      rowCount = 15;
      rowLock = true;
      justification = 0;
      windowStyleId = 0;
      penStyleId = 0;
      windowFillColor = COLOR_SOLID_BLACK;
      foregroundColor = COLOR_SOLID_WHITE;
      backgroundColor = COLOR_SOLID_BLACK;
    }
    
    public void setPenAttributes(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, boolean paramBoolean2, int paramInt4, int paramInt5)
    {
      if (italicsStartPosition != -1)
      {
        if (!paramBoolean1)
        {
          captionStringBuilder.setSpan(new StyleSpan(2), italicsStartPosition, captionStringBuilder.length(), 33);
          italicsStartPosition = -1;
        }
      }
      else if (paramBoolean1) {
        italicsStartPosition = captionStringBuilder.length();
      }
      if (underlineStartPosition != -1)
      {
        if (!paramBoolean2)
        {
          captionStringBuilder.setSpan(new UnderlineSpan(), underlineStartPosition, captionStringBuilder.length(), 33);
          underlineStartPosition = -1;
        }
      }
      else if (paramBoolean2) {
        underlineStartPosition = captionStringBuilder.length();
      }
    }
    
    public void setPenColor(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((foregroundColorStartPosition != -1) && (foregroundColor != paramInt1)) {
        captionStringBuilder.setSpan(new ForegroundColorSpan(foregroundColor), foregroundColorStartPosition, captionStringBuilder.length(), 33);
      }
      if (paramInt1 != COLOR_SOLID_WHITE)
      {
        foregroundColorStartPosition = captionStringBuilder.length();
        foregroundColor = paramInt1;
      }
      if ((backgroundColorStartPosition != -1) && (backgroundColor != paramInt2)) {
        captionStringBuilder.setSpan(new BackgroundColorSpan(backgroundColor), backgroundColorStartPosition, captionStringBuilder.length(), 33);
      }
      if (paramInt2 != COLOR_SOLID_BLACK)
      {
        backgroundColorStartPosition = captionStringBuilder.length();
        backgroundColor = paramInt2;
      }
    }
    
    public void setPenLocation(int paramInt1, int paramInt2)
    {
      if (fsync != paramInt1) {
        append('\n');
      }
      fsync = paramInt1;
    }
    
    public void setVisibility(boolean paramBoolean)
    {
      visible = paramBoolean;
    }
    
    public void setWindowAttributes(int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3, int paramInt4, int paramInt5, int paramInt6)
    {
      windowFillColor = paramInt1;
      justification = paramInt6;
    }
  }
  
  final class DtvCcPacket
  {
    int currentIndex;
    public final byte[] packetData;
    public final int packetSize;
    public final int sequenceNumber;
    
    public DtvCcPacket(int paramInt)
    {
      sequenceNumber = this$1;
      packetSize = paramInt;
      packetData = new byte[paramInt * 2 - 1];
      currentIndex = 0;
    }
  }
}
