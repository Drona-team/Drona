package bleshadow.dagger.internal;

public final class Preconditions
{
  private Preconditions() {}
  
  public static Object checkNotNull(Object paramObject)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new NullPointerException();
  }
  
  public static Object checkNotNull(Object paramObject, String paramString)
  {
    if (paramObject != null) {
      return paramObject;
    }
    throw new NullPointerException(paramString);
  }
  
  public static Object checkNotNull(Object paramObject1, String paramString, Object paramObject2)
  {
    if (paramObject1 == null)
    {
      if (paramString.contains("%s"))
      {
        if (paramString.indexOf("%s") == paramString.lastIndexOf("%s"))
        {
          if ((paramObject2 instanceof Class)) {
            paramObject1 = ((Class)paramObject2).getCanonicalName();
          } else {
            paramObject1 = String.valueOf(paramObject2);
          }
          throw new NullPointerException(paramString.replace("%s", paramObject1));
        }
        throw new IllegalArgumentException("errorMessageTemplate has more than one format specifier");
      }
      throw new IllegalArgumentException("errorMessageTemplate has no format specifiers");
    }
    return paramObject1;
  }
}
