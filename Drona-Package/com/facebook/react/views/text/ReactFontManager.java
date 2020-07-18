package com.facebook.react.views.text;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.SparseArray;
import androidx.core.content.delay.ResourcesCompat;
import java.util.HashMap;
import java.util.Map;

public class ReactFontManager
{
  private static final String[] EXTENSIONS = { "", "_bold", "_italic", "_bold_italic" };
  private static final String[] FILE_EXTENSIONS = { ".ttf", ".otf" };
  private static final String FONTS_ASSET_PATH = "fonts/";
  private static ReactFontManager sReactFontManagerInstance;
  private final Map<String, Typeface> mCustomTypefaceCache = new HashMap();
  private final Map<String, FontFamily> mFontCache = new HashMap();
  
  private ReactFontManager() {}
  
  private static Typeface createTypeface(String paramString, int paramInt, AssetManager paramAssetManager)
  {
    String str = EXTENSIONS[paramInt];
    String[] arrayOfString = FILE_EXTENSIONS;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      Object localObject = arrayOfString[i];
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("fonts/");
      localStringBuilder.append(paramString);
      localStringBuilder.append(str);
      localStringBuilder.append((String)localObject);
      localObject = localStringBuilder.toString();
      try
      {
        localObject = Typeface.createFromAsset(paramAssetManager, (String)localObject);
        return localObject;
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;) {}
      }
      i += 1;
    }
    return Typeface.create(paramString, paramInt);
  }
  
  public static ReactFontManager getInstance()
  {
    if (sReactFontManagerInstance == null) {
      sReactFontManagerInstance = new ReactFontManager();
    }
    return sReactFontManagerInstance;
  }
  
  public void addCustomFont(Context paramContext, String paramString, int paramInt)
  {
    paramContext = ResourcesCompat.getFont(paramContext, paramInt);
    if (paramContext != null) {
      mCustomTypefaceCache.put(paramString, paramContext);
    }
  }
  
  public Typeface getTypeface(String paramString, int paramInt1, int paramInt2, AssetManager paramAssetManager)
  {
    if (mCustomTypefaceCache.containsKey(paramString))
    {
      paramString = (Typeface)mCustomTypefaceCache.get(paramString);
      if ((Build.VERSION.SDK_INT >= 28) && (paramInt2 >= 100) && (paramInt2 <= 1000))
      {
        boolean bool;
        if ((paramInt1 & 0x2) != 0) {
          bool = true;
        } else {
          bool = false;
        }
        return Typeface.create(paramString, paramInt2, bool);
      }
      return Typeface.create(paramString, paramInt1);
    }
    Object localObject2 = (FontFamily)mFontCache.get(paramString);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      localObject1 = new FontFamily(null);
      mFontCache.put(paramString, localObject1);
    }
    Typeface localTypeface = ((FontFamily)localObject1).getTypeface(paramInt1);
    localObject2 = localTypeface;
    if (localTypeface == null)
    {
      paramString = createTypeface(paramString, paramInt1, paramAssetManager);
      localObject2 = paramString;
      if (paramString != null)
      {
        ((FontFamily)localObject1).setTypeface(paramInt1, paramString);
        localObject2 = paramString;
      }
    }
    return localObject2;
  }
  
  public Typeface getTypeface(String paramString, int paramInt, AssetManager paramAssetManager)
  {
    return getTypeface(paramString, paramInt, 0, paramAssetManager);
  }
  
  public void setTypeface(String paramString, int paramInt, Typeface paramTypeface)
  {
    if (paramTypeface != null)
    {
      FontFamily localFontFamily2 = (FontFamily)mFontCache.get(paramString);
      FontFamily localFontFamily1 = localFontFamily2;
      if (localFontFamily2 == null)
      {
        localFontFamily1 = new FontFamily(null);
        mFontCache.put(paramString, localFontFamily1);
      }
      localFontFamily1.setTypeface(paramInt, paramTypeface);
    }
  }
  
  private static class FontFamily
  {
    private SparseArray<Typeface> mTypefaceSparseArray = new SparseArray(4);
    
    private FontFamily() {}
    
    public Typeface getTypeface(int paramInt)
    {
      return (Typeface)mTypefaceSparseArray.get(paramInt);
    }
    
    public void setTypeface(int paramInt, Typeface paramTypeface)
    {
      mTypefaceSparseArray.put(paramInt, paramTypeface);
    }
  }
}
