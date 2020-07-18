package com.google.android.exoplayer2.extractor;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract interface Extractor
{
  public static final int RESULT_CONTINUE = 0;
  public static final int RESULT_END_OF_INPUT = -1;
  public static final int RESULT_SEEK = 1;
  
  public abstract void init(ExtractorOutput paramExtractorOutput);
  
  public abstract int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException;
  
  public abstract void release();
  
  public abstract void seek(long paramLong1, long paramLong2);
  
  public abstract boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ReadResult {}
}
