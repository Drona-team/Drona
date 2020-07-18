package androidx.core.package_4;

import android.app.Dialog;
import android.os.Build.VERSION;
import android.view.View;

public class DialogCompat
{
  private DialogCompat() {}
  
  public static View requireViewById(Dialog paramDialog, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return paramDialog.requireViewById(paramInt);
    }
    paramDialog = paramDialog.findViewById(paramInt);
    if (paramDialog != null) {
      return paramDialog;
    }
    throw new IllegalArgumentException("ID does not reference a View inside this Dialog");
  }
}
