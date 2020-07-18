package androidx.core.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.CancellationSignal;
import android.os.Handler;
import androidx.collection.LruCache;
import androidx.core.content.delay.FontResourcesParserCompat.FamilyResourceEntry;
import androidx.core.content.delay.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import androidx.core.content.delay.FontResourcesParserCompat.ProviderResourceEntry;
import androidx.core.content.delay.ResourcesCompat.FontCallback;
import androidx.core.provider.FontsContractCompat;
import androidx.core.provider.FontsContractCompat.FontInfo;

public class TypefaceCompat
{
  private static final LruCache<String, Typeface> sTypefaceCache = new LruCache(16);
  private static final TypefaceCompatBaseImpl sTypefaceCompatImpl;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 28) {
      sTypefaceCompatImpl = new TypefaceCompatApi28Impl();
    } else if (Build.VERSION.SDK_INT >= 26) {
      sTypefaceCompatImpl = new TypefaceCompatApi26Impl();
    } else if ((Build.VERSION.SDK_INT >= 24) && (TypefaceCompatApi24Impl.isUsable())) {
      sTypefaceCompatImpl = new TypefaceCompatApi24Impl();
    } else if (Build.VERSION.SDK_INT >= 21) {
      sTypefaceCompatImpl = new TypefaceCompatApi21Impl();
    } else {
      sTypefaceCompatImpl = new TypefaceCompatBaseImpl();
    }
  }
  
  private TypefaceCompat() {}
  
  public static Typeface create(Context paramContext, Typeface paramTypeface, int paramInt)
  {
    if (paramContext != null)
    {
      if (Build.VERSION.SDK_INT < 21)
      {
        paramContext = getBestFontFromFamily(paramContext, paramTypeface, paramInt);
        if (paramContext != null) {
          return paramContext;
        }
      }
      return Typeface.create(paramTypeface, paramInt);
    }
    throw new IllegalArgumentException("Context cannot be null");
  }
  
  public static Typeface createFromFontInfo(Context paramContext, CancellationSignal paramCancellationSignal, FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    return sTypefaceCompatImpl.createFromFontInfo(paramContext, paramCancellationSignal, paramArrayOfFontInfo, paramInt);
  }
  
  public static Typeface createFromResourcesFamilyXml(Context paramContext, FontResourcesParserCompat.FamilyResourceEntry paramFamilyResourceEntry, Resources paramResources, int paramInt1, int paramInt2, ResourcesCompat.FontCallback paramFontCallback, Handler paramHandler, boolean paramBoolean)
  {
    if ((paramFamilyResourceEntry instanceof FontResourcesParserCompat.ProviderResourceEntry))
    {
      paramFamilyResourceEntry = (FontResourcesParserCompat.ProviderResourceEntry)paramFamilyResourceEntry;
      boolean bool = false;
      if (paramBoolean)
      {
        if (paramFamilyResourceEntry.getFetchStrategy() != 0) {}
      }
      else {
        while (paramFontCallback == null)
        {
          bool = true;
          break;
        }
      }
      int i;
      if (paramBoolean) {
        i = paramFamilyResourceEntry.getTimeout();
      } else {
        i = -1;
      }
      paramFamilyResourceEntry = FontsContractCompat.getFontSync(paramContext, paramFamilyResourceEntry.getRequest(), paramFontCallback, paramHandler, bool, i, paramInt2);
    }
    else
    {
      Typeface localTypeface = sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(paramContext, (FontResourcesParserCompat.FontFamilyFilesResourceEntry)paramFamilyResourceEntry, paramResources, paramInt2);
      paramContext = localTypeface;
      paramFamilyResourceEntry = paramContext;
      if (paramFontCallback != null) {
        if (localTypeface != null)
        {
          paramFontCallback.callbackSuccessAsync(localTypeface, paramHandler);
          paramFamilyResourceEntry = paramContext;
        }
        else
        {
          paramFontCallback.callbackFailAsync(-3, paramHandler);
          paramFamilyResourceEntry = paramContext;
        }
      }
    }
    if (paramFamilyResourceEntry != null) {
      sTypefaceCache.put(createResourceUid(paramResources, paramInt1, paramInt2), paramFamilyResourceEntry);
    }
    return paramFamilyResourceEntry;
  }
  
  public static Typeface createFromResourcesFontFile(Context paramContext, Resources paramResources, int paramInt1, String paramString, int paramInt2)
  {
    paramContext = sTypefaceCompatImpl.createFromResourcesFontFile(paramContext, paramResources, paramInt1, paramString, paramInt2);
    if (paramContext != null)
    {
      paramResources = createResourceUid(paramResources, paramInt1, paramInt2);
      sTypefaceCache.put(paramResources, paramContext);
    }
    return paramContext;
  }
  
  private static String createResourceUid(Resources paramResources, int paramInt1, int paramInt2)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramResources.getResourcePackageName(paramInt1));
    localStringBuilder.append("-");
    localStringBuilder.append(paramInt1);
    localStringBuilder.append("-");
    localStringBuilder.append(paramInt2);
    return localStringBuilder.toString();
  }
  
  public static Typeface findFromCache(Resources paramResources, int paramInt1, int paramInt2)
  {
    return (Typeface)sTypefaceCache.get(createResourceUid(paramResources, paramInt1, paramInt2));
  }
  
  private static Typeface getBestFontFromFamily(Context paramContext, Typeface paramTypeface, int paramInt)
  {
    paramTypeface = sTypefaceCompatImpl.getFontFamily(paramTypeface);
    if (paramTypeface == null) {
      return null;
    }
    return sTypefaceCompatImpl.createFromFontFamilyFilesResourceEntry(paramContext, paramTypeface, paramContext.getResources(), paramInt);
  }
}
