package com.graphLeap;

import twitter4j.ResponseList;
import twitter4j.Status;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.graphLeap.twitter.TwitterApp;
import com.graphLeap.twitter.TwitterApp.TwDialogListener;

public class MainActivity extends Activity implements OnClickListener {
	
	private TwitterApp mTwitter;
	Button mBtnTwitter;
	TextView theText;
	ResponseList<Status> theTimeline;
	String messageFromTheCloud;
	
	private static final String CONSUMER_KEY = "NLFSU46FJTOdm0y4OPyjQ";
	private static final String CONSUMER_SECRET = "ce2JX36nhfZ7dexYXjelZg8IDhYR6E6NFHrCfIMY3qM";

	private enum FROM {
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE {
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		mTwitter = new TwitterApp(this, CONSUMER_KEY, CONSUMER_SECRET);
		mBtnTwitter = (Button) findViewById(R.id.read_timeline_button);
		theText = (TextView) findViewById(R.id.timelineText);
		mBtnTwitter.setOnClickListener(this);
	}

	public void onClick(View v) {
		mTwitter.setListener(mTwLoginDialogListener);
		mTwitter.resetAccessToken();
		if (mTwitter.hasAccessToken() == true) {
			try {
				parseTimeline();
				postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
			}
			mTwitter.resetAccessToken();
		} else {
			mTwitter.authorize();
		}
	}

	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				Toast.makeText(this, "Login Successful.", Toast.LENGTH_LONG).show();
				break;
			case FAILED:
				Toast.makeText(this, "Login Failed.", Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				//Toast.makeText(this, "Posted Successfully.", Toast.LENGTH_LONG).show();
				break;
			case FAILED:
				Toast.makeText(this, "Posting Failed.", Toast.LENGTH_LONG).show();
				break;
			case DUPLICATE:
				Toast.makeText(this,"Posting failed because of duplicate message...",Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		}
	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {
			postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
			Log.e("TWITTER", value);
			mTwitter.resetAccessToken();
		}

		public void onComplete(String value) {
			try {
				parseTimeline();
				postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
			}
			mTwitter.resetAccessToken();
		}
	};
	
	public String makeMessage(Status theStatus){
		String message = new String();
		message += theStatus.getUser().getName();
		message += "  ::  ";
		message += theStatus.getText();
		return message;
	}
	
	private void parseTimeline(){
		try {
			theTimeline = mTwitter.getHomeTimeline();
		} catch (Exception e) {
			e.printStackTrace();
		}
		messageFromTheCloud = "";
		for(int i = 0; i<theTimeline.size(); i++){
			messageFromTheCloud += makeMessage(theTimeline.get(i));
			messageFromTheCloud += "\n\n";
		}
		theText.setText(messageFromTheCloud);
	}
}