package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import java.util.Collections;

public final class H265Reader
  implements ElementaryStreamReader
{
  private static final int BLA_W_LP = 16;
  private static final int CRA_NUT = 21;
  private static final int PPS_NUT = 34;
  private static final int PREFIX_SEI_NUT = 39;
  private static final int RASL_R = 9;
  private static final int SPS_NUT = 33;
  private static final int SUFFIX_SEI_NUT = 40;
  private static final String TAG = "H265Reader";
  private static final int VPS_NUT = 32;
  private String formatId;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps;
  private final boolean[] prefixFlags;
  private final NalUnitTargetBuffer prefixSei;
  private SampleReader sampleReader;
  private final SeiReader seiReader;
  private final ParsableByteArray seiWrapper;
  private final NalUnitTargetBuffer sps;
  private final NalUnitTargetBuffer suffixSei;
  private long totalBytesWritten;
  private final NalUnitTargetBuffer vps;
  
  public H265Reader(SeiReader paramSeiReader)
  {
    seiReader = paramSeiReader;
    prefixFlags = new boolean[3];
    vps = new NalUnitTargetBuffer(32, 128);
    sps = new NalUnitTargetBuffer(33, 128);
    pps = new NalUnitTargetBuffer(34, 128);
    prefixSei = new NalUnitTargetBuffer(39, 128);
    suffixSei = new NalUnitTargetBuffer(40, 128);
    seiWrapper = new ParsableByteArray();
  }
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (hasOutputFormat)
    {
      sampleReader.endNalUnit(paramLong1, paramInt1);
    }
    else
    {
      vps.endNalUnit(paramInt2);
      sps.endNalUnit(paramInt2);
      pps.endNalUnit(paramInt2);
      if ((vps.isCompleted()) && (sps.isCompleted()) && (pps.isCompleted()))
      {
        output.format(parseMediaFormat(formatId, vps, sps, pps));
        hasOutputFormat = true;
      }
    }
    if (prefixSei.endNalUnit(paramInt2))
    {
      paramInt1 = NalUnitUtil.unescapeStream(prefixSei.nalData, prefixSei.nalLength);
      seiWrapper.reset(prefixSei.nalData, paramInt1);
      seiWrapper.skipBytes(5);
      seiReader.consume(paramLong2, seiWrapper);
    }
    if (suffixSei.endNalUnit(paramInt2))
    {
      paramInt1 = NalUnitUtil.unescapeStream(suffixSei.nalData, suffixSei.nalLength);
      seiWrapper.reset(suffixSei.nalData, paramInt1);
      seiWrapper.skipBytes(5);
      seiReader.consume(paramLong2, seiWrapper);
    }
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (hasOutputFormat)
    {
      sampleReader.readNalUnitData(paramArrayOfByte, paramInt1, paramInt2);
    }
    else
    {
      vps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
    prefixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    suffixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private static Format parseMediaFormat(String paramString, NalUnitTargetBuffer paramNalUnitTargetBuffer1, NalUnitTargetBuffer paramNalUnitTargetBuffer2, NalUnitTargetBuffer paramNalUnitTargetBuffer3)
  {
    byte[] arrayOfByte1 = new byte[nalLength + nalLength + nalLength];
    byte[] arrayOfByte2 = nalData;
    int i = nalLength;
    int m = 0;
    System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, i);
    System.arraycopy(nalData, 0, arrayOfByte1, nalLength, nalLength);
    System.arraycopy(nalData, 0, arrayOfByte1, nalLength + nalLength, nalLength);
    paramNalUnitTargetBuffer1 = new ParsableNalUnitBitArray(nalData, 0, nalLength);
    paramNalUnitTargetBuffer1.skipBits(44);
    int n = paramNalUnitTargetBuffer1.readBits(3);
    paramNalUnitTargetBuffer1.skipBit();
    paramNalUnitTargetBuffer1.skipBits(88);
    paramNalUnitTargetBuffer1.skipBits(8);
    int k = 0;
    i = 0;
    while (k < n)
    {
      j = i;
      if (paramNalUnitTargetBuffer1.readBit()) {
        j = i + 89;
      }
      i = j;
      if (paramNalUnitTargetBuffer1.readBit()) {
        i = j + 8;
      }
      k += 1;
    }
    paramNalUnitTargetBuffer1.skipBits(i);
    if (n > 0) {
      paramNalUnitTargetBuffer1.skipBits((8 - n) * 2);
    }
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int i3 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if (i3 == 3) {
      paramNalUnitTargetBuffer1.skipBit();
    }
    int i2 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int j = i2;
    int i1 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    k = i1;
    if (paramNalUnitTargetBuffer1.readBit())
    {
      int i5 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i6 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      k = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i4 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      if ((i3 != 1) && (i3 != 2)) {
        i = 1;
      } else {
        i = 2;
      }
      if (i3 == 1) {
        j = 2;
      } else {
        j = 1;
      }
      i = i2 - i * (i5 + i6);
      k = i1 - j * (k + i4);
      j = i;
    }
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    i1 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if (paramNalUnitTargetBuffer1.readBit()) {
      i = 0;
    } else {
      i = n;
    }
    while (i <= n)
    {
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      i += 1;
    }
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if ((paramNalUnitTargetBuffer1.readBit()) && (paramNalUnitTargetBuffer1.readBit())) {
      skipScalingList(paramNalUnitTargetBuffer1);
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    if (paramNalUnitTargetBuffer1.readBit())
    {
      paramNalUnitTargetBuffer1.skipBits(8);
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.skipBit();
    }
    skipShortTermRefPicSets(paramNalUnitTargetBuffer1);
    if (paramNalUnitTargetBuffer1.readBit())
    {
      i = m;
      while (i < paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt())
      {
        paramNalUnitTargetBuffer1.skipBits(i1 + 4 + 1);
        i += 1;
      }
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    float f2 = 1.0F;
    float f1;
    if ((paramNalUnitTargetBuffer1.readBit()) && (paramNalUnitTargetBuffer1.readBit()))
    {
      i = paramNalUnitTargetBuffer1.readBits(8);
      if (i == 255)
      {
        i = paramNalUnitTargetBuffer1.readBits(16);
        m = paramNalUnitTargetBuffer1.readBits(16);
        f1 = f2;
        if (i != 0)
        {
          f1 = f2;
          if (m != 0) {
            f1 = i / m;
          }
        }
      }
      else if (i < NalUnitUtil.ASPECT_RATIO_IDC_VALUES.length)
      {
        f1 = NalUnitUtil.ASPECT_RATIO_IDC_VALUES[i];
      }
      else
      {
        paramNalUnitTargetBuffer1 = new StringBuilder();
        paramNalUnitTargetBuffer1.append("Unexpected aspect_ratio_idc value: ");
        paramNalUnitTargetBuffer1.append(i);
        Log.w("H265Reader", paramNalUnitTargetBuffer1.toString());
      }
    }
    else
    {
      f1 = 1.0F;
    }
    return Format.createVideoSampleFormat(paramString, "video/hevc", null, -1, -1, j, k, -1.0F, Collections.singletonList(arrayOfByte1), -1, f1, null);
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    int i = 0;
    while (i < 4)
    {
      int j = 0;
      while (j < 6)
      {
        if (!paramParsableNalUnitBitArray.readBit())
        {
          paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        }
        else
        {
          int m = Math.min(64, 1 << (i << 1) + 4);
          if (i > 1) {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
          }
          k = 0;
          while (k < m)
          {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
            k += 1;
          }
        }
        int k = 3;
        if (i != 3) {
          k = 1;
        }
        j += k;
      }
      i += 1;
    }
  }
  
  private static void skipShortTermRefPicSets(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    int n = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
    int i = 0;
    boolean bool = false;
    int k;
    for (int j = 0; i < n; j = k)
    {
      if (i != 0) {
        bool = paramParsableNalUnitBitArray.readBit();
      }
      if (bool)
      {
        paramParsableNalUnitBitArray.skipBit();
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        m = 0;
        for (;;)
        {
          k = j;
          if (m > j) {
            break;
          }
          if (paramParsableNalUnitBitArray.readBit()) {
            paramParsableNalUnitBitArray.skipBit();
          }
          m += 1;
        }
      }
      k = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      int m = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      j = 0;
      while (j < k)
      {
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBit();
        j += 1;
      }
      j = 0;
      while (j < m)
      {
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBit();
        j += 1;
      }
      k += m;
      i += 1;
    }
  }
  
  private void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (hasOutputFormat)
    {
      sampleReader.startNalUnit(paramLong1, paramInt1, paramInt2, paramLong2);
    }
    else
    {
      vps.startNalUnit(paramInt2);
      sps.startNalUnit(paramInt2);
      pps.startNalUnit(paramInt2);
    }
    prefixSei.startNalUnit(paramInt2);
    suffixSei.startNalUnit(paramInt2);
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() > 0)
    {
      int i = paramParsableByteArray.getPosition();
      int j = paramParsableByteArray.limit();
      byte[] arrayOfByte = data;
      totalBytesWritten += paramParsableByteArray.bytesLeft();
      output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
      while (i < j)
      {
        int k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, prefixFlags);
        if (k == j)
        {
          nalUnitData(arrayOfByte, i, j);
          return;
        }
        int m = NalUnitUtil.getH265NalUnitType(arrayOfByte, k);
        int i1 = k - i;
        if (i1 > 0) {
          nalUnitData(arrayOfByte, i, k);
        }
        int n = j - k;
        long l = totalBytesWritten - n;
        if (i1 < 0) {
          i = -i1;
        } else {
          i = 0;
        }
        endNalUnit(l, n, i, pesTimeUs);
        startNalUnit(l, n, m, pesTimeUs);
        i = k + 3;
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    formatId = paramTrackIdGenerator.getFormatId();
    output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 2);
    sampleReader = new SampleReader(output);
    seiReader.createTracks(paramExtractorOutput, paramTrackIdGenerator);
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    pesTimeUs = paramLong;
  }
  
  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(prefixFlags);
    vps.reset();
    sps.reset();
    pps.reset();
    prefixSei.reset();
    suffixSei.reset();
    sampleReader.reset();
    totalBytesWritten = 0L;
  }
  
  final class SampleReader
  {
    private static final int FIRST_SLICE_FLAG_OFFSET = 2;
    private boolean isFirstParameterSet;
    private boolean isFirstSlice;
    private boolean lookingForFirstSliceFlag;
    private int nalUnitBytesRead;
    private boolean nalUnitHasKeyframeData;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean writingParameterSets;
    
    public SampleReader() {}
    
    private void outputSample(int paramInt)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      if ((writingParameterSets) && (isFirstSlice))
      {
        sampleIsKeyframe = nalUnitHasKeyframeData;
        writingParameterSets = false;
        return;
      }
      if ((isFirstParameterSet) || (isFirstSlice))
      {
        if (readingSample) {
          outputSample(paramInt + (int)(paramLong - nalUnitStartPosition));
        }
        samplePosition = nalUnitStartPosition;
        sampleTimeUs = nalUnitTimeUs;
        readingSample = true;
        sampleIsKeyframe = nalUnitHasKeyframeData;
      }
    }
    
    public void readNalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (lookingForFirstSliceFlag)
      {
        int i = paramInt1 + 2 - nalUnitBytesRead;
        if (i < paramInt2)
        {
          boolean bool;
          if ((paramArrayOfByte[i] & 0x80) != 0) {
            bool = true;
          } else {
            bool = false;
          }
          isFirstSlice = bool;
          lookingForFirstSliceFlag = false;
          return;
        }
        nalUnitBytesRead += paramInt2 - paramInt1;
      }
    }
    
    public void reset()
    {
      lookingForFirstSliceFlag = false;
      isFirstSlice = false;
      isFirstParameterSet = false;
      readingSample = false;
      writingParameterSets = false;
    }
    
    public void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
    {
      isFirstSlice = false;
      isFirstParameterSet = false;
      nalUnitTimeUs = paramLong2;
      nalUnitBytesRead = 0;
      nalUnitStartPosition = paramLong1;
      boolean bool2 = true;
      if (paramInt2 >= 32)
      {
        if ((!writingParameterSets) && (readingSample))
        {
          outputSample(paramInt1);
          readingSample = false;
        }
        if (paramInt2 <= 34)
        {
          isFirstParameterSet = (writingParameterSets ^ true);
          writingParameterSets = true;
        }
      }
      if ((paramInt2 >= 16) && (paramInt2 <= 21)) {
        bool1 = true;
      } else {
        bool1 = false;
      }
      nalUnitHasKeyframeData = bool1;
      boolean bool1 = bool2;
      if (!nalUnitHasKeyframeData) {
        if (paramInt2 <= 9) {
          bool1 = bool2;
        } else {
          bool1 = false;
        }
      }
      lookingForFirstSliceFlag = bool1;
    }
  }
}
