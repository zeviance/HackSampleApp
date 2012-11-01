package net.chriswong.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.android.*;
import com.facebook.android.Facebook.*;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.FacebookError;
import net.chriswong.main.model.*;
import net.chriswong.main.R;

public class MainActivity extends Activity {
	
	Facebook facebook = new Facebook(Constants.FACEBOOK_APP_ID);
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    
    ListView mListView;
    
    List<Friend> mFriendList = new ArrayList<Friend>();
    List<String> mNameList = new ArrayList<String>();
    
    private void updateListView() {
    	mListView.setAdapter(new ArrayAdapter<String>(
    			this,
    			android.R.layout.simple_list_item_1,
    			mNameList));
    	mListView.setTextFilterEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 
        
        mListView = (ListView) findViewById(R.id.listview);
        
        facebook.authorize(
        		this,
        		Permission.permissions,
        		new DialogListener() {
            @Override
            public void onComplete(Bundle values) {
            	// TODO: Add AysncRunner here.
            	mAsyncRunner.request(
            			"/me/friends",
            			new FriendListGetterRequestListener());            	
            }
            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);
    }
    
     
    public class FriendListGetterRequestListener implements RequestListener {

    	@Override
    	public void onComplete(String response, Object state) {
    		try {
    			final JSONObject json = new JSONObject(response);
    			JSONArray array = json.getJSONArray("data");
    			for (int i = 0; i < array.length(); i++) {
    				JSONObject object = array.getJSONObject(i);
    				String name = object.getString("name");
    				String id = object.getString("id");
    				Friend friend = new Friend(name, id);
    				mFriendList.add(friend); 
    				mNameList.add(name);
    			}
    			
    			MainActivity.this.runOnUiThread(new Runnable() {
    	  			public void run() {
    	  				updateListView(); 
    	  			}
    			});    			
			} catch (Exception e) {
				e.printStackTrace();
			}    
    	}

    	@Override
    	public void onIOException(IOException e, Object state) {		
    	}

    	@Override
    	public void onFileNotFoundException(FileNotFoundException e, Object state) {		
    	}

    	@Override
    	public void onMalformedURLException(MalformedURLException e, Object state) {		
    	}

    	@Override
    	public void onFacebookError(FacebookError e, Object state) {		
    	}
    }
}
