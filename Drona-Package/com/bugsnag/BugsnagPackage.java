package com.bugsnag;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Collections;
import java.util.List;

class BugsnagPackage
  implements ReactPackage
{
  BugsnagPackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.singletonList(new BugsnagReactNative(paramReactApplicationContext));
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
}
