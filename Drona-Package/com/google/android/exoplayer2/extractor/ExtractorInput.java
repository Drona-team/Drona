package com.google.android.exoplayer2.extractor;

import java.io.IOException;

public abstract interface ExtractorInput
{
  public abstract void advancePeekPosition(int paramInt)
    throws IOException, InterruptedException;
  
  public abstract boolean advancePeekPosition(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException;
  
  public abstract long getLength();
  
  public abstract long getPeekPosition();
  
  public abstract long getPosition();
  
  public abstract void peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException;
  
  public abstract boolean peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException;
  
  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException;
  
  public abstract void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException, InterruptedException;
  
  public abstract boolean readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
    throws IOException, InterruptedException;
  
  public abstract void resetPeekPosition();
  
  public abstract void setRetryPosition(long paramLong, Throwable paramThrowable)
    throws Throwable;
  
  public abstract int skip(int paramInt)
    throws IOException, InterruptedException;
  
  public abstract void skipFully(int paramInt)
    throws IOException, InterruptedException;
  
  public abstract boolean skipFully(int paramInt, boolean paramBoolean)
    throws IOException, InterruptedException;
}
