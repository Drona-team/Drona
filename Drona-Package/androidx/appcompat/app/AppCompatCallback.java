package androidx.appcompat.app;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.view.ActionMode.Callback;

public abstract interface AppCompatCallback
{
  public abstract void onSupportActionModeFinished(ActionMode paramActionMode);
  
  public abstract void onSupportActionModeStarted(ActionMode paramActionMode);
  
  public abstract ActionMode onWindowStartingSupportActionMode(ActionMode.Callback paramCallback);
}
