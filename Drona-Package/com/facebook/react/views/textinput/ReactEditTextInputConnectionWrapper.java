package com.facebook.react.views.textinput;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;

class ReactEditTextInputConnectionWrapper
  extends InputConnectionWrapper
{
  public static final String BACKSPACE_KEY_VALUE = "Backspace";
  public static final String ENTER_KEY_VALUE = "Enter";
  public static final String NEWLINE_RAW_VALUE = "\n";
  private ReactEditText mEditText;
  private EventDispatcher mEventDispatcher;
  private boolean mIsBatchEdit;
  @Nullable
  private String mKey = null;
  
  public ReactEditTextInputConnectionWrapper(InputConnection paramInputConnection, ReactContext paramReactContext, ReactEditText paramReactEditText)
  {
    super(paramInputConnection, false);
    mEventDispatcher = ((UIManagerModule)paramReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
    mEditText = paramReactEditText;
  }
  
  private void dispatchKeyEvent(String paramString)
  {
    String str = paramString;
    if (paramString.equals("\n")) {
      str = "Enter";
    }
    mEventDispatcher.dispatchEvent(new ReactTextInputKeyPressEvent(mEditText.getId(), str));
  }
  
  private void dispatchKeyEventOrEnqueue(String paramString)
  {
    if (mIsBatchEdit)
    {
      mKey = paramString;
      return;
    }
    dispatchKeyEvent(paramString);
  }
  
  public boolean beginBatchEdit()
  {
    mIsBatchEdit = true;
    return super.beginBatchEdit();
  }
  
  public boolean commitText(CharSequence paramCharSequence, int paramInt)
  {
    String str2 = paramCharSequence.toString();
    String str1 = str2;
    if (str2.length() <= 2)
    {
      if (str2.equals("")) {
        str1 = "Backspace";
      }
      dispatchKeyEventOrEnqueue(str1);
    }
    return super.commitText(paramCharSequence, paramInt);
  }
  
  public boolean deleteSurroundingText(int paramInt1, int paramInt2)
  {
    dispatchKeyEvent("Backspace");
    return super.deleteSurroundingText(paramInt1, paramInt2);
  }
  
  public boolean endBatchEdit()
  {
    mIsBatchEdit = false;
    if (mKey != null)
    {
      dispatchKeyEvent(mKey);
      mKey = null;
    }
    return super.endBatchEdit();
  }
  
  public boolean sendKeyEvent(KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0) {
      if (paramKeyEvent.getKeyCode() == 67) {
        dispatchKeyEvent("Backspace");
      } else if (paramKeyEvent.getKeyCode() == 66) {
        dispatchKeyEvent("Enter");
      }
    }
    return super.sendKeyEvent(paramKeyEvent);
  }
  
  public boolean setComposingText(CharSequence paramCharSequence, int paramInt)
  {
    int m = mEditText.getSelectionStart();
    int i = mEditText.getSelectionEnd();
    boolean bool = super.setComposingText(paramCharSequence, paramInt);
    int k = mEditText.getSelectionStart();
    int j = 0;
    if (m == i) {
      paramInt = 1;
    } else {
      paramInt = 0;
    }
    if (k == m) {
      i = 1;
    } else {
      i = 0;
    }
    if ((k < m) || (k <= 0)) {
      j = 1;
    }
    if ((j == 0) && ((paramInt != 0) || (i == 0))) {
      paramCharSequence = String.valueOf(mEditText.getText().charAt(k - 1));
    } else {
      paramCharSequence = "Backspace";
    }
    dispatchKeyEventOrEnqueue(paramCharSequence);
    return bool;
  }
}
