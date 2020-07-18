package com.airbnb.lottie.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.Drawable.Callback;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import androidx.annotation.Nullable;
import com.airbnb.lottie.ImageAssetDelegate;
import com.airbnb.lottie.LottieImageAsset;
import com.airbnb.lottie.utils.Logger;
import com.airbnb.lottie.utils.Utils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageAssetManager
{
  private static final Object bitmapHashLock = new Object();
  private final Context context;
  @Nullable
  private ImageAssetDelegate delegate;
  private final Map<String, LottieImageAsset> imageAssets;
  private String imagesFolder;
  
  public ImageAssetManager(Drawable.Callback paramCallback, String paramString, ImageAssetDelegate paramImageAssetDelegate, Map paramMap)
  {
    imagesFolder = paramString;
    if ((!TextUtils.isEmpty(paramString)) && (imagesFolder.charAt(imagesFolder.length() - 1) != '/'))
    {
      paramString = new StringBuilder();
      paramString.append(imagesFolder);
      paramString.append('/');
      imagesFolder = paramString.toString();
    }
    if (!(paramCallback instanceof View))
    {
      Logger.warning("LottieDrawable must be inside of a view for images to work.");
      imageAssets = new HashMap();
      context = null;
      return;
    }
    context = ((View)paramCallback).getContext();
    imageAssets = paramMap;
    setDelegate(paramImageAssetDelegate);
  }
  
  private Bitmap putBitmap(String paramString, Bitmap paramBitmap)
  {
    Object localObject = bitmapHashLock;
    try
    {
      ((LottieImageAsset)imageAssets.get(paramString)).setBitmap(paramBitmap);
      return paramBitmap;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public Bitmap bitmapForId(String paramString)
  {
    Object localObject1 = (LottieImageAsset)imageAssets.get(paramString);
    if (localObject1 == null) {
      return null;
    }
    Object localObject2 = ((LottieImageAsset)localObject1).getBitmap();
    if (localObject2 != null) {
      return localObject2;
    }
    if (delegate != null)
    {
      localObject1 = delegate.fetchBitmap((LottieImageAsset)localObject1);
      if (localObject1 != null)
      {
        putBitmap(paramString, (Bitmap)localObject1);
        return localObject1;
      }
    }
    else
    {
      Object localObject3 = ((LottieImageAsset)localObject1).getFileName();
      localObject2 = new BitmapFactory.Options();
      inScaled = true;
      inDensity = 160;
      if ((((String)localObject3).startsWith("data:")) && (((String)localObject3).indexOf("base64,") > 0)) {
        try
        {
          int i = ((String)localObject3).indexOf(',');
          localObject1 = Base64.decode(((String)localObject3).substring(i + 1), 0);
          return putBitmap(paramString, BitmapFactory.decodeByteArray((byte[])localObject1, 0, localObject1.length, (BitmapFactory.Options)localObject2));
        }
        catch (IllegalArgumentException paramString)
        {
          Logger.warning("data URL did not have correct base64 format.", paramString);
          return null;
        }
      }
      Object localObject4 = imagesFolder;
      try
      {
        boolean bool = TextUtils.isEmpty((CharSequence)localObject4);
        if (!bool)
        {
          localObject4 = context;
          localObject4 = ((Context)localObject4).getAssets();
          StringBuilder localStringBuilder = new StringBuilder();
          String str = imagesFolder;
          localStringBuilder.append(str);
          localStringBuilder.append((String)localObject3);
          localObject3 = ((AssetManager)localObject4).open(localStringBuilder.toString());
          return putBitmap(paramString, Utils.resizeBitmapIfNeeded(BitmapFactory.decodeStream((InputStream)localObject3, null, (BitmapFactory.Options)localObject2), ((LottieImageAsset)localObject1).getWidth(), ((LottieImageAsset)localObject1).getHeight()));
        }
        paramString = new IllegalStateException("You must set an images folder before loading an image. Set it with LottieComposition#setImagesFolder or LottieDrawable#setImagesFolder");
        throw paramString;
      }
      catch (IOException paramString)
      {
        Logger.warning("Unable to open asset.", paramString);
        return null;
      }
    }
    return localObject1;
  }
  
  public boolean hasSameContext(Context paramContext)
  {
    return ((paramContext == null) && (context == null)) || (context.equals(paramContext));
  }
  
  public void setDelegate(ImageAssetDelegate paramImageAssetDelegate)
  {
    delegate = paramImageAssetDelegate;
  }
  
  public Bitmap updateBitmap(String paramString, Bitmap paramBitmap)
  {
    if (paramBitmap == null)
    {
      paramString = (LottieImageAsset)imageAssets.get(paramString);
      paramBitmap = paramString.getBitmap();
      paramString.setBitmap(null);
      return paramBitmap;
    }
    Bitmap localBitmap = ((LottieImageAsset)imageAssets.get(paramString)).getBitmap();
    putBitmap(paramString, paramBitmap);
    return localBitmap;
  }
}
