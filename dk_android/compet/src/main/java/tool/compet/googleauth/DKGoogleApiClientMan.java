/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.googleauth;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

public class DKGoogleApiClientMan implements GoogleApiClient.OnConnectionFailedListener {
	private final GoogleApiClient googleApiClient;

	public DKGoogleApiClientMan(FragmentActivity fa, String clientId) {
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(clientId)
			.build();

		googleApiClient = new GoogleApiClient.Builder(fa)
			.enableAutoManage(fa, this)
			.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
			.build();
	}

	public OptionalPendingResult<GoogleSignInResult> silentLogIn() {
		return Auth.GoogleSignInApi.silentSignIn(googleApiClient);
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
	}
}
