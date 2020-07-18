package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.io.IOException;

public class OggExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = -..Lambda.OggExtractor.Ibu4KG2n586HVQ8R-UQJ8hUhsso.INSTANCE;
  private static final int MAX_VERIFICATION_BYTES = 8;
  private ExtractorOutput output;
  private StreamReader streamReader;
  private boolean streamReaderInitialized;
  
  public OggExtractor() {}
  
  private static ParsableByteArray resetPosition(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(0);
    return paramParsableByteArray;
  }
  
  private boolean sniffInternal(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    Object localObject = new OggPageHeader();
    if (((OggPageHeader)localObject).populate(paramExtractorInput, true))
    {
      if ((type & 0x2) != 2) {
        return false;
      }
      int i = Math.min(bodySize, 8);
      localObject = new ParsableByteArray(i);
      paramExtractorInput.peekFully(data, 0, i);
      if (FlacReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
      {
        streamReader = new FlacReader();
        return true;
      }
      if (VorbisReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
      {
        streamReader = new VorbisReader();
        return true;
      }
      if (OpusReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
      {
        streamReader = new OpusReader();
        return true;
      }
    }
    return false;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    output = paramExtractorOutput;
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    if (streamReader == null) {
      if (sniffInternal(paramExtractorInput)) {
        paramExtractorInput.resetPeekPosition();
      } else {
        throw new ParserException("Failed to determine bitstream type");
      }
    }
    if (!streamReaderInitialized)
    {
      TrackOutput localTrackOutput = output.track(0, 1);
      output.endTracks();
      streamReader.init(output, localTrackOutput);
      streamReaderInitialized = true;
    }
    return streamReader.read(paramExtractorInput, paramPositionHolder);
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    if (streamReader != null) {
      streamReader.seek(paramLong1, paramLong2);
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    try
    {
      boolean bool = sniffInternal(paramExtractorInput);
      return bool;
    }
    catch (ParserException paramExtractorInput)
    {
      for (;;) {}
    }
    return false;
  }
}
