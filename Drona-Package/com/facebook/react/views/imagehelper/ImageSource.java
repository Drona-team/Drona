package com.facebook.react.views.imagehelper;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;

public class ImageSource
{
  private boolean isResource;
  private double mSize;
  private String mSource;
  @Nullable
  private Uri mUri;
  
  public ImageSource(Context paramContext, String paramString)
  {
    this(paramContext, paramString, 0.0D, 0.0D);
  }
  
  public ImageSource(Context paramContext, String paramString, double paramDouble1, double paramDouble2)
  {
    mSource = paramString;
    mSize = (paramDouble1 * paramDouble2);
    mUri = computeUri(paramContext);
  }
  
  private Uri computeLocalUri(Context paramContext)
  {
    isResource = true;
    return ResourceDrawableIdHelper.getInstance().getResourceDrawableUri(paramContext, mSource);
  }
  
  private Uri computeUri(Context paramContext)
  {
    Object localObject = mSource;
    try
    {
      localObject = Uri.parse((String)localObject);
      String str = ((Uri)localObject).getScheme();
      if (str != null) {
        return localObject;
      }
      localObject = computeLocalUri(paramContext);
      return localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
      return localException;
    }
    return computeLocalUri(paramContext);
  }
  
  public double getSize()
  {
    return mSize;
  }
  
  public String getSource()
  {
    return mSource;
  }
  
  public Uri getUri()
  {
    return (Uri)Assertions.assertNotNull(mUri);
  }
  
  public boolean isResource()
  {
    return isResource;
  }
}
