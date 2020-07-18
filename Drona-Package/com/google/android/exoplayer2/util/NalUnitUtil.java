package com.google.android.exoplayer2.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public final class NalUnitUtil
{
  public static final float[] ASPECT_RATIO_IDC_VALUES;
  public static final int EXTENDED_SAR = 255;
  private static final int H264_NAL_UNIT_TYPE_SEI = 6;
  private static final int H264_NAL_UNIT_TYPE_SPS = 7;
  private static final int H265_NAL_UNIT_TYPE_PREFIX_SEI = 39;
  public static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };
  private static final String TAG = "NalUnitUtil";
  private static int[] scratchEscapePositions = new int[10];
  private static final Object scratchEscapePositionsLock;
  
  static
  {
    ASPECT_RATIO_IDC_VALUES = new float[] { 1.0F, 1.0F, 1.0909091F, 0.90909094F, 1.4545455F, 1.2121212F, 2.1818182F, 1.8181819F, 2.909091F, 2.4242425F, 1.6363636F, 1.3636364F, 1.939394F, 1.6161616F, 1.3333334F, 1.5F, 2.0F };
    scratchEscapePositionsLock = new Object();
  }
  
  private NalUnitUtil() {}
  
  public static void clearPrefixFlags(boolean[] paramArrayOfBoolean)
  {
    paramArrayOfBoolean[0] = false;
    paramArrayOfBoolean[1] = false;
    paramArrayOfBoolean[2] = false;
  }
  
  public static void discardToSps(ByteBuffer paramByteBuffer)
  {
    int n = paramByteBuffer.position();
    int k = 0;
    int i = 0;
    for (;;)
    {
      int m = k + 1;
      if (m >= n) {
        break;
      }
      int i1 = paramByteBuffer.get(k) & 0xFF;
      int j;
      if (i == 3)
      {
        j = i;
        if (i1 == 1)
        {
          j = i;
          if ((paramByteBuffer.get(m) & 0x1F) == 7)
          {
            ByteBuffer localByteBuffer = paramByteBuffer.duplicate();
            localByteBuffer.position(k - 3);
            localByteBuffer.limit(n);
            paramByteBuffer.position(0);
            paramByteBuffer.put(localByteBuffer);
          }
        }
      }
      else
      {
        j = i;
        if (i1 == 0) {
          j = i + 1;
        }
      }
      i = j;
      if (i1 != 0) {
        i = 0;
      }
      k = m;
    }
    paramByteBuffer.clear();
  }
  
  public static int findNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean[] paramArrayOfBoolean)
  {
    int i = paramInt2 - paramInt1;
    boolean bool2 = false;
    boolean bool1;
    if (i >= 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Assertions.checkState(bool1);
    if (i == 0) {
      return paramInt2;
    }
    if (paramArrayOfBoolean != null)
    {
      if (paramArrayOfBoolean[0] != 0)
      {
        clearPrefixFlags(paramArrayOfBoolean);
        return paramInt1 - 3;
      }
      if ((i > 1) && (paramArrayOfBoolean[1] != 0) && (paramArrayOfByte[paramInt1] == 1))
      {
        clearPrefixFlags(paramArrayOfBoolean);
        return paramInt1 - 2;
      }
      if ((i > 2) && (paramArrayOfBoolean[2] != 0) && (paramArrayOfByte[paramInt1] == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 1))
      {
        clearPrefixFlags(paramArrayOfBoolean);
        return paramInt1 - 1;
      }
    }
    int j = paramInt2 - 1;
    paramInt1 += 2;
    int k;
    while (paramInt1 < j)
    {
      if ((paramArrayOfByte[paramInt1] & 0xFE) == 0)
      {
        k = paramInt1 - 2;
        if ((paramArrayOfByte[k] == 0) && (paramArrayOfByte[(paramInt1 - 1)] == 0) && (paramArrayOfByte[paramInt1] == 1))
        {
          if (paramArrayOfBoolean == null) {
            break label365;
          }
          clearPrefixFlags(paramArrayOfBoolean);
          return k;
        }
        paramInt1 -= 2;
      }
      paramInt1 += 3;
    }
    if (paramArrayOfBoolean != null)
    {
      if (i > 2) {
        if ((paramArrayOfByte[(paramInt2 - 3)] != 0) || (paramArrayOfByte[(paramInt2 - 2)] != 0) || (paramArrayOfByte[j] != 1)) {}
      }
      for (;;)
      {
        bool1 = true;
        break;
        do
        {
          do
          {
            bool1 = false;
            break label285;
            if (i != 2) {
              break;
            }
          } while ((paramArrayOfBoolean[2] == 0) || (paramArrayOfByte[(paramInt2 - 2)] != 0) || (paramArrayOfByte[j] != 1));
          break;
        } while ((paramArrayOfBoolean[1] == 0) || (paramArrayOfByte[j] != 1));
      }
      label285:
      paramArrayOfBoolean[0] = bool1;
      if (i > 1) {
        if ((paramArrayOfByte[(paramInt2 - 2)] != 0) || (paramArrayOfByte[j] != 0)) {}
      }
      for (;;)
      {
        bool1 = true;
        break;
        do
        {
          bool1 = false;
          break;
        } while ((paramArrayOfBoolean[2] == 0) || (paramArrayOfByte[j] != 0));
      }
      paramArrayOfBoolean[1] = bool1;
      bool1 = bool2;
      if (paramArrayOfByte[j] == 0) {
        bool1 = true;
      }
      paramArrayOfBoolean[2] = bool1;
      return paramInt2;
      label365:
      return k;
    }
    return paramInt2;
  }
  
  private static int findNextUnescapeIndex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    while (paramInt1 < paramInt2 - 2)
    {
      if ((paramArrayOfByte[paramInt1] == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 0) && (paramArrayOfByte[(paramInt1 + 2)] == 3)) {
        return paramInt1;
      }
      paramInt1 += 1;
    }
    return paramInt2;
  }
  
  public static int getH265NalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[(paramInt + 3)] & 0x7E) >> 1;
  }
  
  public static int getNalUnitType(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 3)] & 0x1F;
  }
  
  public static boolean isNalUnitSei(String paramString, byte paramByte)
  {
    if ((!"video/avc".equals(paramString)) || ((paramByte & 0x1F) != 6)) {
      return ("video/hevc".equals(paramString)) && ((paramByte & 0x7E) >> 1 == 39);
    }
    return true;
  }
  
  public static PpsData parsePpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte = new ParsableNalUnitBitArray(paramArrayOfByte, paramInt1, paramInt2);
    paramArrayOfByte.skipBits(8);
    paramInt1 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramInt2 = paramArrayOfByte.readUnsignedExpGolombCodedInt();
    paramArrayOfByte.skipBit();
    return new PpsData(paramInt1, paramInt2, paramArrayOfByte.readBit());
  }
  
  public static SpsData parseSpsNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.useAs(TypeTransformer.java:868)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:668)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray, int paramInt)
  {
    int m = 8;
    int j = 0;
    int k = 8;
    while (j < paramInt)
    {
      int i = m;
      if (m != 0) {
        i = (paramParsableNalUnitBitArray.readSignedExpGolombCodedInt() + k + 256) % 256;
      }
      if (i != 0) {
        k = i;
      }
      j += 1;
      m = i;
    }
  }
  
  public static int unescapeStream(byte[] paramArrayOfByte, int paramInt)
  {
    Object localObject = scratchEscapePositionsLock;
    int i = 0;
    int j = 0;
    if (i < paramInt) {}
    for (;;)
    {
      int k;
      try
      {
        k = findNextUnescapeIndex(paramArrayOfByte, i, paramInt);
        i = k;
        if (k >= paramInt) {
          break;
        }
        if (scratchEscapePositions.length <= j) {
          scratchEscapePositions = Arrays.copyOf(scratchEscapePositions, scratchEscapePositions.length * 2);
        }
        scratchEscapePositions[j] = k;
        i = k + 3;
        j += 1;
      }
      catch (Throwable paramArrayOfByte)
      {
        int n;
        int i1;
        continue;
      }
      if (paramInt < j)
      {
        n = scratchEscapePositions[paramInt] - i;
        System.arraycopy(paramArrayOfByte, i, paramArrayOfByte, k, n);
        k += n;
        i1 = k + 1;
        paramArrayOfByte[k] = 0;
        k = i1 + 1;
        paramArrayOfByte[i1] = 0;
        i += n + 3;
        paramInt += 1;
      }
      else
      {
        System.arraycopy(paramArrayOfByte, i, paramArrayOfByte, k, m - k);
        return m;
        throw paramArrayOfByte;
        int m = paramInt - j;
        paramInt = 0;
        k = 0;
        i = 0;
      }
    }
  }
  
  public static final class PpsData
  {
    public final boolean bottomFieldPicOrderInFramePresentFlag;
    public final int picParameterSetId;
    public final int seqParameterSetId;
    
    public PpsData(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      picParameterSetId = paramInt1;
      seqParameterSetId = paramInt2;
      bottomFieldPicOrderInFramePresentFlag = paramBoolean;
    }
  }
  
  public static final class SpsData
  {
    public final int constraintsFlagsAndReservedZero2Bits;
    public final boolean deltaPicOrderAlwaysZeroFlag;
    public final boolean frameMbsOnlyFlag;
    public final int frameNumLength;
    public final int height;
    public final int levelIdc;
    public final int picOrderCntLsbLength;
    public final int picOrderCountType;
    public final float pixelWidthAspectRatio;
    public final int profileIdc;
    public final boolean separateColorPlaneFlag;
    public final int seqParameterSetId;
    public final int width;
    
    public SpsData(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, float paramFloat, boolean paramBoolean1, boolean paramBoolean2, int paramInt7, int paramInt8, int paramInt9, boolean paramBoolean3)
    {
      profileIdc = paramInt1;
      constraintsFlagsAndReservedZero2Bits = paramInt2;
      levelIdc = paramInt3;
      seqParameterSetId = paramInt4;
      width = paramInt5;
      height = paramInt6;
      pixelWidthAspectRatio = paramFloat;
      separateColorPlaneFlag = paramBoolean1;
      frameMbsOnlyFlag = paramBoolean2;
      frameNumLength = paramInt7;
      picOrderCountType = paramInt8;
      picOrderCntLsbLength = paramInt9;
      deltaPicOrderAlwaysZeroFlag = paramBoolean3;
    }
  }
}
