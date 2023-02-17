package com.ebookfrenzy.numad23sp_team16;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class WebServiceConstraintLayout extends ConstraintLayout {
    public WebServiceConstraintLayout(@NonNull Context context) {
        super(context);

        // set attributes for constraint layout
        setId(View.generateViewId());
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        // set child views and constraints using ConstraintSet convenience class
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        constraintSet.applyTo(this);
    }

    //TODO

}
