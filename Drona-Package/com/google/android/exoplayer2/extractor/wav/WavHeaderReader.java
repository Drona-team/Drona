package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.WavUtil;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

final class WavHeaderReader
{
  private static final String PAGE_KEY = "WavHeaderReader";
  
  private WavHeaderReader() {}
  
  public static WavHeader peek(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    ParsableByteArray localParsableByteArray = new ParsableByteArray(16);
    if (peektail != WavUtil.RIFF_FOURCC) {
      return null;
    }
    paramExtractorInput.peekFully(data, 0, 4);
    localParsableByteArray.setPosition(0);
    int i = localParsableByteArray.readInt();
    if (i != WavUtil.WAVE_FOURCC)
    {
      paramExtractorInput = new StringBuilder();
      paramExtractorInput.append("Unsupported RIFF format: ");
      paramExtractorInput.append(i);
      Log.e("WavHeaderReader", paramExtractorInput.toString());
      return null;
    }
    for (ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray); tail != WavUtil.FMT_FOURCC; localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray)) {
      paramExtractorInput.advancePeekPosition((int)size);
    }
    boolean bool;
    if (size >= 16L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    paramExtractorInput.peekFully(data, 0, 16);
    localParsableByteArray.setPosition(0);
    i = localParsableByteArray.readLittleEndianUnsignedShort();
    int j = localParsableByteArray.readLittleEndianUnsignedShort();
    int k = localParsableByteArray.readLittleEndianUnsignedIntToInt();
    int m = localParsableByteArray.readLittleEndianUnsignedIntToInt();
    int n = localParsableByteArray.readLittleEndianUnsignedShort();
    int i1 = localParsableByteArray.readLittleEndianUnsignedShort();
    int i2 = j * i1 / 8;
    if (n == i2)
    {
      i2 = WavUtil.getEncodingForType(i, i1);
      if (i2 == 0)
      {
        paramExtractorInput = new StringBuilder();
        paramExtractorInput.append("Unsupported WAV format: ");
        paramExtractorInput.append(i1);
        paramExtractorInput.append(" bit/sample, type ");
        paramExtractorInput.append(i);
        Log.e("WavHeaderReader", paramExtractorInput.toString());
        return null;
      }
      paramExtractorInput.advancePeekPosition((int)size - 16);
      return new WavHeader(j, k, m, n, i1, i2);
    }
    paramExtractorInput = new StringBuilder();
    paramExtractorInput.append("Expected block alignment: ");
    paramExtractorInput.append(i2);
    paramExtractorInput.append("; got: ");
    paramExtractorInput.append(n);
    throw new ParserException(paramExtractorInput.toString());
  }
  
  public static void skipToData(ExtractorInput paramExtractorInput, WavHeader paramWavHeader)
    throws IOException, InterruptedException
  {
    Assertions.checkNotNull(paramExtractorInput);
    Assertions.checkNotNull(paramWavHeader);
    paramExtractorInput.resetPeekPosition();
    ParsableByteArray localParsableByteArray = new ParsableByteArray(8);
    ChunkHeader localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray);
    while (tail != Util.getIntegerCodeForString("data"))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Ignoring unknown WAV chunk: ");
      localStringBuilder.append(tail);
      Log.w("WavHeaderReader", localStringBuilder.toString());
      long l = size + 8L;
      if (tail == Util.getIntegerCodeForString("RIFF")) {
        l = 12L;
      }
      if (l <= 2147483647L)
      {
        paramExtractorInput.skipFully((int)l);
        localChunkHeader = ChunkHeader.peek(paramExtractorInput, localParsableByteArray);
      }
      else
      {
        paramExtractorInput = new StringBuilder();
        paramExtractorInput.append("Chunk is too large (~2GB+) to skip; id: ");
        paramExtractorInput.append(tail);
        throw new ParserException(paramExtractorInput.toString());
      }
    }
    paramExtractorInput.skipFully(8);
    paramWavHeader.setDataBounds(paramExtractorInput.getPosition(), size);
  }
  
  private static final class ChunkHeader
  {
    public static final int SIZE_IN_BYTES = 8;
    public final long size;
    public final int tail;
    
    private ChunkHeader(int paramInt, long paramLong)
    {
      tail = paramInt;
      size = paramLong;
    }
    
    public static ChunkHeader peek(ExtractorInput paramExtractorInput, ParsableByteArray paramParsableByteArray)
      throws IOException, InterruptedException
    {
      paramExtractorInput.peekFully(data, 0, 8);
      paramParsableByteArray.setPosition(0);
      return new ChunkHeader(paramParsableByteArray.readInt(), paramParsableByteArray.readLittleEndianUnsignedInt());
    }
  }
}
