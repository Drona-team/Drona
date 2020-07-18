package com.facebook.react.modules.debug;

import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.devsupport.interfaces.DevSupportManager;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name="DevSettings")
public class DevSettingsModule
  extends BaseJavaModule
{
  public static final String NAME = "DevSettings";
  private final DevSupportManager mDevSupportManager;
  
  public DevSettingsModule(DevSupportManager paramDevSupportManager)
  {
    mDevSupportManager = paramDevSupportManager;
  }
  
  public String getName()
  {
    return "DevSettings";
  }
  
  public void reload()
  {
    if (mDevSupportManager.getDevSupportEnabled()) {
      UiThreadUtil.runOnUiThread(new Runnable()
      {
        public void run()
        {
          mDevSupportManager.handleReloadJS();
        }
      });
    }
  }
  
  public void setHotLoadingEnabled(boolean paramBoolean)
  {
    mDevSupportManager.setHotModuleReplacementEnabled(paramBoolean);
  }
  
  public void setIsDebuggingRemotely(boolean paramBoolean)
  {
    mDevSupportManager.setRemoteJSDebugEnabled(paramBoolean);
  }
  
  public void setLiveReloadEnabled(boolean paramBoolean)
  {
    mDevSupportManager.setReloadOnJSChangeEnabled(paramBoolean);
  }
  
  public void setProfilingEnabled(boolean paramBoolean)
  {
    mDevSupportManager.setFpsDebugEnabled(paramBoolean);
  }
  
  public void toggleElementInspector()
  {
    mDevSupportManager.toggleElementInspector();
  }
}
