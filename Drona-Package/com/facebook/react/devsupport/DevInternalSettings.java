package com.facebook.react.devsupport;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import com.facebook.react.common.annotations.VisibleForTesting;
import com.facebook.react.modules.debug.interfaces.DeveloperSettings;
import com.facebook.react.packagerconnection.PackagerConnectionSettings;

@VisibleForTesting
public class DevInternalSettings
  implements DeveloperSettings, SharedPreferences.OnSharedPreferenceChangeListener
{
  private static final String PREFS_ANIMATIONS_DEBUG_KEY = "animations_debug";
  private static final String PREFS_FPS_DEBUG_KEY = "fps_debug";
  private static final String PREFS_HOT_MODULE_REPLACEMENT_KEY = "hot_module_replacement";
  private static final String PREFS_INSPECTOR_DEBUG_KEY = "inspector_debug";
  private static final String PREFS_JS_BUNDLE_DELTAS_CPP_KEY = "js_bundle_deltas_cpp";
  private static final String PREFS_JS_BUNDLE_DELTAS_KEY = "js_bundle_deltas";
  private static final String PREFS_JS_DEV_MODE_DEBUG_KEY = "js_dev_mode_debug";
  private static final String PREFS_JS_MINIFY_DEBUG_KEY = "js_minify_debug";
  private static final String PREFS_RELOAD_ON_JS_CHANGE_KEY = "reload_on_js_change_LEGACY";
  private static final String PREFS_REMOTE_JS_DEBUG_KEY = "remote_js_debug";
  private static final String PREFS_START_SAMPLING_PROFILER_ON_INIT = "start_sampling_profiler_on_init";
  private final Listener mListener;
  private final PackagerConnectionSettings mPackagerConnectionSettings;
  private final SharedPreferences mPreferences;
  private final boolean mSupportsNativeDeltaClients;
  
  public DevInternalSettings(Context paramContext, Listener paramListener)
  {
    this(paramContext, paramListener, true);
  }
  
  private DevInternalSettings(Context paramContext, Listener paramListener, boolean paramBoolean)
  {
    mListener = paramListener;
    mPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    mPreferences.registerOnSharedPreferenceChangeListener(this);
    mPackagerConnectionSettings = new PackagerConnectionSettings(paramContext);
    mSupportsNativeDeltaClients = paramBoolean;
  }
  
  public static DevInternalSettings withoutNativeDeltaClient(Context paramContext, Listener paramListener)
  {
    return new DevInternalSettings(paramContext, paramListener, false);
  }
  
  public PackagerConnectionSettings getPackagerConnectionSettings()
  {
    return mPackagerConnectionSettings;
  }
  
  public boolean isAnimationFpsDebugEnabled()
  {
    return mPreferences.getBoolean("animations_debug", false);
  }
  
  public boolean isBundleDeltasCppEnabled()
  {
    return (mSupportsNativeDeltaClients) && (mPreferences.getBoolean("js_bundle_deltas_cpp", false));
  }
  
  public boolean isBundleDeltasEnabled()
  {
    return mPreferences.getBoolean("js_bundle_deltas", false);
  }
  
  public boolean isElementInspectorEnabled()
  {
    return mPreferences.getBoolean("inspector_debug", false);
  }
  
  public boolean isFpsDebugEnabled()
  {
    return mPreferences.getBoolean("fps_debug", false);
  }
  
  public boolean isHotModuleReplacementEnabled()
  {
    return mPreferences.getBoolean("hot_module_replacement", true);
  }
  
  public boolean isJSDevModeEnabled()
  {
    return mPreferences.getBoolean("js_dev_mode_debug", true);
  }
  
  public boolean isJSMinifyEnabled()
  {
    return mPreferences.getBoolean("js_minify_debug", false);
  }
  
  public boolean isNuclideJSDebugEnabled()
  {
    return false;
  }
  
  public boolean isReloadOnJSChangeEnabled()
  {
    return mPreferences.getBoolean("reload_on_js_change_LEGACY", false);
  }
  
  public boolean isRemoteJSDebugEnabled()
  {
    return mPreferences.getBoolean("remote_js_debug", false);
  }
  
  public boolean isStartSamplingProfilerOnInit()
  {
    return mPreferences.getBoolean("start_sampling_profiler_on_init", false);
  }
  
  public void onSharedPreferenceChanged(SharedPreferences paramSharedPreferences, String paramString)
  {
    if ((mListener != null) && (("fps_debug".equals(paramString)) || ("reload_on_js_change_LEGACY".equals(paramString)) || ("js_dev_mode_debug".equals(paramString)) || ("js_bundle_deltas".equals(paramString)) || ("js_bundle_deltas_cpp".equals(paramString)) || ("start_sampling_profiler_on_init".equals(paramString)) || ("js_minify_debug".equals(paramString)))) {
      mListener.onInternalSettingsChanged();
    }
  }
  
  public void setBundleDeltasCppEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("js_bundle_deltas_cpp", paramBoolean).apply();
  }
  
  public void setBundleDeltasEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("js_bundle_deltas", paramBoolean).apply();
  }
  
  public void setElementInspectorEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("inspector_debug", paramBoolean).apply();
  }
  
  public void setFpsDebugEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("fps_debug", paramBoolean).apply();
  }
  
  public void setHotModuleReplacementEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("hot_module_replacement", paramBoolean).apply();
  }
  
  public void setJSDevModeEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("js_dev_mode_debug", paramBoolean).apply();
  }
  
  public void setReloadOnJSChangeEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("reload_on_js_change_LEGACY", paramBoolean).apply();
  }
  
  public void setRemoteJSDebugEnabled(boolean paramBoolean)
  {
    mPreferences.edit().putBoolean("remote_js_debug", paramBoolean).apply();
  }
  
  public static abstract interface Listener
  {
    public abstract void onInternalSettingsChanged();
  }
}
