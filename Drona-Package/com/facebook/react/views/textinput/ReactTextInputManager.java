package com.facebook.react.views.textinput;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidx.core.content.ContextCompat;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.common.MapBuilder.Builder;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.views.imagehelper.ResourceDrawableIdHelper;
import com.facebook.react.views.scroll.ScrollEvent;
import com.facebook.react.views.scroll.ScrollEventType;
import com.facebook.react.views.text.DefaultStyleValuesUtil;
import com.facebook.react.views.text.ReactFontManager;
import com.facebook.react.views.text.ReactTextUpdate;
import com.facebook.react.views.text.TextInlineImageSpan;
import com.facebook.yoga.YogaConstants;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Map;

@ReactModule(name="AndroidTextInput")
public class ReactTextInputManager
  extends BaseViewManager<ReactEditText, LayoutShadowNode>
{
  private static final int BLUR_TEXT_INPUT = 2;
  private static final InputFilter[] EMPTY_FILTERS = new InputFilter[0];
  private static final int FOCUS_TEXT_INPUT = 1;
  private static final int IME_ACTION_ID = 1648;
  private static final int INPUT_TYPE_KEYBOARD_DECIMAL_PAD = 8194;
  private static final int INPUT_TYPE_KEYBOARD_NUMBERED = 12290;
  private static final int INPUT_TYPE_KEYBOARD_NUMBER_PAD = 2;
  private static final String KEYBOARD_TYPE_DECIMAL_PAD = "decimal-pad";
  private static final String KEYBOARD_TYPE_EMAIL_ADDRESS = "email-address";
  private static final int KEYBOARD_TYPE_FLAGS = 12339;
  private static final String KEYBOARD_TYPE_NUMBER_PAD = "number-pad";
  private static final String KEYBOARD_TYPE_NUMERIC = "numeric";
  private static final String KEYBOARD_TYPE_PHONE_PAD = "phone-pad";
  private static final String KEYBOARD_TYPE_VISIBLE_PASSWORD = "visible-password";
  private static final int PASSWORD_VISIBILITY_FLAG = 16;
  protected static final String REACT_CLASS = "AndroidTextInput";
  private static final int[] SPACING_TYPES = { 8, 0, 2, 1, 3 };
  private static final int UNSET = -1;
  public static final String responseMessage = "ReactTextInputManager";
  
  public ReactTextInputManager() {}
  
  private static void checkPasswordType(ReactEditText paramReactEditText)
  {
    if (((paramReactEditText.getStagedInputType() & 0x3002) != 0) && ((paramReactEditText.getStagedInputType() & 0x80) != 0)) {
      updateStagedInputTypeFlag(paramReactEditText, 128, 16);
    }
  }
  
  private static int parseNumericFontWeight(String paramString)
  {
    if ((paramString.length() == 3) && (paramString.endsWith("00")) && (paramString.charAt(0) <= '9') && (paramString.charAt(0) >= '1')) {
      return (paramString.charAt(0) - '0') * 100;
    }
    return -1;
  }
  
  private void setAutofillHints(ReactEditText paramReactEditText, String... paramVarArgs)
  {
    if (Build.VERSION.SDK_INT < 26) {
      return;
    }
    paramReactEditText.setAutofillHints(paramVarArgs);
  }
  
  private void setImportantForAutofill(ReactEditText paramReactEditText, int paramInt)
  {
    if (Build.VERSION.SDK_INT < 26) {
      return;
    }
    paramReactEditText.setImportantForAutofill(paramInt);
  }
  
  private static void updateStagedInputTypeFlag(ReactEditText paramReactEditText, int paramInt1, int paramInt2)
  {
    paramReactEditText.setStagedInputType(paramInt1 & paramReactEditText.getStagedInputType() | paramInt2);
  }
  
  protected void addEventEmitters(final ThemedReactContext paramThemedReactContext, final ReactEditText paramReactEditText)
  {
    paramReactEditText.addTextChangedListener(new ReactTextInputTextWatcher(paramThemedReactContext, paramReactEditText));
    paramReactEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
    {
      public void onFocusChange(View paramAnonymousView, boolean paramAnonymousBoolean)
      {
        paramAnonymousView = ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
        if (paramAnonymousBoolean)
        {
          paramAnonymousView.dispatchEvent(new ReactTextInputFocusEvent(paramReactEditText.getId()));
          return;
        }
        paramAnonymousView.dispatchEvent(new ReactTextInputBlurEvent(paramReactEditText.getId()));
        paramAnonymousView.dispatchEvent(new ReactTextInputEndEditingEvent(paramReactEditText.getId(), paramReactEditText.getText().toString()));
      }
    });
    paramReactEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramAnonymousTextView, int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
      {
        if (((paramAnonymousInt & 0xFF) == 0) && (paramAnonymousInt != 0)) {
          return true;
        }
        boolean bool1 = paramReactEditText.getBlurOnSubmit();
        boolean bool2 = paramReactEditText.isMultiline();
        ((UIManagerModule)paramThemedReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher().dispatchEvent(new ReactTextInputSubmitEditingEvent(paramReactEditText.getId(), paramReactEditText.getText().toString()));
        if (bool1) {
          paramReactEditText.clearFocus();
        }
        if (!bool1)
        {
          if (!bool2) {
            return true;
          }
          if (paramAnonymousInt != 5) {
            return paramAnonymousInt == 7;
          }
        }
        return true;
      }
    });
  }
  
  public LayoutShadowNode createShadowNodeInstance()
  {
    return new ReactTextInputShadowNode();
  }
  
  public ReactEditText createViewInstance(ThemedReactContext paramThemedReactContext)
  {
    paramThemedReactContext = new ReactEditText(paramThemedReactContext);
    paramThemedReactContext.setInputType(paramThemedReactContext.getInputType() & 0xFFFDFFFF);
    paramThemedReactContext.setReturnKeyType("done");
    return paramThemedReactContext;
  }
  
  public Map getCommandsMap()
  {
    return MapBuilder.get("focusTextInput", Integer.valueOf(1), "blurTextInput", Integer.valueOf(2));
  }
  
  public Map getExportedCustomBubblingEventTypeConstants()
  {
    return MapBuilder.builder().put("topSubmitEditing", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onSubmitEditing", "captured", "onSubmitEditingCapture"))).put("topEndEditing", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onEndEditing", "captured", "onEndEditingCapture"))).put("topTextInput", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onTextInput", "captured", "onTextInputCapture"))).put("topFocus", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onFocus", "captured", "onFocusCapture"))).put("topBlur", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onBlur", "captured", "onBlurCapture"))).put("topKeyPress", MapBuilder.get("phasedRegistrationNames", MapBuilder.get("bubbled", "onKeyPress", "captured", "onKeyPressCapture"))).build();
  }
  
  public Map getExportedCustomDirectEventTypeConstants()
  {
    return MapBuilder.builder().put(ScrollEventType.getJSEventName(ScrollEventType.SCROLL), MapBuilder.get("registrationName", "onScroll")).build();
  }
  
  public Map getExportedViewConstants()
  {
    return MapBuilder.get("AutoCapitalizationType", MapBuilder.get("none", Integer.valueOf(0), "characters", Integer.valueOf(4096), "words", Integer.valueOf(8192), "sentences", Integer.valueOf(16384)));
  }
  
  public String getName()
  {
    return "AndroidTextInput";
  }
  
  public Class getShadowNodeClass()
  {
    return ReactTextInputShadowNode.class;
  }
  
  protected void onAfterUpdateTransaction(ReactEditText paramReactEditText)
  {
    super.onAfterUpdateTransaction(paramReactEditText);
    paramReactEditText.commitStagedInputType();
  }
  
  public void receiveCommand(ReactEditText paramReactEditText, int paramInt, ReadableArray paramReadableArray)
  {
    switch (paramInt)
    {
    default: 
      return;
      return;
    case 2: 
      paramReactEditText.clearFocusFromJS();
      return;
    }
    paramReactEditText.requestFocusFromJS();
  }
  
  public void receiveCommand(ReactEditText paramReactEditText, String paramString, ReadableArray paramReadableArray)
  {
    int i = paramString.hashCode();
    if (i != -1699362314)
    {
      if (i != 3027047)
      {
        if (i != 97604824)
        {
          if ((i == 1690703013) && (paramString.equals("focusTextInput")))
          {
            i = 1;
            break label106;
          }
        }
        else if (paramString.equals("focus"))
        {
          i = 0;
          break label106;
        }
      }
      else if (paramString.equals("blur"))
      {
        i = 2;
        break label106;
      }
    }
    else if (paramString.equals("blurTextInput"))
    {
      i = 3;
      break label106;
    }
    i = -1;
    switch (i)
    {
    default: 
      return;
    case 2: 
    case 3: 
      label106:
      paramReactEditText.clearFocusFromJS();
      return;
    }
    paramReactEditText.requestFocusFromJS();
  }
  
  public void setAllowFontScaling(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setAllowFontScaling(paramBoolean);
  }
  
  public void setAutoCapitalize(ReactEditText paramReactEditText, Dynamic paramDynamic)
  {
    ReadableType localReadableType1 = paramDynamic.getType();
    ReadableType localReadableType2 = ReadableType.Number;
    int j = 16384;
    int i;
    if (localReadableType1 == localReadableType2)
    {
      i = paramDynamic.asInt();
    }
    else
    {
      i = j;
      if (paramDynamic.getType() == ReadableType.String)
      {
        paramDynamic = paramDynamic.asString();
        if (paramDynamic.equals("none"))
        {
          i = 0;
        }
        else if (paramDynamic.equals("characters"))
        {
          i = 4096;
        }
        else if (paramDynamic.equals("words"))
        {
          i = 8192;
        }
        else
        {
          paramDynamic.equals("sentences");
          i = j;
        }
      }
    }
    updateStagedInputTypeFlag(paramReactEditText, 28672, i);
  }
  
  public void setAutoCorrect(ReactEditText paramReactEditText, Boolean paramBoolean)
  {
    int i;
    if (paramBoolean != null)
    {
      if (paramBoolean.booleanValue()) {
        i = 32768;
      } else {
        i = 524288;
      }
    }
    else {
      i = 0;
    }
    updateStagedInputTypeFlag(paramReactEditText, 557056, i);
  }
  
  public void setBlurOnSubmit(ReactEditText paramReactEditText, Boolean paramBoolean)
  {
    paramReactEditText.setBlurOnSubmit(paramBoolean);
  }
  
  public void setBorderColor(ReactEditText paramReactEditText, int paramInt, Integer paramInteger)
  {
    float f2 = NaN.0F;
    float f1;
    if (paramInteger == null) {
      f1 = NaN.0F;
    } else {
      f1 = paramInteger.intValue() & 0xFFFFFF;
    }
    if (paramInteger != null) {
      f2 = paramInteger.intValue() >>> 24;
    }
    paramReactEditText.setBorderColor(SPACING_TYPES[paramInt], f1, f2);
  }
  
  public void setBorderRadius(ReactEditText paramReactEditText, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    if (paramInt == 0)
    {
      paramReactEditText.setBorderRadius(f);
      return;
    }
    paramReactEditText.setBorderRadius(f, paramInt - 1);
  }
  
  public void setBorderStyle(ReactEditText paramReactEditText, String paramString)
  {
    paramReactEditText.setBorderStyle(paramString);
  }
  
  public void setBorderWidth(ReactEditText paramReactEditText, int paramInt, float paramFloat)
  {
    float f = paramFloat;
    if (!YogaConstants.isUndefined(paramFloat)) {
      f = PixelUtil.toPixelFromDIP(paramFloat);
    }
    paramReactEditText.setBorderWidth(SPACING_TYPES[paramInt], f);
  }
  
  public void setCaretHidden(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setCursorVisible(paramBoolean ^ true);
  }
  
  public void setColor(ReactEditText paramReactEditText, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactEditText.setTextColor(DefaultStyleValuesUtil.getDefaultTextColor(paramReactEditText.getContext()));
      return;
    }
    paramReactEditText.setTextColor(paramInteger.intValue());
  }
  
  public void setContextMenuHidden(ReactEditText paramReactEditText, final boolean paramBoolean)
  {
    paramReactEditText.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        return paramBoolean;
      }
    });
  }
  
  public void setCursorColor(ReactEditText paramReactEditText, Integer paramInteger)
  {
    try
    {
      Object localObject = TextView.class.getDeclaredField("mCursorDrawableRes");
      ((Field)localObject).setAccessible(true);
      int i = ((Field)localObject).getInt(paramReactEditText);
      if (i == 0) {
        return;
      }
      localObject = ContextCompat.getDrawable(paramReactEditText.getContext(), i);
      if (paramInteger != null)
      {
        i = paramInteger.intValue();
        paramInteger = PorterDuff.Mode.SRC_IN;
        ((Drawable)localObject).setColorFilter(i, paramInteger);
      }
      paramInteger = TextView.class.getDeclaredField("mEditor");
      paramInteger.setAccessible(true);
      paramReactEditText = paramInteger.get(paramReactEditText);
      paramInteger = paramReactEditText.getClass().getDeclaredField("mCursorDrawable");
      paramInteger.setAccessible(true);
      paramInteger.set(paramReactEditText, new Drawable[] { localObject, localObject });
      return;
    }
    catch (NoSuchFieldException paramReactEditText) {}catch (IllegalAccessException paramReactEditText) {}
  }
  
  public void setDisableFullscreenUI(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setDisableFullscreenUI(paramBoolean);
  }
  
  public void setEditable(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setEnabled(paramBoolean);
  }
  
  public void setFontFamily(ReactEditText paramReactEditText, String paramString)
  {
    int i;
    if (paramReactEditText.getTypeface() != null) {
      i = paramReactEditText.getTypeface().getStyle();
    } else {
      i = 0;
    }
    paramReactEditText.setTypeface(ReactFontManager.getInstance().getTypeface(paramString, i, paramReactEditText.getContext().getAssets()));
  }
  
  public void setFontSize(ReactEditText paramReactEditText, float paramFloat)
  {
    paramReactEditText.setFontSize(paramFloat);
  }
  
  public void setFontStyle(ReactEditText paramReactEditText, String paramString)
  {
    int i;
    if ("italic".equals(paramString)) {
      i = 2;
    } else if ("normal".equals(paramString)) {
      i = 0;
    } else {
      i = -1;
    }
    Typeface localTypeface = paramReactEditText.getTypeface();
    paramString = localTypeface;
    if (localTypeface == null) {
      paramString = Typeface.DEFAULT;
    }
    if (i != paramString.getStyle()) {
      paramReactEditText.setTypeface(paramString, i);
    }
  }
  
  public void setFontWeight(ReactEditText paramReactEditText, String paramString)
  {
    int k = -1;
    int j;
    if (paramString != null) {
      j = parseNumericFontWeight(paramString);
    } else {
      j = -1;
    }
    int i;
    if ((j < 500) && (!"bold".equals(paramString)))
    {
      if (!"normal".equals(paramString))
      {
        i = k;
        if (j != -1)
        {
          i = k;
          if (j >= 500) {}
        }
      }
      else
      {
        i = 0;
      }
    }
    else {
      i = 1;
    }
    Typeface localTypeface = paramReactEditText.getTypeface();
    paramString = localTypeface;
    if (localTypeface == null) {
      paramString = Typeface.DEFAULT;
    }
    if (i != paramString.getStyle()) {
      paramReactEditText.setTypeface(paramString, i);
    }
  }
  
  public void setImportantForAutofill(ReactEditText paramReactEditText, String paramString)
  {
    int i;
    if ("no".equals(paramString)) {
      i = 2;
    } else if ("noExcludeDescendants".equals(paramString)) {
      i = 8;
    } else if ("yes".equals(paramString)) {
      i = 1;
    } else if ("yesExcludeDescendants".equals(paramString)) {
      i = 4;
    } else {
      i = 0;
    }
    setImportantForAutofill(paramReactEditText, i);
  }
  
  public void setInlineImageLeft(ReactEditText paramReactEditText, String paramString)
  {
    paramReactEditText.setCompoundDrawablesWithIntrinsicBounds(ResourceDrawableIdHelper.getInstance().getResourceDrawableId(paramReactEditText.getContext(), paramString), 0, 0, 0);
  }
  
  public void setInlineImagePadding(ReactEditText paramReactEditText, int paramInt)
  {
    paramReactEditText.setCompoundDrawablePadding(paramInt);
  }
  
  public void setKeyboardType(ReactEditText paramReactEditText, String paramString)
  {
    int i;
    if ("numeric".equalsIgnoreCase(paramString)) {
      i = 12290;
    } else if ("number-pad".equalsIgnoreCase(paramString)) {
      i = 2;
    } else if ("decimal-pad".equalsIgnoreCase(paramString)) {
      i = 8194;
    } else if ("email-address".equalsIgnoreCase(paramString)) {
      i = 33;
    } else if ("phone-pad".equalsIgnoreCase(paramString)) {
      i = 3;
    } else if ("visible-password".equalsIgnoreCase(paramString)) {
      i = 144;
    } else {
      i = 1;
    }
    updateStagedInputTypeFlag(paramReactEditText, 12339, i);
    checkPasswordType(paramReactEditText);
  }
  
  public void setLetterSpacing(ReactEditText paramReactEditText, float paramFloat)
  {
    paramReactEditText.setLetterSpacingPt(paramFloat);
  }
  
  public void setMaxFontSizeMultiplier(ReactEditText paramReactEditText, float paramFloat)
  {
    paramReactEditText.setMaxFontSizeMultiplier(paramFloat);
  }
  
  public void setMaxLength(ReactEditText paramReactEditText, Integer paramInteger)
  {
    InputFilter[] arrayOfInputFilter1 = paramReactEditText.getFilters();
    InputFilter[] arrayOfInputFilter2 = EMPTY_FILTERS;
    int i = 0;
    if (paramInteger == null)
    {
      paramInteger = arrayOfInputFilter2;
      if (arrayOfInputFilter1.length > 0)
      {
        LinkedList localLinkedList = new LinkedList();
        while (i < arrayOfInputFilter1.length)
        {
          if (!(arrayOfInputFilter1[i] instanceof InputFilter.LengthFilter)) {
            localLinkedList.add(arrayOfInputFilter1[i]);
          }
          i += 1;
        }
        paramInteger = arrayOfInputFilter2;
        if (!localLinkedList.isEmpty()) {
          paramInteger = (InputFilter[])localLinkedList.toArray(new InputFilter[localLinkedList.size()]);
        }
      }
    }
    else if (arrayOfInputFilter1.length > 0)
    {
      i = 0;
      int j = 0;
      while (i < arrayOfInputFilter1.length)
      {
        if ((arrayOfInputFilter1[i] instanceof InputFilter.LengthFilter))
        {
          arrayOfInputFilter1[i] = new InputFilter.LengthFilter(paramInteger.intValue());
          j = 1;
        }
        i += 1;
      }
      if (j == 0)
      {
        arrayOfInputFilter2 = new InputFilter[arrayOfInputFilter1.length + 1];
        System.arraycopy(arrayOfInputFilter1, 0, arrayOfInputFilter2, 0, arrayOfInputFilter1.length);
        arrayOfInputFilter1[arrayOfInputFilter1.length] = new InputFilter.LengthFilter(paramInteger.intValue());
        paramInteger = arrayOfInputFilter2;
      }
      else
      {
        paramInteger = arrayOfInputFilter1;
      }
    }
    else
    {
      arrayOfInputFilter1 = new InputFilter[1];
      arrayOfInputFilter1[0] = new InputFilter.LengthFilter(paramInteger.intValue());
      paramInteger = arrayOfInputFilter1;
    }
    paramReactEditText.setFilters(paramInteger);
  }
  
  public void setMostRecentEventCount(ReactEditText paramReactEditText, int paramInt)
  {
    paramReactEditText.setMostRecentEventCount(paramInt);
  }
  
  public void setMultiline(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    int j = 131072;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 131072;
    }
    if (!paramBoolean) {
      j = 0;
    }
    updateStagedInputTypeFlag(paramReactEditText, i, j);
  }
  
  public void setNumLines(ReactEditText paramReactEditText, int paramInt)
  {
    paramReactEditText.setLines(paramInt);
  }
  
  public void setOnContentSizeChange(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramReactEditText.setContentSizeWatcher(new ReactContentSizeWatcher(paramReactEditText));
      return;
    }
    paramReactEditText.setContentSizeWatcher(null);
  }
  
  public void setOnKeyPress(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setOnKeyPress(paramBoolean);
  }
  
  public void setOnScroll(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramReactEditText.setScrollWatcher(new ReactScrollWatcher(paramReactEditText));
      return;
    }
    paramReactEditText.setScrollWatcher(null);
  }
  
  public void setOnSelectionChange(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramReactEditText.setSelectionWatcher(new ReactSelectionWatcher(paramReactEditText));
      return;
    }
    paramReactEditText.setSelectionWatcher(null);
  }
  
  public void setPlaceholder(ReactEditText paramReactEditText, String paramString)
  {
    paramReactEditText.setHint(paramString);
  }
  
  public void setPlaceholderTextColor(ReactEditText paramReactEditText, Integer paramInteger)
  {
    if (paramInteger == null)
    {
      paramReactEditText.setHintTextColor(DefaultStyleValuesUtil.getDefaultTextColorHint(paramReactEditText.getContext()));
      return;
    }
    paramReactEditText.setHintTextColor(paramInteger.intValue());
  }
  
  public void setReturnKeyLabel(ReactEditText paramReactEditText, String paramString)
  {
    paramReactEditText.setImeActionLabel(paramString, 1648);
  }
  
  public void setReturnKeyType(ReactEditText paramReactEditText, String paramString)
  {
    paramReactEditText.setReturnKeyType(paramString);
  }
  
  public void setSecureTextEntry(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    int j = 0;
    int i;
    if (paramBoolean) {
      i = 0;
    } else {
      i = 144;
    }
    if (paramBoolean) {
      j = 128;
    }
    updateStagedInputTypeFlag(paramReactEditText, i, j);
    checkPasswordType(paramReactEditText);
  }
  
  public void setSelectTextOnFocus(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setSelectAllOnFocus(paramBoolean);
  }
  
  public void setSelectionColor(ReactEditText paramReactEditText, Integer paramInteger)
  {
    if (paramInteger == null) {
      paramReactEditText.setHighlightColor(DefaultStyleValuesUtil.getDefaultTextColorHighlight(paramReactEditText.getContext()));
    } else {
      paramReactEditText.setHighlightColor(paramInteger.intValue());
    }
    setCursorColor(paramReactEditText, paramInteger);
  }
  
  public void setTextAlign(ReactEditText paramReactEditText, String paramString)
  {
    if ("justify".equals(paramString))
    {
      if (Build.VERSION.SDK_INT >= 26) {
        paramReactEditText.setJustificationMode(1);
      }
      paramReactEditText.setGravityHorizontal(3);
      return;
    }
    if (Build.VERSION.SDK_INT >= 26) {
      paramReactEditText.setJustificationMode(0);
    }
    if ((paramString != null) && (!"auto".equals(paramString)))
    {
      if ("left".equals(paramString))
      {
        paramReactEditText.setGravityHorizontal(3);
        return;
      }
      if ("right".equals(paramString))
      {
        paramReactEditText.setGravityHorizontal(5);
        return;
      }
      if ("center".equals(paramString))
      {
        paramReactEditText.setGravityHorizontal(1);
        return;
      }
      paramReactEditText = new StringBuilder();
      paramReactEditText.append("Invalid textAlign: ");
      paramReactEditText.append(paramString);
      throw new JSApplicationIllegalArgumentException(paramReactEditText.toString());
    }
    paramReactEditText.setGravityHorizontal(0);
  }
  
  public void setTextAlignVertical(ReactEditText paramReactEditText, String paramString)
  {
    if ((paramString != null) && (!"auto".equals(paramString)))
    {
      if ("top".equals(paramString))
      {
        paramReactEditText.setGravityVertical(48);
        return;
      }
      if ("bottom".equals(paramString))
      {
        paramReactEditText.setGravityVertical(80);
        return;
      }
      if ("center".equals(paramString))
      {
        paramReactEditText.setGravityVertical(16);
        return;
      }
      paramReactEditText = new StringBuilder();
      paramReactEditText.append("Invalid textAlignVertical: ");
      paramReactEditText.append(paramString);
      throw new JSApplicationIllegalArgumentException(paramReactEditText.toString());
    }
    paramReactEditText.setGravityVertical(0);
  }
  
  public void setTextContentType(ReactEditText paramReactEditText, String paramString)
  {
    if (paramString == null)
    {
      setImportantForAutofill(paramReactEditText, 2);
      return;
    }
    if ("username".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "username" });
      return;
    }
    if ("password".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "password" });
      return;
    }
    if ("email".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "emailAddress" });
      return;
    }
    if ("name".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "name" });
      return;
    }
    if ("tel".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "phone" });
      return;
    }
    if ("street-address".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "postalAddress" });
      return;
    }
    if ("postal-code".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "postalCode" });
      return;
    }
    if ("cc-number".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "creditCardNumber" });
      return;
    }
    if ("cc-csc".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "creditCardSecurityCode" });
      return;
    }
    if ("cc-exp".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "creditCardExpirationDate" });
      return;
    }
    if ("cc-exp-month".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "creditCardExpirationMonth" });
      return;
    }
    if ("cc-exp-year".equals(paramString))
    {
      setAutofillHints(paramReactEditText, new String[] { "creditCardExpirationYear" });
      return;
    }
    if ("off".equals(paramString))
    {
      setImportantForAutofill(paramReactEditText, 2);
      return;
    }
    paramReactEditText = new StringBuilder();
    paramReactEditText.append("Invalid autoCompleteType: ");
    paramReactEditText.append(paramString);
    throw new JSApplicationIllegalArgumentException(paramReactEditText.toString());
  }
  
  public void setUnderlineColor(ReactEditText paramReactEditText, Integer paramInteger)
  {
    Drawable localDrawable = paramReactEditText.getBackground();
    paramReactEditText = localDrawable;
    Object localObject = paramReactEditText;
    ReactEditText localReactEditText;
    if (localDrawable.getConstantState() != null) {
      try
      {
        localObject = localDrawable.mutate();
      }
      catch (NullPointerException localNullPointerException)
      {
        FLog.e(responseMessage, "NullPointerException when setting underlineColorAndroid for TextInput", localNullPointerException);
        localReactEditText = paramReactEditText;
      }
    }
    if (paramInteger == null)
    {
      localReactEditText.clearColorFilter();
      return;
    }
    localReactEditText.setColorFilter(paramInteger.intValue(), PorterDuff.Mode.SRC_IN);
  }
  
  public void showKeyboardOnFocus(ReactEditText paramReactEditText, boolean paramBoolean)
  {
    paramReactEditText.setShowSoftInputOnFocus(paramBoolean);
  }
  
  public void updateExtraData(ReactEditText paramReactEditText, Object paramObject)
  {
    if ((paramObject instanceof ReactTextUpdate))
    {
      paramObject = (ReactTextUpdate)paramObject;
      paramReactEditText.setPadding((int)paramObject.getPaddingLeft(), (int)paramObject.getPaddingTop(), (int)paramObject.getPaddingRight(), (int)paramObject.getPaddingBottom());
      if (paramObject.containsImages()) {
        TextInlineImageSpan.possiblyUpdateInlineImageSpans(paramObject.getText(), paramReactEditText);
      }
      paramReactEditText.maybeSetText(paramObject);
      if ((paramObject.getSelectionStart() != -1) && (paramObject.getSelectionEnd() != -1)) {
        paramReactEditText.setSelection(paramObject.getSelectionStart(), paramObject.getSelectionEnd());
      }
    }
  }
  
  private class ReactContentSizeWatcher
    implements ContentSizeWatcher
  {
    private ReactEditText mEditText;
    private EventDispatcher mEventDispatcher;
    private int mPreviousContentHeight = 0;
    private int mPreviousContentWidth = 0;
    
    public ReactContentSizeWatcher(ReactEditText paramReactEditText)
    {
      mEditText = paramReactEditText;
      mEventDispatcher = ((UIManagerModule)((ReactContext)paramReactEditText.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher();
    }
    
    public void onLayout()
    {
      int i = mEditText.getWidth();
      int j = mEditText.getHeight();
      if (mEditText.getLayout() != null)
      {
        i = mEditText.getCompoundPaddingLeft() + mEditText.getLayout().getWidth() + mEditText.getCompoundPaddingRight();
        j = mEditText.getCompoundPaddingTop() + mEditText.getLayout().getHeight() + mEditText.getCompoundPaddingBottom();
      }
      if ((i != mPreviousContentWidth) || (j != mPreviousContentHeight))
      {
        mPreviousContentHeight = j;
        mPreviousContentWidth = i;
        mEventDispatcher.dispatchEvent(new ReactContentSizeChangedEvent(mEditText.getId(), PixelUtil.toDIPFromPixel(i), PixelUtil.toDIPFromPixel(j)));
      }
    }
  }
  
  private class ReactScrollWatcher
    implements ScrollWatcher
  {
    private EventDispatcher mEventDispatcher;
    private int mPreviousHoriz;
    private int mPreviousVert;
    private ReactEditText mReactEditText;
    
    public ReactScrollWatcher(ReactEditText paramReactEditText)
    {
      mReactEditText = paramReactEditText;
      mEventDispatcher = ((UIManagerModule)((ReactContext)paramReactEditText.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher();
    }
    
    public void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if ((mPreviousHoriz != paramInt1) || (mPreviousVert != paramInt2))
      {
        ScrollEvent localScrollEvent = ScrollEvent.obtain(mReactEditText.getId(), ScrollEventType.SCROLL, paramInt1, paramInt2, 0.0F, 0.0F, 0, 0, mReactEditText.getWidth(), mReactEditText.getHeight());
        mEventDispatcher.dispatchEvent(localScrollEvent);
        mPreviousHoriz = paramInt1;
        mPreviousVert = paramInt2;
      }
    }
  }
  
  private class ReactSelectionWatcher
    implements SelectionWatcher
  {
    private EventDispatcher mEventDispatcher;
    private int mPreviousSelectionEnd;
    private int mPreviousSelectionStart;
    private ReactEditText mReactEditText;
    
    public ReactSelectionWatcher(ReactEditText paramReactEditText)
    {
      mReactEditText = paramReactEditText;
      mEventDispatcher = ((UIManagerModule)((ReactContext)paramReactEditText.getContext()).getNativeModule(UIManagerModule.class)).getEventDispatcher();
    }
    
    public void onSelectionChanged(int paramInt1, int paramInt2)
    {
      int i = Math.min(paramInt1, paramInt2);
      paramInt1 = Math.max(paramInt1, paramInt2);
      if ((mPreviousSelectionStart != i) || (mPreviousSelectionEnd != paramInt1))
      {
        mEventDispatcher.dispatchEvent(new ReactTextInputSelectionEvent(mReactEditText.getId(), i, paramInt1));
        mPreviousSelectionStart = i;
        mPreviousSelectionEnd = paramInt1;
      }
    }
  }
  
  private class ReactTextInputTextWatcher
    implements TextWatcher
  {
    private ReactEditText mEditText;
    private EventDispatcher mEventDispatcher;
    private String mPreviousText;
    
    public ReactTextInputTextWatcher(ReactContext paramReactContext, ReactEditText paramReactEditText)
    {
      mEventDispatcher = ((UIManagerModule)paramReactContext.getNativeModule(UIManagerModule.class)).getEventDispatcher();
      mEditText = paramReactEditText;
      mPreviousText = null;
    }
    
    public void afterTextChanged(Editable paramEditable) {}
    
    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      mPreviousText = paramCharSequence.toString();
    }
    
    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
      if ((paramInt3 == 0) && (paramInt2 == 0)) {
        return;
      }
      Assertions.assertNotNull(mPreviousText);
      String str1 = paramCharSequence.toString().substring(paramInt1, paramInt1 + paramInt3);
      String str2 = mPreviousText;
      int i = paramInt1 + paramInt2;
      str2 = str2.substring(paramInt1, i);
      if ((paramInt3 == paramInt2) && (str1.equals(str2))) {
        return;
      }
      mEventDispatcher.dispatchEvent(new ReactTextChangedEvent(mEditText.getId(), paramCharSequence.toString(), mEditText.incrementAndGetEventCounter()));
      mEventDispatcher.dispatchEvent(new ReactTextInputEvent(mEditText.getId(), str1, str2, paramInt1, i));
    }
  }
}
