package com.google.android.exoplayer2.decoder;

public abstract class Buffer
{
  private int flags;
  
  public Buffer() {}
  
  public final void addFlag(int paramInt)
  {
    flags = (paramInt | flags);
  }
  
  public void clear()
  {
    flags = 0;
  }
  
  public final void clearFlag(int paramInt)
  {
    flags = (paramInt & flags);
  }
  
  protected final boolean getFlag(int paramInt)
  {
    return (flags & paramInt) == paramInt;
  }
  
  public final boolean isDecodeOnly()
  {
    return getFlag(Integer.MIN_VALUE);
  }
  
  public final boolean isEndOfStream()
  {
    return getFlag(4);
  }
  
  public final boolean isKeyFrame()
  {
    return getFlag(1);
  }
  
  public final void setFlags(int paramInt)
  {
    flags = paramInt;
  }
}
