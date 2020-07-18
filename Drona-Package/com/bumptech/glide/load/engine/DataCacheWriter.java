package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.cache.DiskCache.Writer;
import java.io.File;

class DataCacheWriter<DataType>
  implements DiskCache.Writer
{
  private final DataType data;
  private final Encoder<DataType> encoder;
  private final Options options;
  
  DataCacheWriter(Encoder paramEncoder, Object paramObject, Options paramOptions)
  {
    encoder = paramEncoder;
    data = paramObject;
    options = paramOptions;
  }
  
  public boolean write(File paramFile)
  {
    return encoder.encode(data, paramFile, options);
  }
}
