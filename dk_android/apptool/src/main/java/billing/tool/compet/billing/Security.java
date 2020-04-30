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

package tool.compet.billing;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import tool.compet.core.util.DkLogs;

import static tool.compet.core.util.DkObjects.isEmpty;

class Security {
	private static final String KEY_FACTORY_ALGORITHM = "RSA";
	private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

	static boolean verifyPurchase(String base64PublicKey, String signedData, String signature) {
		if (isEmpty(signedData) || isEmpty(base64PublicKey) || isEmpty(signature)) {
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
			DkLogs.logex(Security.class, e);
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
			DkLogs.logex(Security.class, e);
		}
		
		return false;
	}
}
