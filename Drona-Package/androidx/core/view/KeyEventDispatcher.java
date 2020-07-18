package androidx.core.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnKeyListener;
import android.os.Build.VERSION;
import android.view.KeyEvent;
import android.view.KeyEvent.DispatcherState;
import android.view.View;
import android.view.Window;
import android.view.Window.Callback;
import androidx.annotation.RestrictTo;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class KeyEventDispatcher
{
  private static boolean sActionBarFieldsFetched;
  private static Method sActionBarOnMenuKeyMethod;
  private static boolean sDialogFieldsFetched;
  private static Field sDialogKeyListenerField;
  
  private KeyEventDispatcher() {}
  
  private static boolean actionBarOnMenuKeyEventPre28(ActionBar paramActionBar, KeyEvent paramKeyEvent)
  {
    if (!sActionBarFieldsFetched) {}
    try
    {
      localObject = paramActionBar.getClass();
      localObject = ((Class)localObject).getMethod("onMenuKeyEvent", new Class[] { KeyEvent.class });
      sActionBarOnMenuKeyMethod = (Method)localObject;
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      Object localObject;
      for (;;) {}
    }
    sActionBarFieldsFetched = true;
    if (sActionBarOnMenuKeyMethod != null) {
      localObject = sActionBarOnMenuKeyMethod;
    }
    try
    {
      paramActionBar = ((Method)localObject).invoke(paramActionBar, new Object[] { paramKeyEvent });
      paramActionBar = (Boolean)paramActionBar;
      boolean bool = paramActionBar.booleanValue();
      return bool;
    }
    catch (IllegalAccessException paramActionBar)
    {
      return false;
    }
    catch (InvocationTargetException paramActionBar) {}
    return false;
    return false;
  }
  
  private static boolean activitySuperDispatchKeyEventPre28(Activity paramActivity, KeyEvent paramKeyEvent)
  {
    paramActivity.onUserInteraction();
    Object localObject = paramActivity.getWindow();
    if (((Window)localObject).hasFeature(8))
    {
      ActionBar localActionBar = paramActivity.getActionBar();
      if ((paramKeyEvent.getKeyCode() == 82) && (localActionBar != null) && (actionBarOnMenuKeyEventPre28(localActionBar, paramKeyEvent))) {
        return true;
      }
    }
    if (((Window)localObject).superDispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    localObject = ((Window)localObject).getDecorView();
    if (ViewCompat.dispatchUnhandledKeyEventBeforeCallback((View)localObject, paramKeyEvent)) {
      return true;
    }
    if (localObject != null) {
      localObject = ((View)localObject).getKeyDispatcherState();
    } else {
      localObject = null;
    }
    return paramKeyEvent.dispatch(paramActivity, (KeyEvent.DispatcherState)localObject, paramActivity);
  }
  
  private static boolean dialogSuperDispatchKeyEventPre28(Dialog paramDialog, KeyEvent paramKeyEvent)
  {
    Object localObject = getDialogKeyListenerPre28(paramDialog);
    if ((localObject != null) && (((DialogInterface.OnKeyListener)localObject).onKey(paramDialog, paramKeyEvent.getKeyCode(), paramKeyEvent))) {
      return true;
    }
    localObject = paramDialog.getWindow();
    if (((Window)localObject).superDispatchKeyEvent(paramKeyEvent)) {
      return true;
    }
    localObject = ((Window)localObject).getDecorView();
    if (ViewCompat.dispatchUnhandledKeyEventBeforeCallback((View)localObject, paramKeyEvent)) {
      return true;
    }
    if (localObject != null) {
      localObject = ((View)localObject).getKeyDispatcherState();
    } else {
      localObject = null;
    }
    return paramKeyEvent.dispatch(paramDialog, (KeyEvent.DispatcherState)localObject, paramDialog);
  }
  
  public static boolean dispatchBeforeHierarchy(View paramView, KeyEvent paramKeyEvent)
  {
    return ViewCompat.dispatchUnhandledKeyEventBeforeHierarchy(paramView, paramKeyEvent);
  }
  
  public static boolean dispatchKeyEvent(Component paramComponent, View paramView, Window.Callback paramCallback, KeyEvent paramKeyEvent)
  {
    if (paramComponent == null) {
      return false;
    }
    if (Build.VERSION.SDK_INT >= 28) {
      return paramComponent.superDispatchKeyEvent(paramKeyEvent);
    }
    if ((paramCallback instanceof Activity)) {
      return activitySuperDispatchKeyEventPre28((Activity)paramCallback, paramKeyEvent);
    }
    if ((paramCallback instanceof Dialog)) {
      return dialogSuperDispatchKeyEventPre28((Dialog)paramCallback, paramKeyEvent);
    }
    return ((paramView != null) && (ViewCompat.dispatchUnhandledKeyEventBeforeCallback(paramView, paramKeyEvent))) || (paramComponent.superDispatchKeyEvent(paramKeyEvent));
  }
  
  private static DialogInterface.OnKeyListener getDialogKeyListenerPre28(Dialog paramDialog)
  {
    if (!sDialogFieldsFetched) {}
    try
    {
      localField = Dialog.class.getDeclaredField("mOnKeyListener");
      sDialogKeyListenerField = localField;
      localField = sDialogKeyListenerField;
      localField.setAccessible(true);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      Field localField;
      for (;;) {}
    }
    sDialogFieldsFetched = true;
    if (sDialogKeyListenerField != null) {
      localField = sDialogKeyListenerField;
    }
    try
    {
      paramDialog = localField.get(paramDialog);
      return (DialogInterface.OnKeyListener)paramDialog;
    }
    catch (IllegalAccessException paramDialog)
    {
      for (;;) {}
    }
    return null;
  }
  
  public static abstract interface Component
  {
    public abstract boolean superDispatchKeyEvent(KeyEvent paramKeyEvent);
  }
}
