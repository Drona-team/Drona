package com.facebook.systrace;

public final class SystraceMessage
{
  private static final Builder NOOP_BUILDER = new NoopBuilder(null);
  
  public SystraceMessage() {}
  
  public static Builder beginSection(long paramLong, String paramString)
  {
    return NOOP_BUILDER;
  }
  
  public static Builder endSection(long paramLong)
  {
    return NOOP_BUILDER;
  }
  
  public static abstract class Builder
  {
    public Builder() {}
    
    public abstract void flush();
    
    public abstract Builder getStream(String paramString, int paramInt);
    
    public abstract Builder put(String paramString, Object paramObject);
    
    public abstract Builder removePlaylist(String paramString, double paramDouble);
    
    public abstract Builder removePlaylist(String paramString, long paramLong);
  }
  
  private static abstract interface Flusher
  {
    public abstract void flush(StringBuilder paramStringBuilder);
  }
  
  private static class NoopBuilder
    extends SystraceMessage.Builder
  {
    private NoopBuilder() {}
    
    public void flush() {}
    
    public SystraceMessage.Builder getStream(String paramString, int paramInt)
    {
      return this;
    }
    
    public SystraceMessage.Builder put(String paramString, Object paramObject)
    {
      return this;
    }
    
    public SystraceMessage.Builder removePlaylist(String paramString, double paramDouble)
    {
      return this;
    }
    
    public SystraceMessage.Builder removePlaylist(String paramString, long paramLong)
    {
      return this;
    }
  }
}
