package androidx.browser.customtabs;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.BaseBundle;
import android.os.Bundle;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.package_4.ActivityOptionsCompat;
import androidx.core.package_4.BundleCompat;
import java.util.ArrayList;

public final class CustomTabsIntent
{
  public static final String EXTRA_ACTION_BUTTON_BUNDLE = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
  public static final String EXTRA_CLOSE_BUTTON_ICON = "android.support.customtabs.extra.CLOSE_BUTTON_ICON";
  public static final String EXTRA_DEFAULT_SHARE_MENU_ITEM = "android.support.customtabs.extra.SHARE_MENU_ITEM";
  public static final String EXTRA_ENABLE_INSTANT_APPS = "android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS";
  public static final String EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING";
  public static final String EXTRA_EXIT_ANIMATION_BUNDLE = "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE";
  public static final String EXTRA_MENU_ITEMS = "android.support.customtabs.extra.MENU_ITEMS";
  public static final String EXTRA_REMOTEVIEWS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS";
  public static final String EXTRA_REMOTEVIEWS_CLICKED_ID = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_CLICKED_ID";
  public static final String EXTRA_REMOTEVIEWS_PENDINGINTENT = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT";
  public static final String EXTRA_REMOTEVIEWS_VIEW_IDS = "android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS";
  public static final String EXTRA_SECONDARY_TOOLBAR_COLOR = "android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR";
  public static final String EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
  public static final String EXTRA_TINT_ACTION_BUTTON = "android.support.customtabs.extra.TINT_ACTION_BUTTON";
  public static final String EXTRA_TITLE_VISIBILITY_STATE = "android.support.customtabs.extra.TITLE_VISIBILITY";
  public static final String EXTRA_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
  public static final String EXTRA_TOOLBAR_ITEMS = "android.support.customtabs.extra.TOOLBAR_ITEMS";
  private static final String EXTRA_USER_OPT_OUT_FROM_CUSTOM_TABS = "android.support.customtabs.extra.user_opt_out";
  public static final String KEY_DESCRIPTION = "android.support.customtabs.customaction.DESCRIPTION";
  public static final String KEY_ICON = "android.support.customtabs.customaction.ICON";
  public static final String KEY_ID = "android.support.customtabs.customaction.ID";
  public static final String KEY_MENU_ITEM_TITLE = "android.support.customtabs.customaction.MENU_ITEM_TITLE";
  public static final String KEY_PENDING_INTENT = "android.support.customtabs.customaction.PENDING_INTENT";
  private static final int MAX_TOOLBAR_ITEMS = 5;
  public static final int NO_TITLE = 0;
  public static final int SHOW_PAGE_TITLE = 1;
  public static final int TOOLBAR_ACTION_BUTTON_ID = 0;
  @NonNull
  public final Intent intent;
  @Nullable
  public final Bundle startAnimationBundle;
  
  CustomTabsIntent(Intent paramIntent, Bundle paramBundle)
  {
    intent = paramIntent;
    startAnimationBundle = paramBundle;
  }
  
  public static int getMaxToolbarItems()
  {
    return 5;
  }
  
  public static Intent setAlwaysUseBrowserUI(Intent paramIntent)
  {
    Intent localIntent = paramIntent;
    if (paramIntent == null) {
      localIntent = new Intent("android.intent.action.VIEW");
    }
    localIntent.addFlags(268435456);
    localIntent.putExtra("android.support.customtabs.extra.user_opt_out", true);
    return localIntent;
  }
  
  public static boolean shouldAlwaysUseBrowserUI(Intent paramIntent)
  {
    return (paramIntent.getBooleanExtra("android.support.customtabs.extra.user_opt_out", false)) && ((paramIntent.getFlags() & 0x10000000) != 0);
  }
  
  public void launchUrl(Context paramContext, Uri paramUri)
  {
    intent.setData(paramUri);
    ContextCompat.startActivity(paramContext, intent, startAnimationBundle);
  }
  
  public static final class Builder
  {
    private ArrayList<Bundle> mActionButtons;
    private boolean mInstantAppsEnabled;
    private final Intent mIntent = new Intent("android.intent.action.VIEW");
    private ArrayList<Bundle> mMenuItems;
    private Bundle mStartAnimationBundle;
    
    public Builder()
    {
      this(null);
    }
    
