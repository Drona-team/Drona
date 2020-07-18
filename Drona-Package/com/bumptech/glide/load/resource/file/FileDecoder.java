package com.bumptech.glide.load.resource.file;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.io.File;

public class FileDecoder
  implements ResourceDecoder<File, File>
{
  public FileDecoder() {}
  
  public Resource decode(File paramFile, int paramInt1, int paramInt2, Options paramOptions)
  {
    return new FileResource(paramFile);
  }
  
  public boolean handles(File paramFile, Options paramOptions)
  {
    return true;
  }
}
