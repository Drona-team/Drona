package com.google.android.exoplayer2.extractor.configurations;

import android.util.SparseArray;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.BinarySearchSeeker;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap.Unseekable;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import java.io.IOException;

public final class PsExtractor
  implements Extractor
{
  public static final int AUDIO_STREAM = 192;
  public static final int AUDIO_STREAM_MASK = 224;
  public static final ExtractorsFactory FACTORY = -..Lambda.PsExtractor.U8l9TedlJUwsYwV9EOSFo_ngcXY.INSTANCE;
  private static final long MAX_SEARCH_LENGTH = 1048576L;
  private static final long MAX_SEARCH_LENGTH_AFTER_AUDIO_AND_VIDEO_FOUND = 8192L;
  private static final int MAX_STREAM_ID_PLUS_ONE = 256;
  static final int MPEG_PROGRAM_END_CODE = 441;
  static final int PACKET_START_CODE_PREFIX = 1;
  static final int PACK_START_CODE = 442;
  public static final int PRIVATE_STREAM_1 = 189;
  static final int SYSTEM_HEADER_START_CODE = 443;
  public static final int VIDEO_STREAM = 224;
  public static final int VIDEO_STREAM_MASK = 240;
  private final PsDurationReader durationReader;
  private boolean foundAllTracks;
  private boolean foundAudioTrack;
  private boolean foundVideoTrack;
  private boolean hasOutputSeekMap;
  private long lastTrackPosition;
  private ExtractorOutput output;
  private PsBinarySearchSeeker psBinarySearchSeeker;
  private final ParsableByteArray psPacketBuffer;
  private final SparseArray<com.google.android.exoplayer2.extractor.ts.PsExtractor.PesReader> psPayloadReaders;
  private final TimestampAdjuster timestampAdjuster;
  
  public PsExtractor()
  {
    this(new TimestampAdjuster(0L));
  }
  
  public PsExtractor(TimestampAdjuster paramTimestampAdjuster)
  {
    timestampAdjuster = paramTimestampAdjuster;
    psPacketBuffer = new ParsableByteArray(4096);
    psPayloadReaders = new SparseArray();
    durationReader = new PsDurationReader();
  }
  
  private void maybeOutputSeekMap(long paramLong)
  {
    if (!hasOutputSeekMap)
    {
      hasOutputSeekMap = true;
      if (durationReader.getDurationUs() != -9223372036854775807L)
      {
        psBinarySearchSeeker = new PsBinarySearchSeeker(durationReader.getScrTimestampAdjuster(), durationReader.getDurationUs(), paramLong);
        output.seekMap(psBinarySearchSeeker.getSeekMap());
        return;
      }
      output.seekMap(new SeekMap.Unseekable(durationReader.getDurationUs()));
    }
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    output = paramExtractorOutput;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a17 = a16\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    paramLong1 = timestampAdjuster.getTimestampOffsetUs();
    int j = 0;
    if (paramLong1 == -9223372036854775807L) {
      i = 1;
    } else {
      i = 0;
    }
    if ((i != 0) || ((timestampAdjuster.getFirstSampleTimestampUs() != 0L) && (timestampAdjuster.getFirstSampleTimestampUs() != paramLong2)))
    {
      timestampAdjuster.reset();
      timestampAdjuster.setFirstSampleTimestampUs(paramLong2);
    }
    int i = j;
    if (psBinarySearchSeeker != null)
    {
      psBinarySearchSeeker.setSeekTargetUs(paramLong2);
      i = j;
    }
    while (i < psPayloadReaders.size())
    {
      ((PesReader)psPayloadReaders.valueAt(i)).seek();
      i += 1;
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = new byte[14];
    paramExtractorInput.peekFully(arrayOfByte, 0, 14);
    if (442 != ((arrayOfByte[0] & 0xFF) << 24 | (arrayOfByte[1] & 0xFF) << 16 | (arrayOfByte[2] & 0xFF) << 8 | arrayOfByte[3] & 0xFF)) {
      return false;
    }
    if ((arrayOfByte[4] & 0xC4) != 68) {
      return false;
    }
    if ((arrayOfByte[6] & 0x4) != 4) {
      return false;
    }
    if ((arrayOfByte[8] & 0x4) != 4) {
      return false;
    }
    if ((arrayOfByte[9] & 0x1) != 1) {
      return false;
    }
    if ((arrayOfByte[12] & 0x3) != 3) {
      return false;
    }
    paramExtractorInput.advancePeekPosition(arrayOfByte[13] & 0x7);
    paramExtractorInput.peekFully(arrayOfByte, 0, 3);
    return 1 == ((arrayOfByte[0] & 0xFF) << 16 | (arrayOfByte[1] & 0xFF) << 8 | arrayOfByte[2] & 0xFF);
  }
  
  final class PesReader
  {
    private static final int PES_SCRATCH_SIZE = 64;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private final ParsableBitArray pesScratch;
    private boolean ptsFlag;
    private boolean seenFirstDts;
    private long timeUs;
    private final TimestampAdjuster timestampAdjuster;
    
    public PesReader(TimestampAdjuster paramTimestampAdjuster)
    {
      timestampAdjuster = paramTimestampAdjuster;
      pesScratch = new ParsableBitArray(new byte[64]);
    }
    
    private void parseHeader()
    {
      pesScratch.skipBits(8);
      ptsFlag = pesScratch.readBit();
      dtsFlag = pesScratch.readBit();
      pesScratch.skipBits(6);
      extendedHeaderLength = pesScratch.readBits(8);
    }
    
    private void parseHeaderExtension()
    {
      timeUs = 0L;
      if (ptsFlag)
      {
        pesScratch.skipBits(4);
        long l1 = pesScratch.readBits(3);
        pesScratch.skipBits(1);
        long l2 = pesScratch.readBits(15) << 15;
        pesScratch.skipBits(1);
        long l3 = pesScratch.readBits(15);
        pesScratch.skipBits(1);
        if ((!seenFirstDts) && (dtsFlag))
        {
          pesScratch.skipBits(4);
          long l4 = pesScratch.readBits(3);
          pesScratch.skipBits(1);
          long l5 = pesScratch.readBits(15) << 15;
          pesScratch.skipBits(1);
          long l6 = pesScratch.readBits(15);
          pesScratch.skipBits(1);
          timestampAdjuster.adjustTsTimestamp(l4 << 30 | l5 | l6);
          seenFirstDts = true;
        }
        timeUs = timestampAdjuster.adjustTsTimestamp(l1 << 30 | l2 | l3);
      }
    }
    
    public void consume(ParsableByteArray paramParsableByteArray)
      throws ParserException
    {
      paramParsableByteArray.readBytes(pesScratch.data, 0, 3);
      pesScratch.setPosition(0);
      parseHeader();
      paramParsableByteArray.readBytes(pesScratch.data, 0, extendedHeaderLength);
      pesScratch.setPosition(0);
      parseHeaderExtension();
      packetStarted(timeUs, true);
      PsExtractor.this.consume(paramParsableByteArray);
      packetFinished();
    }
    
    public void seek()
    {
      seenFirstDts = false;
      PsExtractor.this.seek();
    }
  }
}
