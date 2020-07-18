package email.apptailor.googlesignin;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RNGoogleSigninPackage
  implements ReactPackage
{
  public RNGoogleSigninPackage() {}
  
  public List createNativeModules(ReactApplicationContext paramReactApplicationContext)
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new RNGoogleSigninModule(paramReactApplicationContext));
    return localArrayList;
  }
  
  public List createViewManagers(ReactApplicationContext paramReactApplicationContext)
  {
    return Arrays.asList(new ViewManager[] { new RNGoogleSigninButtonViewManager() });
  }
}
