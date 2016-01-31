package net.seip.botwatch;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class EditTextPreferenceWithSummary extends EditTextPreference {

    public EditTextPreferenceWithSummary(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreferenceWithSummary(Context context) {
        super(context);
    }

    @Override
    public CharSequence getSummary() {
        return getText();
    }
}