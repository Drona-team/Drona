package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.FormatHolder;
import com.google.android.exoplayer2.decoder.DecoderInputBuffer;
import java.io.IOException;

public abstract interface SampleStream
{
  public abstract boolean isReady();
  
  public abstract void maybeThrowError()
    throws IOException;
  
  public abstract int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean);
  
  public abstract int skipData(long paramLong);
}
