package androidx.appcompat.widget;

import android.content.Context;
import android.view.View;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

final class AppCompatTextClassifierHelper
{
  @Nullable
  private TextClassifier mTextClassifier;
  @NonNull
  private TextView mTextView;
  
  AppCompatTextClassifierHelper(TextView paramTextView)
  {
    mTextView = ((TextView)Preconditions.checkNotNull(paramTextView));
  }
  
  public TextClassifier getTextClassifier()
  {
    if (mTextClassifier == null)
    {
      TextClassificationManager localTextClassificationManager = (TextClassificationManager)mTextView.getContext().getSystemService(TextClassificationManager.class);
      if (localTextClassificationManager != null) {
        return localTextClassificationManager.getTextClassifier();
      }
      return TextClassifier.NO_OP;
    }
    return mTextClassifier;
  }
  
  public void setTextClassifier(TextClassifier paramTextClassifier)
  {
    mTextClassifier = paramTextClassifier;
  }
}
