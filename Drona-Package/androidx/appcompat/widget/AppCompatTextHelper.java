package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources.NotFoundException;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.LocaleList;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.R.styleable;
import androidx.core.content.delay.ResourcesCompat.FontCallback;
import androidx.core.widget.AutoSizeableTextView;
import androidx.core.widget.TextViewCompat;
import java.lang.ref.WeakReference;
import java.util.Locale;

class AppCompatTextHelper
{
  private static final int MONOSPACE = 3;
  private static final int SANS = 1;
  private static final int SERIF = 2;
  private static final int TEXT_FONT_WEIGHT_UNSPECIFIED = -1;
  private boolean mAsyncFontPending;
  @NonNull
  private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper;
  private TintInfo mDrawableBottomTint;
  private TintInfo mDrawableEndTint;
  private TintInfo mDrawableLeftTint;
  private TintInfo mDrawableRightTint;
  private TintInfo mDrawableStartTint;
  private TintInfo mDrawableTint;
  private TintInfo mDrawableTopTint;
  private Typeface mFontTypeface;
  private int mFontWeight = -1;
  private int mStyle = 0;
  private final TextView mView;
  
  AppCompatTextHelper(TextView paramTextView)
  {
    mView = paramTextView;
    mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(mView);
  }
  
  private void applyCompoundDrawableTint(Drawable paramDrawable, TintInfo paramTintInfo)
  {
    if ((paramDrawable != null) && (paramTintInfo != null)) {
      AppCompatDrawableManager.tintDrawable(paramDrawable, paramTintInfo, mView.getDrawableState());
    }
  }
  
  private static TintInfo createTintInfo(Context paramContext, AppCompatDrawableManager paramAppCompatDrawableManager, int paramInt)
  {
    paramContext = paramAppCompatDrawableManager.getTintList(paramContext, paramInt);
    if (paramContext != null)
    {
      paramAppCompatDrawableManager = new TintInfo();
      mHasTintList = true;
      mTintList = paramContext;
      return paramAppCompatDrawableManager;
    }
    return null;
  }
  
  private void setCompoundDrawables(Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4, Drawable paramDrawable5, Drawable paramDrawable6)
  {
    if ((Build.VERSION.SDK_INT >= 17) && ((paramDrawable5 != null) || (paramDrawable6 != null)))
    {
      paramDrawable3 = mView.getCompoundDrawablesRelative();
      paramDrawable1 = mView;
      if (paramDrawable5 == null) {
        paramDrawable5 = paramDrawable3[0];
      }
      if (paramDrawable2 == null) {
        paramDrawable2 = paramDrawable3[1];
      }
      if (paramDrawable6 == null) {
        paramDrawable6 = paramDrawable3[2];
      }
      if (paramDrawable4 == null) {
        paramDrawable4 = paramDrawable3[3];
      }
      paramDrawable1.setCompoundDrawablesRelativeWithIntrinsicBounds(paramDrawable5, paramDrawable2, paramDrawable6, paramDrawable4);
      return;
    }
    if ((paramDrawable1 != null) || (paramDrawable2 != null) || (paramDrawable3 != null) || (paramDrawable4 != null))
    {
      if (Build.VERSION.SDK_INT >= 17)
      {
        paramDrawable5 = mView.getCompoundDrawablesRelative();
        if ((paramDrawable5[0] != null) || (paramDrawable5[2] != null))
        {
          paramDrawable1 = mView;
          paramDrawable3 = paramDrawable5[0];
          if (paramDrawable2 == null) {
            paramDrawable2 = paramDrawable5[1];
          }
          paramDrawable6 = paramDrawable5[2];
          if (paramDrawable4 == null) {
            paramDrawable4 = paramDrawable5[3];
          }
          paramDrawable1.setCompoundDrawablesRelativeWithIntrinsicBounds(paramDrawable3, paramDrawable2, paramDrawable6, paramDrawable4);
          return;
        }
      }
      paramDrawable6 = mView.getCompoundDrawables();
      paramDrawable5 = mView;
      if (paramDrawable1 == null) {
        paramDrawable1 = paramDrawable6[0];
      }
      if (paramDrawable2 == null) {
        paramDrawable2 = paramDrawable6[1];
      }
      if (paramDrawable3 == null) {
        paramDrawable3 = paramDrawable6[2];
      }
      if (paramDrawable4 == null) {
        paramDrawable4 = paramDrawable6[3];
      }
      paramDrawable5.setCompoundDrawablesWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
    }
  }
  
