package client.testing.android;

import android.content.Context;
import androidx.annotation.NonNull;
import client.testing.AIServiceContext;
import com.google.gson.Gson;
import java.util.TimeZone;

public class AIDataService
  extends client.testing.AIDataService
{
  public static final String PAGE_KEY = "ai.api.android.AIDataService";
  @NonNull
  private final AIConfiguration config;
  @NonNull
  private final Context context;
  @NonNull
  private final Gson gson = GsonFactory.getGson();
  
  public AIDataService(Context paramContext, AIConfiguration paramAIConfiguration)
  {
    super(paramAIConfiguration, new AIAndroidServiceContext(paramContext));
    context = paramContext;
    config = paramAIConfiguration;
  }
  
  class AIAndroidServiceContext
    implements AIServiceContext
  {
    private final String sessionId;
    
    public AIAndroidServiceContext()
    {
      sessionId = SessionIdStorage.getSessionId(this$1);
    }
    
    public String getSessionId()
    {
      return sessionId;
    }
    
    public TimeZone getTimeZone()
    {
      return TimeZone.getDefault();
    }
  }
}
