package com.google.android.exoplayer2.upstream;

import java.io.IOException;

public abstract interface LoaderErrorThrower
{
  public abstract void maybeThrowError()
    throws IOException;
  
  public abstract void maybeThrowError(int paramInt)
    throws IOException;
  
  public static final class Dummy
    implements LoaderErrorThrower
  {
    public Dummy() {}
    
    public void maybeThrowError()
      throws IOException
    {}
    
    public void maybeThrowError(int paramInt)
      throws IOException
    {}
  }
}
