package androidx.transition;

import android.animation.LayoutTransition;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class ViewGroupUtilsApi14
{
  private static final int LAYOUT_TRANSITION_CHANGING = 4;
  private static final String PAGE_KEY = "ViewGroupUtilsApi14";
  private static Method sCancelMethod;
  private static boolean sCancelMethodFetched;
  private static LayoutTransition sEmptyLayoutTransition;
  private static Field sLayoutSuppressedField;
  private static boolean sLayoutSuppressedFieldFetched;
  
  private ViewGroupUtilsApi14() {}
  
  private static void cancelLayoutTransition(LayoutTransition paramLayoutTransition)
  {
    if (!sCancelMethodFetched) {}
    try
    {
      localMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
      sCancelMethod = localMethod;
      localMethod = sCancelMethod;
      localMethod.setAccessible(true);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Method localMethod;
      for (;;) {}
    }
    Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
    sCancelMethodFetched = true;
    if (sCancelMethod != null)
    {
      localMethod = sCancelMethod;
      try
      {
        localMethod.invoke(paramLayoutTransition, new Object[0]);
        return;
      }
      catch (IllegalAccessException paramLayoutTransition)
      {
        for (;;) {}
      }
      catch (InvocationTargetException paramLayoutTransition)
      {
        for (;;) {}
      }
      Log.i("ViewGroupUtilsApi14", "Failed to invoke cancel method by reflection");
      return;
      Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
      return;
    }
  }
  
  static void suppressLayout(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    Object localObject = sEmptyLayoutTransition;
    boolean bool2 = false;
    boolean bool1 = false;
    if (localObject == null)
    {
      sEmptyLayoutTransition = new LayoutTransition()
      {
        public boolean isChangingLayout()
        {
          return true;
        }
      };
      sEmptyLayoutTransition.setAnimator(2, null);
      sEmptyLayoutTransition.setAnimator(0, null);
      sEmptyLayoutTransition.setAnimator(1, null);
      sEmptyLayoutTransition.setAnimator(3, null);
      sEmptyLayoutTransition.setAnimator(4, null);
    }
    if (paramBoolean)
    {
      localObject = paramViewGroup.getLayoutTransition();
      if (localObject != null)
      {
        if (((LayoutTransition)localObject).isRunning()) {
          cancelLayoutTransition((LayoutTransition)localObject);
        }
        if (localObject != sEmptyLayoutTransition) {
          paramViewGroup.setTag(R.id.transition_layout_save, localObject);
        }
      }
      paramViewGroup.setLayoutTransition(sEmptyLayoutTransition);
      return;
    }
    paramViewGroup.setLayoutTransition(null);
    if (!sLayoutSuppressedFieldFetched) {}
    try
    {
      localObject = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
      sLayoutSuppressedField = (Field)localObject;
      localObject = sLayoutSuppressedField;
      ((Field)localObject).setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;) {}
    }
    Log.i("ViewGroupUtilsApi14", "Failed to access mLayoutSuppressed field by reflection");
    sLayoutSuppressedFieldFetched = true;
    paramBoolean = bool2;
    if (sLayoutSuppressedField != null) {
      localObject = sLayoutSuppressedField;
    }
    for (;;)
    {
      try
      {
        paramBoolean = ((Field)localObject).getBoolean(paramViewGroup);
        if (paramBoolean) {
          localObject = sLayoutSuppressedField;
        }
      }
      catch (IllegalAccessException localIllegalAccessException1)
      {
        paramBoolean = bool1;
        continue;
      }
      try
      {
        ((Field)localObject).setBoolean(paramViewGroup, false);
      }
      catch (IllegalAccessException localIllegalAccessException2) {}
    }
    break label213;
    break label221;
    label213:
    Log.i("ViewGroupUtilsApi14", "Failed to get mLayoutSuppressed field by reflection");
    label221:
    if (paramBoolean) {
      paramViewGroup.requestLayout();
    }
    localObject = (LayoutTransition)paramViewGroup.getTag(R.id.transition_layout_save);
    if (localObject != null)
    {
      paramViewGroup.setTag(R.id.transition_layout_save, null);
      paramViewGroup.setLayoutTransition((LayoutTransition)localObject);
      return;
    }
  }
}
