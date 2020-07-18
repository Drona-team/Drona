package androidx.core.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.core.text.PrecomputedTextCompat;
import androidx.core.text.PrecomputedTextCompat.Params;
import androidx.core.text.PrecomputedTextCompat.Params.Builder;
import androidx.core.util.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class TextViewCompat
{
  public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
  public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
  private static final int LINES = 1;
  private static final String LOG_TAG = "TextViewCompat";
  private static Field sMaxModeField;
  private static boolean sMaxModeFieldFetched;
  private static Field sMaximumField;
  private static boolean sMaximumFieldFetched;
  private static Field sMinModeField;
  private static boolean sMinModeFieldFetched;
  private static Field sMinimumField;
  private static boolean sMinimumFieldFetched;
  
  private TextViewCompat() {}
  
  public static int getAutoSizeMaxTextSize(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 27) {
      return paramTextView.getAutoSizeMaxTextSize();
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      return ((AutoSizeableTextView)paramTextView).getAutoSizeMaxTextSize();
    }
    return -1;
  }
  
  public static int getAutoSizeMinTextSize(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 27) {
      return paramTextView.getAutoSizeMinTextSize();
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      return ((AutoSizeableTextView)paramTextView).getAutoSizeMinTextSize();
    }
    return -1;
  }
  
  public static int getAutoSizeStepGranularity(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 27) {
      return paramTextView.getAutoSizeStepGranularity();
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      return ((AutoSizeableTextView)paramTextView).getAutoSizeStepGranularity();
    }
    return -1;
  }
  
  public static int[] getAutoSizeTextAvailableSizes(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 27) {
      return paramTextView.getAutoSizeTextAvailableSizes();
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      return ((AutoSizeableTextView)paramTextView).getAutoSizeTextAvailableSizes();
    }
    return new int[0];
  }
  
  public static int getAutoSizeTextType(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 27) {
      return paramTextView.getAutoSizeTextType();
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      return ((AutoSizeableTextView)paramTextView).getAutoSizeTextType();
    }
    return 0;
  }
  
  public static ColorStateList getCompoundDrawableTintList(TextView paramTextView)
  {
    Preconditions.checkNotNull(paramTextView);
    if (Build.VERSION.SDK_INT >= 23) {
      return paramTextView.getCompoundDrawableTintList();
    }
    if ((paramTextView instanceof TintableCompoundDrawablesView)) {
      return ((TintableCompoundDrawablesView)paramTextView).getSupportCompoundDrawablesTintList();
    }
    return null;
  }
  
  public static PorterDuff.Mode getCompoundDrawableTintMode(TextView paramTextView)
  {
    Preconditions.checkNotNull(paramTextView);
    if (Build.VERSION.SDK_INT >= 23) {
      return paramTextView.getCompoundDrawableTintMode();
    }
    if ((paramTextView instanceof TintableCompoundDrawablesView)) {
      return ((TintableCompoundDrawablesView)paramTextView).getSupportCompoundDrawablesTintMode();
    }
    return null;
  }
  
  public static Drawable[] getCompoundDrawablesRelative(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      return paramTextView.getCompoundDrawablesRelative();
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      int j = paramTextView.getLayoutDirection();
      int i = 1;
      if (j != 1) {
        i = 0;
      }
      Drawable[] arrayOfDrawable = paramTextView.getCompoundDrawables();
      paramTextView = arrayOfDrawable;
      if (i != 0)
      {
        paramTextView = arrayOfDrawable[2];
        Drawable localDrawable = arrayOfDrawable[0];
        arrayOfDrawable[0] = paramTextView;
        arrayOfDrawable[2] = localDrawable;
        return arrayOfDrawable;
      }
    }
    else
    {
      paramTextView = paramTextView.getCompoundDrawables();
    }
    return paramTextView;
  }
  
  public static int getFirstBaselineToTopHeight(TextView paramTextView)
  {
    return paramTextView.getPaddingTop() - getPaintgetFontMetricsInttop;
  }
  
  public static int getLastBaselineToBottomHeight(TextView paramTextView)
  {
    return paramTextView.getPaddingBottom() + getPaintgetFontMetricsIntbottom;
  }
  
  public static int getMaxLines(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramTextView.getMaxLines();
    }
    if (!sMaxModeFieldFetched)
    {
      sMaxModeField = retrieveField("mMaxMode");
      sMaxModeFieldFetched = true;
    }
    if ((sMaxModeField != null) && (retrieveIntFromField(sMaxModeField, paramTextView) == 1))
    {
      if (!sMaximumFieldFetched)
      {
        sMaximumField = retrieveField("mMaximum");
        sMaximumFieldFetched = true;
      }
      if (sMaximumField != null) {
        return retrieveIntFromField(sMaximumField, paramTextView);
      }
    }
    return -1;
  }
  
  public static int getMinLines(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 16) {
      return paramTextView.getMinLines();
    }
    if (!sMinModeFieldFetched)
    {
      sMinModeField = retrieveField("mMinMode");
      sMinModeFieldFetched = true;
    }
    if ((sMinModeField != null) && (retrieveIntFromField(sMinModeField, paramTextView) == 1))
    {
      if (!sMinimumFieldFetched)
      {
        sMinimumField = retrieveField("mMinimum");
        sMinimumFieldFetched = true;
      }
      if (sMinimumField != null) {
        return retrieveIntFromField(sMinimumField, paramTextView);
      }
    }
    return -1;
  }
  
  private static int getTextDirection(TextDirectionHeuristic paramTextDirectionHeuristic)
  {
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
      return 1;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
      return 1;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.ANYRTL_LTR) {
      return 2;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.LTR) {
      return 3;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.RTL) {
      return 4;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.LOCALE) {
      return 5;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
      return 6;
    }
    if (paramTextDirectionHeuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL) {
      return 7;
    }
    return 1;
  }
  
  private static TextDirectionHeuristic getTextDirectionHeuristic(TextView paramTextView)
  {
    if ((paramTextView.getTransformationMethod() instanceof PasswordTransformationMethod)) {
      return TextDirectionHeuristics.LTR;
    }
    int j = Build.VERSION.SDK_INT;
    int i = 0;
    if ((j >= 28) && ((paramTextView.getInputType() & 0xF) == 3))
    {
      i = Character.getDirectionality(android.icu.text.DecimalFormatSymbols.getInstance(paramTextView.getTextLocale()).getDigitStrings()[0].codePointAt(0));
      if ((i != 1) && (i != 2)) {
        return TextDirectionHeuristics.LTR;
      }
      return TextDirectionHeuristics.RTL;
    }
    if (paramTextView.getLayoutDirection() == 1) {
      i = 1;
    }
    switch (paramTextView.getTextDirection())
    {
    default: 
      if (i != 0) {
        return TextDirectionHeuristics.FIRSTSTRONG_RTL;
      }
      break;
    case 7: 
      return TextDirectionHeuristics.FIRSTSTRONG_RTL;
    case 6: 
      return TextDirectionHeuristics.FIRSTSTRONG_LTR;
    case 5: 
      return TextDirectionHeuristics.LOCALE;
    case 4: 
      return TextDirectionHeuristics.RTL;
    case 3: 
      return TextDirectionHeuristics.LTR;
    case 2: 
      return TextDirectionHeuristics.ANYRTL_LTR;
    }
    return TextDirectionHeuristics.FIRSTSTRONG_LTR;
  }
  
  public static PrecomputedTextCompat.Params getTextMetricsParams(TextView paramTextView)
  {
    if (Build.VERSION.SDK_INT >= 28) {
      return new PrecomputedTextCompat.Params(paramTextView.getTextMetricsParams());
    }
    PrecomputedTextCompat.Params.Builder localBuilder = new PrecomputedTextCompat.Params.Builder(new TextPaint(paramTextView.getPaint()));
    if (Build.VERSION.SDK_INT >= 23)
    {
      localBuilder.setBreakStrategy(paramTextView.getBreakStrategy());
      localBuilder.setHyphenationFrequency(paramTextView.getHyphenationFrequency());
    }
    if (Build.VERSION.SDK_INT >= 18) {
      localBuilder.setTextDirection(getTextDirectionHeuristic(paramTextView));
    }
    return localBuilder.build();
  }
  
  private static Field retrieveField(String paramString)
  {
    try
    {
      localObject2 = TextView.class.getDeclaredField(paramString);
      localObject1 = localObject2;
    }
    catch (NoSuchFieldException localNoSuchFieldException1)
    {
      Object localObject2;
      Object localObject1;
      label18:
      for (;;) {}
    }
    try
    {
      ((Field)localObject2).setAccessible(true);
      return localObject2;
    }
    catch (NoSuchFieldException localNoSuchFieldException2)
    {
      break label18;
    }
    localObject1 = null;
    localObject2 = new StringBuilder();
    ((StringBuilder)localObject2).append("Could not retrieve ");
    ((StringBuilder)localObject2).append(paramString);
    ((StringBuilder)localObject2).append(" field.");
    Log.e("TextViewCompat", ((StringBuilder)localObject2).toString());
    return localObject1;
  }
  
  private static int retrieveIntFromField(Field paramField, TextView paramTextView)
  {
    try
    {
      int i = paramField.getInt(paramTextView);
      return i;
    }
    catch (IllegalAccessException paramTextView)
    {
      for (;;) {}
    }
    paramTextView = new StringBuilder();
    paramTextView.append("Could not retrieve value of ");
    paramTextView.append(paramField.getName());
    paramTextView.append(" field.");
    Log.d("TextViewCompat", paramTextView.toString());
    return -1;
  }
  
  public static void setAutoSizeTextTypeUniformWithConfiguration(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IllegalArgumentException
  {
    if (Build.VERSION.SDK_INT >= 27)
    {
      paramTextView.setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeUniformWithConfiguration(paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }
  
  public static void setAutoSizeTextTypeUniformWithPresetSizes(TextView paramTextView, int[] paramArrayOfInt, int paramInt)
    throws IllegalArgumentException
  {
    if (Build.VERSION.SDK_INT >= 27)
    {
      paramTextView.setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
      return;
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeUniformWithPresetSizes(paramArrayOfInt, paramInt);
    }
  }
  
  public static void setAutoSizeTextTypeWithDefaults(TextView paramTextView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 27)
    {
      paramTextView.setAutoSizeTextTypeWithDefaults(paramInt);
      return;
    }
    if ((paramTextView instanceof AutoSizeableTextView)) {
      ((AutoSizeableTextView)paramTextView).setAutoSizeTextTypeWithDefaults(paramInt);
    }
  }
  
  public static void setCompoundDrawableTintList(TextView paramTextView, ColorStateList paramColorStateList)
  {
    Preconditions.checkNotNull(paramTextView);
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramTextView.setCompoundDrawableTintList(paramColorStateList);
      return;
    }
    if ((paramTextView instanceof TintableCompoundDrawablesView)) {
      ((TintableCompoundDrawablesView)paramTextView).setSupportCompoundDrawablesTintList(paramColorStateList);
    }
  }
  
  public static void setCompoundDrawableTintMode(TextView paramTextView, PorterDuff.Mode paramMode)
  {
    Preconditions.checkNotNull(paramTextView);
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramTextView.setCompoundDrawableTintMode(paramMode);
      return;
    }
    if ((paramTextView instanceof TintableCompoundDrawablesView)) {
      ((TintableCompoundDrawablesView)paramTextView).setSupportCompoundDrawablesTintMode(paramMode);
    }
  }
  
  public static void setCompoundDrawablesRelative(TextView paramTextView, Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    if (Build.VERSION.SDK_INT >= 18)
    {
      paramTextView.setCompoundDrawablesRelative(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      int j = paramTextView.getLayoutDirection();
      int i = 1;
      if (j != 1) {
        i = 0;
      }
      Drawable localDrawable;
      if (i != 0) {
        localDrawable = paramDrawable3;
      } else {
        localDrawable = paramDrawable1;
      }
      if (i == 0) {
        paramDrawable1 = paramDrawable3;
      }
      paramTextView.setCompoundDrawables(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
      return;
    }
    paramTextView.setCompoundDrawables(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView paramTextView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (Build.VERSION.SDK_INT >= 18)
    {
      paramTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      int j = paramTextView.getLayoutDirection();
      int i = 1;
      if (j != 1) {
        i = 0;
      }
      if (i != 0) {
        j = paramInt3;
      } else {
        j = paramInt1;
      }
      if (i == 0) {
        paramInt1 = paramInt3;
      }
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(j, paramInt2, paramInt1, paramInt4);
      return;
    }
    paramTextView.setCompoundDrawablesWithIntrinsicBounds(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static void setCompoundDrawablesRelativeWithIntrinsicBounds(TextView paramTextView, Drawable paramDrawable1, Drawable paramDrawable2, Drawable paramDrawable3, Drawable paramDrawable4)
  {
    if (Build.VERSION.SDK_INT >= 18)
    {
      paramTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
      return;
    }
    if (Build.VERSION.SDK_INT >= 17)
    {
      int j = paramTextView.getLayoutDirection();
      int i = 1;
      if (j != 1) {
        i = 0;
      }
      Drawable localDrawable;
      if (i != 0) {
        localDrawable = paramDrawable3;
      } else {
        localDrawable = paramDrawable1;
      }
      if (i == 0) {
        paramDrawable1 = paramDrawable3;
      }
      paramTextView.setCompoundDrawablesWithIntrinsicBounds(localDrawable, paramDrawable2, paramDrawable1, paramDrawable4);
      return;
    }
    paramTextView.setCompoundDrawablesWithIntrinsicBounds(paramDrawable1, paramDrawable2, paramDrawable3, paramDrawable4);
  }
  
  public static void setCustomSelectionActionModeCallback(TextView paramTextView, ActionMode.Callback paramCallback)
  {
    paramTextView.setCustomSelectionActionModeCallback(wrapCustomSelectionActionModeCallback(paramTextView, paramCallback));
  }
  
  public static void setFirstBaselineToTopHeight(TextView paramTextView, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt);
    if (Build.VERSION.SDK_INT >= 28)
    {
      paramTextView.setFirstBaselineToTopHeight(paramInt);
      return;
    }
    Paint.FontMetricsInt localFontMetricsInt = paramTextView.getPaint().getFontMetricsInt();
    int i;
    if ((Build.VERSION.SDK_INT >= 16) && (!paramTextView.getIncludeFontPadding())) {
      i = ascent;
    } else {
      i = top;
    }
    if (paramInt > Math.abs(i))
    {
      i = -i;
      paramTextView.setPadding(paramTextView.getPaddingLeft(), paramInt - i, paramTextView.getPaddingRight(), paramTextView.getPaddingBottom());
    }
  }
  
  public static void setLastBaselineToBottomHeight(TextView paramTextView, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt);
    Paint.FontMetricsInt localFontMetricsInt = paramTextView.getPaint().getFontMetricsInt();
    int i;
    if ((Build.VERSION.SDK_INT >= 16) && (!paramTextView.getIncludeFontPadding())) {
      i = descent;
    } else {
      i = bottom;
    }
    if (paramInt > Math.abs(i)) {
      paramTextView.setPadding(paramTextView.getPaddingLeft(), paramTextView.getPaddingTop(), paramTextView.getPaddingRight(), paramInt - i);
    }
  }
  
  public static void setLineHeight(TextView paramTextView, int paramInt)
  {
    Preconditions.checkArgumentNonnegative(paramInt);
    int i = paramTextView.getPaint().getFontMetricsInt(null);
    if (paramInt != i) {
      paramTextView.setLineSpacing(paramInt - i, 1.0F);
    }
  }
  
  public static void setPrecomputedText(TextView paramTextView, PrecomputedTextCompat paramPrecomputedTextCompat)
  {
    if (getTextMetricsParams(paramTextView).equalsWithoutTextDirection(paramPrecomputedTextCompat.getParams()))
    {
      paramTextView.setText(paramPrecomputedTextCompat);
      return;
    }
    throw new IllegalArgumentException("Given text can not be applied to TextView.");
  }
  
  public static void setTextAppearance(TextView paramTextView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 23)
    {
      paramTextView.setTextAppearance(paramInt);
      return;
    }
    paramTextView.setTextAppearance(paramTextView.getContext(), paramInt);
  }
  
  public static void setTextMetricsParams(TextView paramTextView, PrecomputedTextCompat.Params paramParams)
  {
    if (Build.VERSION.SDK_INT >= 18) {
      paramTextView.setTextDirection(getTextDirection(paramParams.getTextDirection()));
    }
    if (Build.VERSION.SDK_INT < 23)
    {
      float f = paramParams.getTextPaint().getTextScaleX();
      paramTextView.getPaint().set(paramParams.getTextPaint());
      if (f == paramTextView.getTextScaleX()) {
        paramTextView.setTextScaleX(f / 2.0F + 1.0F);
      }
      paramTextView.setTextScaleX(f);
      return;
    }
    paramTextView.getPaint().set(paramParams.getTextPaint());
    paramTextView.setBreakStrategy(paramParams.getBreakStrategy());
    paramTextView.setHyphenationFrequency(paramParams.getHyphenationFrequency());
  }
  
  public static ActionMode.Callback wrapCustomSelectionActionModeCallback(TextView paramTextView, ActionMode.Callback paramCallback)
  {
    if ((Build.VERSION.SDK_INT >= 26) && (Build.VERSION.SDK_INT <= 27))
    {
      if ((paramCallback instanceof OreoCallback)) {
        return paramCallback;
      }
      return new OreoCallback(paramCallback, paramTextView);
    }
    return paramCallback;
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static @interface AutoSizeTextType {}
  
  @RequiresApi(26)
  private static class OreoCallback
    implements ActionMode.Callback
  {
    private static final int MENU_ITEM_ORDER_PROCESS_TEXT_INTENT_ACTIONS_START = 100;
    private final ActionMode.Callback mCallback;
    private boolean mCanUseMenuBuilderReferences;
    private boolean mInitializedMenuBuilderReferences;
    private Class mMenuBuilderClass;
    private Method mMenuBuilderRemoveItemAtMethod;
    private final TextView mTextView;
    
    OreoCallback(ActionMode.Callback paramCallback, TextView paramTextView)
    {
      mCallback = paramCallback;
      mTextView = paramTextView;
      mInitializedMenuBuilderReferences = false;
    }
    
    private Intent createProcessTextIntent()
    {
      return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
    }
    
    private Intent createProcessTextIntentForResolveInfo(ResolveInfo paramResolveInfo, TextView paramTextView)
    {
      return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", isEditable(paramTextView) ^ true).setClassName(activityInfo.packageName, activityInfo.name);
    }
    
    private List getSupportedActivities(Context paramContext, PackageManager paramPackageManager)
    {
      ArrayList localArrayList = new ArrayList();
      if (!(paramContext instanceof Activity)) {
        return localArrayList;
      }
      paramPackageManager = paramPackageManager.queryIntentActivities(createProcessTextIntent(), 0).iterator();
      while (paramPackageManager.hasNext())
      {
        ResolveInfo localResolveInfo = (ResolveInfo)paramPackageManager.next();
        if (isSupportedActivity(localResolveInfo, paramContext)) {
          localArrayList.add(localResolveInfo);
        }
      }
      return localArrayList;
    }
    
    private boolean isEditable(TextView paramTextView)
    {
      return ((paramTextView instanceof Editable)) && (paramTextView.onCheckIsTextEditor()) && (paramTextView.isEnabled());
    }
    
    private boolean isSupportedActivity(ResolveInfo paramResolveInfo, Context paramContext)
    {
      if (paramContext.getPackageName().equals(activityInfo.packageName)) {
        return true;
      }
      if (!activityInfo.exported) {
        return false;
      }
      if (activityInfo.permission != null) {
        return paramContext.checkSelfPermission(activityInfo.permission) == 0;
      }
      return true;
    }
    
    private void recomputeProcessTextMenuItems(Menu paramMenu)
    {
      Object localObject2 = mTextView.getContext();
      PackageManager localPackageManager = ((Context)localObject2).getPackageManager();
      if (!mInitializedMenuBuilderReferences) {
        mInitializedMenuBuilderReferences = true;
      }
      try
      {
        localObject1 = Class.forName("com.android.internal.view.menu.MenuBuilder");
        mMenuBuilderClass = ((Class)localObject1);
        localObject1 = mMenuBuilderClass;
        localObject3 = Integer.TYPE;
        localObject1 = ((Class)localObject1).getDeclaredMethod("removeItemAt", new Class[] { localObject3 });
        mMenuBuilderRemoveItemAtMethod = ((Method)localObject1);
        mCanUseMenuBuilderReferences = true;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        for (;;)
        {
          try
          {
            Object localObject3;
            boolean bool = ((Class)localObject1).isInstance(paramMenu);
            if (bool)
            {
              localObject1 = mMenuBuilderRemoveItemAtMethod;
            }
            else
            {
              localObject1 = paramMenu.getClass();
              localObject3 = Integer.TYPE;
              localObject1 = ((Class)localObject1).getDeclaredMethod("removeItemAt", new Class[] { localObject3 });
            }
            int i = paramMenu.size();
            i -= 1;
            if (i >= 0)
            {
              localObject3 = paramMenu.getItem(i);
              Intent localIntent = ((MenuItem)localObject3).getIntent();
              if (localIntent != null)
              {
                bool = "android.intent.action.PROCESS_TEXT".equals(((MenuItem)localObject3).getIntent().getAction());
                if (bool) {
                  ((Method)localObject1).invoke(paramMenu, new Object[] { Integer.valueOf(i) });
                }
              }
              i -= 1;
              continue;
            }
            Object localObject1 = getSupportedActivities((Context)localObject2, localPackageManager);
            i = 0;
            if (i < ((List)localObject1).size())
            {
              localObject2 = (ResolveInfo)((List)localObject1).get(i);
              paramMenu.add(0, 0, i + 100, ((ResolveInfo)localObject2).loadLabel(localPackageManager)).setIntent(createProcessTextIntentForResolveInfo((ResolveInfo)localObject2, mTextView)).setShowAsAction(1);
              i += 1;
              continue;
            }
            return;
          }
          catch (NoSuchMethodException paramMenu)
          {
            return;
          }
          catch (IllegalAccessException paramMenu)
          {
            return;
          }
          catch (InvocationTargetException paramMenu) {}
          localClassNotFoundException = localClassNotFoundException;
        }
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;) {}
      }
      mMenuBuilderClass = null;
      mMenuBuilderRemoveItemAtMethod = null;
      mCanUseMenuBuilderReferences = false;
      if (mCanUseMenuBuilderReferences) {
        localObject1 = mMenuBuilderClass;
      }
    }
    
    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      return mCallback.onActionItemClicked(paramActionMode, paramMenuItem);
    }
    
    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return mCallback.onCreateActionMode(paramActionMode, paramMenu);
    }
    
    public void onDestroyActionMode(ActionMode paramActionMode)
    {
      mCallback.onDestroyActionMode(paramActionMode);
    }
    
    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      recomputeProcessTextMenuItems(paramMenu);
      return mCallback.onPrepareActionMode(paramActionMode, paramMenu);
    }
  }
}
