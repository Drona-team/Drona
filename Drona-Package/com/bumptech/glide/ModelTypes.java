package com.bumptech.glide;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import java.io.File;
import java.net.URL;

abstract interface ModelTypes<T>
{
  public abstract Object load(Bitmap paramBitmap);
  
  public abstract Object load(Drawable paramDrawable);
  
  public abstract Object load(Uri paramUri);
  
  public abstract Object load(File paramFile);
  
  public abstract Object load(Integer paramInteger);
  
  public abstract Object load(Object paramObject);
  
  public abstract Object load(String paramString);
  
  public abstract Object load(URL paramURL);
  
  public abstract Object load(byte[] paramArrayOfByte);
}
