package com.google.android.exoplayer2.extractor.configurations;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;

public abstract interface ElementaryStreamReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray)
    throws ParserException;
  
  public abstract void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator);
  
  public abstract void packetFinished();
  
  public abstract void packetStarted(long paramLong, boolean paramBoolean);
  
  public abstract void seek();
}
