package expo.modules.package_3.player.datasource;

import android.content.Context;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import java.util.Map;
import org.unimodules.core.ModuleRegistry;

public abstract interface DataSourceFactoryProvider
{
  public abstract DataSource.Factory createFactory(Context paramContext, ModuleRegistry paramModuleRegistry, String paramString, Map paramMap);
}
