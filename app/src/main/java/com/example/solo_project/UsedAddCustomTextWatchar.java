package com.example.solo_project;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

public class UsedAddCustomTextWatchar implements TextWatcher {

    private EditText editText;
    String strAmount = "";

    UsedAddCustomTextWatchar(EditText et) {
        editText = et;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(!TextUtils.isEmpty(s.toString()) && !s.toString().equals(strAmount)) {
            strAmount = makeStringComma(s.toString().replace(",", ""));
            editText.setText(strAmount);
            Editable editable = editText.getText();
            Selection.setSelection(editable, strAmount.length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    protected String makeStringComma(String str) {
        if (str.length() == 0) {
            return "";
        }
        long value = Long.parseLong(str);
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(value);
    }
}
