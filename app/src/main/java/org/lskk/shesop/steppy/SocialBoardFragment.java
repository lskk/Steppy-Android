package org.lskk.shesop.steppy;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.lskk.shesop.steppy.connection.AccountHandler;
import org.lskk.shesop.steppy.connection.IConnectionResponseHandler;
import org.lskk.shesop.steppy.data.Account;
import org.lskk.shesop.steppy.data.Friend;
import org.lskk.shesop.steppy.utils.Tools;
import org.lskk.teamshesop.steppy.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class SocialBoardFragment extends Fragment {
	
	public static final String TAG = "SocialBoardFragment";
	
	/**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    
    /**
     * The Listview for this fragment
     */
    private ListView mListView;
    
    private TextView mTitle;
    
    /**
     * Adapter for listview
     */
    SocialBoardLocalAdapter adapter1;
    SocialBoardGlobalAdapter adapter2;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static SocialBoardFragment create(int pageNumber) {
    	SocialBoardFragment fragment = new SocialBoardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	mPageNumber = getArguments().getInt(ARG_PAGE);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
    	// Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_social_board_listview, container, false);
        
    	mListView 	= (ListView) rootView.findViewById(R.id.sb_listview);
    	mTitle		= (TextView) rootView.findViewById(R.id.sb_title);
    	
    	boolean haveFriend = getAllFriends() != null; 
    	
    	switch (mPageNumber) {
			case 1:
				if(haveFriend) {
					adapter1 = new SocialBoardLocalAdapter(getActivity(), getAllFriends()); 
					
					mListView.setAdapter(adapter1);
					mListView.setTop(getId());
					mTitle.setText(R.string.sb_title_local);
					
				} else {
					mTitle.setText("You Have no friends from your contacts");
				}				
				break;
			case 2:
				if(haveFriend) {
					adapter2 = new SocialBoardGlobalAdapter(getActivity(), getAllFriends());
					
					mListView.setAdapter(adapter2);
					mTitle.setText(R.string.sb_title_global);
				} else {
					mTitle.setText("You Have no friends from your contacts");
				}
				break;
			default:
				break;
		}
    	
    	handlerSyncFriends.postDelayed(taskSyncFriends, 60000);
        
    	return rootView;
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	handlerSyncFriends.removeCallbacks(taskSyncFriends);
    }
    
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
    
    private List<Friend> getAllFriends() {
    	return Account.getSingleton(getActivity()).getFriends();
    }
    
    private Handler handlerSyncFriends = new Handler();	
	
	private Runnable taskSyncFriends = new Runnable() {
		   @Override
		   public void run() {
		      /* do what you need to do */
			   //off for edit
	//		  syncFriendBoard();
		      /* and here comes the "trick" */
		      handlerSyncFriends.postDelayed(this, 30000);
		   }
	};
	
	private void syncFriendBoard() {
		AccountHandler account = new AccountHandler(getActivity(), 
				new IConnectionResponseHandler() {
			
			@Override
			public void OnSuccessArray(JSONArray pResult){
				
			}
			@Override
			public void onSuccessJSONObject(String pResult) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onSuccessJSONArray(String pResult) {
				try {					 
					Tools.friendSync(getActivity(), new JSONArray(pResult), friendListener);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(String e) {
				Log.e(TAG, e);
			}
		});
//		System.out.println("tokennya: "+Account.getSingleton(getActivity()).getToken());
	//	account.syncFriends(Account.getSingleton(getActivity()).getToken());
	}		
	
	Friend.Listener friendListener = new Friend.Listener() {
		@Override
		public void dataChanged() {
			adapter1.swapObject(getAllFriends());
//			adapter2.swapObject(getAllFriends());
		}
	};
    
}
