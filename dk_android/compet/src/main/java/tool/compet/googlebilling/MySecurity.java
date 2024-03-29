/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.googlebilling;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import tool.compet.core.DkLogcats;

import static tool.compet.core4j.DkObjects.empty;

class MySecurity {
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
		if (empty(signedData) || empty(base64PublicKey) || empty(signature)) {
			return false;
		}
		PublicKey key = generatePublicKey(base64PublicKey);

		return verify(key, signedData, signature);
	}

	private static PublicKey generatePublicKey(String encodedPublicKey) {
		try {
			byte[] decodedKey = Base64.decode(encodedPublicKey, Base64.DEFAULT);
			KeyFactory keyFactory = KeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
			return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
		}
		catch (Exception e) {
			DkLogcats.error(MySecurity.class, e);
			throw new RuntimeException(e);
		}
	}

	private static boolean verify(PublicKey publicKey, String signedData, String signature) {
		byte[] signatureBytes;

		try {
			signatureBytes = Base64.decode(signature, Base64.DEFAULT);
		}
		catch (IllegalArgumentException e) {
			return false;
		}

		try {
			Signature signatureAlgorithm = Signature.getInstance(SIGNATURE_ALGORITHM);
			signatureAlgorithm.initVerify(publicKey);
			signatureAlgorithm.update(signedData.getBytes());
			return signatureAlgorithm.verify(signatureBytes);
		}
		catch (Exception e) {
			DkLogcats.error(MySecurity.class, e);
		}

		return false;
	}
}
