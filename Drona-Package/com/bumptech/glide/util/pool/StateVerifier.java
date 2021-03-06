package com.bumptech.glide.util.pool;

public abstract class StateVerifier
{
  private static final boolean DEBUG = false;
  
  private StateVerifier() {}
  
  public static StateVerifier newInstance()
  {
    return new DefaultStateVerifier();
  }
  
  abstract void setRecycled(boolean paramBoolean);
  
  public abstract void throwIfRecycled();
  
  private static class DebugStateVerifier
    extends StateVerifier
  {
    private volatile RuntimeException recycledAtStackTraceException;
    
    DebugStateVerifier()
    {
      super();
    }
    
    void setRecycled(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        recycledAtStackTraceException = new RuntimeException("Released");
        return;
      }
      recycledAtStackTraceException = null;
    }
    
    public void throwIfRecycled()
    {
      if (recycledAtStackTraceException == null) {
        return;
      }
      throw new IllegalStateException("Already released", recycledAtStackTraceException);
    }
  }
  
  private static class DefaultStateVerifier
    extends StateVerifier
  {
    private volatile boolean isReleased;
    
    DefaultStateVerifier()
    {
      super();
    }
    
    public void setRecycled(boolean paramBoolean)
    {
      isReleased = paramBoolean;
    }
    
    public void throwIfRecycled()
    {
      if (!isReleased) {
        return;
      }
      throw new IllegalStateException("Already released");
    }
  }
}
