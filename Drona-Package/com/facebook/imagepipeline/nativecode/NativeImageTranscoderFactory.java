package com.facebook.imagepipeline.nativecode;

import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class NativeImageTranscoderFactory
{
  private NativeImageTranscoderFactory() {}
  
  public static ImageTranscoderFactory getNativeImageTranscoderFactory(int paramInt, boolean paramBoolean)
  {
    try
    {
      Object localObject = Class.forName("com.facebook.imagepipeline.nativecode.NativeJpegTranscoderFactory");
      Class localClass1 = Integer.TYPE;
      Class localClass2 = Boolean.TYPE;
      localObject = ((Class)localObject).getConstructor(new Class[] { localClass1, localClass2 });
      localObject = ((Constructor)localObject).newInstance(new Object[] { Integer.valueOf(paramInt), Boolean.valueOf(paramBoolean) });
      return (ImageTranscoderFactory)localObject;
    }
    catch (NoSuchMethodException|SecurityException|InstantiationException|InvocationTargetException|IllegalAccessException|IllegalArgumentException|ClassNotFoundException localNoSuchMethodException)
    {
      throw new RuntimeException("Dependency ':native-imagetranscoder' is needed to use the default native image transcoder.", localNoSuchMethodException);
    }
  }
}
