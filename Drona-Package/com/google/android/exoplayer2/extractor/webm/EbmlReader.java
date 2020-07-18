package com.google.android.exoplayer2.extractor.webm;

import com.google.android.exoplayer2.extractor.ExtractorInput;
import java.io.IOException;

abstract interface EbmlReader
{
  public abstract void init(EbmlReaderOutput paramEbmlReaderOutput);
  
  public abstract boolean read(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
  
  public abstract void reset();
}
