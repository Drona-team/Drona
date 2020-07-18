package androidx.activity;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LifecycleEventObserver;
import java.lang.reflect.Field;

@RequiresApi(19)
final class ImmLeaksCleaner
  implements LifecycleEventObserver
{
  private static final int INIT_FAILED = 2;
  private static final int INIT_SUCCESS = 1;
  private static final int NOT_INITIALIAZED = 0;
  private static Field sHField;
  private static Field sNextServedViewField;
  private static int sReflectedFieldsInitialized;
  private static Field sServedViewField;
  private Activity mActivity;
  
  ImmLeaksCleaner(Activity paramActivity)
  {
    mActivity = paramActivity;
  }
  
  private static void initializeReflectiveFields()
  {
    sReflectedFieldsInitialized = 2;
    try
    {
      Field localField = InputMethodManager.class.getDeclaredField("mServedView");
      sServedViewField = localField;
      localField = sServedViewField;
      localField.setAccessible(true);
      localField = InputMethodManager.class.getDeclaredField("mNextServedView");
      sNextServedViewField = localField;
      localField = sNextServedViewField;
      localField.setAccessible(true);
      localField = InputMethodManager.class.getDeclaredField("mH");
      sHField = localField;
      localField = sHField;
      localField.setAccessible(true);
      sReflectedFieldsInitialized = 1;
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException) {}
  }
  
  /* Error */
  public void onStateChanged(androidx.lifecycle.LifecycleOwner paramLifecycleOwner, androidx.lifecycle.Lifecycle.Event paramEvent)
  {
    // Byte code:
    //   0: aload_2
    //   1: getstatic 77	androidx/lifecycle/Lifecycle$Event:ON_DESTROY	Landroidx/lifecycle/Lifecycle$Event;
    //   4: if_acmpeq +4 -> 8
    //   7: return
    //   8: getstatic 37	androidx/activity/ImmLeaksCleaner:sReflectedFieldsInitialized	I
    //   11: ifne +6 -> 17
    //   14: invokestatic 79	androidx/activity/ImmLeaksCleaner:initializeReflectiveFields	()V
    //   17: getstatic 37	androidx/activity/ImmLeaksCleaner:sReflectedFieldsInitialized	I
    //   20: iconst_1
    //   21: if_icmpne +100 -> 121
    //   24: aload_0
    //   25: getfield 32	androidx/activity/ImmLeaksCleaner:mActivity	Landroid/app/Activity;
    //   28: ldc 81
    //   30: invokevirtual 87	android/app/Activity:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   33: checkcast 39	android/view/inputmethod/InputMethodManager
    //   36: astore_2
    //   37: getstatic 63	androidx/activity/ImmLeaksCleaner:sHField	Ljava/lang/reflect/Field;
    //   40: astore_1
    //   41: aload_1
    //   42: aload_2
    //   43: invokevirtual 91	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   46: astore_1
    //   47: aload_1
    //   48: ifnonnull +4 -> 52
    //   51: return
    //   52: aload_1
    //   53: monitorenter
    //   54: getstatic 49	androidx/activity/ImmLeaksCleaner:sServedViewField	Ljava/lang/reflect/Field;
    //   57: astore_3
    //   58: aload_3
    //   59: aload_2
    //   60: invokevirtual 91	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   63: astore_3
    //   64: aload_3
    //   65: checkcast 93	android/view/View
    //   68: astore_3
    //   69: aload_3
    //   70: ifnonnull +6 -> 76
    //   73: aload_1
    //   74: monitorexit
    //   75: return
    //   76: aload_3
    //   77: invokevirtual 97	android/view/View:isAttachedToWindow	()Z
    //   80: ifeq +6 -> 86
    //   83: aload_1
    //   84: monitorexit
    //   85: return
    //   86: getstatic 59	androidx/activity/ImmLeaksCleaner:sNextServedViewField	Ljava/lang/reflect/Field;
    //   89: astore_3
    //   90: aload_3
    //   91: aload_2
    //   92: aconst_null
    //   93: invokevirtual 101	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   96: aload_1
    //   97: monitorexit
    //   98: aload_2
    //   99: invokevirtual 104	android/view/inputmethod/InputMethodManager:isActive	()Z
    //   102: pop
    //   103: return
    //   104: aload_1
    //   105: monitorexit
    //   106: return
    //   107: astore_2
    //   108: goto +9 -> 117
    //   111: aload_1
    //   112: monitorexit
    //   113: return
    //   114: aload_1
    //   115: monitorexit
    //   116: return
    //   117: aload_1
    //   118: monitorexit
    //   119: aload_2
    //   120: athrow
    //   121: return
    //   122: astore_1
    //   123: return
    //   124: astore_2
    //   125: goto -11 -> 114
    //   128: astore_2
    //   129: goto -18 -> 111
    //   132: astore_2
    //   133: goto -29 -> 104
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	136	0	this	ImmLeaksCleaner
    //   0	136	1	paramLifecycleOwner	androidx.lifecycle.LifecycleOwner
    //   0	136	2	paramEvent	androidx.lifecycle.Lifecycle.Event
    //   57	34	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   54	58	107	java/lang/Throwable
    //   58	64	107	java/lang/Throwable
    //   64	69	107	java/lang/Throwable
    //   73	75	107	java/lang/Throwable
    //   76	85	107	java/lang/Throwable
    //   90	96	107	java/lang/Throwable
    //   96	98	107	java/lang/Throwable
    //   104	106	107	java/lang/Throwable
    //   111	113	107	java/lang/Throwable
    //   114	116	107	java/lang/Throwable
    //   117	119	107	java/lang/Throwable
    //   41	47	122	java/lang/IllegalAccessException
    //   58	64	124	java/lang/IllegalAccessException
    //   58	64	128	java/lang/ClassCastException
    //   64	69	128	java/lang/ClassCastException
    //   90	96	132	java/lang/IllegalAccessException
  }
}
