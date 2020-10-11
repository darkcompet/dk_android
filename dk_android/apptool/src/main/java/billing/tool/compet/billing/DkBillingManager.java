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

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import tool.compet.core.type.DkCallback;
import tool.compet.core.util.DkLogs;

public class DkBillingManager implements PurchasesUpdatedListener {
	/**
	 * This is listener when purchase is responsed if #purchase() or #subscribe() called before.
	 * About history purchases fetching, you can call some method like #queryAllPurchases()...
	 * and handle result manually or handle via #PurchaseListener.onPurchasesUpdated() if
	 * you consider history purchases should be acted as newly purchases.
	 */
	public interface PurchaseListener {
		/**
		 * Callback with list of active purchases. Depends on your request before, it can
		 * include in-app or subscription which has been bought by user and still valid (not expired...).
		 */
		void onPurchasesUpdated(@NonNull List<Purchase> purchases);

		/**
		 * Called when user cancel this purchase
		 */
		void onPurchaseCancelled();

		/**
		 * Otherwise called if purchase failed
		 */
		void onPurchaseFailed(int responseCode);

		/**
		 * Called when finished revoke a item. Note that, depends on #responceCode, revoking status
		 * maybe success or failed.
		 *
		 * @param responseCode in BillingClient.BillingResponse.*
		 */
		void onPurchaseRevoked(int responseCode, String purchaseToken);
	}

	private BillingClient billingClient;
	private final String publicKey;
	private PurchaseListener purchaseListener;
	private HashSet<String> revokedTokens;

	public DkBillingManager(Context context, String publicKey, PurchaseListener purchaseListener) {
		this.billingClient = BillingClient.newBuilder(context).setListener(this).build();
		this.publicKey = publicKey;
		this.purchaseListener = purchaseListener;

		startConnection(new BillingClientStateListener() {
			@Override
			public void onBillingSetupFinished(int responseCode) {
			}
			@Override
			public void onBillingServiceDisconnected() {
			}
		});
	}

	public void startConnection(@NonNull BillingClientStateListener clientStateListener) {
		billingClient.startConnection(clientStateListener);
	}

	/**
	 * For in-app purchase.
	 */
	public void purchase(Activity host, String sku) {
		purchase(host, sku, BillingClient.SkuType.INAPP);
	}

	/**
	 * For subscription.
	 */
	public void subscribe(Activity host, String sku) {
		purchase(host, sku, BillingClient.SkuType.SUBS);
	}

	public void purchase(Activity host, String sku, String skuType) {
		purchase(host, sku, skuType, null);
	}

	public void purchase(Activity host, String sku, String skuType, ArrayList<String> oldSkus) {
		executeService(() -> {
			billingClient.launchBillingFlow(host, BillingFlowParams.newBuilder()
				.setSku(sku)
				.setType(skuType)
				.setOldSkus(oldSkus)
				.build());
		});
	}

