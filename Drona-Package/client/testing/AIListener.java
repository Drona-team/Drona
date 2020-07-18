package client.testing;

import client.testing.model.AIError;
import client.testing.model.AIResponse;

public abstract interface AIListener
{
  public abstract void onAudioLevel(float paramFloat);
  
  public abstract void onError(AIError paramAIError);
  
  public abstract void onListeningCanceled();
  
  public abstract void onListeningFinished();
  
  public abstract void onListeningStarted();
  
  public abstract void onResult(AIResponse paramAIResponse);
}
