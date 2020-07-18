package expo.modules.constants;

import android.content.Context;
import java.util.Map;
import org.unimodules.core.ExportedModule;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.Promise;
import org.unimodules.interfaces.constants.ConstantsInterface;

public class ConstantsModule
  extends ExportedModule
{
  private ModuleRegistry mModuleRegistry;
  
  public ConstantsModule(Context paramContext)
  {
    super(paramContext);
  }
  
  public Map getConstants()
  {
    return ((ConstantsInterface)mModuleRegistry.getModule(ConstantsInterface.class)).getConstants();
  }
  
  public String getName()
  {
    return "ExponentConstants";
  }
  
  public void getWebViewUserAgentAsync(Promise paramPromise)
  {
    paramPromise.resolve(System.getProperty("http.agent"));
  }
  
  public void onCreate(ModuleRegistry paramModuleRegistry)
  {
    mModuleRegistry = paramModuleRegistry;
  }
}
