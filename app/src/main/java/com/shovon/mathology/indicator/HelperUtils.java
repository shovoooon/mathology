package com.shovon.mathology.indicator;

import android.content.res.Resources;
import android.util.TypedValue;

class HelperUtils {



    static int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
