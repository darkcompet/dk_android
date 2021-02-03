/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.packages.auth;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.util.concurrent.ExecutionException;

import tool.compet.core.stream.DkObservable;

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
		DkObservable
			.fromExecution(() -> {
				Task<Void> revokeTask = googleSignInClient.revokeAccess();
				Task<Void> signoutTask = googleSignInClient.signOut();

				Tasks.await(revokeTask);
				Tasks.await(signoutTask);

				revokeTask.getResult();
				signoutTask.getResult();
				return true;
			})
			.scheduleInBackground()
			.subscribe();
	}
}
