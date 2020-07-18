package androidx.lifecycle;

import android.app.Application;
import android.os.Bundle;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class SavedStateViewModelFactory
  extends ViewModelProvider.KeyedFactory
{
  private static final Class<?>[] ANDROID_VIEWMODEL_SIGNATURE = { Application.class, SavedStateHandle.class };
  private static final Class<?>[] VIEWMODEL_SIGNATURE = { SavedStateHandle.class };
  private final Application mApplication;
  private final Bundle mDefaultArgs;
  private final ViewModelProvider.AndroidViewModelFactory mFactory;
  private final Lifecycle mLifecycle;
  private final SavedStateRegistry mSavedStateRegistry;
  
  public SavedStateViewModelFactory(Application paramApplication, SavedStateRegistryOwner paramSavedStateRegistryOwner)
  {
    this(paramApplication, paramSavedStateRegistryOwner, null);
  }
  
  public SavedStateViewModelFactory(Application paramApplication, SavedStateRegistryOwner paramSavedStateRegistryOwner, Bundle paramBundle)
  {
    mSavedStateRegistry = paramSavedStateRegistryOwner.getSavedStateRegistry();
    mLifecycle = paramSavedStateRegistryOwner.getLifecycle();
    mDefaultArgs = paramBundle;
    mApplication = paramApplication;
    mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(paramApplication);
  }
  
  private static Constructor findMatchingConstructor(Class paramClass, Class[] paramArrayOfClass)
  {
    paramClass = paramClass.getConstructors();
    int j = paramClass.length;
    int i = 0;
    while (i < j)
    {
      Constructor localConstructor = paramClass[i];
      if (Arrays.equals(paramArrayOfClass, localConstructor.getParameterTypes())) {
        return localConstructor;
      }
      i += 1;
    }
    return null;
  }
  
  public ViewModel create(Class paramClass)
  {
    String str = paramClass.getCanonicalName();
    if (str != null) {
      return create(str, paramClass);
    }
    throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
  }
  
  public ViewModel create(String paramString, Class paramClass)
  {
    boolean bool = AndroidViewModel.class.isAssignableFrom(paramClass);
    if (bool) {
      localObject = findMatchingConstructor(paramClass, ANDROID_VIEWMODEL_SIGNATURE);
    } else {
      localObject = findMatchingConstructor(paramClass, VIEWMODEL_SIGNATURE);
    }
    if (localObject == null) {
      return mFactory.create(paramClass);
    }
    SavedStateHandleController localSavedStateHandleController = SavedStateHandleController.create(mSavedStateRegistry, mLifecycle, paramString, mDefaultArgs);
    if (bool)
    {
      paramString = mApplication;
      try
      {
        SavedStateHandle localSavedStateHandle = localSavedStateHandleController.getHandle();
        paramString = ((Constructor)localObject).newInstance(new Object[] { paramString, localSavedStateHandle });
        paramString = (ViewModel)paramString;
      }
      catch (InvocationTargetException paramString)
      {
        break label155;
      }
      catch (InstantiationException paramString)
      {
        break label196;
      }
      catch (IllegalAccessException paramString)
      {
        break label242;
      }
    }
    else
    {
      paramString = localSavedStateHandleController.getHandle();
      paramString = ((Constructor)localObject).newInstance(new Object[] { paramString });
      paramString = (ViewModel)paramString;
    }
    paramString.setTagIfAbsent("androidx.lifecycle.savedstate.vm.tag", localSavedStateHandleController);
    return paramString;
    label155:
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("An exception happened in constructor of ");
    ((StringBuilder)localObject).append(paramClass);
    throw new RuntimeException(((StringBuilder)localObject).toString(), paramString.getCause());
    label196:
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("A ");
    ((StringBuilder)localObject).append(paramClass);
    ((StringBuilder)localObject).append(" cannot be instantiated.");
    throw new RuntimeException(((StringBuilder)localObject).toString(), paramString);
    label242:
    localObject = new StringBuilder();
    ((StringBuilder)localObject).append("Failed to access ");
    ((StringBuilder)localObject).append(paramClass);
    throw new RuntimeException(((StringBuilder)localObject).toString(), paramString);
  }
  
  void onRequery(ViewModel paramViewModel)
  {
    SavedStateHandleController.attachHandleIfNeeded(paramViewModel, mSavedStateRegistry, mLifecycle);
  }
}