  private void setCompoundTints()
  {
    mDrawableLeftTint = mDrawableTint;
    mDrawableTopTint = mDrawableTint;
    mDrawableRightTint = mDrawableTint;
    mDrawableBottomTint = mDrawableTint;
    mDrawableStartTint = mDrawableTint;
    mDrawableEndTint = mDrawableTint;
  }
  
  private void setTextSizeInternal(int paramInt, float paramFloat)
  {
    mAutoSizeTextHelper.setTextSizeInternal(paramInt, paramFloat);
  }
  
  private void updateTypefaceAndStyle(Context paramContext, TintTypedArray paramTintTypedArray)
  {
    mStyle = paramTintTypedArray.getInt(R.styleable.TextAppearance_android_textStyle, mStyle);
    int i = Build.VERSION.SDK_INT;
    boolean bool2 = false;
    if (i >= 28)
    {
      mFontWeight = paramTintTypedArray.getInt(R.styleable.TextAppearance_android_textFontWeight, -1);
      if (mFontWeight != -1) {
        mStyle = (mStyle & 0x2 | 0x0);
      }
    }
    if ((!paramTintTypedArray.hasValue(R.styleable.TextAppearance_android_fontFamily)) && (!paramTintTypedArray.hasValue(R.styleable.TextAppearance_fontFamily)))
    {
      if (paramTintTypedArray.hasValue(R.styleable.TextAppearance_android_typeface))
      {
        mAsyncFontPending = false;
        switch (paramTintTypedArray.getInt(R.styleable.TextAppearance_android_typeface, 1))
        {
        default: 
          return;
        case 3: 
          mFontTypeface = Typeface.MONOSPACE;
          return;
        case 2: 
          mFontTypeface = Typeface.SERIF;
          return;
        }
        mFontTypeface = Typeface.SANS_SERIF;
      }
    }
    else
    {
      mFontTypeface = null;
      if (paramTintTypedArray.hasValue(R.styleable.TextAppearance_fontFamily)) {
        i = R.styleable.TextAppearance_fontFamily;
      } else {
        i = R.styleable.TextAppearance_android_fontFamily;
      }
      int j = mFontWeight;
      int k = mStyle;
      if (!paramContext.isRestricted())
      {
        paramContext = new ApplyTextViewCallback(this, j, k);
        j = mStyle;
      }
      try
      {
        paramContext = paramTintTypedArray.getFont(i, j, paramContext);
        if (paramContext != null) {
          if ((Build.VERSION.SDK_INT >= 28) && (mFontWeight != -1))
          {
            paramContext = Typeface.create(paramContext, 0);
            j = mFontWeight;
            if ((mStyle & 0x2) != 0) {
              bool1 = true;
            } else {
              bool1 = false;
            }
            paramContext = Typeface.create(paramContext, j, bool1);
            mFontTypeface = paramContext;
          }
          else
          {
            mFontTypeface = paramContext;
          }
        }
        if (mFontTypeface == null) {
          bool1 = true;
        } else {
          bool1 = false;
        }
        mAsyncFontPending = bool1;
      }
      catch (UnsupportedOperationException paramContext)
      {
        boolean bool1;
        for (;;) {}
      }
      catch (Resources.NotFoundException paramContext)
      {
        for (;;) {}
      }
      if (mFontTypeface == null)
      {
        paramContext = paramTintTypedArray.getString(i);
        if (paramContext != null)
        {
          if ((Build.VERSION.SDK_INT >= 28) && (mFontWeight != -1))
          {
            paramContext = Typeface.create(paramContext, 0);
            i = mFontWeight;
            bool1 = bool2;
            if ((mStyle & 0x2) != 0) {
              bool1 = true;
            }
            mFontTypeface = Typeface.create(paramContext, i, bool1);
            return;
          }
          mFontTypeface = Typeface.create(paramContext, mStyle);
          return;
        }
      }
    }
  }
  
