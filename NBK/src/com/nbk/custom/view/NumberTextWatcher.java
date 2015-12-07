package com.nbk.custom.view;

import java.text.DecimalFormat;
import java.text.ParseException;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class NumberTextWatcher implements TextWatcher {

    private DecimalFormat df;
    private DecimalFormat dfnd;
    private DecimalFormat dfsd; // single decimal
    private DecimalFormat dfdd; // double decimal
    private boolean hasFractionalPart;
    
    private int decimalCount;

    private EditText et;

    public NumberTextWatcher(EditText et)
    {
        df = new DecimalFormat("###,###,###.##");
        df.setDecimalSeparatorAlwaysShown(true);
        dfnd = new DecimalFormat("###,###,###");
        dfsd = new DecimalFormat("###,###,###.0");
        dfdd = new DecimalFormat("###,###,###.00");
        this.et = et;
        hasFractionalPart = false;
        decimalCount = 0;
    }

    @SuppressWarnings("unused")
    private static final String TAG = "NumberTextWatcher";

    @Override
    public void afterTextChanged(Editable s)
    {
        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            Number n = df.parse(v);
            int cp = et.getSelectionStart();
            if (hasFractionalPart) {

                switch (decimalCount) {
    			case 1:
    				et.setText(dfsd.format(n));
    				break;
    				
    			case 2:
    				et.setText(dfdd.format(n));
    				break;

    			default:
    				et.setText(df.format(n));
    				break;
    			}
                
            } else {
                et.setText(dfnd.format(n));
            }
            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }
        } catch (NumberFormatException nfe) {
            // do nothing?
        } catch (ParseException e) {
            // do nothing?
        }

        et.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        int decimalIndex = s.toString().indexOf(df.getDecimalFormatSymbols().getDecimalSeparator());
        
        if (s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator())))
//        if (decimalIndex >= 0)
        {
            hasFractionalPart = true;
            
            String decimalValues = s.toString().substring(decimalIndex + 1, s.toString().length());
            
            decimalCount = decimalValues.length();
            
        } else {
            hasFractionalPart = false;
        }
    }

}