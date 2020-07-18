package com.bumptech.glide.module;

import android.content.Context;
import com.bumptech.glide.GlideBuilder;

public abstract class AppGlideModule
  extends LibraryGlideModule
  implements AppliesOptions
{
  public AppGlideModule() {}
  
  public void applyOptions(Context paramContext, GlideBuilder paramGlideBuilder) {}
  
  public boolean isManifestParsingEnabled()
  {
    return true;
  }
}
