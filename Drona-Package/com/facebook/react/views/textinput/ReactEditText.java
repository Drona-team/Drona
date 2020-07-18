package com.facebook.react.views.textinput;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.QwertyKeyListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.text.ReactSpan;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.TextAttributes;
import com.facebook.react.views.text.TextInlineImageSpan;
import com.facebook.react.views.view.ReactViewBackgroundManager;
import java.util.ArrayList;
import java.util.Iterator;

public class ReactEditText
  extends EditText
{
  private static final KeyListener sKeyListener = ;
  @Nullable
  private Boolean mBlurOnSubmit;
  protected boolean mContainsImages;
  @Nullable
  private ContentSizeWatcher mContentSizeWatcher;
  private int mDefaultGravityHorizontal;
  private int mDefaultGravityVertical;
  private boolean mDetectScrollMovement = false;
  private boolean mDisableFullscreen;
  private final InputMethodManager mInputMethodManager;
  protected boolean mIsSettingTextFromJS;
  private final InternalKeyListener mKeyListener;
  @Nullable
  private ArrayList<TextWatcher> mListeners;
  protected int mMostRecentEventCount;
  protected int mNativeEventCount;
  private boolean mOnKeyPress = false;
  private ReactViewBackgroundManager mReactBackgroundManager;
  @Nullable
  private String mReturnKeyType;
  @Nullable
  private ScrollWatcher mScrollWatcher;
  @Nullable
  private SelectionWatcher mSelectionWatcher;
  private boolean mShouldAllowFocus;
  private int mStagedInputType;
  private TextAttributes mTextAttributes;
  @Nullable
  private TextWatcherDelegator mTextWatcherDelegator;
  
  public ReactEditText(Context paramContext)
  {
    super(paramContext);
    setFocusableInTouchMode(false);
    mReactBackgroundManager = new ReactViewBackgroundManager(this);
    mInputMethodManager = ((InputMethodManager)Assertions.assertNotNull(getContext().getSystemService("input_method")));
    mDefaultGravityHorizontal = (getGravity() & 0x800007);
    mDefaultGravityVertical = (getGravity() & 0x70);
    mNativeEventCount = 0;
    mMostRecentEventCount = 0;
    mIsSettingTextFromJS = false;
    mShouldAllowFocus = false;
    mBlurOnSubmit = null;
    mDisableFullscreen = false;
    mListeners = null;
    mTextWatcherDelegator = null;
    mStagedInputType = getInputType();
    mKeyListener = new InternalKeyListener();
    mScrollWatcher = null;
    mTextAttributes = new TextAttributes();
    applyTextAttributes();
    if ((Build.VERSION.SDK_INT >= 26) && (Build.VERSION.SDK_INT <= 27)) {
      setLayerType(1, null);
    }
    ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat()
    {
      public boolean performAccessibilityAction(View paramAnonymousView, int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if (paramAnonymousInt == 16)
        {
          ReactEditText.access$002(ReactEditText.this, true);
          requestFocus();
          ReactEditText.access$002(ReactEditText.this, false);
          return true;
        }
        return super.performAccessibilityAction(paramAnonymousView, paramAnonymousInt, paramAnonymousBundle);
      }
    });
  }
  
  private TextWatcherDelegator getTextWatcherDelegator()
  {
    if (mTextWatcherDelegator == null) {
      mTextWatcherDelegator = new TextWatcherDelegator(null);
    }
    return mTextWatcherDelegator;
  }
  
  private boolean isSecureText()
  {
    return (getInputType() & 0x90) != 0;
  }
  
  private void manageSpans(SpannableStringBuilder paramSpannableStringBuilder)
  {
    Object localObject1 = getText();
    int j = length();
    int i = 0;
    localObject1 = ((Spanned)localObject1).getSpans(0, j, Object.class);
    while (i < localObject1.length)
    {
      if ((localObject1[i] instanceof ReactSpan)) {
        getText().removeSpan(localObject1[i]);
      }
      if ((getText().getSpanFlags(localObject1[i]) & 0x21) == 33)
      {
        Object localObject2 = localObject1[i];
        j = getText().getSpanStart(localObject1[i]);
        int k = getText().getSpanEnd(localObject1[i]);
        int m = getText().getSpanFlags(localObject1[i]);
        getText().removeSpan(localObject1[i]);
        if (sameTextForSpan(getText(), paramSpannableStringBuilder, j, k)) {
          paramSpannableStringBuilder.setSpan(localObject2, j, k, m);
        }
      }
      i += 1;
    }
  }
  
  private void onContentSizeChange()
  {
    if (mContentSizeWatcher != null) {
      mContentSizeWatcher.onLayout();
    }
    setIntrinsicContentSize();
  }
  
  private static boolean sameTextForSpan(Editable paramEditable, SpannableStringBuilder paramSpannableStringBuilder, int paramInt1, int paramInt2)
  {
    if (paramInt1 <= paramSpannableStringBuilder.length())
    {
      if (paramInt2 > paramSpannableStringBuilder.length()) {
        return false;
      }
      while (paramInt1 < paramInt2)
      {
        if (paramEditable.charAt(paramInt1) != paramSpannableStringBuilder.charAt(paramInt1)) {
          return false;
        }
        paramInt1 += 1;
      }
      return true;
    }
    return false;
  }
  
  private void setIntrinsicContentSize()
  {
    UIManagerModule localUIManagerModule = (UIManagerModule)((ReactContext)getContext()).getNativeModule(UIManagerModule.class);
    ReactTextInputLocalData localReactTextInputLocalData = new ReactTextInputLocalData(this);
    localUIManagerModule.setViewLocalData(getId(), localReactTextInputLocalData);
  }
  
  private void updateImeOptions()
  {
    String str = mReturnKeyType;
    int j = 4;
    int i;
    if (str != null)
    {
      str = mReturnKeyType;
      switch (str.hashCode())
      {
      default: 
        break;
      case 3526536: 
        if (str.equals("send")) {
          i = 5;
        }
        break;
      case 3387192: 
        if (str.equals("none")) {
          i = 2;
        }
        break;
      case 3377907: 
        if (str.equals("next")) {
          i = 1;
        }
        break;
      case 3089282: 
        if (str.equals("done")) {
          i = 6;
        }
        break;
      case 3304: 
        if (str.equals("go")) {
          i = 0;
        }
        break;
      case -906336856: 
        if (str.equals("search")) {
          i = 4;
        }
        break;
      case -1273775369: 
        if (str.equals("previous")) {
          i = 3;
        }
        break;
      }
      i = -1;
    }
    switch (i)
    {
    default: 
      break;
    case 4: 
      j = 3;
      break;
    case 3: 
      j = 7;
      break;
    case 2: 
      j = 1;
      break;
    case 1: 
      j = 5;
      break;
    case 0: 
      j = 2;
      break;
    case 6: 
      j = 6;
    }
    if (mDisableFullscreen)
    {
      setImeOptions(0x2000000 | j);
      return;
    }
    setImeOptions(j);
  }
  
  public void addTextChangedListener(TextWatcher paramTextWatcher)
  {
    if (mListeners == null)
    {
      mListeners = new ArrayList();
      super.addTextChangedListener(getTextWatcherDelegator());
    }
    mListeners.add(paramTextWatcher);
  }
  
  protected void applyTextAttributes()
  {
    setTextSize(0, mTextAttributes.getEffectiveFontSize());
    if (Build.VERSION.SDK_INT >= 21)
    {
      float f = mTextAttributes.getEffectiveLetterSpacing();
      if (!Float.isNaN(f)) {
        setLetterSpacing(f);
      }
    }
  }
  
  public void clearFocus()
  {
    setFocusableInTouchMode(false);
    super.clearFocus();
    hideSoftKeyboard();
  }
  
  void clearFocusFromJS()
  {
    clearFocus();
  }
  
  void commitStagedInputType()
  {
    if (getInputType() != mStagedInputType)
    {
      int i = getSelectionStart();
      int j = getSelectionEnd();
      setInputType(mStagedInputType);
      setSelection(i, j);
    }
  }
  
  public boolean getBlurOnSubmit()
  {
    if (mBlurOnSubmit == null) {
      return isMultiline() ^ true;
    }
    return mBlurOnSubmit.booleanValue();
  }
  
  public int getBreakStrategy()
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.textinput.ReactEditText: int getBreakStrategy()> does not exist!");
  }
  
  public boolean getDisableFullscreenUI()
  {
    return mDisableFullscreen;
  }
  
  public String getReturnKeyType()
  {
    return mReturnKeyType;
  }
  
  int getStagedInputType()
  {
    return mStagedInputType;
  }
  
  protected void hideSoftKeyboard()
  {
    mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
  }
  
  public int incrementAndGetEventCounter()
  {
    int i = mNativeEventCount + 1;
    mNativeEventCount = i;
    return i;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        if (localObject[i].getDrawable() == paramDrawable) {
          invalidate();
        }
        i += 1;
      }
    }
    super.invalidateDrawable(paramDrawable);
  }
  
  public boolean isLayoutRequested()
  {
    return false;
  }
  
  boolean isMultiline()
  {
    return (getInputType() & 0x20000) != 0;
  }
  
  public void maybeSetText(ReactTextUpdate paramReactTextUpdate)
  {
    if ((isSecureText()) && (TextUtils.equals(getText(), paramReactTextUpdate.getText()))) {
      return;
    }
    mMostRecentEventCount = paramReactTextUpdate.getJsEventCounter();
    if (mMostRecentEventCount < mNativeEventCount) {
      return;
    }
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramReactTextUpdate.getText());
    manageSpans(localSpannableStringBuilder);
    mContainsImages = paramReactTextUpdate.containsImages();
    mIsSettingTextFromJS = true;
    if (paramReactTextUpdate.getText().length() == 0) {
      setText(null);
    } else {
      getText().replace(0, length(), localSpannableStringBuilder);
    }
    mIsSettingTextFromJS = false;
    if ((Build.VERSION.SDK_INT >= 23) && (getBreakStrategy() != paramReactTextUpdate.getTextBreakStrategy())) {
      setBreakStrategy(paramReactTextUpdate.getTextBreakStrategy());
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onAttachedToWindow();
        i += 1;
      }
    }
  }
  
  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
  {
    ReactContext localReactContext = (ReactContext)getContext();
    InputConnection localInputConnection2 = super.onCreateInputConnection(paramEditorInfo);
    InputConnection localInputConnection1 = localInputConnection2;
    Object localObject = localInputConnection1;
    if (localInputConnection2 != null)
    {
      localObject = localInputConnection1;
      if (mOnKeyPress) {
        localObject = new ReactEditTextInputConnectionWrapper(localInputConnection2, localReactContext, this);
      }
    }
    if ((isMultiline()) && (getBlurOnSubmit())) {
      imeOptions &= 0xBFFFFFFF;
    }
    return localObject;
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onDetachedFromWindow();
        i += 1;
      }
    }
  }
  
  public void onFinishTemporaryDetach()
  {
    super.onFinishTemporaryDetach();
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onFinishTemporaryDetach();
        i += 1;
      }
    }
  }
  
  protected void onFocusChanged(boolean paramBoolean, int paramInt, Rect paramRect)
  {
    super.onFocusChanged(paramBoolean, paramInt, paramRect);
    if ((paramBoolean) && (mSelectionWatcher != null)) {
      mSelectionWatcher.onSelectionChanged(getSelectionStart(), getSelectionEnd());
    }
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramInt == 66) && (!isMultiline()))
    {
      hideSoftKeyboard();
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    onContentSizeChange();
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (mScrollWatcher != null) {
      mScrollWatcher.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onSelectionChanged(int paramInt1, int paramInt2)
  {
    super.onSelectionChanged(paramInt1, paramInt2);
    if ((mSelectionWatcher != null) && (hasFocus())) {
      mSelectionWatcher.onSelectionChanged(paramInt1, paramInt2);
    }
  }
  
  public void onStartTemporaryDetach()
  {
    super.onStartTemporaryDetach();
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        localObject[i].onStartTemporaryDetach();
        i += 1;
      }
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i != 0)
    {
      if ((i == 2) && (mDetectScrollMovement))
      {
        if ((!canScrollVertically(-1)) && (!canScrollVertically(1)) && (!canScrollHorizontally(-1)) && (!canScrollHorizontally(1))) {
          getParent().requestDisallowInterceptTouchEvent(false);
        }
        mDetectScrollMovement = false;
      }
    }
    else
    {
      mDetectScrollMovement = true;
      getParent().requestDisallowInterceptTouchEvent(true);
    }
    return super.onTouchEvent(paramMotionEvent);
  }
  
  public void removeTextChangedListener(TextWatcher paramTextWatcher)
  {
    if (mListeners != null)
    {
      mListeners.remove(paramTextWatcher);
      if (mListeners.isEmpty())
      {
        mListeners = null;
        super.removeTextChangedListener(getTextWatcherDelegator());
      }
    }
  }
  
  public boolean requestFocus(int paramInt, Rect paramRect)
  {
    if (isFocused()) {
      return true;
    }
    if (!mShouldAllowFocus) {
      return false;
    }
    setFocusableInTouchMode(true);
    boolean bool = super.requestFocus(paramInt, paramRect);
    if (getShowSoftInputOnFocus()) {
      showSoftKeyboard();
    }
    return bool;
  }
  
  public void requestFocusFromJS()
  {
    mShouldAllowFocus = true;
    requestFocus();
    mShouldAllowFocus = false;
  }
  
  public void setAllowFontScaling(boolean paramBoolean)
  {
    if (mTextAttributes.getAllowFontScaling() != paramBoolean)
    {
      mTextAttributes.setAllowFontScaling(paramBoolean);
      applyTextAttributes();
    }
  }
  
  public void setAutofillHints(String[] paramArrayOfString)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.textinput.ReactEditText: void setAutofillHints(java.lang.String[])> does not exist!");
  }
  
  public void setBackgroundColor(int paramInt)
  {
    mReactBackgroundManager.setBackgroundColor(paramInt);
  }
  
  public void setBlurOnSubmit(Boolean paramBoolean)
  {
    mBlurOnSubmit = paramBoolean;
  }
  
  public void setBorderColor(int paramInt, float paramFloat1, float paramFloat2)
  {
    mReactBackgroundManager.setBorderColor(paramInt, paramFloat1, paramFloat2);
  }
  
  public void setBorderRadius(float paramFloat)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat);
  }
  
  public void setBorderRadius(float paramFloat, int paramInt)
  {
    mReactBackgroundManager.setBorderRadius(paramFloat, paramInt);
  }
  
  public void setBorderStyle(String paramString)
  {
    mReactBackgroundManager.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(int paramInt, float paramFloat)
  {
    mReactBackgroundManager.setBorderWidth(paramInt, paramFloat);
  }
  
  public void setBreakStrategy(int paramInt)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.textinput.ReactEditText: void setBreakStrategy(int)> does not exist!");
  }
  
  public void setContentSizeWatcher(ContentSizeWatcher paramContentSizeWatcher)
  {
    mContentSizeWatcher = paramContentSizeWatcher;
  }
  
  public void setDisableFullscreenUI(boolean paramBoolean)
  {
    mDisableFullscreen = paramBoolean;
    updateImeOptions();
  }
  
  public void setFontSize(float paramFloat)
  {
    mTextAttributes.setFontSize(paramFloat);
    applyTextAttributes();
  }
  
  void setGravityHorizontal(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = mDefaultGravityHorizontal;
    }
    setGravity(i | getGravity() & 0xFFFFFFF8 & 0xFF7FFFF8);
  }
  
  void setGravityVertical(int paramInt)
  {
    int i = paramInt;
    if (paramInt == 0) {
      i = mDefaultGravityVertical;
    }
    setGravity(i | getGravity() & 0xFFFFFF8F);
  }
  
  public void setImportantForAutofill(int paramInt)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.textinput.ReactEditText: void setImportantForAutofill(int)> does not exist!");
  }
  
  public void setInputType(int paramInt)
  {
    Typeface localTypeface = super.getTypeface();
    super.setInputType(paramInt);
    mStagedInputType = paramInt;
    super.setTypeface(localTypeface);
    if (isMultiline()) {
      setSingleLine(false);
    }
    mKeyListener.setInputType(paramInt);
    setKeyListener(mKeyListener);
  }
  
  public void setJustificationMode(int paramInt)
  {
    throw new Error("Unresolved compilation error: Method <com.facebook.react.views.textinput.ReactEditText: void setJustificationMode(int)> does not exist!");
  }
  
  public void setLetterSpacingPt(float paramFloat)
  {
    mTextAttributes.setLetterSpacing(paramFloat);
    applyTextAttributes();
  }
  
  public void setMaxFontSizeMultiplier(float paramFloat)
  {
    if (paramFloat != mTextAttributes.getMaxFontSizeMultiplier())
    {
      mTextAttributes.setMaxFontSizeMultiplier(paramFloat);
      applyTextAttributes();
    }
  }
  
  public void setMostRecentEventCount(int paramInt)
  {
    mMostRecentEventCount = paramInt;
  }
  
  public void setOnKeyPress(boolean paramBoolean)
  {
    mOnKeyPress = paramBoolean;
  }
  
  public void setReturnKeyType(String paramString)
  {
    mReturnKeyType = paramString;
    updateImeOptions();
  }
  
  public void setScrollWatcher(ScrollWatcher paramScrollWatcher)
  {
    mScrollWatcher = paramScrollWatcher;
  }
  
  public void setSelection(int paramInt1, int paramInt2)
  {
    if (mMostRecentEventCount < mNativeEventCount) {
      return;
    }
    super.setSelection(paramInt1, paramInt2);
  }
  
  public void setSelectionWatcher(SelectionWatcher paramSelectionWatcher)
  {
    mSelectionWatcher = paramSelectionWatcher;
  }
  
  void setStagedInputType(int paramInt)
  {
    mStagedInputType = paramInt;
  }
  
  protected boolean showSoftKeyboard()
  {
    return mInputMethodManager.showSoftInput(this, 0);
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    if (mContainsImages)
    {
      Object localObject = getText();
      int j = ((CharSequence)localObject).length();
      int i = 0;
      localObject = (TextInlineImageSpan[])((Spanned)localObject).getSpans(0, j, TextInlineImageSpan.class);
      j = localObject.length;
      while (i < j)
      {
        if (localObject[i].getDrawable() == paramDrawable) {
          return true;
        }
        i += 1;
      }
    }
    return super.verifyDrawable(paramDrawable);
  }
  
  private static class InternalKeyListener
    implements KeyListener
  {
    private int mInputType = 0;
    
    public InternalKeyListener() {}
    
    public void clearMetaKeyState(View paramView, Editable paramEditable, int paramInt)
    {
      ReactEditText.sKeyListener.clearMetaKeyState(paramView, paramEditable, paramInt);
    }
    
    public int getInputType()
    {
      return mInputType;
    }
    
    public boolean onKeyDown(View paramView, Editable paramEditable, int paramInt, KeyEvent paramKeyEvent)
    {
      return ReactEditText.sKeyListener.onKeyDown(paramView, paramEditable, paramInt, paramKeyEvent);
    }
    
    public boolean onKeyOther(View paramView, Editable paramEditable, KeyEvent paramKeyEvent)
    {
      return ReactEditText.sKeyListener.onKeyOther(paramView, paramEditable, paramKeyEvent);
    }
    
    public boolean onKeyUp(View paramView, Editable paramEditable, int paramInt, KeyEvent paramKeyEvent)
    {
      return ReactEditText.sKeyListener.onKeyUp(paramView, paramEditable, paramInt, paramKeyEvent);
    }
    
    public void setInputType(int paramInt)
    {
      mInputType = paramInt;
    }
  }
  
  private class TextWatcherDelegator
    implements TextWatcher
  {
    private TextWatcherDelegator() {}
    
    public void afterTextChanged(Editable paramEditable)
    {
      if ((!mIsSettingTextFromJS) && (mListeners != null))
      {
        Iterator localIterator = mListeners.iterator();
        while (localIterator.hasNext()) {
          ((TextWatcher)localIterator.next()).afterTextChanged(paramEditable);
        }
      }
    }
    
    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((!mIsSettingTextFromJS) && (mListeners != null))
      {
        Iterator localIterator = mListeners.iterator();
        while (localIterator.hasNext()) {
          ((TextWatcher)localIterator.next()).beforeTextChanged(paramCharSequence, paramInt1, paramInt2, paramInt3);
        }
      }
    }
    
    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((!mIsSettingTextFromJS) && (mListeners != null))
      {
        Iterator localIterator = mListeners.iterator();
        while (localIterator.hasNext()) {
          ((TextWatcher)localIterator.next()).onTextChanged(paramCharSequence, paramInt1, paramInt2, paramInt3);
        }
      }
      ReactEditText.this.onContentSizeChange();
    }
  }
}
