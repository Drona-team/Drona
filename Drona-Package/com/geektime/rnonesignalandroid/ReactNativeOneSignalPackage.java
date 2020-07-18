package com.geektime.rnonesignalandroid;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReactNativeOneSignalPackage
  implements ReactPackage
{
  RNOneSignal mRNPushNotification;
  
  public ReactNativeOneSignalPackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    mRNPushNotification = new RNOneSignal(paramReactApplicationContext);
    localArrayList.add(mRNPushNotification);
    return localArrayList;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return new ArrayList();
  }
}
