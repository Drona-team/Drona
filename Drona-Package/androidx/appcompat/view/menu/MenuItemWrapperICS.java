package androidx.appcompat.view.menu;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.internal.view.SupportMenuItem;
import java.lang.reflect.Method;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class MenuItemWrapperICS
  extends BaseMenuWrapper
  implements MenuItem
{
  static final String LOG_TAG = "MenuItemWrapper";
  private Method mSetExclusiveCheckableMethod;
  private final SupportMenuItem mWrappedObject;
  
  public MenuItemWrapperICS(Context paramContext, SupportMenuItem paramSupportMenuItem)
  {
    super(paramContext);
    if (paramSupportMenuItem != null)
    {
      mWrappedObject = paramSupportMenuItem;
      return;
    }
    throw new IllegalArgumentException("Wrapped Object can not be null.");
  }
  
  public boolean collapseActionView()
  {
    return mWrappedObject.collapseActionView();
  }
  
  public boolean expandActionView()
  {
    return mWrappedObject.expandActionView();
  }
  
  public android.view.ActionProvider getActionProvider()
  {
    androidx.core.view.ActionProvider localActionProvider = mWrappedObject.getSupportActionProvider();
    if ((localActionProvider instanceof ActionProviderWrapper)) {
      return mInner;
    }
    return null;
  }
  
  public View getActionView()
  {
    View localView2 = mWrappedObject.getActionView();
    View localView1 = localView2;
    if ((localView2 instanceof CollapsibleActionViewWrapper)) {
      localView1 = ((CollapsibleActionViewWrapper)localView2).getWrappedView();
    }
    return localView1;
  }
  
  public int getAlphabeticModifiers()
  {
    return mWrappedObject.getAlphabeticModifiers();
  }
  
  public char getAlphabeticShortcut()
  {
    return mWrappedObject.getAlphabeticShortcut();
  }
  
  public CharSequence getContentDescription()
  {
    return mWrappedObject.getContentDescription();
  }
  
  public int getGroupId()
  {
    return mWrappedObject.getGroupId();
  }
  
  public Drawable getIcon()
  {
    return mWrappedObject.getIcon();
  }
  
  public ColorStateList getIconTintList()
  {
    return mWrappedObject.getIconTintList();
  }
  
  public PorterDuff.Mode getIconTintMode()
  {
    return mWrappedObject.getIconTintMode();
  }
  
  public Intent getIntent()
  {
    return mWrappedObject.getIntent();
  }
  
  public int getItemId()
  {
    return mWrappedObject.getItemId();
  }
  
  public ContextMenu.ContextMenuInfo getMenuInfo()
  {
    return mWrappedObject.getMenuInfo();
  }
  
  public int getNumericModifiers()
  {
    return mWrappedObject.getNumericModifiers();
  }
  
  public char getNumericShortcut()
  {
    return mWrappedObject.getNumericShortcut();
  }
  
  public int getOrder()
  {
    return mWrappedObject.getOrder();
  }
  
  public SubMenu getSubMenu()
  {
    return getSubMenuWrapper(mWrappedObject.getSubMenu());
  }
  
  public CharSequence getTitle()
  {
    return mWrappedObject.getTitle();
  }
  
  public CharSequence getTitleCondensed()
  {
    return mWrappedObject.getTitleCondensed();
  }
  
  public CharSequence getTooltipText()
  {
    return mWrappedObject.getTooltipText();
  }
  
  public boolean hasSubMenu()
  {
    return mWrappedObject.hasSubMenu();
  }
  
  public boolean isActionViewExpanded()
  {
    return mWrappedObject.isActionViewExpanded();
  }
  
  public boolean isCheckable()
  {
    return mWrappedObject.isCheckable();
  }
  
  public boolean isChecked()
  {
    return mWrappedObject.isChecked();
  }
  
  public boolean isEnabled()
  {
    return mWrappedObject.isEnabled();
  }
  
  public boolean isVisible()
  {
    return mWrappedObject.isVisible();
  }
  
  public MenuItem setActionProvider(android.view.ActionProvider paramActionProvider)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a3 = a2\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  public MenuItem setActionView(int paramInt)
  {
    mWrappedObject.setActionView(paramInt);
    View localView = mWrappedObject.getActionView();
    if ((localView instanceof android.view.CollapsibleActionView)) {
      mWrappedObject.setActionView(new CollapsibleActionViewWrapper(localView));
    }
    return this;
  }
  
  public MenuItem setActionView(View paramView)
  {
    Object localObject = paramView;
    if ((paramView instanceof android.view.CollapsibleActionView)) {
      localObject = new CollapsibleActionViewWrapper(paramView);
    }
    mWrappedObject.setActionView((View)localObject);
    return this;
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar)
  {
    mWrappedObject.setAlphabeticShortcut(paramChar);
    return this;
  }
  
  public MenuItem setAlphabeticShortcut(char paramChar, int paramInt)
  {
    mWrappedObject.setAlphabeticShortcut(paramChar, paramInt);
    return this;
  }
  
  public MenuItem setCheckable(boolean paramBoolean)
  {
    mWrappedObject.setCheckable(paramBoolean);
    return this;
  }
  
  public MenuItem setChecked(boolean paramBoolean)
  {
    mWrappedObject.setChecked(paramBoolean);
    return this;
  }
  
  public MenuItem setContentDescription(CharSequence paramCharSequence)
  {
    mWrappedObject.setContentDescription(paramCharSequence);
    return this;
  }
  
  public MenuItem setEnabled(boolean paramBoolean)
  {
    mWrappedObject.setEnabled(paramBoolean);
    return this;
  }
  
  public void setExclusiveCheckable(boolean paramBoolean)
  {
    Object localObject1;
    if (mSetExclusiveCheckableMethod == null) {
      localObject1 = mWrappedObject;
    }
    try
    {
      localObject1 = localObject1.getClass();
      Object localObject2 = Boolean.TYPE;
      localObject1 = ((Class)localObject1).getDeclaredMethod("setExclusiveCheckable", new Class[] { localObject2 });
      mSetExclusiveCheckableMethod = ((Method)localObject1);
      localObject1 = mSetExclusiveCheckableMethod;
      localObject2 = mWrappedObject;
      ((Method)localObject1).invoke(localObject2, new Object[] { Boolean.valueOf(paramBoolean) });
      return;
    }
    catch (Exception localException)
    {
      Log.w("MenuItemWrapper", "Error while calling setExclusiveCheckable", localException);
    }
  }
  
  public MenuItem setIcon(int paramInt)
  {
    mWrappedObject.setIcon(paramInt);
    return this;
  }
  
  public MenuItem setIcon(Drawable paramDrawable)
  {
    mWrappedObject.setIcon(paramDrawable);
    return this;
  }
  
  public MenuItem setIconTintList(ColorStateList paramColorStateList)
  {
    mWrappedObject.setIconTintList(paramColorStateList);
    return this;
  }
  
  public MenuItem setIconTintMode(PorterDuff.Mode paramMode)
  {
    mWrappedObject.setIconTintMode(paramMode);
    return this;
  }
  
  public MenuItem setIntent(Intent paramIntent)
  {
    mWrappedObject.setIntent(paramIntent);
    return this;
  }
  
  public MenuItem setNumericShortcut(char paramChar)
  {
    mWrappedObject.setNumericShortcut(paramChar);
    return this;
  }
  
  public MenuItem setNumericShortcut(char paramChar, int paramInt)
  {
    mWrappedObject.setNumericShortcut(paramChar, paramInt);
    return this;
  }
  
  public MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener paramOnActionExpandListener)
  {
    SupportMenuItem localSupportMenuItem = mWrappedObject;
    if (paramOnActionExpandListener != null) {
      paramOnActionExpandListener = new OnActionExpandListenerWrapper(paramOnActionExpandListener);
    } else {
      paramOnActionExpandListener = null;
    }
    localSupportMenuItem.setOnActionExpandListener(paramOnActionExpandListener);
    return this;
  }
  
  public MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener paramOnMenuItemClickListener)
  {
    SupportMenuItem localSupportMenuItem = mWrappedObject;
    if (paramOnMenuItemClickListener != null) {
      paramOnMenuItemClickListener = new OnMenuItemClickListenerWrapper(paramOnMenuItemClickListener);
    } else {
      paramOnMenuItemClickListener = null;
    }
    localSupportMenuItem.setOnMenuItemClickListener(paramOnMenuItemClickListener);
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2)
  {
    mWrappedObject.setShortcut(paramChar1, paramChar2);
    return this;
  }
  
  public MenuItem setShortcut(char paramChar1, char paramChar2, int paramInt1, int paramInt2)
  {
    mWrappedObject.setShortcut(paramChar1, paramChar2, paramInt1, paramInt2);
    return this;
  }
  
  public void setShowAsAction(int paramInt)
  {
    mWrappedObject.setShowAsAction(paramInt);
  }
  
  public MenuItem setShowAsActionFlags(int paramInt)
  {
    mWrappedObject.setShowAsActionFlags(paramInt);
    return this;
  }
  
  public MenuItem setTitle(int paramInt)
  {
    mWrappedObject.setTitle(paramInt);
    return this;
  }
  
  public MenuItem setTitle(CharSequence paramCharSequence)
  {
    mWrappedObject.setTitle(paramCharSequence);
    return this;
  }
  
  public MenuItem setTitleCondensed(CharSequence paramCharSequence)
  {
    mWrappedObject.setTitleCondensed(paramCharSequence);
    return this;
  }
  
  public MenuItem setTooltipText(CharSequence paramCharSequence)
  {
    mWrappedObject.setTooltipText(paramCharSequence);
    return this;
  }
  
  public MenuItem setVisible(boolean paramBoolean)
  {
    return mWrappedObject.setVisible(paramBoolean);
  }
  
  private class ActionProviderWrapper
    extends androidx.core.view.ActionProvider
  {
    final android.view.ActionProvider mInner;
    
    ActionProviderWrapper(Context paramContext, android.view.ActionProvider paramActionProvider)
    {
      super();
      mInner = paramActionProvider;
    }
    
    public boolean hasSubMenu()
    {
      return mInner.hasSubMenu();
    }
    
    public View onCreateActionView()
    {
      return mInner.onCreateActionView();
    }
    
    public boolean onPerformDefaultAction()
    {
      return mInner.onPerformDefaultAction();
    }
    
    public void onPrepareSubMenu(SubMenu paramSubMenu)
    {
      mInner.onPrepareSubMenu(getSubMenuWrapper(paramSubMenu));
    }
  }
  
  @RequiresApi(16)
  private class ActionProviderWrapperJB
    extends MenuItemWrapperICS.ActionProviderWrapper
    implements android.view.ActionProvider.VisibilityListener
  {
    private androidx.core.view.ActionProvider.VisibilityListener mListener;
    
    ActionProviderWrapperJB(Context paramContext, android.view.ActionProvider paramActionProvider)
    {
      super(paramContext, paramActionProvider);
    }
    
    public boolean isVisible()
    {
      return mInner.isVisible();
    }
    
    public void onActionProviderVisibilityChanged(boolean paramBoolean)
    {
      if (mListener != null) {
        mListener.onActionProviderVisibilityChanged(paramBoolean);
      }
    }
    
    public View onCreateActionView(MenuItem paramMenuItem)
    {
      return mInner.onCreateActionView(paramMenuItem);
    }
    
    public boolean overridesItemVisibility()
    {
      return mInner.overridesItemVisibility();
    }
    
    public void refreshVisibility()
    {
      mInner.refreshVisibility();
    }
    
    public void setVisibilityListener(androidx.core.view.ActionProvider.VisibilityListener paramVisibilityListener)
    {
      mListener = paramVisibilityListener;
      android.view.ActionProvider localActionProvider = mInner;
      if (paramVisibilityListener != null) {
        paramVisibilityListener = this;
      } else {
        paramVisibilityListener = null;
      }
      localActionProvider.setVisibilityListener(paramVisibilityListener);
    }
  }
  
  static class CollapsibleActionViewWrapper
    extends FrameLayout
    implements androidx.appcompat.view.CollapsibleActionView
  {
    final android.view.CollapsibleActionView mWrappedView;
    
    CollapsibleActionViewWrapper(View paramView)
    {
      super();
      mWrappedView = ((android.view.CollapsibleActionView)paramView);
      addView(paramView);
    }
    
    View getWrappedView()
    {
      return (View)mWrappedView;
    }
    
    public void onActionViewCollapsed()
    {
      mWrappedView.onActionViewCollapsed();
    }
    
    public void onActionViewExpanded()
    {
      mWrappedView.onActionViewExpanded();
    }
  }
  
  private class OnActionExpandListenerWrapper
    implements MenuItem.OnActionExpandListener
  {
    private final MenuItem.OnActionExpandListener mObject;
    
    OnActionExpandListenerWrapper(MenuItem.OnActionExpandListener paramOnActionExpandListener)
    {
      mObject = paramOnActionExpandListener;
    }
    
    public boolean onMenuItemActionCollapse(MenuItem paramMenuItem)
    {
      return mObject.onMenuItemActionCollapse(getMenuItemWrapper(paramMenuItem));
    }
    
    public boolean onMenuItemActionExpand(MenuItem paramMenuItem)
    {
      return mObject.onMenuItemActionExpand(getMenuItemWrapper(paramMenuItem));
    }
  }
  
  private class OnMenuItemClickListenerWrapper
    implements MenuItem.OnMenuItemClickListener
  {
    private final MenuItem.OnMenuItemClickListener mObject;
    
    OnMenuItemClickListenerWrapper(MenuItem.OnMenuItemClickListener paramOnMenuItemClickListener)
    {
      mObject = paramOnMenuItemClickListener;
    }
    
    public boolean onMenuItemClick(MenuItem paramMenuItem)
    {
      return mObject.onMenuItemClick(getMenuItemWrapper(paramMenuItem));
    }
  }
}