    public Builder(CustomTabsSession paramCustomTabsSession)
    {
      Object localObject = null;
      mMenuItems = null;
      mStartAnimationBundle = null;
      mActionButtons = null;
      mInstantAppsEnabled = true;
      if (paramCustomTabsSession != null) {
        mIntent.setPackage(paramCustomTabsSession.getComponentName().getPackageName());
      }
      Bundle localBundle = new Bundle();
      if (paramCustomTabsSession == null) {
        paramCustomTabsSession = localObject;
      } else {
        paramCustomTabsSession = paramCustomTabsSession.getBinder();
      }
      BundleCompat.putBinder(localBundle, "android.support.customtabs.extra.SESSION", paramCustomTabsSession);
      mIntent.putExtras(localBundle);
    }
    
    public Builder addDefaultShareMenuItem()
    {
      mIntent.putExtra("android.support.customtabs.extra.SHARE_MENU_ITEM", true);
      return this;
    }
    
    public Builder addMenuItem(String paramString, PendingIntent paramPendingIntent)
    {
      if (mMenuItems == null) {
        mMenuItems = new ArrayList();
      }
      Bundle localBundle = new Bundle();
      localBundle.putString("android.support.customtabs.customaction.MENU_ITEM_TITLE", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      mMenuItems.add(localBundle);
      return this;
    }
    
    public Builder addToolbarItem(int paramInt, Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent)
      throws IllegalStateException
    {
      if (mActionButtons == null) {
        mActionButtons = new ArrayList();
      }
      if (mActionButtons.size() < 5)
      {
        Bundle localBundle = new Bundle();
        localBundle.putInt("android.support.customtabs.customaction.ID", paramInt);
        localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
        localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
        localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
        mActionButtons.add(localBundle);
        return this;
      }
      throw new IllegalStateException("Exceeded maximum toolbar item count of 5");
    }
    
    public CustomTabsIntent build()
    {
      if (mMenuItems != null) {
        mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.MENU_ITEMS", mMenuItems);
      }
      if (mActionButtons != null) {
        mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.TOOLBAR_ITEMS", mActionButtons);
      }
      mIntent.putExtra("android.support.customtabs.extra.EXTRA_ENABLE_INSTANT_APPS", mInstantAppsEnabled);
      return new CustomTabsIntent(mIntent, mStartAnimationBundle);
    }
    
    public Builder enableUrlBarHiding()
    {
      mIntent.putExtra("android.support.customtabs.extra.ENABLE_URLBAR_HIDING", true);
      return this;
    }
    
    public Builder setActionButton(Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent)
    {
      return setActionButton(paramBitmap, paramString, paramPendingIntent, false);
    }
    
    public Builder setActionButton(Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.support.customtabs.customaction.ID", 0);
      localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
      localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      mIntent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
      mIntent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", paramBoolean);
      return this;
    }
    
    public Builder setCloseButtonIcon(Bitmap paramBitmap)
    {
      mIntent.putExtra("android.support.customtabs.extra.CLOSE_BUTTON_ICON", paramBitmap);
      return this;
    }
    
    public Builder setExitAnimations(Context paramContext, int paramInt1, int paramInt2)
    {
      paramContext = ActivityOptionsCompat.makeCustomAnimation(paramContext, paramInt1, paramInt2).toBundle();
      mIntent.putExtra("android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE", paramContext);
      return this;
    }
    
    public Builder setInstantAppsEnabled(boolean paramBoolean)
    {
      mInstantAppsEnabled = paramBoolean;
      return this;
    }
    
    public Builder setSecondaryToolbarColor(int paramInt)
    {
      mIntent.putExtra("android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR", paramInt);
      return this;
    }
    
    public Builder setSecondaryToolbarViews(RemoteViews paramRemoteViews, int[] paramArrayOfInt, PendingIntent paramPendingIntent)
    {
      mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS", paramRemoteViews);
      mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_VIEW_IDS", paramArrayOfInt);
      mIntent.putExtra("android.support.customtabs.extra.EXTRA_REMOTEVIEWS_PENDINGINTENT", paramPendingIntent);
      return this;
    }
    
    public Builder setShowTitle(boolean paramBoolean)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.copyTypes(TypeTransformer.java:311)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.fixTypes(TypeTransformer.java:226)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:207)\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\n");
    }
    
    public Builder setStartAnimations(Context paramContext, int paramInt1, int paramInt2)
    {
      mStartAnimationBundle = ActivityOptionsCompat.makeCustomAnimation(paramContext, paramInt1, paramInt2).toBundle();
      return this;
    }
    
    public Builder setToolbarColor(int paramInt)
    {
      mIntent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", paramInt);
      return this;
    }
  }
}
