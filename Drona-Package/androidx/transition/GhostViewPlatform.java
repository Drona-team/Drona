package androidx.transition;

import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.RequiresApi;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RequiresApi(21)
class GhostViewPlatform
  implements GhostView
{
  private static final String PAGE_KEY = "GhostViewApi21";
  private static Method sAddGhostMethod;
  private static boolean sAddGhostMethodFetched;
  private static Class<?> sGhostViewClass;
  private static boolean sGhostViewClassFetched;
  private static Method sRemoveGhostMethod;
  private static boolean sRemoveGhostMethodFetched;
  private final View mGhostView;
  
  private GhostViewPlatform(View paramView)
  {
    mGhostView = paramView;
  }
  
  static GhostView addGhost(View paramView, ViewGroup paramViewGroup, Matrix paramMatrix)
  {
    
    if (sAddGhostMethod != null)
    {
      Method localMethod = sAddGhostMethod;
      try
      {
        paramView = localMethod.invoke(null, new Object[] { paramView, paramViewGroup, paramMatrix });
        paramView = (View)paramView;
        paramView = new GhostViewPlatform(paramView);
        return paramView;
      }
      catch (InvocationTargetException paramView)
      {
        throw new RuntimeException(paramView.getCause());
      }
      catch (IllegalAccessException paramView) {}
    }
    return null;
  }
  
  private static void fetchAddGhostMethod()
  {
    if (!sAddGhostMethodFetched)
    {
      try
      {
        fetchGhostViewClass();
        Object localObject = sGhostViewClass;
        localObject = ((Class)localObject).getDeclaredMethod("addGhost", new Class[] { View.class, ViewGroup.class, Matrix.class });
        sAddGhostMethod = (Method)localObject;
        localObject = sAddGhostMethod;
        ((Method)localObject).setAccessible(true);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        Log.i("GhostViewApi21", "Failed to retrieve addGhost method", localNoSuchMethodException);
      }
      sAddGhostMethodFetched = true;
    }
  }
  
  private static void fetchGhostViewClass()
  {
    if (!sGhostViewClassFetched)
    {
      try
      {
        Class localClass = Class.forName("android.view.GhostView");
        sGhostViewClass = localClass;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        Log.i("GhostViewApi21", "Failed to retrieve GhostView class", localClassNotFoundException);
      }
      sGhostViewClassFetched = true;
    }
  }
  
  private static void fetchRemoveGhostMethod()
  {
    if (!sRemoveGhostMethodFetched)
    {
      try
      {
        fetchGhostViewClass();
        Object localObject = sGhostViewClass;
        localObject = ((Class)localObject).getDeclaredMethod("removeGhost", new Class[] { View.class });
        sRemoveGhostMethod = (Method)localObject;
        localObject = sRemoveGhostMethod;
        ((Method)localObject).setAccessible(true);
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        Log.i("GhostViewApi21", "Failed to retrieve removeGhost method", localNoSuchMethodException);
      }
      sRemoveGhostMethodFetched = true;
    }
  }
  
  static void removeGhost(View paramView)
  {
    
    if (sRemoveGhostMethod != null)
    {
      Method localMethod = sRemoveGhostMethod;
      try
      {
        localMethod.invoke(null, new Object[] { paramView });
        return;
      }
      catch (InvocationTargetException paramView)
      {
        throw new RuntimeException(paramView.getCause());
      }
      catch (IllegalAccessException paramView) {}
    }
  }
  
  public void reserveEndViewTransition(ViewGroup paramViewGroup, View paramView) {}
  
  public void setVisibility(int paramInt)
  {
    mGhostView.setVisibility(paramInt);
  }
}
