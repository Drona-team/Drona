package com.google.android.exoplayer2.extractor.wav;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.Assertions;
import java.io.IOException;

public final class WavExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = -..Lambda.WavExtractor.5r6M_S0QCNNj_Xavzq9WwuFHep0.INSTANCE;
  private static final int MAX_INPUT_SIZE = 32768;
  private int bytesPerFrame;
  private ExtractorOutput extractorOutput;
  private int pendingBytes;
  private TrackOutput trackOutput;
  private WavHeader wavHeader;
  
  public WavExtractor() {}
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    extractorOutput = paramExtractorOutput;
    trackOutput = paramExtractorOutput.track(0, 1);
    wavHeader = null;
    paramExtractorOutput.endTracks();
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (wavHeader == null)
    {
      wavHeader = WavHeaderReader.peek(paramExtractorInput);
      if (wavHeader != null)
      {
        paramPositionHolder = Format.createAudioSampleFormat(null, "audio/raw", null, wavHeader.getBitrate(), 32768, wavHeader.getNumChannels(), wavHeader.getSampleRateHz(), wavHeader.getEncoding(), null, null, 0, null);
        trackOutput.format(paramPositionHolder);
        bytesPerFrame = wavHeader.getBytesPerFrame();
      }
      else
      {
        throw new ParserException("Unsupported or unrecognized wav header.");
      }
    }
    if (!wavHeader.hasDataBounds())
    {
      WavHeaderReader.skipToData(paramExtractorInput, wavHeader);
      extractorOutput.seekMap(wavHeader);
    }
    long l = wavHeader.getDataLimit();
    boolean bool;
    if (l != -1L) {
      bool = true;
    } else {
      bool = false;
    }
    Assertions.checkState(bool);
    l -= paramExtractorInput.getPosition();
    if (l <= 0L) {
      return -1;
    }
    int i = (int)Math.min(32768 - pendingBytes, l);
    i = trackOutput.sampleData(paramExtractorInput, i, true);
    if (i != -1) {
      pendingBytes += i;
    }
    int j = pendingBytes / bytesPerFrame;
    if (j > 0)
    {
      l = wavHeader.getTimeUs(paramExtractorInput.getPosition() - pendingBytes);
      j *= bytesPerFrame;
      pendingBytes -= j;
      trackOutput.sampleMetadata(l, 1, j, pendingBytes, null);
    }
    if (i == -1) {
      return -1;
    }
    return 0;
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    pendingBytes = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    return WavHeaderReader.peek(paramExtractorInput) != null;
  }
}
