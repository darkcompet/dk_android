/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.googleauth;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Provide sign in with Google service. That is, user can use Gmail to login, logout...
 */
public class DkGoogleAuthManager {
	protected final GoogleSignInClient googleSignInClient;

	public DkGoogleAuthManager(Context context, String googleSignInClientId) {
		GoogleSignInOptions opt = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(googleSignInClientId)
			.requestEmail()
			.build();

		googleSignInClient = GoogleSignIn.getClient(context, opt);
	}

	public Intent getLogInIntent() {
		return googleSignInClient.getSignInIntent();
	}

	public GoogleSignInAccount getLastSignedInAccount(Context context) {
		return GoogleSignIn.getLastSignedInAccount(context);
	}
}
