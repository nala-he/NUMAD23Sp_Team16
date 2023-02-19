package edu.northeastern.numad23sp_team16;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

// Not working properly as XML layouts. Keep it here as a reference for the attempt to try ConstraintSet.
public class WebServiceConstraintLayout extends ConstraintLayout {
    TextView country_name;
    EditText country_edittext;
    SwitchCompat capital_switch_button;
    SwitchCompat flag_switch_button;
    SwitchCompat currency_switch_button;
    SwitchCompat translation_switch_button;
    Button call_service_button;
    ProgressBar progressBar;
    TextView country_textview;
    TextView capital_textview;
    TextView currency_textview;
    ImageView flag_imageview;
    TextView temp_translation_textview;
    RecyclerView nameRecyclerView;

    @SuppressLint("SetTextI18n")
    public WebServiceConstraintLayout(@NonNull Context context) {
        super(context);

        // set attributes for constraint layout
        setId(View.generateViewId());
        ViewGroup.LayoutParams layoutParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        // set child views and constraints using ConstraintSet class
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);

        country_name = new TextView(context);
        country_name.setId(View.generateViewId());
        country_name.setText("Country Name:");
        addView(country_name);

        country_edittext = new EditText(context);
        country_edittext.setId(View.generateViewId());
        country_edittext.setHint("United States");
        addView(country_edittext);


        capital_switch_button = new SwitchCompat(context);
        capital_switch_button.setId(View.generateViewId());
        capital_switch_button.setText("Show capital city");
        addView(capital_switch_button);

        flag_switch_button = new SwitchCompat(context);
        flag_switch_button.setId(View.generateViewId());
        flag_switch_button.setText("Show flag");
        addView(flag_switch_button);

        currency_switch_button = new SwitchCompat(context);
        currency_switch_button.setId(View.generateViewId());
        currency_switch_button.setText("Show currency");
        addView(currency_switch_button);

        progressBar = new ProgressBar(context);
        progressBar.setId(View.generateViewId());
        progressBar.setVisibility(View.INVISIBLE);
        addView(progressBar);

        translation_switch_button = new SwitchCompat(context);
        translation_switch_button.setId(View.generateViewId());
        translation_switch_button.setText("Show list of translations");
        addView(translation_switch_button);

        call_service_button = new Button(context);
        call_service_button.setId(View.generateViewId());
        call_service_button.setText("PING WEB SERVICE");
        addView(call_service_button);

        flag_imageview = new ImageView(context);
        flag_imageview.setId(View.generateViewId());
        flag_imageview.setImageResource(R.drawable.flag);
        addView(flag_imageview);

        country_textview = new TextView(context);
        country_textview.setId(View.generateViewId());
        country_textview.setText("Official Name: ");
        addView(country_textview);

        capital_textview = new TextView(context);
        capital_textview.setId(View.generateViewId());
        capital_textview.setText("Capital: ");
        addView(capital_textview);

        currency_textview = new TextView(context);
        currency_textview.setId(View.generateViewId());
        currency_textview.setText("Currency: ");
        addView(currency_textview);


        temp_translation_textview = new TextView(context);
        temp_translation_textview.setId(View.generateViewId());
        temp_translation_textview.setText("Translations:");
        addView(temp_translation_textview);

        nameRecyclerView = new RecyclerView(context);
        nameRecyclerView.setId(View.generateViewId());
        addView(nameRecyclerView);

        // user input area
        setCountryName(context, constraintSet);
        setCountryEditText(context, constraintSet);
        setCapitalSwitchButton(context, constraintSet);
        setFlagSwitchButton(context, constraintSet);
        setCurrencySwitchButton(context, constraintSet);
        setTranslationSwitchButton(context, constraintSet);
        setCallServiceButton(context, constraintSet);

        // output area
        setProgressBar(context, constraintSet);
        setCountryTextview(context, constraintSet);
        setCapitalTextview(context, constraintSet);
        setCurrencyTextview(context, constraintSet);
        setFlagImageview(context, constraintSet);
        setTempTranslationTextView(context, constraintSet);
        setNameRecyclerView(context, constraintSet);

