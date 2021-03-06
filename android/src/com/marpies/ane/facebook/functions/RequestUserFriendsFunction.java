/*
 * Copyright (c) 2018 Marcel Piestansky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.marpies.ane.facebook.functions;

import android.os.Bundle;
import android.text.TextUtils;
import com.adobe.fre.FREArray;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.marpies.ane.facebook.data.AIRFacebookEvent;
import com.marpies.ane.facebook.utils.AIR;
import com.marpies.ane.facebook.utils.FREObjectUtils;
import com.marpies.ane.facebook.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RequestUserFriendsFunction extends BaseFunction {

	private static GraphRequest.GraphJSONArrayCallback mUserFriendsCallback;

	/**
	 *
	 *
	 * Public API
	 *
	 *
	 */

	@Override
	public FREObject call( FREContext context, FREObject[] args ) {
		super.call( context, args );

		mListenerID = FREObjectUtils.getIntFromFREObject( args[1] );

		AccessToken accessToken = AccessToken.getCurrentAccessToken();
		/* Request the friend list only if we have valid access token */
		if( accessToken != null && !accessToken.isExpired() ) {
			GraphRequest request = GraphRequest.newMyFriendsRequest( accessToken, getCallback() );
			List<String> fields = (args[0] == null) ? null : FREObjectUtils.getListOfStringFromFREArray( (FREArray) args[0] );
			if( fields != null && fields.size() > 0 ) {
				Bundle params = new Bundle();
				params.putString( "fields", TextUtils.join( ",", fields ) );
				request.setParameters( params );
			}
			request.executeAsync();
		} else {
			AIR.log( "User must be logged in to request friends." );
			AIR.dispatchEvent( AIRFacebookEvent.USER_FRIENDS_REQUEST_ERROR,
					StringUtils.getEventErrorJSON( mListenerID, "User is not logged in." )
			);
		}

		return null;
	}

	/**
	 *
	 *
	 * Private API
	 *
	 *
	 */

	private void onUserFriendsLoaded( JSONArray objects, GraphResponse response ) {
		if( response.getError() != null ) {
			AIR.log( "Error requesting user friends: " + response.getError().getErrorMessage() );
			AIR.dispatchEvent( AIRFacebookEvent.USER_FRIENDS_REQUEST_ERROR,
					StringUtils.getEventErrorJSON( mListenerID, response.getError().getErrorMessage() )
			);
			return;
		}
		if( AIR.getContext() != null ) {
			try {
				JSONObject result = new JSONObject();
				result.put( "friends", objects );
				result.put( "listenerID", mListenerID );
				AIR.dispatchEvent( AIRFacebookEvent.USER_FRIENDS_LOADED, result.toString() );
			} catch( JSONException e ) {
				e.printStackTrace();
				AIR.dispatchEvent( AIRFacebookEvent.USER_FRIENDS_REQUEST_ERROR,
						StringUtils.getEventErrorJSON( mListenerID, "Error parsing returned user friends data." )
				);
			}
		} else {
			AIR.log( "ANE CONTEXT IS NULL WHEN REQUESTING USER FRIENDS" );
		}
	}

	private GraphRequest.GraphJSONArrayCallback getCallback() {
		if( mUserFriendsCallback == null ) {
			mUserFriendsCallback = new GraphRequest.GraphJSONArrayCallback() {

				@Override
				public void onCompleted( JSONArray objects, GraphResponse response ) {
					onUserFriendsLoaded( objects, response );
				}

			};
		}
		return mUserFriendsCallback;
	}

}
