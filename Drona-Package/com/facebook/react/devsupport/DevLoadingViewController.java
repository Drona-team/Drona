package com.facebook.react.devsupport;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;
import com.facebook.react.R.layout;
import com.facebook.react.R.string;
import com.facebook.react.bridge.UiThreadUtil;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class DevLoadingViewController
{
  private static boolean sEnabled;
  @Nullable
  private PopupWindow mDevLoadingPopup;
  @Nullable
  private TextView mDevLoadingView;
  private final ReactInstanceManagerDevHelper mReactInstanceManagerHelper;
  
  public DevLoadingViewController(Context paramContext, ReactInstanceManagerDevHelper paramReactInstanceManagerDevHelper)
  {
    mReactInstanceManagerHelper = paramReactInstanceManagerDevHelper;
  }
  
  private Context getContext()
  {
    return mReactInstanceManagerHelper.getCurrentActivity();
  }
  
  private void hideInternal()
  {
    if ((mDevLoadingPopup != null) && (mDevLoadingPopup.isShowing()))
    {
      mDevLoadingPopup.dismiss();
      mDevLoadingPopup = null;
      mDevLoadingView = null;
    }
  }
  
  public static void setDevLoadingEnabled(boolean paramBoolean)
  {
    sEnabled = paramBoolean;
  }
  
  private void showInternal(String paramString)
  {
    if ((mDevLoadingPopup != null) && (mDevLoadingPopup.isShowing())) {
      return;
    }
    Activity localActivity = mReactInstanceManagerHelper.getCurrentActivity();
    if (localActivity == null)
    {
      FLog.e("ReactNative", "Unable to display loading message because react activity isn't available");
      return;
    }
    Rect localRect = new Rect();
    localActivity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
    int i = top;
    mDevLoadingView = ((TextView)((LayoutInflater)localActivity.getSystemService("layout_inflater")).inflate(R.layout.dev_loading_view, null));
    mDevLoadingView.setText(paramString);
    mDevLoadingPopup = new PopupWindow(mDevLoadingView, -1, -2);
    mDevLoadingPopup.setTouchable(false);
    mDevLoadingPopup.showAtLocation(localActivity.getWindow().getDecorView(), 0, 0, i);
  }
  
  public void hide()
  {
    if (!sEnabled) {
      return;
    }
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        DevLoadingViewController.this.hideInternal();
      }
    });
  }
  
  public void showForRemoteJSEnabled()
  {
    Context localContext = getContext();
    if (localContext == null) {
      return;
    }
    showMessage(localContext.getString(R.string.catalyst_debug_connecting));
  }
  
  public void showForUrl(String paramString)
  {
    Object localObject = getContext();
    if (localObject == null) {
      return;
    }
    try
    {
      paramString = new URL(paramString);
      int i = R.string.catalyst_loading_from_url;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramString.getHost());
      localStringBuilder.append(":");
      localStringBuilder.append(paramString.getPort());
      showMessage(((Context)localObject).getString(i, new Object[] { localStringBuilder.toString() }));
      return;
    }
    catch (MalformedURLException paramString)
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("Bundle url format is invalid. \n\n");
      ((StringBuilder)localObject).append(paramString.toString());
      FLog.e("ReactNative", ((StringBuilder)localObject).toString());
    }
  }
  
  public void showMessage(final String paramString)
  {
    if (!sEnabled) {
      return;
    }
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        DevLoadingViewController.this.showInternal(paramString);
      }
    });
  }
  
  public void updateProgress(final String paramString, final Integer paramInteger1, final Integer paramInteger2)
  {
    if (!sEnabled) {
      return;
    }
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        StringBuilder localStringBuilder = new StringBuilder();
        String str;
        if (paramString != null) {
          str = paramString;
        } else {
          str = "Loading";
        }
        localStringBuilder.append(str);
        if ((paramInteger1 != null) && (paramInteger2 != null) && (paramInteger2.intValue() > 0)) {
          localStringBuilder.append(String.format(Locale.getDefault(), " %.1f%% (%d/%d)", new Object[] { Float.valueOf(paramInteger1.intValue() / paramInteger2.intValue() * 100.0F), paramInteger1, paramInteger2 }));
        }
        localStringBuilder.append("?");
        if (mDevLoadingView != null) {
          mDevLoadingView.setText(localStringBuilder);
        }
      }
    });
  }
}
