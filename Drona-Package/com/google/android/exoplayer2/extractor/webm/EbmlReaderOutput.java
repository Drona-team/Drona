package com.google.android.exoplayer2.extractor.webm;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

abstract interface EbmlReaderOutput
{
  public static final int TYPE_BINARY = 4;
  public static final int TYPE_FLOAT = 5;
  public static final int TYPE_MASTER = 1;
  public static final int TYPE_STRING = 3;
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_UNSIGNED_INT = 2;
  
  public abstract void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException;
  
  public abstract void endMasterElement(int paramInt)
    throws ParserException;
  
  public abstract void floatElement(int paramInt, double paramDouble)
    throws ParserException;
  
  public abstract int getElementType(int paramInt);
  
  public abstract void integerElement(int paramInt, long paramLong)
    throws ParserException;
  
  public abstract boolean isLevel1Element(int paramInt);
  
  public abstract void startMasterElement(int paramInt, long paramLong1, long paramLong2)
    throws ParserException;
  
  public abstract void stringElement(int paramInt, String paramString)
    throws ParserException;
  
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  public @interface ElementType {}
}