  void applyCompoundDrawablesTints()
  {
    Drawable[] arrayOfDrawable;
    if ((mDrawableLeftTint != null) || (mDrawableTopTint != null) || (mDrawableRightTint != null) || (mDrawableBottomTint != null))
    {
      arrayOfDrawable = mView.getCompoundDrawables();
      applyCompoundDrawableTint(arrayOfDrawable[0], mDrawableLeftTint);
      applyCompoundDrawableTint(arrayOfDrawable[1], mDrawableTopTint);
      applyCompoundDrawableTint(arrayOfDrawable[2], mDrawableRightTint);
      applyCompoundDrawableTint(arrayOfDrawable[3], mDrawableBottomTint);
    }
    if ((Build.VERSION.SDK_INT >= 17) && ((mDrawableStartTint != null) || (mDrawableEndTint != null)))
    {
      arrayOfDrawable = mView.getCompoundDrawablesRelative();
      applyCompoundDrawableTint(arrayOfDrawable[0], mDrawableStartTint);
      applyCompoundDrawableTint(arrayOfDrawable[2], mDrawableEndTint);
    }
  }
  
  void autoSizeText()
  {
    mAutoSizeTextHelper.autoSizeText();
  }
  
  int getAutoSizeMaxTextSize()
  {
    return mAutoSizeTextHelper.getAutoSizeMaxTextSize();
  }
  
  int getAutoSizeMinTextSize()
  {
    return mAutoSizeTextHelper.getAutoSizeMinTextSize();
  }
  
  int getAutoSizeStepGranularity()
  {
    return mAutoSizeTextHelper.getAutoSizeStepGranularity();
  }
  
  int[] getAutoSizeTextAvailableSizes()
  {
    return mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
  }
  
  int getAutoSizeTextType()
  {
    return mAutoSizeTextHelper.getAutoSizeTextType();
  }
  
  ColorStateList getCompoundDrawableTintList()
  {
    if (mDrawableTint != null) {
      return mDrawableTint.mTintList;
    }
    return null;
  }
  
  PorterDuff.Mode getCompoundDrawableTintMode()
  {
    if (mDrawableTint != null) {
      return mDrawableTint.mTintMode;
    }
    return null;
  }
  
  boolean isAutoSizeEnabled()
  {
    return mAutoSizeTextHelper.isAutoSizeEnabled();
  }
  
