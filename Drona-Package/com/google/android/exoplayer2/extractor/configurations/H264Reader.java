package com.google.android.exoplayer2.extractor.configurations;

import android.util.SparseArray;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.NalUnitUtil.PpsData;
import com.google.android.exoplayer2.util.NalUnitUtil.SpsData;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.ParsableNalUnitBitArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class H264Reader
  implements ElementaryStreamReader
{
  private static final int NAL_UNIT_TYPE_PPS = 8;
  private static final int NAL_UNIT_TYPE_SEI = 6;
  private static final int NAL_UNIT_TYPE_SPS = 7;
  private final boolean allowNonIdrKeyframes;
  private final boolean detectAccessUnits;
  private String formatId;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps;
  private final boolean[] prefixFlags;
  private SampleReader sampleReader;
  private final NalUnitTargetBuffer sei;
  private final SeiReader seiReader;
  private final ParsableByteArray seiWrapper;
  private final NalUnitTargetBuffer sps;
  private long totalBytesWritten;
  
  public H264Reader(SeiReader paramSeiReader, boolean paramBoolean1, boolean paramBoolean2)
  {
    seiReader = paramSeiReader;
    allowNonIdrKeyframes = paramBoolean1;
    detectAccessUnits = paramBoolean2;
    prefixFlags = new boolean[3];
    sps = new NalUnitTargetBuffer(7, 128);
    pps = new NalUnitTargetBuffer(8, 128);
    sei = new NalUnitTargetBuffer(6, 128);
    seiWrapper = new ParsableByteArray();
  }
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if ((!hasOutputFormat) || (sampleReader.needsSpsPps()))
    {
      sps.endNalUnit(paramInt2);
      pps.endNalUnit(paramInt2);
      Object localObject;
      if (!hasOutputFormat)
      {
        if ((sps.isCompleted()) && (pps.isCompleted()))
        {
          localObject = new ArrayList();
          ((List)localObject).add(Arrays.copyOf(sps.nalData, sps.nalLength));
          ((List)localObject).add(Arrays.copyOf(pps.nalData, pps.nalLength));
          NalUnitUtil.SpsData localSpsData = NalUnitUtil.parseSpsNalUnit(sps.nalData, 3, sps.nalLength);
          NalUnitUtil.PpsData localPpsData = NalUnitUtil.parsePpsNalUnit(pps.nalData, 3, pps.nalLength);
          output.format(Format.createVideoSampleFormat(formatId, "video/avc", CodecSpecificDataUtil.buildAvcCodecString(profileIdc, constraintsFlagsAndReservedZero2Bits, levelIdc), -1, -1, width, height, -1.0F, (List)localObject, -1, pixelWidthAspectRatio, null));
          hasOutputFormat = true;
          sampleReader.putSps(localSpsData);
          sampleReader.putPps(localPpsData);
          sps.reset();
          pps.reset();
        }
      }
      else if (sps.isCompleted())
      {
        localObject = NalUnitUtil.parseSpsNalUnit(sps.nalData, 3, sps.nalLength);
        sampleReader.putSps((NalUnitUtil.SpsData)localObject);
        sps.reset();
      }
      else if (pps.isCompleted())
      {
        localObject = NalUnitUtil.parsePpsNalUnit(pps.nalData, 3, pps.nalLength);
        sampleReader.putPps((NalUnitUtil.PpsData)localObject);
        pps.reset();
      }
    }
    if (sei.endNalUnit(paramInt2))
    {
      paramInt2 = NalUnitUtil.unescapeStream(sei.nalData, sei.nalLength);
      seiWrapper.reset(sei.nalData, paramInt2);
      seiWrapper.setPosition(4);
      seiReader.consume(paramLong2, seiWrapper);
    }
    sampleReader.endNalUnit(paramLong1, paramInt1);
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((!hasOutputFormat) || (sampleReader.needsSpsPps()))
    {
      sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
    sei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    sampleReader.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void startNalUnit(long paramLong1, int paramInt, long paramLong2)
  {
    if ((!hasOutputFormat) || (sampleReader.needsSpsPps()))
    {
      sps.startNalUnit(paramInt);
      pps.startNalUnit(paramInt);
    }
    sei.startNalUnit(paramInt);
    sampleReader.startNalUnit(paramLong1, paramInt, paramLong2);
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    byte[] arrayOfByte = data;
    totalBytesWritten += paramParsableByteArray.bytesLeft();
    output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
    for (;;)
    {
      int k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, prefixFlags);
      if (k == j)
      {
        nalUnitData(arrayOfByte, i, j);
        return;
      }
      int m = NalUnitUtil.getNalUnitType(arrayOfByte, k);
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
      startNalUnit(l, m, pesTimeUs);
      i = k + 3;
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    formatId = paramTrackIdGenerator.getFormatId();
    output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 2);
    sampleReader = new SampleReader(output, allowNonIdrKeyframes, detectAccessUnits);
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
    sps.reset();
    pps.reset();
    sei.reset();
    sampleReader.reset();
    totalBytesWritten = 0L;
  }
  
  final class SampleReader
  {
    private static final int DEFAULT_BUFFER_SIZE = 128;
    private static final int NAL_UNIT_TYPE_AUD = 9;
    private static final int NAL_UNIT_TYPE_IDR = 5;
    private static final int NAL_UNIT_TYPE_NON_IDR = 1;
    private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
    private final boolean allowNonIdrKeyframes;
    private final ParsableNalUnitBitArray bitArray;
    private byte[] buffer;
    private int bufferLength;
    private final boolean detectAccessUnits;
    private boolean isFilling;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private int nalUnitType;
    private final SparseArray<NalUnitUtil.PpsData> pps;
    private SliceHeaderData previousSliceHeader;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private SliceHeaderData sliceHeader;
    private final SparseArray<NalUnitUtil.SpsData> sps;
    
    public SampleReader(boolean paramBoolean1, boolean paramBoolean2)
    {
      allowNonIdrKeyframes = paramBoolean1;
      detectAccessUnits = paramBoolean2;
      sps = new SparseArray();
      pps = new SparseArray();
      previousSliceHeader = new SliceHeaderData(null);
      sliceHeader = new SliceHeaderData(null);
      buffer = new byte['?'];
      bitArray = new ParsableNalUnitBitArray(buffer, 0, 0);
      reset();
    }
    
    private void outputSample(int paramInt)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
    
    public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (!isFilling) {
        return;
      }
      paramInt2 -= paramInt1;
      if (buffer.length < bufferLength + paramInt2) {
        buffer = Arrays.copyOf(buffer, (bufferLength + paramInt2) * 2);
      }
      System.arraycopy(paramArrayOfByte, paramInt1, buffer, bufferLength, paramInt2);
      bufferLength += paramInt2;
      bitArray.reset(buffer, 0, bufferLength);
      if (!bitArray.canReadBits(8)) {
        return;
      }
      bitArray.skipBit();
      int i1 = bitArray.readBits(2);
      bitArray.skipBits(5);
      if (!bitArray.canReadExpGolombCodedNum()) {
        return;
      }
      bitArray.readUnsignedExpGolombCodedInt();
      if (!bitArray.canReadExpGolombCodedNum()) {
        return;
      }
      int i2 = bitArray.readUnsignedExpGolombCodedInt();
      if (!detectAccessUnits)
      {
        isFilling = false;
        sliceHeader.setSliceType(i2);
        return;
      }
      if (!bitArray.canReadExpGolombCodedNum()) {
        return;
      }
      int i3 = bitArray.readUnsignedExpGolombCodedInt();
      if (pps.indexOfKey(i3) < 0)
      {
        isFilling = false;
        return;
      }
      paramArrayOfByte = (NalUnitUtil.PpsData)pps.get(i3);
      NalUnitUtil.SpsData localSpsData = (NalUnitUtil.SpsData)sps.get(seqParameterSetId);
      if (separateColorPlaneFlag)
      {
        if (!bitArray.canReadBits(2)) {
          return;
        }
        bitArray.skipBits(2);
      }
      if (!bitArray.canReadBits(frameNumLength)) {
        return;
      }
      int i4 = bitArray.readBits(frameNumLength);
      boolean bool1;
      if (!frameMbsOnlyFlag)
      {
        if (!bitArray.canReadBits(1)) {
          return;
        }
        bool1 = bitArray.readBit();
        if (bool1)
        {
          if (!bitArray.canReadBits(1)) {
            return;
          }
          bool2 = bitArray.readBit();
          bool3 = true;
          break label382;
        }
      }
      else
      {
        bool1 = false;
      }
      boolean bool3 = false;
      boolean bool2 = false;
      label382:
      boolean bool4;
      if (nalUnitType == 5) {
        bool4 = true;
      } else {
        bool4 = false;
      }
      int j;
      if (bool4)
      {
        if (!bitArray.canReadExpGolombCodedNum()) {
          return;
        }
        j = bitArray.readUnsignedExpGolombCodedInt();
      }
      else
      {
        j = 0;
      }
      if (picOrderCountType == 0)
      {
        if (!bitArray.canReadBits(picOrderCntLsbLength)) {
          return;
        }
        paramInt2 = bitArray.readBits(picOrderCntLsbLength);
        if ((bottomFieldPicOrderInFramePresentFlag) && (!bool1))
        {
          if (!bitArray.canReadExpGolombCodedNum()) {
            return;
          }
          paramInt1 = bitArray.readSignedExpGolombCodedInt();
          break label597;
        }
      }
      else
      {
        if ((picOrderCountType == 1) && (!deltaPicOrderAlwaysZeroFlag))
        {
          if (!bitArray.canReadExpGolombCodedNum()) {
            return;
          }
          paramInt1 = bitArray.readSignedExpGolombCodedInt();
          if ((bottomFieldPicOrderInFramePresentFlag) && (!bool1))
          {
            if (!bitArray.canReadExpGolombCodedNum()) {
              return;
            }
            i = bitArray.readSignedExpGolombCodedInt();
            k = 0;
            paramInt2 = 0;
            m = paramInt1;
            break label616;
          }
          i = paramInt1;
          paramInt2 = 0;
          paramInt1 = 0;
          break label600;
        }
        paramInt2 = 0;
      }
      paramInt1 = 0;
      label597:
      int i = 0;
      label600:
      int n = 0;
      int m = i;
      int k = paramInt2;
      i = n;
      paramInt2 = paramInt1;
      label616:
      sliceHeader.setAll(localSpsData, i1, i2, i4, i3, bool1, bool3, bool2, bool4, j, k, paramInt2, m, i);
      isFilling = false;
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      int j = nalUnitType;
      int i = 0;
      if ((j == 9) || ((detectAccessUnits) && (sliceHeader.isFirstVclNalUnitOfPicture(previousSliceHeader))))
      {
        if (readingSample) {
          outputSample(paramInt + (int)(paramLong - nalUnitStartPosition));
        }
        samplePosition = nalUnitStartPosition;
        sampleTimeUs = nalUnitTimeUs;
        sampleIsKeyframe = false;
        readingSample = true;
      }
      int k = sampleIsKeyframe;
      if (nalUnitType != 5)
      {
        paramInt = i;
        if (allowNonIdrKeyframes)
        {
          paramInt = i;
          if (nalUnitType == 1)
          {
            paramInt = i;
            if (!sliceHeader.isISlice()) {}
          }
        }
      }
      else
      {
        paramInt = 1;
      }
      sampleIsKeyframe = (k | paramInt);
    }
    
    public boolean needsSpsPps()
    {
      return detectAccessUnits;
    }
    
    public void putPps(NalUnitUtil.PpsData paramPpsData)
    {
      pps.append(picParameterSetId, paramPpsData);
    }
    
    public void putSps(NalUnitUtil.SpsData paramSpsData)
    {
      sps.append(seqParameterSetId, paramSpsData);
    }
    
    public void reset()
    {
      isFilling = false;
      readingSample = false;
      sliceHeader.clear();
    }
    
    public void startNalUnit(long paramLong1, int paramInt, long paramLong2)
    {
      nalUnitType = paramInt;
      nalUnitTimeUs = paramLong2;
      nalUnitStartPosition = paramLong1;
      if (((allowNonIdrKeyframes) && (nalUnitType == 1)) || ((detectAccessUnits) && ((nalUnitType == 5) || (nalUnitType == 1) || (nalUnitType == 2))))
      {
        SliceHeaderData localSliceHeaderData = previousSliceHeader;
        previousSliceHeader = sliceHeader;
        sliceHeader = localSliceHeaderData;
        sliceHeader.clear();
        bufferLength = 0;
        isFilling = true;
      }
    }
    
    final class SliceHeaderData
    {
      private static final int SLICE_TYPE_ALL_I = 7;
      private static final int SLICE_TYPE_I = 2;
      private boolean bottomFieldFlag;
      private boolean bottomFieldFlagPresent;
      private int deltaPicOrderCnt0;
      private int deltaPicOrderCnt1;
      private int deltaPicOrderCntBottom;
      private boolean fieldPicFlag;
      private int frameNum;
      private boolean hasSliceType;
      private boolean idrPicFlag;
      private int idrPicId;
      private boolean isComplete;
      private int nalRefIdc;
      private int picOrderCntLsb;
      private int picParameterSetId;
      private int sliceType;
      private NalUnitUtil.SpsData spsData;
      
      private SliceHeaderData() {}
      
      private boolean isFirstVclNalUnitOfPicture(SliceHeaderData paramSliceHeaderData)
      {
        if (isComplete)
        {
          if ((!isComplete) || (frameNum != frameNum) || (picParameterSetId != picParameterSetId) || (fieldPicFlag != fieldPicFlag) || ((bottomFieldFlagPresent) && (bottomFieldFlagPresent) && (bottomFieldFlag != bottomFieldFlag)) || ((nalRefIdc != nalRefIdc) && ((nalRefIdc == 0) || (nalRefIdc == 0))) || ((spsData.picOrderCountType == 0) && (spsData.picOrderCountType == 0) && ((picOrderCntLsb != picOrderCntLsb) || (deltaPicOrderCntBottom != deltaPicOrderCntBottom))) || ((spsData.picOrderCountType == 1) && (spsData.picOrderCountType == 1) && ((deltaPicOrderCnt0 != deltaPicOrderCnt0) || (deltaPicOrderCnt1 != deltaPicOrderCnt1))) || (idrPicFlag != idrPicFlag)) {
            break label223;
          }
          if ((idrPicFlag) && (idrPicFlag) && (idrPicId != idrPicId)) {
            return true;
          }
        }
        return false;
        label223:
        return true;
      }
      
      public void clear()
      {
        hasSliceType = false;
        isComplete = false;
      }
      
      public boolean isISlice()
      {
        return (hasSliceType) && ((sliceType == 7) || (sliceType == 2));
      }
      
      public void setAll(NalUnitUtil.SpsData paramSpsData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
      {
        spsData = paramSpsData;
        nalRefIdc = paramInt1;
        sliceType = paramInt2;
        frameNum = paramInt3;
        picParameterSetId = paramInt4;
        fieldPicFlag = paramBoolean1;
        bottomFieldFlagPresent = paramBoolean2;
        bottomFieldFlag = paramBoolean3;
        idrPicFlag = paramBoolean4;
        idrPicId = paramInt5;
        picOrderCntLsb = paramInt6;
        deltaPicOrderCntBottom = paramInt7;
        deltaPicOrderCnt0 = paramInt8;
        deltaPicOrderCnt1 = paramInt9;
        isComplete = true;
        hasSliceType = true;
      }
      
      public void setSliceType(int paramInt)
      {
        sliceType = paramInt;
        hasSliceType = true;
      }
    }
  }
}
