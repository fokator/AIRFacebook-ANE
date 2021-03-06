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

import com.adobe.fre.FREContext;
import com.adobe.fre.FREObject;
import com.facebook.Profile;
import com.marpies.ane.facebook.utils.FREObjectUtils;

public class GetUserProfilePictureUriFunction extends BaseFunction {

	@Override
	public FREObject call( FREContext context, FREObject[] args ) {
		super.call( context, args );

		try
		{
			int width = FREObjectUtils.getIntFromFREObject( args[0] );
			int height = FREObjectUtils.getIntFromFREObject( args[1] );
			Profile profile = Profile.getCurrentProfile();
			if( profile != null ) {
				return FREObject.newObject( profile.getProfilePictureUri( width, height ).toString() );
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

}
