package com.facebook.react.modules.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.BaseBundle;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.package_5.DialogFragment;
import androidx.fragment.package_5.Fragment;
import androidx.fragment.package_5.FragmentActivity;
import androidx.fragment.package_5.FragmentManager;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.module.annotations.ReactModule;
import java.util.Map;

@ReactModule(name="DialogManagerAndroid")
public class DialogModule
  extends ReactContextBaseJavaModule
  implements LifecycleEventListener
{
  static final String ACTION_BUTTON_CLICKED = "buttonClicked";
  static final String ACTION_DISMISSED = "dismissed";
  static final Map<String, Object> CONSTANTS = MapBuilder.of("buttonClicked", "buttonClicked", "dismissed", "dismissed", "buttonPositive", Integer.valueOf(-1), "buttonNegative", Integer.valueOf(-2), "buttonNeutral", Integer.valueOf(-3));
  static final String FRAGMENT_TAG = "com.facebook.catalyst.react.dialog.DialogModule";
  static final String KEY_BUTTON_NEGATIVE = "buttonNegative";
  static final String KEY_BUTTON_NEUTRAL = "buttonNeutral";
  static final String KEY_BUTTON_POSITIVE = "buttonPositive";
  static final String KEY_CANCELABLE = "cancelable";
  static final String KEY_ITEMS = "items";
  static final String KEY_MESSAGE = "message";
  static final String KEY_TITLE = "title";
  public static final String NAME = "DialogManagerAndroid";
  private boolean mIsInForeground;
  
  public DialogModule(ReactApplicationContext paramReactApplicationContext)
  {
    super(paramReactApplicationContext);
  }
  
  private FragmentManagerHelper getFragmentManagerHelper()
  {
    Activity localActivity = getCurrentActivity();
    if (localActivity == null) {
      return null;
    }
    return new FragmentManagerHelper(((FragmentActivity)localActivity).getSupportFragmentManager());
  }
  
  public Map getConstants()
  {
    return CONSTANTS;
  }
  
  public String getName()
  {
    return "DialogManagerAndroid";
  }
  
  public void initialize()
  {
    getReactApplicationContext().addLifecycleEventListener(this);
  }
  
  public void onHostDestroy() {}
  
  public void onHostPause()
  {
    mIsInForeground = false;
  }
  
  public void onHostResume()
  {
    mIsInForeground = true;
    FragmentManagerHelper localFragmentManagerHelper = getFragmentManagerHelper();
    if (localFragmentManagerHelper != null)
    {
      localFragmentManagerHelper.showPendingAlert();
      return;
    }
    FLog.w(DialogModule.class, "onHostResume called but no FragmentManager found");
  }
  
  public void showAlert(ReadableMap paramReadableMap, final Callback paramCallback1, final Callback paramCallback2)
  {
    final FragmentManagerHelper localFragmentManagerHelper = getFragmentManagerHelper();
    int i = 0;
    if (localFragmentManagerHelper == null)
    {
      paramCallback1.invoke(new Object[] { "Tried to show an alert while not attached to an Activity" });
      return;
    }
    paramCallback1 = new Bundle();
    if (paramReadableMap.hasKey("title")) {
      paramCallback1.putString("title", paramReadableMap.getString("title"));
    }
    if (paramReadableMap.hasKey("message")) {
      paramCallback1.putString("message", paramReadableMap.getString("message"));
    }
    if (paramReadableMap.hasKey("buttonPositive")) {
      paramCallback1.putString("button_positive", paramReadableMap.getString("buttonPositive"));
    }
    if (paramReadableMap.hasKey("buttonNegative")) {
      paramCallback1.putString("button_negative", paramReadableMap.getString("buttonNegative"));
    }
    if (paramReadableMap.hasKey("buttonNeutral")) {
      paramCallback1.putString("button_neutral", paramReadableMap.getString("buttonNeutral"));
    }
    if (paramReadableMap.hasKey("items"))
    {
      ReadableArray localReadableArray = paramReadableMap.getArray("items");
      CharSequence[] arrayOfCharSequence = new CharSequence[localReadableArray.size()];
      while (i < localReadableArray.size())
      {
        arrayOfCharSequence[i] = localReadableArray.getString(i);
        i += 1;
      }
      paramCallback1.putCharSequenceArray("items", (CharSequence[])arrayOfCharSequence);
    }
    if (paramReadableMap.hasKey("cancelable")) {
      paramCallback1.putBoolean("cancelable", paramReadableMap.getBoolean("cancelable"));
    }
    UiThreadUtil.runOnUiThread(new Runnable()
    {
      public void run()
      {
        localFragmentManagerHelper.showNewAlert(paramCallback1, paramCallback2);
      }
    });
  }
  
  class AlertFragmentListener
    implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener
  {
    private final Callback mCallback;
    private boolean mCallbackConsumed = false;
    
    public AlertFragmentListener(Callback paramCallback)
    {
      mCallback = paramCallback;
    }
    
    public void onClick(DialogInterface paramDialogInterface, int paramInt)
    {
      if ((!mCallbackConsumed) && (getReactApplicationContext().hasActiveCatalystInstance()))
      {
        mCallback.invoke(new Object[] { "buttonClicked", Integer.valueOf(paramInt) });
        mCallbackConsumed = true;
      }
    }
    
    public void onDismiss(DialogInterface paramDialogInterface)
    {
      if ((!mCallbackConsumed) && (getReactApplicationContext().hasActiveCatalystInstance()))
      {
        mCallback.invoke(new Object[] { "dismissed" });
        mCallbackConsumed = true;
      }
    }
  }
  
  private class FragmentManagerHelper
  {
    @NonNull
    private final FragmentManager mFragmentManager;
    @Nullable
    private Object mFragmentToShow;
    
    public FragmentManagerHelper(FragmentManager paramFragmentManager)
    {
      mFragmentManager = paramFragmentManager;
    }
    
    private void dismissExisting()
    {
      if (!mIsInForeground) {
        return;
      }
      AlertFragment localAlertFragment = (AlertFragment)mFragmentManager.findFragmentByTag("com.facebook.catalyst.react.dialog.DialogModule");
      if ((localAlertFragment != null) && (localAlertFragment.isResumed())) {
        localAlertFragment.dismiss();
      }
    }
    
    public void showNewAlert(Bundle paramBundle, Callback paramCallback)
    {
      UiThreadUtil.assertOnUiThread();
      dismissExisting();
      if (paramCallback != null) {
        paramCallback = new DialogModule.AlertFragmentListener(DialogModule.this, paramCallback);
      } else {
        paramCallback = null;
      }
      paramCallback = new AlertFragment(paramCallback, paramBundle);
      if ((mIsInForeground) && (!mFragmentManager.isStateSaved()))
      {
        if (paramBundle.containsKey("cancelable")) {
          paramCallback.setCancelable(paramBundle.getBoolean("cancelable"));
        }
        paramCallback.show(mFragmentManager, "com.facebook.catalyst.react.dialog.DialogModule");
        return;
      }
      mFragmentToShow = paramCallback;
    }
    
    public void showPendingAlert()
    {
      UiThreadUtil.assertOnUiThread();
      SoftAssertions.assertCondition(mIsInForeground, "showPendingAlert() called in background");
      if (mFragmentToShow == null) {
        return;
      }
      dismissExisting();
      ((AlertFragment)mFragmentToShow).show(mFragmentManager, "com.facebook.catalyst.react.dialog.DialogModule");
      mFragmentToShow = null;
    }
  }
}
