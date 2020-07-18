package com.bumptech.glide.load.resource.drawable;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import java.util.List;

public class ResourceDrawableDecoder
  implements ResourceDecoder<Uri, Drawable>
{
  private static final String ANDROID_PACKAGE_NAME = "android";
  private static final int ID_PATH_SEGMENTS = 1;
  private static final int MISSING_RESOURCE_ID = 0;
  private static final int NAME_PATH_SEGMENT_INDEX = 1;
  private static final int NAME_URI_PATH_SEGMENTS = 2;
  private static final int RESOURCE_ID_SEGMENT_INDEX = 0;
  private static final int TYPE_PATH_SEGMENT_INDEX = 0;
  private final Context context;
  
  public ResourceDrawableDecoder(Context paramContext)
  {
    context = paramContext.getApplicationContext();
  }
  
  private Context findContextForPackage(Uri paramUri, String paramString)
  {
    if (paramString.equals(context.getPackageName())) {
      return context;
    }
    Context localContext = context;
    try
    {
      localContext = localContext.createPackageContext(paramString, 0);
      return localContext;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      if (paramString.contains(context.getPackageName())) {
        return context;
      }
      paramString = new StringBuilder();
      paramString.append("Failed to obtain context or unrecognized Uri format for: ");
      paramString.append(paramUri);
      throw new IllegalArgumentException(paramString.toString(), localNameNotFoundException);
    }
  }
  
  private int findResourceIdFromResourceIdUri(Uri paramUri)
  {
    Object localObject = paramUri.getPathSegments();
    try
    {
      localObject = ((List)localObject).get(0);
      localObject = (String)localObject;
      int i = Integer.parseInt((String)localObject);
      return i;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unrecognized Uri format: ");
      localStringBuilder.append(paramUri);
      throw new IllegalArgumentException(localStringBuilder.toString(), localNumberFormatException);
    }
  }
  
  private int findResourceIdFromTypeAndNameResourceUri(Context paramContext, Uri paramUri)
  {
    Object localObject = paramUri.getPathSegments();
    String str1 = paramUri.getAuthority();
    String str2 = (String)((List)localObject).get(0);
    localObject = (String)((List)localObject).get(1);
    int j = paramContext.getResources().getIdentifier((String)localObject, str2, str1);
    int i = j;
    if (j == 0) {
      i = Resources.getSystem().getIdentifier((String)localObject, str2, "android");
    }
    if (i != 0) {
      return i;
    }
    paramContext = new StringBuilder();
    paramContext.append("Failed to find resource id for: ");
    paramContext.append(paramUri);
    throw new IllegalArgumentException(paramContext.toString());
  }
  
  private int findResourceIdFromUri(Context paramContext, Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    if (localList.size() == 2) {
      return findResourceIdFromTypeAndNameResourceUri(paramContext, paramUri);
    }
    if (localList.size() == 1) {
      return findResourceIdFromResourceIdUri(paramUri);
    }
    paramContext = new StringBuilder();
    paramContext.append("Unrecognized Uri format: ");
    paramContext.append(paramUri);
    throw new IllegalArgumentException(paramContext.toString());
  }
  
  public Resource decode(Uri paramUri, int paramInt1, int paramInt2, Options paramOptions)
  {
    paramOptions = findContextForPackage(paramUri, paramUri.getAuthority());
    paramInt1 = findResourceIdFromUri(paramOptions, paramUri);
    return NonOwnedDrawableResource.newInstance(DrawableDecoderCompat.getDrawable(context, paramOptions, paramInt1));
  }
  
  public boolean handles(Uri paramUri, Options paramOptions)
  {
    return paramUri.getScheme().equals("android.resource");
  }
}
