package com.bumptech.glide.provider;

import com.bumptech.glide.load.ImageHeaderParser;
import java.util.ArrayList;
import java.util.List;

public final class ImageHeaderParserRegistry
{
  private final List<ImageHeaderParser> parsers = new ArrayList();
  
  public ImageHeaderParserRegistry() {}
  
  public List getParsers()
  {
    try
    {
      List localList = parsers;
      return localList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void sendPacket(ImageHeaderParser paramImageHeaderParser)
  {
    try
    {
      parsers.add(paramImageHeaderParser);
      return;
    }
    catch (Throwable paramImageHeaderParser)
    {
      throw paramImageHeaderParser;
    }
  }
}
