package expo.modules.package_3.player.datasource;

import android.content.Context;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource.Factory;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import java.net.CookieHandler;
import java.util.Map;
import okhttp3.CookieJar;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient.Builder;
import org.unimodules.core.ModuleRegistry;

public class SharedCookiesDataSourceFactory
  implements DataSource.Factory
{
  private final DataSource.Factory mDataSourceFactory;
  
  public SharedCookiesDataSourceFactory(Context paramContext, ModuleRegistry paramModuleRegistry, String paramString, Map paramMap)
  {
    paramModuleRegistry = (CookieHandler)paramModuleRegistry.getModule(CookieHandler.class);
    OkHttpClient.Builder localBuilder = new OkHttpClient.Builder();
    if (paramModuleRegistry != null) {
      localBuilder.cookieJar((CookieJar)new JavaNetCookieJar(paramModuleRegistry));
    }
    mDataSourceFactory = new DefaultDataSourceFactory(paramContext, null, new CustomHeadersOkHttpDataSourceFactory(localBuilder.build(), paramString, paramMap));
  }
  
  public DataSource createDataSource()
  {
    return mDataSourceFactory.createDataSource();
  }
}
