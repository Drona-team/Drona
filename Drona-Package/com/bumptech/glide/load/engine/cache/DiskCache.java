package com.bumptech.glide.load.engine.cache;

import com.bumptech.glide.load.Key;
import java.io.File;

public abstract interface DiskCache
{
  public abstract void clear();
  
  public abstract void delete(Key paramKey);
  
  public abstract File get(Key paramKey);
  
  public abstract void put(Key paramKey, Writer paramWriter);
  
  public static abstract interface Factory
  {
    public static final String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";
    public static final int DEFAULT_DISK_CACHE_SIZE = 262144000;
    
    public abstract DiskCache build();
  }
  
  public static abstract interface Writer
  {
    public abstract boolean write(File paramFile);
  }
}