        constraintSet.applyTo(this);
    }

    //  XML <TextView android:id="@+id/country_name">
    @SuppressLint("SetTextI18n")
    void setCountryName(Context context, ConstraintSet constraintSet) {

        // constraint country_name's width and height
        constraintSet.constrainWidth(country_name.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(country_name.getId(), ConstraintSet.WRAP_CONTENT);

        // add country_name's vertical and horizontal constraints
        constraintSet.connect(country_name.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(country_name.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(country_name.getId(), ConstraintSet.END,
                country_edittext.getId(), ConstraintSet.START);
        constraintSet.connect(country_name.getId(), ConstraintSet.BASELINE,
                country_edittext.getId(), ConstraintSet.BASELINE);
        constraintSet.setHorizontalChainStyle(country_name.getId(), ConstraintSet.CHAIN_PACKED);
        constraintSet.setMargin(country_name.getId(), ConstraintSet.TOP, 5);
    }

    // XML <EditText android:id="@+id/country_edittext">
    void setCountryEditText(Context context, ConstraintSet constraintSet) {

        // constraint country_edittext's width and height
        constraintSet.constrainWidth(country_edittext.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(country_edittext.getId(), ConstraintSet.WRAP_CONTENT);

        // add country_edittext's vertical and horizontal constraints
        constraintSet.connect(country_edittext.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(country_edittext.getId(), ConstraintSet.TOP,
                ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        constraintSet.connect(country_edittext.getId(), ConstraintSet.START,
                country_name.getId(), ConstraintSet.END);
        constraintSet.setMargin(country_edittext.getId(), ConstraintSet.TOP, 5);
    }

    // XML <androidx.appcompat.widget.SwitchCompat android:id="@+id/capital_switch_button">
    @SuppressLint("SetTextI18n")
    void setCapitalSwitchButton(Context context, ConstraintSet constraintSet) {

        // constraint capital_switch_button's width and height
        constraintSet.constrainWidth(capital_switch_button.getId(), 153);
        constraintSet.constrainHeight(capital_switch_button.getId(), 48);

        // add capital_switch_button's vertical and horizontal constraints
        constraintSet.connect(capital_switch_button.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(capital_switch_button.getId(), ConstraintSet.END,
                flag_switch_button.getId(), ConstraintSet.START);
        constraintSet.connect(capital_switch_button.getId(), ConstraintSet.TOP,
                country_edittext.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(capital_switch_button.getId(), ConstraintSet.TOP, 5);
    }

    // XML <androidx.appcompat.widget.SwitchCompat android:id="@+id/flag_switch_button">
    @SuppressLint("SetTextI18n")
    void setFlagSwitchButton(Context context, ConstraintSet constraintSet) {

        // constraint flag_switch_button's width and height
        constraintSet.constrainWidth(flag_switch_button.getId(), 200);
        constraintSet.constrainHeight(flag_switch_button.getId(), 48);

        // add flag_switch_button's vertical and horizontal constraints
        constraintSet.connect(flag_switch_button.getId(), ConstraintSet.START,
                capital_switch_button.getId(), ConstraintSet.END);
        constraintSet.connect(flag_switch_button.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(flag_switch_button.getId(), ConstraintSet.TOP,
                country_edittext.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(flag_switch_button.getId(), ConstraintSet.TOP, 5);
    }

    // XML <androidx.appcompat.widget.SwitchCompat android:id="@+id/currency_switch_button">
    @SuppressLint("SetTextI18n")
    void setCurrencySwitchButton(Context context, ConstraintSet constraintSet) {

        // constraint currency_switch_button's width and height
        constraintSet.constrainWidth(currency_switch_button.getId(), 153);
        constraintSet.constrainHeight(currency_switch_button.getId(), 48);

        // add currency_switch_button's vertical and horizontal constraints
        constraintSet.connect(currency_switch_button.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(currency_switch_button.getId(), ConstraintSet.END,
                translation_switch_button.getId(), ConstraintSet.START);
        constraintSet.connect(currency_switch_button.getId(), ConstraintSet.TOP,
                capital_switch_button.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(currency_switch_button.getId(), ConstraintSet.TOP, 1);
    }

    // XML <androidx.appcompat.widget.SwitchCompat android:id="@+id/translation_switch_button">
    @SuppressLint("SetTextI18n")
    void setTranslationSwitchButton(Context context, ConstraintSet constraintSet) {

        // constraint translation_switch_button's width and height
        constraintSet.constrainWidth(translation_switch_button.getId(), 200);
        constraintSet.constrainHeight(translation_switch_button.getId(), 48);

        // add translation_switch_button's vertical and horizontal constraints
        constraintSet.connect(translation_switch_button.getId(), ConstraintSet.START,
                currency_switch_button.getId(), ConstraintSet.END);
        constraintSet.connect(translation_switch_button.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(translation_switch_button.getId(), ConstraintSet.TOP,
                flag_switch_button.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(translation_switch_button.getId(), ConstraintSet.TOP, 1);
    }

    // XML <Button android:id="@+id/call_service_button">
    @SuppressLint("SetTextI18n")
    void setCallServiceButton(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(call_service_button.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(call_service_button.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(call_service_button.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(call_service_button.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(call_service_button.getId(), ConstraintSet.TOP,
                currency_switch_button.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(call_service_button.getId(), ConstraintSet.BOTTOM,
                progressBar.getId(), ConstraintSet.TOP);
        constraintSet.setMargin(call_service_button.getId(), ConstraintSet.TOP, 1);
        constraintSet.setHorizontalBias(call_service_button.getId(), 0.5F);
    }

    //  <ProgressBar android:id="@+id/progressBar"
    @SuppressLint("SetTextI18n")
    void setProgressBar(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(progressBar.getId(), ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(progressBar.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(progressBar.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(progressBar.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(progressBar.getId(), ConstraintSet.TOP,
                call_service_button.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(progressBar.getId(), ConstraintSet.BOTTOM,
                country_textview.getId(), ConstraintSet.TOP);
        constraintSet.setMargin(progressBar.getId(), ConstraintSet.TOP, 5);
        constraintSet.setHorizontalBias(progressBar.getId(), 0.5F);
        constraintSet.setEditorAbsoluteX(progressBar.getId(), 177);
        constraintSet.setEditorAbsoluteY(progressBar.getId(), 194);
    }


    // <TextView android:id="@+id/country_textview">
    @SuppressLint("SetTextI18n")
    void setCountryTextview(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(country_textview.getId(), 0);
        constraintSet.constrainHeight(country_textview.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(country_textview.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(country_textview.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(country_textview.getId(), ConstraintSet.TOP,
                progressBar.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(country_textview.getId(), ConstraintSet.BOTTOM,
                capital_textview.getId(), ConstraintSet.TOP);
        constraintSet.setMargin(country_textview.getId(), ConstraintSet.TOP, 5);
    }

    // <TextView android:id="@+id/capital_textview">
    @SuppressLint("SetTextI18n")
    void setCapitalTextview(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(capital_textview.getId(), 0);
        constraintSet.constrainHeight(capital_textview.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(capital_textview.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(capital_textview.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(capital_textview.getId(), ConstraintSet.TOP,
                country_textview.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(capital_textview.getId(), ConstraintSet.BOTTOM,
                currency_textview.getId(), ConstraintSet.TOP);
        constraintSet.setMargin(capital_textview.getId(), ConstraintSet.TOP, 5);
    }

    // <TextView android:id="@+id/currency_textview">
    @SuppressLint("SetTextI18n")
    void setCurrencyTextview(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(currency_textview.getId(), 0);
        constraintSet.constrainHeight(currency_textview.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(currency_textview.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(currency_textview.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(currency_textview.getId(), ConstraintSet.TOP,
                capital_textview.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(currency_textview.getId(), ConstraintSet.BOTTOM,
                flag_imageview.getId(), ConstraintSet.TOP);
        constraintSet.setMargin(currency_textview.getId(), ConstraintSet.TOP, 5);
    }

    // <TextView android:id="@+id/flag_imageview">
    @SuppressLint("SetTextI18n")
    void setFlagImageview(Context context, ConstraintSet constraintSet) {


        constraintSet.constrainWidth(flag_imageview.getId(), 30);
        constraintSet.constrainHeight(flag_imageview.getId(), 30);

        constraintSet.connect(flag_imageview.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(flag_imageview.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(flag_imageview.getId(), ConstraintSet.TOP,
                currency_textview.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(flag_imageview.getId(), ConstraintSet.TOP, 5);
    }

    // <TextView android:id="@+id/temp_translation_textview">
    @SuppressLint("SetTextI18n")
    void setTempTranslationTextView(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(temp_translation_textview.getId(), 0);
        constraintSet.constrainHeight(temp_translation_textview.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(temp_translation_textview.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(temp_translation_textview.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(temp_translation_textview.getId(), ConstraintSet.TOP,
                flag_imageview.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(temp_translation_textview.getId(), ConstraintSet.TOP, 5);
    }

    // <TextView android:id="@+id/nameRecyclerView">
    @SuppressLint("SetTextI18n")
    void setNameRecyclerView(Context context, ConstraintSet constraintSet) {

        constraintSet.constrainWidth(nameRecyclerView.getId(), ConstraintSet.MATCH_CONSTRAINT);
        constraintSet.constrainHeight(temp_translation_textview.getId(), ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(nameRecyclerView.getId(), ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(nameRecyclerView.getId(), ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(nameRecyclerView.getId(), ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraintSet.connect(nameRecyclerView.getId(), ConstraintSet.TOP,
                temp_translation_textview.getId(), ConstraintSet.BOTTOM);
        constraintSet.setMargin(nameRecyclerView.getId(), ConstraintSet.TOP, 5);
    }

}
