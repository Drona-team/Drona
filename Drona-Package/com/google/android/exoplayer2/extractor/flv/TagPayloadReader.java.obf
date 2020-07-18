package com.google.android.exoplayer2.extractor.flv;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.util.ParsableByteArray;

abstract class TagPayloadReader
{
  protected final TrackOutput output;
  
  protected TagPayloadReader(TrackOutput paramTrackOutput)
  {
    output = paramTrackOutput;
  }
  
  public final void consume(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException
  {
    if (parseHeader(paramParsableByteArray)) {
      parsePayload(paramParsableByteArray, paramLong);
    }
  }
  
  protected abstract boolean parseHeader(ParsableByteArray paramParsableByteArray)
    throws ParserException;
  
  protected abstract void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong)
    throws ParserException;
  
  public abstract void seek();
  
  public static final class UnsupportedFormatException
    extends ParserException
  {
    public UnsupportedFormatException(String paramString)
    {
      super();
    }
  }
}
