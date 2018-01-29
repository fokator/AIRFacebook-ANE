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
 
#import "IsPermissionGrantedFunction.h"
#import <Foundation/Foundation.h>
#import <AIRExtHelpers/MPFREObjectUtils.h>
#import "AIRFacebook.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>

FREObject fb_isPermissionGranted( FREContext context, void *functionData, uint32_t argc, FREObject *argv ) {
    NSString* permission = [MPFREObjectUtils getNSString:argv[0]];

    FBSDKAccessToken* accessToken = [FBSDKAccessToken currentAccessToken];
    if( accessToken ) {
        return [MPFREObjectUtils getFREObjectFromBOOL:[accessToken hasGranted:permission]];
    }

    return nil;
}