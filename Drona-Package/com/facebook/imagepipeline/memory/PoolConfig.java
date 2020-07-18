package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import javax.annotation.concurrent.Immutable;

@Immutable
public class PoolConfig
{
  public static final int BITMAP_POOL_MAX_BITMAP_SIZE_DEFAULT = 4194304;
  private final int mBitmapPoolMaxBitmapSize;
  private final int mBitmapPoolMaxPoolSize;
  private final PoolParams mBitmapPoolParams;
  private final PoolStatsTracker mBitmapPoolStatsTracker;
  private final String mBitmapPoolType;
  private final PoolParams mFlexByteArrayPoolParams;
  private final PoolParams mMemoryChunkPoolParams;
  private final PoolStatsTracker mMemoryChunkPoolStatsTracker;
  private final MemoryTrimmableRegistry mMemoryTrimmableRegistry;
  private final boolean mRegisterLruBitmapPoolAsMemoryTrimmable;
  private final PoolParams mSmallByteArrayPoolParams;
  private final PoolStatsTracker mSmallByteArrayPoolStatsTracker;
  
  private PoolConfig(Builder paramBuilder)
  {
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.beginSection("PoolConfig()");
    }
    Object localObject;
    if (mBitmapPoolParams == null) {
      localObject = DefaultBitmapPoolParams.getBasePath();
    } else {
      localObject = mBitmapPoolParams;
    }
    mBitmapPoolParams = ((PoolParams)localObject);
    if (mBitmapPoolStatsTracker == null) {
      localObject = NoOpPoolStatsTracker.getInstance();
    } else {
      localObject = mBitmapPoolStatsTracker;
    }
    mBitmapPoolStatsTracker = ((PoolStatsTracker)localObject);
    if (mFlexByteArrayPoolParams == null) {
      localObject = DefaultFlexByteArrayPoolParams.log1p();
    } else {
      localObject = mFlexByteArrayPoolParams;
    }
    mFlexByteArrayPoolParams = ((PoolParams)localObject);
    if (mMemoryTrimmableRegistry == null) {
      localObject = NoOpMemoryTrimmableRegistry.getInstance();
    } else {
      localObject = mMemoryTrimmableRegistry;
    }
    mMemoryTrimmableRegistry = ((MemoryTrimmableRegistry)localObject);
    if (mMemoryChunkPoolParams == null) {
      localObject = DefaultNativeMemoryChunkPoolParams.updateStyle();
    } else {
      localObject = mMemoryChunkPoolParams;
    }
    mMemoryChunkPoolParams = ((PoolParams)localObject);
    if (mMemoryChunkPoolStatsTracker == null) {
      localObject = NoOpPoolStatsTracker.getInstance();
    } else {
      localObject = mMemoryChunkPoolStatsTracker;
    }
    mMemoryChunkPoolStatsTracker = ((PoolStatsTracker)localObject);
    if (mSmallByteArrayPoolParams == null) {
      localObject = DefaultByteArrayPoolParams.get();
    } else {
      localObject = mSmallByteArrayPoolParams;
    }
    mSmallByteArrayPoolParams = ((PoolParams)localObject);
    if (mSmallByteArrayPoolStatsTracker == null) {
      localObject = NoOpPoolStatsTracker.getInstance();
    } else {
      localObject = mSmallByteArrayPoolStatsTracker;
    }
    mSmallByteArrayPoolStatsTracker = ((PoolStatsTracker)localObject);
    if (mBitmapPoolType == null) {
      localObject = "legacy";
    } else {
      localObject = mBitmapPoolType;
    }
    mBitmapPoolType = ((String)localObject);
    mBitmapPoolMaxPoolSize = mBitmapPoolMaxPoolSize;
    int i;
    if (mBitmapPoolMaxBitmapSize > 0) {
      i = mBitmapPoolMaxBitmapSize;
    } else {
      i = 4194304;
    }
    mBitmapPoolMaxBitmapSize = i;
    mRegisterLruBitmapPoolAsMemoryTrimmable = mRegisterLruBitmapPoolAsMemoryTrimmable;
    if (FrescoSystrace.isTracing()) {
      FrescoSystrace.endSection();
    }
  }
  
  public static Builder newBuilder()
  {
    return new Builder(null);
  }
  
  public int getBitmapPoolMaxBitmapSize()
  {
    return mBitmapPoolMaxBitmapSize;
  }
  
  public int getBitmapPoolMaxPoolSize()
  {
    return mBitmapPoolMaxPoolSize;
  }
  
  public PoolParams getBitmapPoolParams()
  {
    return mBitmapPoolParams;
  }
  
  public PoolStatsTracker getBitmapPoolStatsTracker()
  {
    return mBitmapPoolStatsTracker;
  }
  
  public String getBitmapPoolType()
  {
    return mBitmapPoolType;
  }
  
  public PoolParams getFlexByteArrayPoolParams()
  {
    return mFlexByteArrayPoolParams;
  }
  
  public PoolParams getMemoryChunkPoolParams()
  {
    return mMemoryChunkPoolParams;
  }
  
  public PoolStatsTracker getMemoryChunkPoolStatsTracker()
  {
    return mMemoryChunkPoolStatsTracker;
  }
  
  public MemoryTrimmableRegistry getMemoryTrimmableRegistry()
  {
    return mMemoryTrimmableRegistry;
  }
  
  public PoolParams getSmallByteArrayPoolParams()
  {
    return mSmallByteArrayPoolParams;
  }
  
  public PoolStatsTracker getSmallByteArrayPoolStatsTracker()
  {
    return mSmallByteArrayPoolStatsTracker;
  }
  
  public boolean isRegisterLruBitmapPoolAsMemoryTrimmable()
  {
    return mRegisterLruBitmapPoolAsMemoryTrimmable;
  }
  
  public static class Builder
  {
    private int mBitmapPoolMaxBitmapSize;
    private int mBitmapPoolMaxPoolSize;
    private PoolParams mBitmapPoolParams;
    private PoolStatsTracker mBitmapPoolStatsTracker;
    private String mBitmapPoolType;
    private PoolParams mFlexByteArrayPoolParams;
    private PoolParams mMemoryChunkPoolParams;
    private PoolStatsTracker mMemoryChunkPoolStatsTracker;
    private MemoryTrimmableRegistry mMemoryTrimmableRegistry;
    private boolean mRegisterLruBitmapPoolAsMemoryTrimmable;
    private PoolParams mSmallByteArrayPoolParams;
    private PoolStatsTracker mSmallByteArrayPoolStatsTracker;
    
    private Builder() {}
    
    public PoolConfig build()
    {
      return new PoolConfig(this, null);
    }
    
    public Builder setBitmapPoolMaxBitmapSize(int paramInt)
    {
      mBitmapPoolMaxBitmapSize = paramInt;
      return this;
    }
    
    public Builder setBitmapPoolMaxPoolSize(int paramInt)
    {
      mBitmapPoolMaxPoolSize = paramInt;
      return this;
    }
    
    public Builder setBitmapPoolParams(PoolParams paramPoolParams)
    {
      mBitmapPoolParams = ((PoolParams)Preconditions.checkNotNull(paramPoolParams));
      return this;
    }
    
    public Builder setBitmapPoolStatsTracker(PoolStatsTracker paramPoolStatsTracker)
    {
      mBitmapPoolStatsTracker = ((PoolStatsTracker)Preconditions.checkNotNull(paramPoolStatsTracker));
      return this;
    }
    
    public Builder setBitmapPoolType(String paramString)
    {
      mBitmapPoolType = paramString;
      return this;
    }
    
    public Builder setFlexByteArrayPoolParams(PoolParams paramPoolParams)
    {
      mFlexByteArrayPoolParams = paramPoolParams;
      return this;
    }
    
    public Builder setMemoryTrimmableRegistry(MemoryTrimmableRegistry paramMemoryTrimmableRegistry)
    {
      mMemoryTrimmableRegistry = paramMemoryTrimmableRegistry;
      return this;
    }
    
    public Builder setNativeMemoryChunkPoolParams(PoolParams paramPoolParams)
    {
      mMemoryChunkPoolParams = ((PoolParams)Preconditions.checkNotNull(paramPoolParams));
      return this;
    }
    
    public Builder setNativeMemoryChunkPoolStatsTracker(PoolStatsTracker paramPoolStatsTracker)
    {
      mMemoryChunkPoolStatsTracker = ((PoolStatsTracker)Preconditions.checkNotNull(paramPoolStatsTracker));
      return this;
    }
    
    public void setRegisterLruBitmapPoolAsMemoryTrimmable(boolean paramBoolean)
    {
      mRegisterLruBitmapPoolAsMemoryTrimmable = paramBoolean;
    }
    
    public Builder setSmallByteArrayPoolParams(PoolParams paramPoolParams)
    {
      mSmallByteArrayPoolParams = ((PoolParams)Preconditions.checkNotNull(paramPoolParams));
      return this;
    }
    
    public Builder setSmallByteArrayPoolStatsTracker(PoolStatsTracker paramPoolStatsTracker)
    {
      mSmallByteArrayPoolStatsTracker = ((PoolStatsTracker)Preconditions.checkNotNull(paramPoolStatsTracker));
      return this;
    }
  }
}