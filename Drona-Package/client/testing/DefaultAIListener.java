package client.testing;

public abstract class DefaultAIListener
  implements AIListener
{
  public DefaultAIListener() {}
  
  public void onAudioLevel(float paramFloat) {}
  
  public void onListeningFinished() {}
  
  public void onListeningStarted() {}
}
