package com.bumptech.glide.load.data.mediastore;

import java.io.File;

class FileService
{
  FileService() {}
  
  public boolean exists(File paramFile)
  {
    return paramFile.exists();
  }
  
  public File get(String paramString)
  {
    return new File(paramString);
  }
  
  public long length(File paramFile)
  {
    return paramFile.length();
  }
}
