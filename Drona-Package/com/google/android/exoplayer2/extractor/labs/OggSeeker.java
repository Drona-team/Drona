package com.google.android.exoplayer2.extractor.labs;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.SeekMap;
import java.io.IOException;

abstract interface OggSeeker
{
  public abstract SeekMap createSeekMap();
  
  public abstract long read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
  
  public abstract long startSeek(long paramLong);
}
