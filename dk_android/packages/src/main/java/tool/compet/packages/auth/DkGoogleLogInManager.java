/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.auth;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Provide sign in with Google service. That is, user can use Gmail to login, logout...
 */
public class DkGoogleLogInManager {
	private final GoogleSignInClient googleSignInClient;

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
