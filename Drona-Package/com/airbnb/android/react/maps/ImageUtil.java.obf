package com.airbnb.android.react.maps;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageUtil
{
  public ImageUtil() {}
  
  public static Bitmap convert(String paramString)
    throws IllegalArgumentException
  {
    paramString = Base64.decode(paramString.substring(paramString.indexOf(",") + 1), 0);
    return BitmapFactory.decodeByteArray(paramString, 0, paramString.length);
  }
  
  public static String convert(Bitmap paramBitmap)
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    paramBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
    return Base64.encodeToString(localByteArrayOutputStream.toByteArray(), 0);
  }
}
