package com.example.graphleap;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	String messageFromTheCloud;
	Twitter twitter;
	ResponseList<Status> theTimeline;
	ConfigurationBuilder cb;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
        .setOAuthConsumerKey("NLFSU46FJTOdm0y4OPyjQ")
        .setOAuthConsumerSecret("ce2JX36nhfZ7dexYXjelZg8IDhYR6E6NFHrCfIMY3qM")
        .setOAuthAccessToken("15201649-HOZbsNkpRwxrunW49ENcCy6mgQP9OQnC3vaSTv33S")
        .setOAuthAccessTokenSecret("kZJMsYNyMHWOFBw9WqRURW1QHLZdSwKF9vtnMs4qvgk");
        TwitterFactory tf = new TwitterFactory(cb.build());
        twitter = tf.getInstance();
    	try {
    		theTimeline = twitter.getHomeTimeline();
    	} catch (TwitterException e) {
    		e.printStackTrace();
    	}
    	messageFromTheCloud = makeMessage(theTimeline.get(0));
    }

	public String makeMessage(Status theStatus){
		String message = new String();
		message += theStatus.getUser().getName();
		message += "  ::  ";
		message += theStatus.getText();
		return message;
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void readTimeline(View view){
    	TextView theText = (TextView) findViewById(R.id.timelineText);
    	theText.setText(messageFromTheCloud + " " + Double.toString(Math.random()));
    }
}
