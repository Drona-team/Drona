package com.google.android.exoplayer2.util;

import android.util.Pair;
import com.google.android.exoplayer2.ParserException;
import java.util.ArrayList;
import java.util.List;

public final class CodecSpecificDataUtil
{
  private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
  private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
  private static final int AUDIO_OBJECT_TYPE_ESCAPE = 31;
  private static final int AUDIO_OBJECT_TYPE_PS = 29;
  private static final int AUDIO_OBJECT_TYPE_SBR = 5;
  private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
  private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = { 0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1 };
  private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
  private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
  private static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  
  static
  {
    AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[] { 96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350 };
  }
  
  private CodecSpecificDataUtil() {}
  
  public static byte[] buildAacAudioSpecificConfig(int paramInt1, int paramInt2, int paramInt3)
  {
    return new byte[] { (byte)(paramInt1 << 3 & 0xF8 | paramInt2 >> 1 & 0x7), (byte)(paramInt2 << 7 & 0x80 | paramInt3 << 3 & 0x78) };
  }
  
  public static byte[] buildAacLcAudioSpecificConfig(int paramInt1, int paramInt2)
  {
    int m = 0;
    int i = 0;
    int j = -1;
    while (i < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length)
    {
      if (paramInt1 == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i]) {
        j = i;
      }
      i += 1;
    }
    int k = -1;
    i = m;
    while (i < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length)
    {
      if (paramInt2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i]) {
        k = i;
      }
      i += 1;
    }
    if ((paramInt1 != -1) && (k != -1)) {
      return buildAacAudioSpecificConfig(2, j, k);
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid sample rate or number of channels: ");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append(", ");
    localStringBuilder.append(paramInt2);
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static String buildAvcCodecString(int paramInt1, int paramInt2, int paramInt3)
  {
    return String.format("avc1.%02X%02X%02X", new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt3) });
  }
  
  public static byte[] buildNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[NAL_START_CODE.length + paramInt2];
    System.arraycopy(NAL_START_CODE, 0, arrayOfByte, 0, NAL_START_CODE.length);
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, NAL_START_CODE.length, paramInt2);
    return arrayOfByte;
  }
  
  private static int findNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = NAL_START_CODE.length;
    while (paramInt <= i - j)
    {
      if (isNalStartCode(paramArrayOfByte, paramInt)) {
        return paramInt;
      }
      paramInt += 1;
    }
    return -1;
  }
  
  private static int getAacAudioObjectType(ParsableBitArray paramParsableBitArray)
  {
    int j = paramParsableBitArray.readBits(5);
    int i = j;
    if (j == 31) {
      i = paramParsableBitArray.readBits(6) + 32;
    }
    return i;
  }
  
  private static int getAacSamplingFrequency(ParsableBitArray paramParsableBitArray)
  {
    int i = paramParsableBitArray.readBits(4);
    if (i == 15) {
      return paramParsableBitArray.readBits(24);
    }
    boolean bool;
    if (i < 13) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkArgument(bool);
    return AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i];
  }
  
  private static boolean isNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte.length - paramInt <= NAL_START_CODE.length) {
      return false;
    }
    int i = 0;
    while (i < NAL_START_CODE.length)
    {
      if (paramArrayOfByte[(paramInt + i)] != NAL_START_CODE[i]) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  public static Pair parseAacAudioSpecificConfig(ParsableBitArray paramParsableBitArray, boolean paramBoolean)
    throws ParserException
  {
    int m = getAacAudioObjectType(paramParsableBitArray);
    int j = m;
    int i = getAacSamplingFrequency(paramParsableBitArray);
    int n = paramParsableBitArray.readBits(4);
    int k;
    if (m != 5)
    {
      k = n;
      if (m != 29) {}
    }
    else
    {
      int i1 = getAacSamplingFrequency(paramParsableBitArray);
      int i2 = getAacAudioObjectType(paramParsableBitArray);
      m = i2;
      j = m;
      i = i1;
      k = n;
      if (i2 == 22)
      {
        k = paramParsableBitArray.readBits(4);
        i = i1;
        j = m;
      }
    }
    if (paramBoolean)
    {
      if (j != 17) {
        switch (j)
        {
        default: 
          switch (j)
          {
          default: 
            switch (j)
            {
            default: 
              paramParsableBitArray = new StringBuilder();
              paramParsableBitArray.append("Unsupported audio object type: ");
              paramParsableBitArray.append(j);
              throw new ParserException(paramParsableBitArray.toString());
            }
            break;
          }
          break;
        }
      }
      parseGaSpecificConfig(paramParsableBitArray, j, k);
      switch (j)
      {
      default: 
        break;
      case 18: 
        break;
      }
      j = paramParsableBitArray.readBits(2);
      if ((j == 2) || (j == 3))
      {
        paramParsableBitArray = new StringBuilder();
        paramParsableBitArray.append("Unsupported epConfig: ");
        paramParsableBitArray.append(j);
        throw new ParserException(paramParsableBitArray.toString());
      }
    }
    j = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[k];
    if (j != -1) {
      paramBoolean = true;
    } else {
      paramBoolean = false;
    }
    Assertions.checkArgument(paramBoolean);
    return Pair.create(Integer.valueOf(i), Integer.valueOf(j));
  }
  
  public static Pair parseAacAudioSpecificConfig(byte[] paramArrayOfByte)
    throws ParserException
  {
    return parseAacAudioSpecificConfig(new ParsableBitArray(paramArrayOfByte), false);
  }
  
  private static void parseGaSpecificConfig(ParsableBitArray paramParsableBitArray, int paramInt1, int paramInt2)
  {
    paramParsableBitArray.skipBits(1);
    if (paramParsableBitArray.readBit()) {
      paramParsableBitArray.skipBits(14);
    }
    boolean bool = paramParsableBitArray.readBit();
    if (paramInt2 != 0)
    {
      if ((paramInt1 == 6) || (paramInt1 == 20)) {
        paramParsableBitArray.skipBits(3);
      }
      if (bool)
      {
        if (paramInt1 == 22) {
          paramParsableBitArray.skipBits(16);
        }
        if ((paramInt1 == 17) || (paramInt1 == 19) || (paramInt1 == 20) || (paramInt1 == 23)) {
          paramParsableBitArray.skipBits(3);
        }
        paramParsableBitArray.skipBits(1);
      }
    }
    else
    {
      throw new UnsupportedOperationException();
    }
  }
  
  public static byte[][] splitNalUnits(byte[] paramArrayOfByte)
  {
    if (!isNalStartCode(paramArrayOfByte, 0)) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j;
    do
    {
      localArrayList.add(Integer.valueOf(i));
      j = findNalStartCode(paramArrayOfByte, i + NAL_START_CODE.length);
      i = j;
    } while (j != -1);
    byte[][] arrayOfByte = new byte[localArrayList.size()][];
    i = 0;
    while (i < localArrayList.size())
    {
      int k = ((Integer)localArrayList.get(i)).intValue();
      if (i < localArrayList.size() - 1) {
        j = ((Integer)localArrayList.get(i + 1)).intValue();
      } else {
        j = paramArrayOfByte.length;
      }
      byte[] arrayOfByte1 = new byte[j - k];
      System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, arrayOfByte1.length);
      arrayOfByte[i] = arrayOfByte1;
      i += 1;
    }
    return arrayOfByte;
  }
}
