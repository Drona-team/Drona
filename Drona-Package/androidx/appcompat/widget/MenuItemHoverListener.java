package androidx.appcompat.widget;

import android.view.MenuItem;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.MenuBuilder;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract interface MenuItemHoverListener
{
  public abstract void onItemHoverEnter(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
  
  public abstract void onItemHoverExit(MenuBuilder paramMenuBuilder, MenuItem paramMenuItem);
}
