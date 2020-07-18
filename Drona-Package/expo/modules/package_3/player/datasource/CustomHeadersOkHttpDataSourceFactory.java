package expo.modules.package_3.player.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.http.okhttp.OkHttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource.RequestProperties;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import okhttp3.CacheControl;
import okhttp3.Call.Factory;

public class CustomHeadersOkHttpDataSourceFactory
  extends HttpDataSource.BaseFactory
{
  @Nullable
  private final CacheControl mCacheControl;
  @NonNull
  private final Call.Factory mCallFactory;
  @Nullable
  private final TransferListener mListener;
  @Nullable
  private final String mUserAgent;
  
  public CustomHeadersOkHttpDataSourceFactory(Call.Factory paramFactory, String paramString, Map paramMap)
  {
    mCallFactory = paramFactory;
    mUserAgent = paramString;
    mListener = null;
    mCacheControl = null;
    updateRequestProperties(getDefaultRequestProperties(), paramMap);
  }
  
  protected OkHttpDataSource createDataSourceInternal(HttpDataSource.RequestProperties paramRequestProperties)
  {
    return new OkHttpDataSource(mCallFactory, mUserAgent, null, mCacheControl, paramRequestProperties);
  }
  
  protected void updateRequestProperties(HttpDataSource.RequestProperties paramRequestProperties, Map paramMap)
  {
    if (paramMap != null)
    {
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        if ((localEntry.getValue() instanceof String)) {
          paramRequestProperties.put((String)localEntry.getKey(), (String)localEntry.getValue());
        }
      }
    }
  }
}
