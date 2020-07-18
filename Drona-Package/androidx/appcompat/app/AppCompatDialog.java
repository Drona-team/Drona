package androidx.appcompat.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import androidx.appcompat.R.attr;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;
import androidx.core.view.KeyEventDispatcher;
import androidx.core.view.KeyEventDispatcher.Component;

public class AppCompatDialog
  extends Dialog
  implements AppCompatCallback
{
  private AppCompatDelegate mDelegate;
  private final KeyEventDispatcher.Component mKeyDispatcher = new KeyEventDispatcher.Component()
  {
    public boolean superDispatchKeyEvent(KeyEvent paramAnonymousKeyEvent)
    {
      return AppCompatDialog.this.superDispatchKeyEvent(paramAnonymousKeyEvent);
    }
  };
  
  public AppCompatDialog(Context paramContext)
  {
    this(paramContext, 0);
  }
  
  public AppCompatDialog(Context paramContext, int paramInt)
  {
    super(paramContext, getThemeResId(paramContext, paramInt));
    AppCompatDelegate localAppCompatDelegate = getDelegate();
    localAppCompatDelegate.setTheme(getThemeResId(paramContext, paramInt));
    localAppCompatDelegate.onCreate(null);
  }
  
  protected AppCompatDialog(Context paramContext, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener)
  {
    super(paramContext, paramBoolean, paramOnCancelListener);
  }
  
  private static int getThemeResId(Context paramContext, int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0)
    {
      TypedValue localTypedValue = new TypedValue();
      paramContext.getTheme().resolveAttribute(R.attr.dialogTheme, localTypedValue, true);
      i = resourceId;
    }
    return i;
  }
  
  public void addContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getDelegate().addContentView(paramView, paramLayoutParams);
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    View localView = getWindow().getDecorView();
    return KeyEventDispatcher.dispatchKeyEvent(mKeyDispatcher, localView, this, paramKeyEvent);
  }
  
  public View findViewById(int paramInt)
  {
    return getDelegate().findViewById(paramInt);
  }
  
  public AppCompatDelegate getDelegate()
  {
    if (mDelegate == null) {
      mDelegate = AppCompatDelegate.create(this, this);
    }
    return mDelegate;
  }
  
  public ActionBar getSupportActionBar()
  {
    return getDelegate().getSupportActionBar();
  }
  
  public void invalidateOptionsMenu()
  {
    getDelegate().invalidateOptionsMenu();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    getDelegate().installViewFactory();
    super.onCreate(paramBundle);
    getDelegate().onCreate(paramBundle);
  }
  
  protected void onStop()
  {
    super.onStop();
    getDelegate().onStop();
  }
  
  public void onSupportActionModeFinished(ActionMode paramActionMode) {}
  
  public void onSupportActionModeStarted(ActionMode paramActionMode) {}
  
  public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback paramCallback)
  {
    return null;
  }
  
  public void setContentView(int paramInt)
  {
    getDelegate().setContentView(paramInt);
  }
  
  public void setContentView(View paramView)
  {
    getDelegate().setContentView(paramView);
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    getDelegate().setContentView(paramView, paramLayoutParams);
  }
  
  public void setTitle(int paramInt)
  {
    super.setTitle(paramInt);
    getDelegate().setTitle(getContext().getString(paramInt));
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    super.setTitle(paramCharSequence);
    getDelegate().setTitle(paramCharSequence);
  }
  
  boolean superDispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  public boolean supportRequestWindowFeature(int paramInt)
  {
    return getDelegate().requestWindowFeature(paramInt);
  }
}
