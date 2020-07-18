package com.RNFetchBlob;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RNFetchBlobPackage
  implements ReactPackage
{
  public RNFetchBlobPackage() {}
  
  public List createJSModules()
  {
    return Collections.emptyList();
  }
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new RNFetchBlob(paramReactApplicationContext));
    return localArrayList;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Collections.emptyList();
  }
}
