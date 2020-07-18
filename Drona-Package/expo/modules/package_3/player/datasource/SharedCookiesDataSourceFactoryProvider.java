package expo.modules.package_3.player.datasource;

import android.content.Context;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.unimodules.core.ModuleRegistry;
import org.unimodules.core.interfaces.InternalModule;

public class SharedCookiesDataSourceFactoryProvider
  implements InternalModule, DataSourceFactoryProvider
{
  public SharedCookiesDataSourceFactoryProvider() {}
  
  public DataSource.Factory createFactory(Context paramContext, ModuleRegistry paramModuleRegistry, String paramString, Map paramMap)
  {
    return new SharedCookiesDataSourceFactory(paramContext, paramModuleRegistry, paramString, paramMap);
  }
  
  public List getExportedInterfaces()
  {
    return Collections.singletonList(expo.modules.av.player.datasource.DataSourceFactoryProvider.class);
  }
}
