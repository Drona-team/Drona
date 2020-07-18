package androidx.appcompat.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.R.attr;
import androidx.appcompat.R.color;
import androidx.appcompat.R.drawable;
import androidx.appcompat.content.wiki.AppCompatResources;
import androidx.core.graphics.ColorUtils;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public final class AppCompatDrawableManager
{
  private static final boolean DEBUG = false;
  private static final PorterDuff.Mode DEFAULT_MODE = PorterDuff.Mode.SRC_IN;
  private static AppCompatDrawableManager INSTANCE;
  private static final String TAG = "AppCompatDrawableManag";
  private ResourceManagerInternal mResourceManager;
  
  public AppCompatDrawableManager() {}
  
  public static AppCompatDrawableManager get()
  {
    try
    {
      if (INSTANCE == null) {
        preload();
      }
      AppCompatDrawableManager localAppCompatDrawableManager = INSTANCE;
      return localAppCompatDrawableManager;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static PorterDuffColorFilter getPorterDuffColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    try
    {
      paramMode = ResourceManagerInternal.getPorterDuffColorFilter(paramInt, paramMode);
      return paramMode;
    }
    catch (Throwable paramMode)
    {
      throw paramMode;
    }
  }
  
  public static void preload()
  {
    try
    {
      if (INSTANCE == null)
      {
        INSTANCE = new AppCompatDrawableManager();
        INSTANCEmResourceManager = ResourceManagerInternal.get();
        INSTANCEmResourceManager.setHooks(new ResourceManagerInternal.ResourceManagerHooks()
        {
          private final int[] COLORFILTER_COLOR_BACKGROUND_MULTIPLY = { R.drawable.abc_popup_background_mtrl_mult, R.drawable.abc_cab_background_internal_bg, R.drawable.abc_menu_hardkey_panel_mtrl_mult };
          private final int[] COLORFILTER_COLOR_CONTROL_ACTIVATED = { R.drawable.abc_textfield_activated_mtrl_alpha, R.drawable.abc_textfield_search_activated_mtrl_alpha, R.drawable.abc_cab_background_top_mtrl_alpha, R.drawable.abc_text_cursor_material, R.drawable.abc_text_select_handle_left_mtrl_dark, R.drawable.abc_text_select_handle_middle_mtrl_dark, R.drawable.abc_text_select_handle_right_mtrl_dark, R.drawable.abc_text_select_handle_left_mtrl_light, R.drawable.abc_text_select_handle_middle_mtrl_light, R.drawable.abc_text_select_handle_right_mtrl_light };
          private final int[] COLORFILTER_TINT_COLOR_CONTROL_NORMAL = { R.drawable.abc_textfield_search_default_mtrl_alpha, R.drawable.abc_textfield_default_mtrl_alpha, R.drawable.abc_ab_share_pack_mtrl_alpha };
          private final int[] TINT_CHECKABLE_BUTTON_LIST = { R.drawable.abc_btn_check_material, R.drawable.abc_btn_radio_material, R.drawable.abc_btn_check_material_anim, R.drawable.abc_btn_radio_material_anim };
          private final int[] TINT_COLOR_CONTROL_NORMAL = { R.drawable.abc_ic_commit_search_api_mtrl_alpha, R.drawable.abc_seekbar_tick_mark_material, R.drawable.abc_ic_menu_share_mtrl_alpha, R.drawable.abc_ic_menu_copy_mtrl_am_alpha, R.drawable.abc_ic_menu_cut_mtrl_alpha, R.drawable.abc_ic_menu_selectall_mtrl_alpha, R.drawable.abc_ic_menu_paste_mtrl_am_alpha };
          private final int[] TINT_COLOR_CONTROL_STATE_LIST = { R.drawable.abc_tab_indicator_material, R.drawable.abc_textfield_search_material };
          
          private boolean arrayContains(int[] paramAnonymousArrayOfInt, int paramAnonymousInt)
          {
            int j = paramAnonymousArrayOfInt.length;
            int i = 0;
            while (i < j)
            {
              if (paramAnonymousArrayOfInt[i] == paramAnonymousInt) {
                return true;
              }
              i += 1;
            }
            return false;
          }
          
          private ColorStateList createBorderlessButtonColorStateList(Context paramAnonymousContext)
          {
            return createButtonColorStateList(paramAnonymousContext, 0);
          }
          
          private ColorStateList createButtonColorStateList(Context paramAnonymousContext, int paramAnonymousInt)
          {
            int k = ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlHighlight);
            int i = ThemeUtils.getDisabledThemeAttrColor(paramAnonymousContext, R.attr.colorButtonNormal);
            paramAnonymousContext = ThemeUtils.DISABLED_STATE_SET;
            int[] arrayOfInt1 = ThemeUtils.PRESSED_STATE_SET;
            int j = ColorUtils.compositeColors(k, paramAnonymousInt);
            int[] arrayOfInt2 = ThemeUtils.FOCUSED_STATE_SET;
            k = ColorUtils.compositeColors(k, paramAnonymousInt);
            return new ColorStateList(new int[][] { paramAnonymousContext, arrayOfInt1, arrayOfInt2, ThemeUtils.EMPTY_STATE_SET }, new int[] { i, j, k, paramAnonymousInt });
          }
          
          private ColorStateList createColoredButtonColorStateList(Context paramAnonymousContext)
          {
            return createButtonColorStateList(paramAnonymousContext, ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorAccent));
          }
          
          private ColorStateList createDefaultButtonColorStateList(Context paramAnonymousContext)
          {
            return createButtonColorStateList(paramAnonymousContext, ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorButtonNormal));
          }
          
          private ColorStateList createSwitchThumbColorStateList(Context paramAnonymousContext)
          {
            int[][] arrayOfInt = new int[3][];
            int[] arrayOfInt1 = new int[3];
            ColorStateList localColorStateList = ThemeUtils.getThemeAttrColorStateList(paramAnonymousContext, R.attr.colorSwitchThumbNormal);
            if ((localColorStateList != null) && (localColorStateList.isStateful()))
            {
              arrayOfInt[0] = ThemeUtils.DISABLED_STATE_SET;
              arrayOfInt1[0] = localColorStateList.getColorForState(arrayOfInt[0], 0);
              arrayOfInt[1] = ThemeUtils.CHECKED_STATE_SET;
              arrayOfInt1[1] = ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlActivated);
              arrayOfInt[2] = ThemeUtils.EMPTY_STATE_SET;
              arrayOfInt1[2] = localColorStateList.getDefaultColor();
            }
            else
            {
              arrayOfInt[0] = ThemeUtils.DISABLED_STATE_SET;
              arrayOfInt1[0] = ThemeUtils.getDisabledThemeAttrColor(paramAnonymousContext, R.attr.colorSwitchThumbNormal);
              arrayOfInt[1] = ThemeUtils.CHECKED_STATE_SET;
              arrayOfInt1[1] = ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlActivated);
              arrayOfInt[2] = ThemeUtils.EMPTY_STATE_SET;
              arrayOfInt1[2] = ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorSwitchThumbNormal);
            }
            return new ColorStateList(arrayOfInt, arrayOfInt1);
          }
          
          private void setPorterDuffColorFilter(Drawable paramAnonymousDrawable, int paramAnonymousInt, PorterDuff.Mode paramAnonymousMode)
          {
            Drawable localDrawable = paramAnonymousDrawable;
            if (DrawableUtils.canSafelyMutateDrawable(paramAnonymousDrawable)) {
              localDrawable = paramAnonymousDrawable.mutate();
            }
            paramAnonymousDrawable = paramAnonymousMode;
            if (paramAnonymousMode == null) {
              paramAnonymousDrawable = AppCompatDrawableManager.DEFAULT_MODE;
            }
            localDrawable.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(paramAnonymousInt, paramAnonymousDrawable));
          }
          
          public Drawable createDrawableFor(ResourceManagerInternal paramAnonymousResourceManagerInternal, Context paramAnonymousContext, int paramAnonymousInt)
          {
            if (paramAnonymousInt == R.drawable.abc_cab_background_top_material) {
              return new LayerDrawable(new Drawable[] { paramAnonymousResourceManagerInternal.getDrawable(paramAnonymousContext, R.drawable.abc_cab_background_internal_bg), paramAnonymousResourceManagerInternal.getDrawable(paramAnonymousContext, R.drawable.abc_cab_background_top_mtrl_alpha) });
            }
            return null;
          }
          
          public ColorStateList getTintListForDrawableRes(Context paramAnonymousContext, int paramAnonymousInt)
          {
            if (paramAnonymousInt == R.drawable.abc_edit_text_material) {
              return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_edittext);
            }
            if (paramAnonymousInt == R.drawable.abc_switch_track_mtrl_alpha) {
              return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_switch_track);
            }
            if (paramAnonymousInt == R.drawable.abc_switch_thumb_material) {
              return createSwitchThumbColorStateList(paramAnonymousContext);
            }
            if (paramAnonymousInt == R.drawable.abc_btn_default_mtrl_shape) {
              return createDefaultButtonColorStateList(paramAnonymousContext);
            }
            if (paramAnonymousInt == R.drawable.abc_btn_borderless_material) {
              return createBorderlessButtonColorStateList(paramAnonymousContext);
            }
            if (paramAnonymousInt == R.drawable.abc_btn_colored_material) {
              return createColoredButtonColorStateList(paramAnonymousContext);
            }
            if ((paramAnonymousInt != R.drawable.abc_spinner_mtrl_am_alpha) && (paramAnonymousInt != R.drawable.abc_spinner_textfield_background_material))
            {
              if (arrayContains(TINT_COLOR_CONTROL_NORMAL, paramAnonymousInt)) {
                return ThemeUtils.getThemeAttrColorStateList(paramAnonymousContext, R.attr.colorControlNormal);
              }
              if (arrayContains(TINT_COLOR_CONTROL_STATE_LIST, paramAnonymousInt)) {
                return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_default);
              }
              if (arrayContains(TINT_CHECKABLE_BUTTON_LIST, paramAnonymousInt)) {
                return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_btn_checkable);
              }
              if (paramAnonymousInt == R.drawable.abc_seekbar_thumb_material) {
                return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_seek_thumb);
              }
              return null;
            }
            return AppCompatResources.getColorStateList(paramAnonymousContext, R.color.abc_tint_spinner);
          }
          
          public PorterDuff.Mode getTintModeForDrawableRes(int paramAnonymousInt)
          {
            if (paramAnonymousInt == R.drawable.abc_switch_thumb_material) {
              return PorterDuff.Mode.MULTIPLY;
            }
            return null;
          }
          
          public boolean tintDrawable(Context paramAnonymousContext, int paramAnonymousInt, Drawable paramAnonymousDrawable)
          {
            if (paramAnonymousInt == R.drawable.abc_seekbar_track_material)
            {
              paramAnonymousDrawable = (LayerDrawable)paramAnonymousDrawable;
              setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908288), ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
              setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
              setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
              return true;
            }
            if ((paramAnonymousInt != R.drawable.abc_ratingbar_material) && (paramAnonymousInt != R.drawable.abc_ratingbar_indicator_material) && (paramAnonymousInt != R.drawable.abc_ratingbar_small_material)) {
              return false;
            }
            paramAnonymousDrawable = (LayerDrawable)paramAnonymousDrawable;
            setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908288), ThemeUtils.getDisabledThemeAttrColor(paramAnonymousContext, R.attr.colorControlNormal), AppCompatDrawableManager.DEFAULT_MODE);
            setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908303), ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
            setPorterDuffColorFilter(paramAnonymousDrawable.findDrawableByLayerId(16908301), ThemeUtils.getThemeAttrColor(paramAnonymousContext, R.attr.colorControlActivated), AppCompatDrawableManager.DEFAULT_MODE);
            return true;
          }
          
          public boolean tintDrawableUsingColorFilter(Context paramAnonymousContext, int paramAnonymousInt, Drawable paramAnonymousDrawable)
          {
            PorterDuff.Mode localMode = AppCompatDrawableManager.DEFAULT_MODE;
            boolean bool = arrayContains(COLORFILTER_TINT_COLOR_CONTROL_NORMAL, paramAnonymousInt);
            int i = 16842801;
            if (bool) {
              paramAnonymousInt = R.attr.colorControlNormal;
            }
            for (;;)
            {
              i = 1;
              j = -1;
              break label127;
              if (arrayContains(COLORFILTER_COLOR_CONTROL_ACTIVATED, paramAnonymousInt))
              {
                paramAnonymousInt = R.attr.colorControlActivated;
              }
              else if (arrayContains(COLORFILTER_COLOR_BACKGROUND_MULTIPLY, paramAnonymousInt))
              {
                localMode = PorterDuff.Mode.MULTIPLY;
                paramAnonymousInt = i;
              }
              else
              {
                if (paramAnonymousInt == R.drawable.abc_list_divider_mtrl_alpha)
                {
                  paramAnonymousInt = 16842800;
                  j = Math.round(40.8F);
                  i = 1;
                  break label127;
                }
                if (paramAnonymousInt != R.drawable.abc_dialog_material_background) {
                  break;
                }
                paramAnonymousInt = i;
              }
            }
            i = 0;
            int j = -1;
            paramAnonymousInt = 0;
            label127:
            if (i != 0)
            {
              Drawable localDrawable = paramAnonymousDrawable;
              if (DrawableUtils.canSafelyMutateDrawable(paramAnonymousDrawable)) {
                localDrawable = paramAnonymousDrawable.mutate();
              }
              localDrawable.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(ThemeUtils.getThemeAttrColor(paramAnonymousContext, paramAnonymousInt), localMode));
              if (j != -1)
              {
                localDrawable.setAlpha(j);
                return true;
              }
            }
            else
            {
              return false;
            }
            return true;
          }
        });
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  static void tintDrawable(Drawable paramDrawable, TintInfo paramTintInfo, int[] paramArrayOfInt)
  {
    ResourceManagerInternal.tintDrawable(paramDrawable, paramTintInfo, paramArrayOfInt);
  }
  
  public Drawable getDrawable(Context paramContext, int paramInt)
  {
    try
    {
      paramContext = mResourceManager.getDrawable(paramContext, paramInt);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  Drawable getDrawable(Context paramContext, int paramInt, boolean paramBoolean)
  {
    try
    {
      paramContext = mResourceManager.getDrawable(paramContext, paramInt, paramBoolean);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  ColorStateList getTintList(Context paramContext, int paramInt)
  {
    try
    {
      paramContext = mResourceManager.getTintList(paramContext, paramInt);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  public void onConfigurationChanged(Context paramContext)
  {
    try
    {
      mResourceManager.onConfigurationChanged(paramContext);
      return;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  Drawable onDrawableLoadedFromResources(Context paramContext, VectorEnabledTintResources paramVectorEnabledTintResources, int paramInt)
  {
    try
    {
      paramContext = mResourceManager.onDrawableLoadedFromResources(paramContext, paramVectorEnabledTintResources, paramInt);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  boolean tintDrawableUsingColorFilter(Context paramContext, int paramInt, Drawable paramDrawable)
  {
    return mResourceManager.tintDrawableUsingColorFilter(paramContext, paramInt, paramDrawable);
  }
}
