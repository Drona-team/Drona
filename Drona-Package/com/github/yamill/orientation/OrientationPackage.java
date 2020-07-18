package com.github.yamill.orientation;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class OrientationPackage
  implements ReactPackage
{
  public OrientationPackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    return Arrays.asList(new NativeModule[] { new OrientationModule(paramReactApplicationContext) });
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Arrays.asList(new ViewManager[0]);
  }
}
