package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;

public class StreamAssetPathFetcher
  extends AssetPathFetcher<InputStream>
{
  public StreamAssetPathFetcher(AssetManager paramAssetManager, String paramString)
  {
    super(paramAssetManager, paramString);
  }
  
  protected void close(InputStream paramInputStream)
    throws IOException
  {
    paramInputStream.close();
  }
  
  public Class getDataClass()
  {
    return InputStream.class;
  }
  
  protected InputStream loadResource(AssetManager paramAssetManager, String paramString)
    throws IOException
  {
    return paramAssetManager.open(paramString);
  }
}
