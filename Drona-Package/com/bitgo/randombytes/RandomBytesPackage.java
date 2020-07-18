package com.bitgo.randombytes;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomBytesPackage
  implements ReactPackage
{
  public RandomBytesPackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new RandomBytesModule(paramReactApplicationContext));
    return localArrayList;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
}
