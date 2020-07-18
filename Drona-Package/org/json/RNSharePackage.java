package org.json;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RNSharePackage
  implements ReactPackage
{
  public RNSharePackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    return Arrays.asList(new NativeModule[] { new RNShareModule(paramReactApplicationContext) });
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
}
