package client.testing.android;

import com.google.gson.Gson;

public class GsonFactory
{
  public GsonFactory() {}
  
  public static Gson getGson()
  {
    return client.testing.GsonFactory.getDefaultFactory().getGson();
  }
}
