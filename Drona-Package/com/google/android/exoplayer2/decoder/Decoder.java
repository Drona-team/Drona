package com.google.android.exoplayer2.decoder;

public abstract interface Decoder<I, O, E extends Exception>
{
  public abstract Object dequeueInputBuffer()
    throws Exception;
  
  public abstract Object dequeueOutputBuffer()
    throws Exception;
  
  public abstract void flush();
  
  public abstract String getName();
  
  public abstract void queueInputBuffer(Object paramObject)
    throws Exception;
  
  public abstract void release();
}
