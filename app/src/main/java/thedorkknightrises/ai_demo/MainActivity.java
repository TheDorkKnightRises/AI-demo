package thedorkknightrises.ai_demo;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.ui.AIButton;

public class MainActivity extends AppCompatActivity implements AIListener, AIButton.AIButtonListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AIConfiguration config = new AIConfiguration(getString(R.string.CLIENT_ACCESS_TOKEN),
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        AIButton aiButton = (AIButton) findViewById(R.id.micButton);

        aiButton.initialize(config);
        aiButton.setResultsListener(this);

        final AIDataService aiDataService = new AIDataService(MainActivity.this, config);

        final AIRequest aiRequest = new AIRequest();

        ((TextInputEditText) findViewById(R.id.inputField)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                    aiRequest.setQuery(exampleView.getText().toString());
                    exampleView.setText("");
                    new AsyncTask<AIRequest, Void, AIResponse>() {
                        @Override
                        protected AIResponse doInBackground(AIRequest... requests) {
                            final AIRequest request = requests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(AIResponse aiResponse) {
                            if (aiResponse != null) {
                                onResult(aiResponse);
                            }
                        }
                    }.execute(aiRequest);
                }
                return true;
            }
        });

    }

    @Override
    public void onResult(final AIResponse result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ApiAi", "onResult");

                TextView textView = new TextView(MainActivity.this);
                textView.setBackgroundColor(Color.parseColor("#3F51B5"));
                textView.setText(result.getResult().getResolvedQuery());
                textView.setTextSize(16);
                textView.setTextColor(Color.WHITE);
                textView.setPadding(20, 20, 20, 20);
                ((LinearLayout) findViewById(R.id.list)).addView(textView);
                ViewGroup.MarginLayoutParams lpt = (ViewGroup.MarginLayoutParams) textView.getLayoutParams();
                lpt.setMargins(80, 20, 20, 20);

                TextView replyTextView = new TextView(MainActivity.this);
                replyTextView.setBackgroundColor(Color.LTGRAY);
                replyTextView.setText(result.getResult().getFulfillment().getSpeech());
                replyTextView.setTextSize(16);
                replyTextView.setPadding(20, 20, 20, 20);
                ((LinearLayout) findViewById(R.id.list)).addView(replyTextView);
                ViewGroup.MarginLayoutParams rlpt = (ViewGroup.MarginLayoutParams) replyTextView.getLayoutParams();
                rlpt.setMargins(20, 20, 80, 20);

                final ScrollView scrollView = ((ScrollView) findViewById(R.id.scrollView));
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    @Override
    public void onError(final AIError error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ApiAi", "onError: " + error.getMessage());
                TextView textView = new TextView(MainActivity.this);
                textView.setText(error.getMessage());
                textView.setPadding(4, 4, 4, 4);
                ((LinearLayout) findViewById(R.id.list)).addView(textView);
            }
        });
    }

    @Override
    public void onCancelled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ApiAi", "onCancelled");
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("ApiAi", "onCancelled");
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onListeningFinished() {

    }
}
