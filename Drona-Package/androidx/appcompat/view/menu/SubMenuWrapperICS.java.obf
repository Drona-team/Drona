package androidx.appcompat.view.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import androidx.annotation.RestrictTo;
import androidx.core.internal.view.SupportSubMenu;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
class SubMenuWrapperICS
  extends MenuWrapperICS
  implements SubMenu
{
  private final SupportSubMenu mSubMenu;
  
  SubMenuWrapperICS(Context paramContext, SupportSubMenu paramSupportSubMenu)
  {
    super(paramContext, paramSupportSubMenu);
    mSubMenu = paramSupportSubMenu;
  }
  
  public void clearHeader()
  {
    mSubMenu.clearHeader();
  }
  
  public MenuItem getItem()
  {
    return getMenuItemWrapper(mSubMenu.getItem());
  }
  
  public SubMenu setHeaderIcon(int paramInt)
  {
    mSubMenu.setHeaderIcon(paramInt);
    return this;
  }
  
  public SubMenu setHeaderIcon(Drawable paramDrawable)
  {
    mSubMenu.setHeaderIcon(paramDrawable);
    return this;
  }
  
  public SubMenu setHeaderTitle(int paramInt)
  {
    mSubMenu.setHeaderTitle(paramInt);
    return this;
  }
  
  public SubMenu setHeaderTitle(CharSequence paramCharSequence)
  {
    mSubMenu.setHeaderTitle(paramCharSequence);
    return this;
  }
  
  public SubMenu setHeaderView(View paramView)
  {
    mSubMenu.setHeaderView(paramView);
    return this;
  }
  
  public SubMenu setIcon(int paramInt)
  {
    mSubMenu.setIcon(paramInt);
    return this;
  }
  
  public SubMenu setIcon(Drawable paramDrawable)
  {
    mSubMenu.setIcon(paramDrawable);
    return this;
  }
}
