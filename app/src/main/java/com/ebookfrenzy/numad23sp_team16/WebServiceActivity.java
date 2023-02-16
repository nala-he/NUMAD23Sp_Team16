package com.ebookfrenzy.numad23sp_team16;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service);

        countryEditText = (EditText)findViewById(R.id.country_edittext);
        capitalTextView = (TextView)findViewById(R.id.capital_textview);
//        currencyTextView = (TextView)findViewById(R.id.currency_textview);

    }

    public void callWebserviceButtonHandler(View view) {
        runnableThread runnableThread = new runnableThread();
        new Thread(runnableThread).start();

    }

    class runnableThread implements Runnable {

        @Override
        public void run() {
            textHandler.post(new Runnable() {

                // REST Country web service: https://restcountries.com/v3.1/name/
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
//                        Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getApplication(),
                                        e.toString(),
                                        Toast.LENGTH_LONG)
                                        .show();
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
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                Toast.makeText(getApplication(),e.toString(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                }

                @Override
                public void run() {

                    try {
                        // call the region of the given country from restcountry api
//                        result_view.setText(jObject.getString("region"));
                        // call the capital info from restcountry api
                        JSONArray capitalArray = jObject.getJSONArray("capital");
                        capitalTextView.setText(capitalArray.getString(0));

//                        JSONObject currencyObject = jObject.getJSONObject("currencies");
//                        currencyTextView.setText(currencyObject.getString("name"));

                    } catch (Exception e) {
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