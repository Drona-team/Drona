package com.google.ads.mediation;

import com.google.android.gms.internal.ads.zzbad;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

@Deprecated
public class MediationServerParameters
{
  public MediationServerParameters() {}
  
  public void load(Map paramMap)
    throws MediationServerParameters.MappingException
  {
    Object localObject1 = new HashMap();
    Object localObject2 = getClass().getFields();
    int j = localObject2.length;
    int i = 0;
    Object localObject3;
    Object localObject4;
    while (i < j)
    {
      localObject3 = localObject2[i];
      localObject4 = (Parameter)((Field)localObject3).getAnnotation(Parameter.class);
      if (localObject4 != null) {
        ((Map)localObject1).put(((Parameter)localObject4).name(), localObject3);
      }
      i += 1;
    }
    if (((Map)localObject1).isEmpty()) {
      zzbad.zzep("No server options fields detected. To suppress this message either add a field with the @Parameter annotation, or override the load() method.");
    }
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      localObject2 = (Map.Entry)paramMap.next();
      localObject3 = (Field)((Map)localObject1).remove(((Map.Entry)localObject2).getKey());
      if (localObject3 != null) {}
      try
      {
        ((Field)localObject3).set(this, ((Map.Entry)localObject2).getValue());
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;) {}
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        for (;;) {}
      }
      localObject2 = (String)((Map.Entry)localObject2).getKey();
      localObject3 = new StringBuilder(String.valueOf(localObject2).length() + 43);
      ((StringBuilder)localObject3).append("Server option \"");
      ((StringBuilder)localObject3).append((String)localObject2);
      ((StringBuilder)localObject3).append("\" could not be set: Bad Type");
      zzbad.zzep(((StringBuilder)localObject3).toString());
      continue;
      localObject2 = (String)((Map.Entry)localObject2).getKey();
      localObject3 = new StringBuilder(String.valueOf(localObject2).length() + 49);
      ((StringBuilder)localObject3).append("Server option \"");
      ((StringBuilder)localObject3).append((String)localObject2);
      ((StringBuilder)localObject3).append("\" could not be set: Illegal Access");
      zzbad.zzep(((StringBuilder)localObject3).toString());
      continue;
      localObject3 = (String)((Map.Entry)localObject2).getKey();
      localObject2 = (String)((Map.Entry)localObject2).getValue();
      localObject4 = new StringBuilder(String.valueOf(localObject3).length() + 31 + String.valueOf(localObject2).length());
      ((StringBuilder)localObject4).append("Unexpected server option: ");
      ((StringBuilder)localObject4).append((String)localObject3);
      ((StringBuilder)localObject4).append(" = \"");
      ((StringBuilder)localObject4).append((String)localObject2);
      ((StringBuilder)localObject4).append("\"");
      zzbad.zzdp(((StringBuilder)localObject4).toString());
    }
    localObject2 = new StringBuilder();
    localObject1 = ((Map)localObject1).values().iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject3 = (Field)((Iterator)localObject1).next();
      if (((Parameter)((Field)localObject3).getAnnotation(Parameter.class)).required())
      {
        paramMap = String.valueOf(((Parameter)((Field)localObject3).getAnnotation(Parameter.class)).name());
        if (paramMap.length() != 0) {
          paramMap = "Required server option missing: ".concat(paramMap);
        } else {
          paramMap = new String("Required server option missing: ");
        }
        zzbad.zzep(paramMap);
        if (((StringBuilder)localObject2).length() > 0) {
          ((StringBuilder)localObject2).append(", ");
        }
        ((StringBuilder)localObject2).append(((Parameter)((Field)localObject3).getAnnotation(Parameter.class)).name());
      }
    }
    if (((StringBuilder)localObject2).length() > 0)
    {
      paramMap = String.valueOf(((StringBuilder)localObject2).toString());
      if (paramMap.length() != 0) {
        paramMap = "Required server option(s) missing: ".concat(paramMap);
      } else {
        paramMap = new String("Required server option(s) missing: ");
      }
      throw new MappingException(paramMap);
    }
  }
  
  public static final class MappingException
    extends Exception
  {
    public MappingException(String paramString)
    {
      super();
    }
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  @Target({java.lang.annotation.ElementType.FIELD})
  public static @interface Parameter
  {
    String name();
    
    boolean required();
  }
}
