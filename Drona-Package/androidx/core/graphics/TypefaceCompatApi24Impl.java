package androidx.core.graphics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CancellationSignal;
import android.util.Log;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.collection.SimpleArrayMap;
import androidx.core.content.delay.FontResourcesParserCompat.FontFamilyFilesResourceEntry;
import androidx.core.content.delay.FontResourcesParserCompat.FontFileResourceEntry;
import androidx.core.provider.FontsContractCompat.FontInfo;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;

@RequiresApi(24)
@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
class TypefaceCompatApi24Impl
  extends TypefaceCompatBaseImpl
{
  private static final String ADD_FONT_WEIGHT_STYLE_METHOD = "addFontWeightStyle";
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  private static final String FONT_FAMILY_CLASS = "android.graphics.FontFamily";
  private static final String PAGE_KEY = "TypefaceCompatApi24Impl";
  private static final Method sAddFontWeightStyle;
  private static final Method sCreateFromFamiliesWithDefault;
  private static final Class sFontFamily;
  private static final Constructor sFontFamilyCtor;
  
  static
  {
    Object localObject3 = null;
    Object localObject4;
    Object localObject5;
    Object localObject2;
    try
    {
      Object localObject6 = Class.forName("android.graphics.FontFamily");
      Object localObject1 = localObject6;
      localObject4 = ((Class)localObject6).getConstructor(new Class[0]);
      localObject5 = Integer.TYPE;
      Class localClass1 = Integer.TYPE;
      Class localClass2 = Boolean.TYPE;
      localObject5 = ((Class)localObject6).getMethod("addFontWeightStyle", new Class[] { ByteBuffer.class, localObject5, List.class, localClass1, localClass2 });
      localObject6 = Array.newInstance((Class)localObject6, 1);
      localObject6 = localObject6.getClass();
      localObject6 = Typeface.class.getMethod("createFromFamiliesWithDefault", new Class[] { localObject6 });
      localObject3 = localObject4;
      localObject4 = localObject5;
      localObject5 = localObject6;
    }
    catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException)
    {
      Log.e("TypefaceCompatApi24Impl", localClassNotFoundException.getClass().getName(), localClassNotFoundException);
      localObject2 = null;
      localObject5 = null;
      localObject4 = null;
    }
    sFontFamilyCtor = localObject3;
    sFontFamily = localObject2;
    sAddFontWeightStyle = (Method)localObject4;
    sCreateFromFamiliesWithDefault = (Method)localObject5;
  }
  
  TypefaceCompatApi24Impl() {}
  
  private static boolean addFontWeightStyle(Object paramObject, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    Method localMethod = sAddFontWeightStyle;
    try
    {
      paramObject = localMethod.invoke(paramObject, new Object[] { paramByteBuffer, Integer.valueOf(paramInt1), null, Integer.valueOf(paramInt2), Boolean.valueOf(paramBoolean) });
      paramObject = (Boolean)paramObject;
      paramBoolean = paramObject.booleanValue();
      return paramBoolean;
    }
    catch (IllegalAccessException paramObject)
    {
      return false;
    }
    catch (InvocationTargetException paramObject) {}
    return false;
  }
  
  private static Typeface createFromFamiliesWithDefault(Object paramObject)
  {
    Object localObject = sFontFamily;
    try
    {
      localObject = Array.newInstance((Class)localObject, 1);
      Array.set(localObject, 0, paramObject);
      paramObject = sCreateFromFamiliesWithDefault;
      paramObject = paramObject.invoke(null, new Object[] { localObject });
      return (Typeface)paramObject;
    }
    catch (IllegalAccessException paramObject)
    {
      return null;
    }
    catch (InvocationTargetException paramObject) {}
    return null;
  }
  
  public static boolean isUsable()
  {
    if (sAddFontWeightStyle == null) {
      Log.w("TypefaceCompatApi24Impl", "Unable to collect necessary private methods.Fallback to legacy implementation.");
    }
    return sAddFontWeightStyle != null;
  }
  
  private static Object newFamily()
  {
    Object localObject = sFontFamilyCtor;
    try
    {
      localObject = ((Constructor)localObject).newInstance(new Object[0]);
      return localObject;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      for (;;) {}
    }
    catch (InstantiationException localInstantiationException)
    {
      for (;;) {}
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;) {}
    }
    return null;
  }
  
  public Typeface createFromFontFamilyFilesResourceEntry(Context paramContext, FontResourcesParserCompat.FontFamilyFilesResourceEntry paramFontFamilyFilesResourceEntry, Resources paramResources, int paramInt)
  {
    Object localObject1 = newFamily();
    if (localObject1 == null) {
      return null;
    }
    paramFontFamilyFilesResourceEntry = paramFontFamilyFilesResourceEntry.getEntries();
    int i = paramFontFamilyFilesResourceEntry.length;
    paramInt = 0;
    while (paramInt < i)
    {
      Object localObject2 = paramFontFamilyFilesResourceEntry[paramInt];
      ByteBuffer localByteBuffer = TypefaceCompatUtil.copyToDirectBuffer(paramContext, paramResources, localObject2.getResourceId());
      if (localByteBuffer == null) {
        return null;
      }
      if (!addFontWeightStyle(localObject1, localByteBuffer, localObject2.getTtcIndex(), localObject2.getWeight(), localObject2.isItalic())) {
        return null;
      }
      paramInt += 1;
    }
    return createFromFamiliesWithDefault(localObject1);
  }
  
  public Typeface createFromFontInfo(Context paramContext, CancellationSignal paramCancellationSignal, FontsContractCompat.FontInfo[] paramArrayOfFontInfo, int paramInt)
  {
    Object localObject = newFamily();
    if (localObject == null) {
      return null;
    }
    SimpleArrayMap localSimpleArrayMap = new SimpleArrayMap();
    int j = paramArrayOfFontInfo.length;
    int i = 0;
    while (i < j)
    {
      FontsContractCompat.FontInfo localFontInfo = paramArrayOfFontInfo[i];
      Uri localUri = localFontInfo.getUri();
      ByteBuffer localByteBuffer2 = (ByteBuffer)localSimpleArrayMap.get(localUri);
      ByteBuffer localByteBuffer1 = localByteBuffer2;
      if (localByteBuffer2 == null)
      {
        localByteBuffer2 = TypefaceCompatUtil.mmap(paramContext, paramCancellationSignal, localUri);
        localByteBuffer1 = localByteBuffer2;
        localSimpleArrayMap.put(localUri, localByteBuffer2);
      }
      if (localByteBuffer1 == null) {
        return null;
      }
      if (!addFontWeightStyle(localObject, localByteBuffer1, localFontInfo.getTtcIndex(), localFontInfo.getWeight(), localFontInfo.isItalic())) {
        return null;
      }
      i += 1;
    }
    paramContext = createFromFamiliesWithDefault(localObject);
    if (paramContext == null) {
      return null;
    }
    return Typeface.create(paramContext, paramInt);
  }
}
