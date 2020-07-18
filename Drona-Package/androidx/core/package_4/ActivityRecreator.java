package androidx.core.package_4;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.RestrictTo;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY})
final class ActivityRecreator
{
  private static final String LOG_TAG = "ActivityRecreator";
  protected static final Class<?> activityThreadClass;
  private static final Handler mainHandler = new Handler(Looper.getMainLooper());
  protected static final Field mainThreadField;
  protected static final Method performStopActivity2ParamsMethod = getPerformStopActivity2Params(activityThreadClass);
  protected static final Method performStopActivity3ParamsMethod;
  protected static final Method requestRelaunchActivityMethod = getRequestRelaunchActivityMethod(activityThreadClass);
  protected static final Field tokenField;
  
  static
  {
    activityThreadClass = getActivityThreadClass();
    mainThreadField = getMainThreadField();
    tokenField = getTokenField();
    performStopActivity3ParamsMethod = getPerformStopActivity3Params(activityThreadClass);
  }
  
  private ActivityRecreator() {}
  
  private static Class getActivityThreadClass()
  {
    try
    {
      Class localClass = Class.forName("android.app.ActivityThread");
      return localClass;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static Field getMainThreadField()
  {
    try
    {
      Field localField = Activity.class.getDeclaredField("mMainThread");
      localField.setAccessible(true);
      return localField;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static Method getPerformStopActivity2Params(Class paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    try
    {
      paramClass = paramClass.getDeclaredMethod("performStopActivity", new Class[] { IBinder.class, Boolean.TYPE });
      paramClass.setAccessible(true);
      return paramClass;
    }
    catch (Throwable paramClass) {}
    return null;
  }
  
  private static Method getPerformStopActivity3Params(Class paramClass)
  {
    if (paramClass == null) {
      return null;
    }
    try
    {
      paramClass = paramClass.getDeclaredMethod("performStopActivity", new Class[] { IBinder.class, Boolean.TYPE, String.class });
      paramClass.setAccessible(true);
      return paramClass;
    }
    catch (Throwable paramClass) {}
    return null;
  }
  
  private static Method getRequestRelaunchActivityMethod(Class paramClass)
  {
    if (needsRelaunchCall()) {
      if (paramClass == null) {
        return null;
      }
    }
    try
    {
      paramClass = paramClass.getDeclaredMethod("requestRelaunchActivity", new Class[] { IBinder.class, List.class, List.class, Integer.TYPE, Boolean.TYPE, Configuration.class, Configuration.class, Boolean.TYPE, Boolean.TYPE });
      paramClass.setAccessible(true);
      return paramClass;
    }
    catch (Throwable paramClass) {}
    return null;
    return null;
  }
  
  private static Field getTokenField()
  {
    try
    {
      Field localField = Activity.class.getDeclaredField("mToken");
      localField.setAccessible(true);
      return localField;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
    return null;
  }
  
  private static boolean needsRelaunchCall()
  {
    return (Build.VERSION.SDK_INT == 26) || (Build.VERSION.SDK_INT == 27);
  }
  
  protected static boolean queueOnStopIfNecessary(Object paramObject, Activity paramActivity)
  {
    try
    {
      Object localObject = tokenField.get(paramActivity);
      if (localObject != paramObject) {
        return false;
      }
      paramObject = mainThreadField.get(paramActivity);
      mainHandler.postAtFrontOfQueue(new ActivityRecreator.3(paramObject, localObject));
      return true;
    }
    catch (Throwable paramObject)
    {
      Log.e("ActivityRecreator", "Exception while fetching field values", paramObject);
    }
    return false;
  }
  
  /* Error */
  static boolean recreate(Activity paramActivity)
  {
    // Byte code:
    //   0: getstatic 141	android/os/Build$VERSION:SDK_INT	I
    //   3: bipush 28
    //   5: if_icmplt +9 -> 14
    //   8: aload_0
    //   9: invokevirtual 168	android/app/Activity:recreate	()V
    //   12: iconst_1
    //   13: ireturn
    //   14: invokestatic 124	androidx/core/package_4/ActivityRecreator:needsRelaunchCall	()Z
    //   17: ifeq +11 -> 28
    //   20: getstatic 75	androidx/core/package_4/ActivityRecreator:requestRelaunchActivityMethod	Ljava/lang/reflect/Method;
    //   23: ifnonnull +5 -> 28
    //   26: iconst_0
    //   27: ireturn
    //   28: getstatic 70	androidx/core/package_4/ActivityRecreator:performStopActivity2ParamsMethod	Ljava/lang/reflect/Method;
    //   31: ifnonnull +11 -> 42
    //   34: getstatic 65	androidx/core/package_4/ActivityRecreator:performStopActivity3ParamsMethod	Ljava/lang/reflect/Method;
    //   37: ifnonnull +5 -> 42
    //   40: iconst_0
    //   41: ireturn
    //   42: getstatic 59	androidx/core/package_4/ActivityRecreator:tokenField	Ljava/lang/reflect/Field;
    //   45: aload_0
    //   46: invokevirtual 147	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   49: astore 4
    //   51: aload 4
    //   53: ifnonnull +5 -> 58
    //   56: iconst_0
    //   57: ireturn
    //   58: getstatic 54	androidx/core/package_4/ActivityRecreator:mainThreadField	Ljava/lang/reflect/Field;
    //   61: aload_0
    //   62: invokevirtual 147	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   65: astore 5
    //   67: aload 5
    //   69: ifnonnull +5 -> 74
    //   72: iconst_0
    //   73: ireturn
    //   74: aload_0
    //   75: invokevirtual 172	android/app/Activity:getApplication	()Landroid/app/Application;
    //   78: astore_2
    //   79: new 6	androidx/core/package_4/ActivityRecreator$LifecycleCheckCallbacks
    //   82: dup
    //   83: aload_0
    //   84: invokespecial 175	androidx/core/package_4/ActivityRecreator$LifecycleCheckCallbacks:<init>	(Landroid/app/Activity;)V
    //   87: astore_3
    //   88: aload_2
    //   89: aload_3
    //   90: invokevirtual 181	android/app/Application:registerActivityLifecycleCallbacks	(Landroid/app/Application$ActivityLifecycleCallbacks;)V
    //   93: getstatic 42	androidx/core/package_4/ActivityRecreator:mainHandler	Landroid/os/Handler;
    //   96: new 183	androidx/core/package_4/ActivityRecreator$1
    //   99: dup
    //   100: aload_3
    //   101: aload 4
    //   103: invokespecial 186	androidx/core/package_4/ActivityRecreator$1:<init>	(Landroidx/core/package_4/ActivityRecreator$LifecycleCheckCallbacks;Ljava/lang/Object;)V
    //   106: invokevirtual 189	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   109: pop
    //   110: invokestatic 124	androidx/core/package_4/ActivityRecreator:needsRelaunchCall	()Z
    //   113: istore_1
    //   114: iload_1
    //   115: ifeq +72 -> 187
    //   118: getstatic 75	androidx/core/package_4/ActivityRecreator:requestRelaunchActivityMethod	Ljava/lang/reflect/Method;
    //   121: aload 5
    //   123: bipush 9
    //   125: anewarray 4	java/lang/Object
    //   128: dup
    //   129: iconst_0
    //   130: aload 4
    //   132: aastore
    //   133: dup
    //   134: iconst_1
    //   135: aconst_null
    //   136: aastore
    //   137: dup
    //   138: iconst_2
    //   139: aconst_null
    //   140: aastore
    //   141: dup
    //   142: iconst_3
    //   143: iconst_0
    //   144: invokestatic 193	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   147: aastore
    //   148: dup
    //   149: iconst_4
    //   150: iconst_0
    //   151: invokestatic 196	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   154: aastore
    //   155: dup
    //   156: iconst_5
    //   157: aconst_null
    //   158: aastore
    //   159: dup
    //   160: bipush 6
    //   162: aconst_null
    //   163: aastore
    //   164: dup
    //   165: bipush 7
    //   167: iconst_0
    //   168: invokestatic 196	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   171: aastore
    //   172: dup
    //   173: bipush 8
    //   175: iconst_0
    //   176: invokestatic 196	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   179: aastore
    //   180: invokevirtual 200	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   183: pop
    //   184: goto +7 -> 191
    //   187: aload_0
    //   188: invokevirtual 168	android/app/Activity:recreate	()V
    //   191: getstatic 42	androidx/core/package_4/ActivityRecreator:mainHandler	Landroid/os/Handler;
    //   194: new 202	androidx/core/package_4/ActivityRecreator$2
    //   197: dup
    //   198: aload_2
    //   199: aload_3
    //   200: invokespecial 205	androidx/core/package_4/ActivityRecreator$2:<init>	(Landroid/app/Application;Landroidx/core/package_4/ActivityRecreator$LifecycleCheckCallbacks;)V
    //   203: invokevirtual 189	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   206: pop
    //   207: iconst_1
    //   208: ireturn
    //   209: astore_0
    //   210: getstatic 42	androidx/core/package_4/ActivityRecreator:mainHandler	Landroid/os/Handler;
    //   213: new 202	androidx/core/package_4/ActivityRecreator$2
    //   216: dup
    //   217: aload_2
    //   218: aload_3
    //   219: invokespecial 205	androidx/core/package_4/ActivityRecreator$2:<init>	(Landroid/app/Application;Landroidx/core/package_4/ActivityRecreator$LifecycleCheckCallbacks;)V
    //   222: invokevirtual 189	android/os/Handler:post	(Ljava/lang/Runnable;)Z
    //   225: pop
    //   226: aload_0
    //   227: athrow
    //   228: astore_0
    //   229: iconst_0
    //   230: ireturn
    //   231: astore_0
    //   232: iconst_0
    //   233: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	234	0	paramActivity	Activity
    //   113	2	1	bool	boolean
    //   78	140	2	localApplication	android.app.Application
    //   87	132	3	localLifecycleCheckCallbacks	LifecycleCheckCallbacks
    //   49	82	4	localObject1	Object
    //   65	57	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   110	114	209	java/lang/Throwable
    //   118	184	209	java/lang/Throwable
    //   187	191	209	java/lang/Throwable
    //   42	51	228	java/lang/Throwable
    //   58	67	228	java/lang/Throwable
    //   74	110	228	java/lang/Throwable
    //   191	207	231	java/lang/Throwable
    //   210	228	231	java/lang/Throwable
  }
  
  final class LifecycleCheckCallbacks
    implements Application.ActivityLifecycleCallbacks
  {
    Object currentlyRecreatingToken;
    private boolean mDestroyed = false;
    private boolean mStarted = false;
    private boolean mStopQueued = false;
    
    LifecycleCheckCallbacks() {}
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityDestroyed(Activity paramActivity)
    {
      if (ActivityRecreator.this == paramActivity)
      {
        mActivity = null;
        mDestroyed = true;
      }
    }
    
    public void onActivityPaused(Activity paramActivity)
    {
      if ((mDestroyed) && (!mStopQueued) && (!mStarted) && (ActivityRecreator.queueOnStopIfNecessary(currentlyRecreatingToken, paramActivity)))
      {
        mStopQueued = true;
        currentlyRecreatingToken = null;
      }
    }
    
    public void onActivityResumed(Activity paramActivity) {}
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityStarted(Activity paramActivity)
    {
      if (ActivityRecreator.this == paramActivity) {
        mStarted = true;
      }
    }
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}
