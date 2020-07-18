package com.airbnb.android.react.lottie;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.Collections;
import java.util.List;

public class LottiePackage
  implements ReactPackage
{
  public LottiePackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.singletonList(new LottieAnimationViewManager());
  }
}
