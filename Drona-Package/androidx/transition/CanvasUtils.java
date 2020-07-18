package androidx.transition;

import android.graphics.Canvas;
import android.os.Build.VERSION;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class CanvasUtils
{
  private static Method sInorderBarrierMethod;
  private static boolean sOrderMethodsFetched;
  private static Method sReorderBarrierMethod;
  
  private CanvasUtils() {}
  
  static void enableZ(Canvas paramCanvas, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 21) {
      return;
    }
    if (Build.VERSION.SDK_INT >= 29)
    {
      if (paramBoolean)
      {
        paramCanvas.enableZ();
        return;
      }
      paramCanvas.disableZ();
      return;
    }
    if ((Build.VERSION.SDK_INT == 28) || (!sOrderMethodsFetched)) {}
    try
    {
      localMethod = Canvas.class.getDeclaredMethod("insertReorderBarrier", new Class[0]);
      sReorderBarrierMethod = localMethod;
      localMethod = sReorderBarrierMethod;
      localMethod.setAccessible(true);
      localMethod = Canvas.class.getDeclaredMethod("insertInorderBarrier", new Class[0]);
      sInorderBarrierMethod = localMethod;
      localMethod = sInorderBarrierMethod;
      localMethod.setAccessible(true);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Method localMethod;
      for (;;) {}
    }
    sOrderMethodsFetched = true;
    if ((paramBoolean) && (sReorderBarrierMethod != null)) {
      localMethod = sReorderBarrierMethod;
    }
    label155:
    try
    {
      try
      {
        localMethod.invoke(paramCanvas, new Object[0]);
      }
      catch (InvocationTargetException paramCanvas)
      {
        break label155;
      }
      if ((paramBoolean) || (sInorderBarrierMethod == null)) {
        return;
      }
      localMethod = sInorderBarrierMethod;
      localMethod.invoke(paramCanvas, new Object[0]);
      return;
    }
    catch (IllegalAccessException paramCanvas) {}
    throw new RuntimeException(paramCanvas.getCause());
    throw new IllegalStateException("This method doesn't work on Pie!");
  }
}
