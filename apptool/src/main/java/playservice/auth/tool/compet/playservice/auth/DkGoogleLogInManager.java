/*
 * Copyright (c) 2018 DarkCompet. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tool.compet.playservice.auth;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class DkGoogleLogInManager {
	private GoogleSignInClient googleSignInClient;

	public DkGoogleLogInManager(Context context, String googleSignInClientId) {
		GoogleSignInOptions opt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(googleSignInClientId)
			.requestEmail()
			.build();

		googleSignInClient = GoogleSignIn.getClient(context, opt);
	}

	public Intent getLogInIntent() {
		return googleSignInClient.getSignInIntent();
	}

	public void logOut() {
		googleSignInClient.revokeAccess().getResult();
		googleSignInClient.signOut().getResult();
	}
}