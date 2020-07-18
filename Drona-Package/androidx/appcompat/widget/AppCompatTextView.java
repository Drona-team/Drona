package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.ActionMode.Callback;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.textclassifier.TextClassifier;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.content.wiki.AppCompatResources;
import androidx.core.graphics.TypefaceCompat;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.text.PrecomputedTextCompat.Params;
import androidx.core.view.TintableBackgroundView;
import androidx.core.widget.AutoSizeableTextView;
import androidx.core.widget.TextViewCompat;
import androidx.core.widget.TintableCompoundDrawablesView;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AppCompatTextView
  extends TextView
  implements TintableBackgroundView, TintableCompoundDrawablesView, AutoSizeableTextView
{
  private final AppCompatBackgroundHelper mBackgroundTintHelper = new AppCompatBackgroundHelper(this);
  @Nullable
  private Future<PrecomputedTextCompat> mPrecomputedTextFuture;
  private final AppCompatTextClassifierHelper mTextClassifierHelper;
  private final AppCompatTextHelper mTextHelper;
  
  public AppCompatTextView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppCompatTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 16842884);
  }
  
  public AppCompatTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(TintContextWrapper.wrap(paramContext), paramAttributeSet, paramInt);
    mBackgroundTintHelper.loadFromAttributes(paramAttributeSet, paramInt);
    mTextHelper = new AppCompatTextHelper(this);
    mTextHelper.loadFromAttributes(paramAttributeSet, paramInt);
    mTextHelper.applyCompoundDrawablesTints();
    mTextClassifierHelper = new AppCompatTextClassifierHelper(this);
  }
  
  private void consumeTextFutureAndSetBlocking()
  {
    if (mPrecomputedTextFuture != null)
    {
      Object localObject = mPrecomputedTextFuture;
      mPrecomputedTextFuture = null;
      try
      {
        localObject = ((Future)localObject).get();
        localObject = (PrecomputedTextCompat)localObject;
        TextViewCompat.setPrecomputedText(this, (PrecomputedTextCompat)localObject);
        return;
      }
      catch (InterruptedException localInterruptedException) {}catch (ExecutionException localExecutionException) {}
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.applySupportBackgroundTint();
    }
    if (mTextHelper != null) {
      mTextHelper.applyCompoundDrawablesTints();
    }
  }
  
  public int getAutoSizeMaxTextSize()
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
      return super.getAutoSizeMaxTextSize();
    }
    if (mTextHelper != null) {
      return mTextHelper.getAutoSizeMaxTextSize();
    }
    return -1;
  }
  
  public int getAutoSizeMinTextSize()
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
      return super.getAutoSizeMinTextSize();
    }
    if (mTextHelper != null) {
      return mTextHelper.getAutoSizeMinTextSize();
    }
    return -1;
  }
  
  public int getAutoSizeStepGranularity()
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
      return super.getAutoSizeStepGranularity();
    }
    if (mTextHelper != null) {
      return mTextHelper.getAutoSizeStepGranularity();
    }
    return -1;
  }
  
  public int[] getAutoSizeTextAvailableSizes()
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
      return super.getAutoSizeTextAvailableSizes();
    }
    if (mTextHelper != null) {
      return mTextHelper.getAutoSizeTextAvailableSizes();
    }
    return new int[0];
  }
  
  public int getAutoSizeTextType()
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE)
    {
      if (super.getAutoSizeTextType() == 1) {
        return 1;
      }
    }
    else if (mTextHelper != null) {
      return mTextHelper.getAutoSizeTextType();
    }
    return 0;
  }
  
  public int getFirstBaselineToTopHeight()
  {
    return TextViewCompat.getFirstBaselineToTopHeight(this);
  }
  
  public int getLastBaselineToBottomHeight()
  {
    return TextViewCompat.getLastBaselineToBottomHeight(this);
  }
  
  public ColorStateList getSupportBackgroundTintList()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintList();
    }
    return null;
  }
  
  public PorterDuff.Mode getSupportBackgroundTintMode()
  {
    if (mBackgroundTintHelper != null) {
      return mBackgroundTintHelper.getSupportBackgroundTintMode();
    }
    return null;
  }
  
  public ColorStateList getSupportCompoundDrawablesTintList()
  {
    return mTextHelper.getCompoundDrawableTintList();
  }
  
  public PorterDuff.Mode getSupportCompoundDrawablesTintMode()
  {
    return mTextHelper.getCompoundDrawableTintMode();
  }
  
  public CharSequence getText()
  {
    consumeTextFutureAndSetBlocking();
    return super.getText();
  }
  
  public TextClassifier getTextClassifier()
  {
    if ((Build.VERSION.SDK_INT < 28) && (mTextClassifierHelper != null)) {
      return mTextClassifierHelper.getTextClassifier();
    }
    return super.getTextClassifier();
  }
  
  public PrecomputedTextCompat.Params getTextMetricsParamsCompat()
  {
    return TextViewCompat.getTextMetricsParams(this);
  }
  
  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
  {
    return AppCompatHintHelper.onCreateInputConnection(super.onCreateInputConnection(paramEditorInfo), paramEditorInfo, this);
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (mTextHelper != null) {
      mTextHelper.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    consumeTextFutureAndSetBlocking();
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
  {
    super.onTextChanged(paramCharSequence, paramInt1, paramInt2, paramInt3);
    if ((mTextHelper != null) && (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) && (mTextHelper.isAutoSizeEnabled())) {
      mTextHelper.autoSizeText();
    }
  }
  
  public void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE)
    {
      super.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    if (mTextHelper != null) {
      mTextHelper.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public void setAutoSizeTextTypeUniformWithPresetSizes(int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE)
    {
      super.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
      return;
    }
    if (mTextHelper != null) {
      mTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
    }
  }
  
  public void setAutoSizeTextTypeWithDefaults(int paramInt)
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE)
    {
      super.setAutoSizeTextTypeWithDefaults(paramInt);
      return;
    }
    if (mTextHelper != null) {
      mTextHelper.setAutoSizeTextTypeWithDefaults(paramInt);
    }
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    super.setBackgroundDrawable(paramDrawable);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundDrawable(paramDrawable);
    }
  }
  
  public void setBackgroundResource(int paramInt)
  {
    super.setBackgroundResource(paramInt);
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.onSetBackgroundResource(paramInt);
    }
  }
  
  public void setCompoundDrawables(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    super.setCompoundDrawables(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCompoundDrawablesRelative(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    super.setCompoundDrawablesRelative(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCompoundDrawablesRelativeWithIntrinsicBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Context localContext = getContext();
    Drawable localDrawable4 = null;
    Drawable localDrawable1;
    if (paramInt1 != 0) {
      localDrawable1 = AppCompatResources.getDrawable(localContext, paramInt1);
    } else {
      localDrawable1 = null;
    }
    Drawable localDrawable2;
    if (paramInt2 != 0) {
      localDrawable2 = AppCompatResources.getDrawable(localContext, paramInt2);
    } else {
      localDrawable2 = null;
    }
    Drawable localDrawable3;
    if (paramInt3 != 0) {
      localDrawable3 = AppCompatResources.getDrawable(localContext, paramInt3);
    } else {
      localDrawable3 = null;
    }
    if (paramInt4 != 0) {
      localDrawable4 = AppCompatResources.getDrawable(localContext, paramInt4);
    }
    setCompoundDrawablesRelativeWithIntrinsicBounds(localDrawable1, localDrawable2, localDrawable3, localDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    super.setCompoundDrawablesRelativeWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCompoundDrawablesWithIntrinsicBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Context localContext = getContext();
    Drawable localDrawable4 = null;
    Drawable localDrawable1;
    if (paramInt1 != 0) {
      localDrawable1 = AppCompatResources.getDrawable(localContext, paramInt1);
    } else {
      localDrawable1 = null;
    }
    Drawable localDrawable2;
    if (paramInt2 != 0) {
      localDrawable2 = AppCompatResources.getDrawable(localContext, paramInt2);
    } else {
      localDrawable2 = null;
    }
    Drawable localDrawable3;
    if (paramInt3 != 0) {
      localDrawable3 = AppCompatResources.getDrawable(localContext, paramInt3);
    } else {
      localDrawable3 = null;
    }
    if (paramInt4 != 0) {
      localDrawable4 = AppCompatResources.getDrawable(localContext, paramInt4);
    }
    setCompoundDrawablesWithIntrinsicBounds(localDrawable1, localDrawable2, localDrawable3, localDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCompoundDrawablesWithIntrinsicBounds(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    super.setCompoundDrawablesWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    if (mTextHelper != null) {
      mTextHelper.onSetCompoundDrawables();
    }
  }
  
  public void setCustomSelectionActionModeCallback(ActionMode.Callback paramCallback)
  {
    super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, paramCallback));
  }
  
  public void setFirstBaselineToTopHeight(int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 28)
    {
      super.setFirstBaselineToTopHeight(paramInt);
      return;
    }
    TextViewCompat.setFirstBaselineToTopHeight(this, paramInt);
  }
  
  public void setLastBaselineToBottomHeight(int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 28)
    {
      super.setLastBaselineToBottomHeight(paramInt);
      return;
    }
    TextViewCompat.setLastBaselineToBottomHeight(this, paramInt);
  }
  
  public void setLineHeight(int paramInt)
  {
    TextViewCompat.setLineHeight(this, paramInt);
  }
  
  public void setPrecomputedText(PrecomputedTextCompat paramPrecomputedTextCompat)
  {
    TextViewCompat.setPrecomputedText(this, paramPrecomputedTextCompat);
  }
  
  public void setSupportBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintList(paramColorStateList);
    }
  }
  
  public void setSupportBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (mBackgroundTintHelper != null) {
      mBackgroundTintHelper.setSupportBackgroundTintMode(paramMode);
    }
  }
  
  public void setSupportCompoundDrawablesTintList(ColorStateList paramColorStateList)
  {
    mTextHelper.setCompoundDrawableTintList(paramColorStateList);
    mTextHelper.applyCompoundDrawablesTints();
  }
  
  public void setSupportCompoundDrawablesTintMode(PorterDuff.Mode paramMode)
  {
    mTextHelper.setCompoundDrawableTintMode(paramMode);
    mTextHelper.applyCompoundDrawablesTints();
  }
  
  public void setTextAppearance(Context paramContext, int paramInt)
  {
    super.setTextAppearance(paramContext, paramInt);
    if (mTextHelper != null) {
      mTextHelper.onSetTextAppearance(paramContext, paramInt);
    }
  }
  
  public void setTextClassifier(TextClassifier paramTextClassifier)
  {
    if ((Build.VERSION.SDK_INT < 28) && (mTextClassifierHelper != null))
    {
      mTextClassifierHelper.setTextClassifier(paramTextClassifier);
      return;
    }
    super.setTextClassifier(paramTextClassifier);
  }
  
  public void setTextFuture(Future paramFuture)
  {
    mPrecomputedTextFuture = paramFuture;
    if (paramFuture != null) {
      requestLayout();
    }
  }
  
  public void setTextMetricsParamsCompat(PrecomputedTextCompat.Params paramParams)
  {
    TextViewCompat.setTextMetricsParams(this, paramParams);
  }
  
  public void setTextSize(int paramInt, float paramFloat)
  {
    if (AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE)
    {
      super.setTextSize(paramInt, paramFloat);
      return;
    }
    if (mTextHelper != null) {
      mTextHelper.setTextSize(paramInt, paramFloat);
    }
  }
  
  public void setTypeface(Typeface paramTypeface, int paramInt)
  {
    Typeface localTypeface;
    if ((paramTypeface != null) && (paramInt > 0)) {
      localTypeface = TypefaceCompat.create(getContext(), paramTypeface, paramInt);
    } else {
      localTypeface = null;
    }
    if (localTypeface != null) {
      paramTypeface = localTypeface;
    }
    super.setTypeface(paramTypeface, paramInt);
  }
}
