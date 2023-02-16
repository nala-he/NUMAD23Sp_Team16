package com.ebookfrenzy.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class WebServiceActivity extends AppCompatActivity {

    private EditText countryEditText ;
    private TextView capitalTextView ;
//    private TextView currencyTextView;
    private Handler textHandler = new Handler();
    private JSONObject jObject;

    private SwitchCompat capitalButton;
    private SwitchCompat currencyButton;
    private SwitchCompat flagButton;
    private SwitchCompat translationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        countryEditText = (EditText)findViewById(R.id.country_edittext);
        capitalTextView = (TextView)findViewById(R.id.capital_textview);
//        currencyTextView = (TextView)findViewById(R.id.currency_textview);
        capitalButton = findViewById(R.id.capital_switch_button);
        currencyButton = findViewById(R.id.currency_switch_button);
        flagButton = findViewById(R.id.flag_switch_button);
        translationButton = findViewById(R.id.translation_switch_button);
    }

    @SuppressLint("SetTextI18n")
    public void callWebserviceButtonHandler(View view) {
        runnableThread runnableThread = new runnableThread();
        capitalTextView.setText("Capital: ");

        new Thread(runnableThread).start();

    }

    class runnableThread implements Runnable {

        @Override
        public void run() {
            textHandler.post(new Runnable() {

                String input;
                {
                    try {
                        input = NetworkUtil.validInput(countryEditText.getText().toString());
                    } catch (NetworkUtil.MyException e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                URL url;
                {
                    try {
                        url = new URL(input);
                    } catch (MalformedURLException e) {
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                // Get String response from the url address
                String resp;
                {
                    try {
                        resp = NetworkUtil.httpResponse(url);
                        JSONArray jArray = new JSONArray(resp);
                        jObject = jArray.getJSONObject(0);
                    } catch (IOException e) {
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),"Please type in a full country name",Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void run() {

                    try {
                        // call the region of the given country from restcountry api
//                        result_view.setText(jObject.getString("region"));

                        if (capitalButton.isChecked()) {
                            // call the capital info from restcountry api
                            JSONArray capitalArray = jObject.getJSONArray("capital");
                            capitalTextView.setText("Capital: " + capitalArray.getString(0).replace(",\n", ","));
                        }


//                        JSONObject currencyObject = jObject.getJSONObject("currencies");
//                        currencyTextView.setText(currencyObject.getString("name"));

                    } catch (Exception e) {
                        // commented out this part to avoid repetitive exception toast messages showing up
                        // when the user input an invalid country name
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                }
            });
            try {
                Thread.sleep(1000); //Makes the thread sleep or be inactive for 10 seconds
            } catch (InterruptedException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}