	@Override
	public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
		if (purchaseListener == null) {
			return;
		}
		if (responseCode == BillingClient.BillingResponse.OK) {
			List<Purchase> list = new ArrayList<>();

			if (purchases != null) {
				for (Purchase p : purchases) {
					if (verifyPurchase(p)) {
						list.add(p);
					}
				}
			}
			purchaseListener.onPurchasesUpdated(list);
		}
		else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {
			purchaseListener.onPurchaseCancelled();
		}
		else {
			purchaseListener.onPurchaseFailed(responseCode);
		}
	}

	/**
	 * For convenience, this method NOT call #onPurchasesUpdated() to update result.
	 * You must handle returned value from this method.
	 * <p></p>
	 * Query active purchases which user bought via in-app. Note that, for purchase which
	 * has been cancelled or expired will not be listed in result.
	 */
	public List<Purchase> queryInAppPurchases() {
		Purchase.PurchasesResult result = queryPurchaseHistories(BillingClient.SkuType.INAPP);

		if (result != null && result.getResponseCode() == BillingClient.BillingResponse.OK) {
			return result.getPurchasesList();
		}

		return null;
	}

	/**
	 * For convenience, this method NOT call #onPurchasesUpdated() to update result.
	 * You must handle returned value from this method.
	 * <p></p>
	 * Query active purchases which user subscribed. Note that, for purchase which
	 * has been cancelled or expired will not be listed in result.
	 */
	public List<Purchase> querySubscriptionPurchases() {
		if (isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS) != BillingClient.BillingResponse.OK) {
			return null;
		}

		Purchase.PurchasesResult result = queryPurchaseHistories(BillingClient.SkuType.SUBS);

		if (result != null && result.getResponseCode() == BillingClient.BillingResponse.OK) {
			return result.getPurchasesList();
		}

		return null;
	}

	/**
	 * For convenience, this method NOT call #onPurchasesUpdated() to update result.
	 * You must handle returned value from this method.
	 * <p></p>
	 * Query all active purchases which user bought in-app or subscribed. Note that, for purchase which
	 * has been cancelled or expired will not be listed in result.
	 */
	public List<Purchase> queryAllPurchases() {
		List<Purchase> purchases = new ArrayList<>();
		List<Purchase> inAppPurchases = queryInAppPurchases();
		List<Purchase> subscriptionPurchases = querySubscriptionPurchases();

		if (inAppPurchases != null) {
			purchases.addAll(inAppPurchases);
		}
		if (subscriptionPurchases != null) {
			purchases.addAll(subscriptionPurchases);
		}

		return purchases;
	}

	/**
	 * Query information for sku (product id).
	 *
	 * @param skuType BillingClient.SkuType.INAPP or BillingClient.SkuType.SUBS
	 * @param skuList List of sku which you want to know.
	 * @param callback Even though success or fail, callback will return NonNull list of SkuDetail
	 */
	public void querySkuDetailsAsync(String skuType, List<String> skuList, DkCallback<List<SkuDetails>> callback) {
		SkuDetailsParams params = SkuDetailsParams.newBuilder()
			.setSkusList(skuList)
			.setType(skuType)
			.build();

		billingClient.querySkuDetailsAsync(params, (responseCode, skuDetailsList) -> {
			if (callback != null) {
				callback.call(skuDetailsList);
			}
		});
	}

	/**
	 * This purchase is not needed anymore, so mark it as invalid item.
	 */
	public void revokeAsync(String purchaseToken) {
		if (revokedTokens == null) {
			revokedTokens = new HashSet<>();
		}
		if (revokedTokens.contains(purchaseToken)) {
			DkLogs.warn(this, "Skip revoke item which has already revoked");
			return;
		}

		revokedTokens.add(purchaseToken);

		executeService(() -> {
			billingClient.consumeAsync(purchaseToken, (responseCode, retPurchaseToken) -> {
				if (purchaseListener != null) {
					purchaseListener.onPurchaseRevoked(responseCode, retPurchaseToken);
				}
			});
		});
	}

	public void setPurchaseListener(PurchaseListener purchaseListener) {
		this.purchaseListener = purchaseListener;
	}

	public boolean isConnected() {
		return billingClient != null && billingClient.isReady();
	}

	public void onDestroy() {
		if (isConnected()) {
			billingClient.endConnection();
			billingClient = null;
		}
	}

	public int isFeatureSupported(String feature) {
		return billingClient.isFeatureSupported(feature);
	}

	private void executeService(Runnable command) {
		if (isConnected()) {
			command.run();
			return;
		}
		// Give a chance to execute the service one more time
		startConnection(new BillingClientStateListener() {
			@Override
			public void onBillingSetupFinished(int responseCode) {
				if (responseCode == BillingClient.BillingResponse.OK) {
					command.run();
				}
			}
			@Override
			public void onBillingServiceDisconnected() {
			}
		});
	}

	private Purchase.PurchasesResult queryPurchaseHistories(String skuType) {
		if (billingClient == null) {
			DkLogs.warn(this, "Stop query purchase histories since billingClient is null");
			return null;
		}
		return billingClient.queryPurchases(skuType);
	}

	private boolean verifyPurchase(Purchase purchase) {
		try {
			return Security.verifyPurchase(publicKey, purchase.getOriginalJson(), purchase.getSignature());
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return false;
	}
}
