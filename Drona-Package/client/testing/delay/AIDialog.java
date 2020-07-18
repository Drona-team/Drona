package client.testing.delay;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import client.testing.AIServiceException;
import client.testing.R.id;
import client.testing.R.layout;
import client.testing.android.AIConfiguration;
import client.testing.android.AIService;
import client.testing.model.AIError;
import client.testing.model.AIRequest;
import client.testing.model.AIResponse;

public class AIDialog
{
  private static final String PAGE_KEY = "ai.api.ui.AIDialog";
  private final AIButton aiButton;
  private final AIConfiguration config;
  private final Context context;
  private final Dialog dialog;
  private final Handler handler;
  private final TextView partialResultsTextView;
  private AIDialogListener resultsListener;
  
  public AIDialog(Context paramContext, AIConfiguration paramAIConfiguration)
  {
    this(paramContext, paramAIConfiguration, R.layout.aidialog);
  }
  
  public AIDialog(Context paramContext, AIConfiguration paramAIConfiguration, int paramInt)
  {
    context = paramContext;
    config = paramAIConfiguration;
    dialog = new Dialog(paramContext);
    handler = new Handler(Looper.getMainLooper());
    dialog.setCanceledOnTouchOutside(true);
    dialog.requestWindowFeature(1);
    dialog.setContentView(paramInt);
    partialResultsTextView = ((TextView)dialog.findViewById(R.id.partialResultsTextView));
    aiButton = ((AIButton)dialog.findViewById(R.id.micButton));
    aiButton.initialize(paramAIConfiguration);
    setAIButtonCallback(aiButton);
  }
  
  private void resetControls()
  {
    if (partialResultsTextView != null) {
      partialResultsTextView.setText("");
    }
  }
  
  private void setAIButtonCallback(AIButton paramAIButton)
  {
    paramAIButton.setResultsListener(new AIDialog.2(this));
    paramAIButton.setPartialResultsListener(new AIDialog.3(this));
  }
  
  private void startListening()
  {
    if (aiButton != null) {
      aiButton.startListening();
    }
  }
  
  public void close()
  {
    handler.post(new AIDialog.4(this));
  }
  
  public AIService getAIService()
  {
    return aiButton.getAIService();
  }
  
  public Dialog getDialog()
  {
    return dialog;
  }
  
  public void pause()
  {
    if (aiButton != null) {
      aiButton.pause();
    }
  }
  
  public void resume()
  {
    if (aiButton != null) {
      aiButton.resume();
    }
  }
  
  public void setResultsListener(AIDialogListener paramAIDialogListener)
  {
    resultsListener = paramAIDialogListener;
  }
  
  public void showAndListen()
  {
    handler.post(new AIDialog.1(this));
  }
  
  public AIResponse textRequest(AIRequest paramAIRequest)
    throws AIServiceException
  {
    return aiButton.textRequest(paramAIRequest);
  }
  
  public AIResponse textRequest(String paramString)
    throws AIServiceException
  {
    return textRequest(new AIRequest(paramString));
  }
  
  public abstract interface AIDialogListener
  {
    public abstract void onCancelled();
    
    public abstract void onError(AIError paramAIError);
    
    public abstract void onResult(AIResponse paramAIResponse);
  }
}
