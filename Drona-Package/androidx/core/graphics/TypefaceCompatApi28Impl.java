package androidx.core.graphics;

import android.graphics.Typeface;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(28)
@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class TypefaceCompatApi28Impl
  extends TypefaceCompatApi26Impl
{
  private static final String CREATE_FROM_FAMILIES_WITH_DEFAULT_METHOD = "createFromFamiliesWithDefault";
  private static final String DEFAULT_FAMILY = "sans-serif";
  private static final int RESOLVE_BY_FONT_TABLE = -1;
  
  public TypefaceCompatApi28Impl() {}
  
  protected Typeface createFromFamiliesWithDefault(Object paramObject)
  {
    Object localObject = mFontFamily;
    try
    {
      localObject = Array.newInstance((Class)localObject, 1);
      Array.set(localObject, 0, paramObject);
      paramObject = mCreateFromFamiliesWithDefault;
      paramObject = paramObject.invoke(null, new Object[] { localObject, "sans-serif", Integer.valueOf(-1), Integer.valueOf(-1) });
      return (Typeface)paramObject;
    }
    catch (IllegalAccessException|InvocationTargetException paramObject)
    {
      throw new RuntimeException(paramObject);
    }
  }
  
  protected Method obtainCreateFromFamiliesWithDefaultMethod(Class paramClass)
    throws NoSuchMethodException
  {
    paramClass = Typeface.class.getDeclaredMethod("createFromFamiliesWithDefault", new Class[] { Array.newInstance(paramClass, 1).getClass(), String.class, Integer.TYPE, Integer.TYPE });
    paramClass.setAccessible(true);
    return paramClass;
  }
}