  void loadFromAttributes(AttributeSet paramAttributeSet, int paramInt)
  {
    Context localContext = mView.getContext();
    AppCompatDrawableManager localAppCompatDrawableManager = AppCompatDrawableManager.get();
    Object localObject1 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.AppCompatTextHelper, paramInt, 0);
    int i = ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_textAppearance, -1);
    if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableLeft)) {
      mDrawableLeftTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableLeft, 0));
    }
    if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableTop)) {
      mDrawableTopTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableTop, 0));
    }
    if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableRight)) {
      mDrawableRightTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableRight, 0));
    }
    if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableBottom)) {
      mDrawableBottomTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableBottom, 0));
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableStart)) {
        mDrawableStartTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableStart, 0));
      }
      if (((TintTypedArray)localObject1).hasValue(R.styleable.AppCompatTextHelper_android_drawableEnd)) {
        mDrawableEndTint = createTintInfo(localContext, localAppCompatDrawableManager, ((TintTypedArray)localObject1).getResourceId(R.styleable.AppCompatTextHelper_android_drawableEnd, 0));
      }
    }
    ((TintTypedArray)localObject1).recycle();
    boolean bool3 = mView.getTransformationMethod() instanceof PasswordTransformationMethod;
    boolean bool1;
    Object localObject2;
    Object localObject5;
    Object localObject3;
    label423:
    Object localObject4;
    if (i != -1)
    {
      localObject7 = TintTypedArray.obtainStyledAttributes(localContext, i, R.styleable.TextAppearance);
      if ((!bool3) && (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_textAllCaps)))
      {
        bool1 = ((TintTypedArray)localObject7).getBoolean(R.styleable.TextAppearance_textAllCaps, false);
        i = 1;
      }
      else
      {
        i = 0;
        bool1 = false;
      }
      updateTypefaceAndStyle(localContext, (TintTypedArray)localObject7);
      if (Build.VERSION.SDK_INT < 23)
      {
        if (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_android_textColor)) {
          localObject1 = ((TintTypedArray)localObject7).getColorStateList(R.styleable.TextAppearance_android_textColor);
        } else {
          localObject1 = null;
        }
        if (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_android_textColorHint)) {
          localObject2 = ((TintTypedArray)localObject7).getColorStateList(R.styleable.TextAppearance_android_textColorHint);
        } else {
          localObject2 = null;
        }
        if (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_android_textColorLink))
        {
          localObject5 = ((TintTypedArray)localObject7).getColorStateList(R.styleable.TextAppearance_android_textColorLink);
          localObject3 = localObject1;
          localObject1 = localObject2;
          break label423;
        }
        localObject3 = localObject1;
        localObject1 = localObject2;
      }
      else
      {
        localObject1 = null;
        localObject3 = null;
      }
      localObject5 = null;
      if (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_textLocale)) {
        localObject4 = ((TintTypedArray)localObject7).getString(R.styleable.TextAppearance_textLocale);
      } else {
        localObject4 = null;
      }
      if ((Build.VERSION.SDK_INT >= 26) && (((TintTypedArray)localObject7).hasValue(R.styleable.TextAppearance_fontVariationSettings))) {
        localObject6 = ((TintTypedArray)localObject7).getString(R.styleable.TextAppearance_fontVariationSettings);
      } else {
        localObject6 = null;
      }
      ((TintTypedArray)localObject7).recycle();
      localObject2 = localObject3;
      localObject3 = localObject6;
    }
    else
    {
      localObject4 = null;
      localObject1 = null;
      localObject3 = null;
      localObject2 = null;
      i = 0;
      bool1 = false;
      localObject5 = null;
    }
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.TextAppearance, paramInt, 0);
    boolean bool2 = bool1;
    int j = i;
    if (!bool3)
    {
      bool2 = bool1;
      j = i;
      if (localTintTypedArray.hasValue(R.styleable.TextAppearance_textAllCaps))
      {
        bool2 = localTintTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false);
        j = 1;
      }
    }
    Object localObject6 = localObject2;
    Object localObject7 = localObject1;
    Object localObject8 = localObject5;
    if (Build.VERSION.SDK_INT < 23)
    {
      if (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor)) {
        localObject2 = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
      }
      if (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColorHint)) {
        localObject1 = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColorHint);
      }
      localObject6 = localObject2;
      localObject7 = localObject1;
      localObject8 = localObject5;
      if (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColorLink))
      {
        localObject8 = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColorLink);
        localObject7 = localObject1;
        localObject6 = localObject2;
      }
    }
    if (localTintTypedArray.hasValue(R.styleable.TextAppearance_textLocale)) {
      localObject4 = localTintTypedArray.getString(R.styleable.TextAppearance_textLocale);
    }
    localObject1 = localObject3;
    if (Build.VERSION.SDK_INT >= 26)
    {
      localObject1 = localObject3;
      if (localTintTypedArray.hasValue(R.styleable.TextAppearance_fontVariationSettings)) {
        localObject1 = localTintTypedArray.getString(R.styleable.TextAppearance_fontVariationSettings);
      }
    }
    if ((Build.VERSION.SDK_INT >= 28) && (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textSize)) && (localTintTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, -1) == 0)) {
      mView.setTextSize(0, 0.0F);
    }
    updateTypefaceAndStyle(localContext, localTintTypedArray);
    localTintTypedArray.recycle();
    if (localObject6 != null) {
      mView.setTextColor((ColorStateList)localObject6);
    }
    if (localObject7 != null) {
      mView.setHintTextColor((ColorStateList)localObject7);
    }
    if (localObject8 != null) {
      mView.setLinkTextColor((ColorStateList)localObject8);
    }
    if ((!bool3) && (j != 0)) {
      setAllCaps(bool2);
    }
    if (mFontTypeface != null) {
      if (mFontWeight == -1) {
        mView.setTypeface(mFontTypeface, mStyle);
      } else {
        mView.setTypeface(mFontTypeface);
      }
    }
    if (localObject1 != null) {
      mView.setFontVariationSettings((String)localObject1);
    }
    if (localObject4 != null) {
      if (Build.VERSION.SDK_INT >= 24)
      {
        mView.setTextLocales(LocaleList.forLanguageTags((String)localObject4));
      }
      else if (Build.VERSION.SDK_INT >= 21)
      {
        localObject1 = ((String)localObject4).substring(0, ((String)localObject4).indexOf(','));
        mView.setTextLocale(Locale.forLanguageTag((String)localObject1));
      }
    }
    mAutoSizeTextHelper.loadFromAttributes(paramAttributeSet, paramInt);
    if ((AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) && (mAutoSizeTextHelper.getAutoSizeTextType() != 0))
    {
      localObject1 = mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
      if (localObject1.length > 0) {
        if (mView.getAutoSizeStepGranularity() != -1.0F) {
          mView.setAutoSizeTextTypeUniformWithConfiguration(mAutoSizeTextHelper.getAutoSizeMinTextSize(), mAutoSizeTextHelper.getAutoSizeMaxTextSize(), mAutoSizeTextHelper.getAutoSizeStepGranularity(), 0);
        } else {
          mView.setAutoSizeTextTypeUniformWithPresetSizes((int[])localObject1, 0);
        }
      }
    }
    localObject6 = TintTypedArray.obtainStyledAttributes(localContext, paramAttributeSet, R.styleable.AppCompatTextView);
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableLeftCompat, -1);
    if (paramInt != -1) {
      paramAttributeSet = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      paramAttributeSet = null;
    }
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableTopCompat, -1);
    if (paramInt != -1) {
      localObject1 = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      localObject1 = null;
    }
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableRightCompat, -1);
    if (paramInt != -1) {
      localObject2 = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      localObject2 = null;
    }
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableBottomCompat, -1);
    if (paramInt != -1) {
      localObject3 = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      localObject3 = null;
    }
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableStartCompat, -1);
    if (paramInt != -1) {
      localObject4 = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      localObject4 = null;
    }
    paramInt = ((TintTypedArray)localObject6).getResourceId(R.styleable.AppCompatTextView_drawableEndCompat, -1);
    if (paramInt != -1) {
      localObject5 = localAppCompatDrawableManager.getDrawable(localContext, paramInt);
    } else {
      localObject5 = null;
    }
    setCompoundDrawables(paramAttributeSet, (Drawable)localObject1, (Drawable)localObject2, (Drawable)localObject3, (Drawable)localObject4, (Drawable)localObject5);
    if (((TintTypedArray)localObject6).hasValue(R.styleable.AppCompatTextView_drawableTint))
    {
      paramAttributeSet = ((TintTypedArray)localObject6).getColorStateList(R.styleable.AppCompatTextView_drawableTint);
      TextViewCompat.setCompoundDrawableTintList(mView, paramAttributeSet);
    }
    if (((TintTypedArray)localObject6).hasValue(R.styleable.AppCompatTextView_drawableTintMode))
    {
      paramAttributeSet = DrawableUtils.parseTintMode(((TintTypedArray)localObject6).getInt(R.styleable.AppCompatTextView_drawableTintMode, -1), null);
      TextViewCompat.setCompoundDrawableTintMode(mView, paramAttributeSet);
    }
    paramInt = ((TintTypedArray)localObject6).getDimensionPixelSize(R.styleable.AppCompatTextView_firstBaselineToTopHeight, -1);
    i = ((TintTypedArray)localObject6).getDimensionPixelSize(R.styleable.AppCompatTextView_lastBaselineToBottomHeight, -1);
    j = ((TintTypedArray)localObject6).getDimensionPixelSize(R.styleable.AppCompatTextView_lineHeight, -1);
    ((TintTypedArray)localObject6).recycle();
    if (paramInt != -1) {
      TextViewCompat.setFirstBaselineToTopHeight(mView, paramInt);
    }
    if (i != -1) {
      TextViewCompat.setLastBaselineToBottomHeight(mView, i);
    }
    if (j != -1) {
      TextViewCompat.setLineHeight(mView, j);
    }
  }
  
  void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
      autoSizeText();
    }
  }
  
  void onSetCompoundDrawables()
  {
    applyCompoundDrawablesTints();
  }
  
  void onSetTextAppearance(Context paramContext, int paramInt)
  {
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramInt, R.styleable.TextAppearance);
    if (localTintTypedArray.hasValue(R.styleable.TextAppearance_textAllCaps)) {
      setAllCaps(localTintTypedArray.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
    }
    if ((Build.VERSION.SDK_INT < 23) && (localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textColor)))
    {
      ColorStateList localColorStateList = localTintTypedArray.getColorStateList(R.styleable.TextAppearance_android_textColor);
      if (localColorStateList != null) {
        mView.setTextColor(localColorStateList);
      }
    }
    if ((localTintTypedArray.hasValue(R.styleable.TextAppearance_android_textSize)) && (localTintTypedArray.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, -1) == 0)) {
      mView.setTextSize(0, 0.0F);
    }
    updateTypefaceAndStyle(paramContext, localTintTypedArray);
    if ((Build.VERSION.SDK_INT >= 26) && (localTintTypedArray.hasValue(R.styleable.TextAppearance_fontVariationSettings)))
    {
      paramContext = localTintTypedArray.getString(R.styleable.TextAppearance_fontVariationSettings);
      if (paramContext != null) {
        mView.setFontVariationSettings(paramContext);
      }
    }
    localTintTypedArray.recycle();
    if (mFontTypeface != null) {
      mView.setTypeface(mFontTypeface, mStyle);
    }
  }
  
  public void runOnUiThread(Runnable paramRunnable)
  {
    mView.post(paramRunnable);
  }
  
  void setAllCaps(boolean paramBoolean)
  {
    mView.setAllCaps(paramBoolean);
  }
  
  void setAutoSizeTextTypeUniformWithConfiguration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  void setAutoSizeTextTypeUniformWithPresetSizes(int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
  }
  
  void setAutoSizeTextTypeWithDefaults(int paramInt)
  {
    mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(paramInt);
  }
  
  void setCompoundDrawableTintList(ColorStateList paramColorStateList)
  {
    if (mDrawableTint == null) {
      mDrawableTint = new TintInfo();
    }
    mDrawableTint.mTintList = paramColorStateList;
    TintInfo localTintInfo = mDrawableTint;
    boolean bool;
    if (paramColorStateList != null) {
      bool = true;
    } else {
      bool = false;
    }
    mHasTintList = bool;
    setCompoundTints();
  }
  
  void setCompoundDrawableTintMode(PorterDuff.Mode paramMode)
  {
    if (mDrawableTint == null) {
      mDrawableTint = new TintInfo();
    }
    mDrawableTint.mTintMode = paramMode;
    TintInfo localTintInfo = mDrawableTint;
    boolean bool;
    if (paramMode != null) {
      bool = true;
    } else {
      bool = false;
    }
    mHasTintMode = bool;
    setCompoundTints();
  }
  
  void setTextSize(int paramInt, float paramFloat)
  {
    if ((!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) && (!isAutoSizeEnabled())) {
      setTextSizeInternal(paramInt, paramFloat);
    }
  }
  
  public void setTypefaceByCallback(Typeface paramTypeface)
  {
    if (mAsyncFontPending)
    {
      mView.setTypeface(paramTypeface);
      mFontTypeface = paramTypeface;
    }
  }
  
  private static class ApplyTextViewCallback
    extends ResourcesCompat.FontCallback
  {
    private final int mFontWeight;
    private final WeakReference<AppCompatTextHelper> mParent;
    private final int mStyle;
    
    ApplyTextViewCallback(AppCompatTextHelper paramAppCompatTextHelper, int paramInt1, int paramInt2)
    {
      mParent = new WeakReference(paramAppCompatTextHelper);
      mFontWeight = paramInt1;
      mStyle = paramInt2;
    }
    
    public void onFontRetrievalFailed(int paramInt) {}
    
    public void onFontRetrieved(Typeface paramTypeface)
    {
      AppCompatTextHelper localAppCompatTextHelper = (AppCompatTextHelper)mParent.get();
      if (localAppCompatTextHelper == null) {
        return;
      }
      Typeface localTypeface = paramTypeface;
      if (Build.VERSION.SDK_INT >= 28)
      {
        localTypeface = paramTypeface;
        if (mFontWeight != -1)
        {
          int i = mFontWeight;
          boolean bool;
          if ((mStyle & 0x2) != 0) {
            bool = true;
          } else {
            bool = false;
          }
          localTypeface = Typeface.create(paramTypeface, i, bool);
        }
      }
      localAppCompatTextHelper.runOnUiThread(new TypefaceApplyCallback(mParent, localTypeface));
    }
    
    private class TypefaceApplyCallback
      implements Runnable
    {
      private final WeakReference<AppCompatTextHelper> mParent;
      private final Typeface mTypeface;
      
      TypefaceApplyCallback(WeakReference paramWeakReference, Typeface paramTypeface)
      {
        mParent = paramWeakReference;
        mTypeface = paramTypeface;
      }
      
      public void run()
      {
        AppCompatTextHelper localAppCompatTextHelper = (AppCompatTextHelper)mParent.get();
        if (localAppCompatTextHelper == null) {
          return;
        }
        localAppCompatTextHelper.setTypefaceByCallback(mTypeface);
      }
    }
  }
}
