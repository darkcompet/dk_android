/*
 * Copyright (c) 2017-2020 DarkCompet. All rights reserved.
 */

package tool.compet.billing;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.ArraySet;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import tool.compet.core.DkLogs;
import tool.compet.core.DkRunner2;

/**
 * Client billing (in-app, subscribe) for app.
 */
public class DkBillingClient implements PurchasesUpdatedListener {
	/**
	 * Caller should set this listener for new purchase which was called via `purchase()` or `subscribe()`.
	 */
	public interface PurchaseListener {
		/**
		 * Callback with list of `active` purchases.
		 * - Purchase: can be in-app or subscribe type.
		 * - Active purchase: item was bought by user and still valid (not expired...).
		 */
		void onPurchasesUpdated(@NonNull List<Purchase> purchases);

		/**
		 * Called when user cancel this purchase
		 */
		void onPurchaseCancelled();

		/**
		 * Called if purchase failed
		 */
		void onPurchaseFailed(int responseCode);

		/**
		 * Called when finished revoke a item. Note that, depends on `responceCode`, revoking status
		 * maybe success or failed.
		 *
		 * @param responseCode in `BillingClient.BillingResponse.*`
		 */
		void onPurchaseRevoked(int responseCode, String purchaseToken);
	}

	// Called when purchase flow finished, we callback to caller at this time
	@Override
	public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchasesList) {
		if (purchaseListener == null) {
			return;
		}
		final int responseCode = billingResult.getResponseCode();
		if (responseCode == BillingClient.BillingResponseCode.OK) {
			List<Purchase> purchases = new ArrayList<>();

			if (purchasesList != null) {
				for (Purchase purchase : purchasesList) {
					if (verifyPurchase(purchase)) {
						purchases.add(purchase);
					}
				}
			}
			purchaseListener.onPurchasesUpdated(purchases);
		}
		else if (responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
			purchaseListener.onPurchaseCancelled();
		}
		else {
			purchaseListener.onPurchaseFailed(responseCode);
		}
	}

	private BillingClient billingClient;
	private final String publicKey;
	private Set<String> revokedTokens;

	// Caller can set this to listen event when purchase finish
	@Nullable private PurchaseListener purchaseListener;

	public DkBillingClient(Context context, String publicKey, @Nullable PurchaseListener purchaseListener) {
		// Setup billing client object
		this.billingClient = BillingClient.newBuilder(context)
			.setListener(this)
			.enablePendingPurchases()
			.build();
		// This key is used for security validation
		this.publicKey = publicKey;
		// Remeber purchase listener from caller
		this.purchaseListener = purchaseListener;
		// Start connection
		this.billingClient.startConnection(new BillingClientStateListener() {
			@Override
			public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
			}

			@Override
			public void onBillingServiceDisconnected() {
			}
		});
	}

	/**
	 * Purchase in-app. When done, we callback at given `purchaseListener`.
	 */
	public void purchase(Activity host, String sku) {
		purchase(host, sku, BillingClient.SkuType.INAPP);
	}

	/**
	 * Subscribe app. When done, we callback at given `purchaseListener`.
	 */
	public void subscribe(Activity host, String sku) {
		purchase(host, sku, BillingClient.SkuType.SUBS);
	}

	/**
	 * Purchase app. When done, we callback at given `purchaseListener`.
	 */
	public void purchase(Activity host, String sku, String skuType) {
		purchase(host, Collections.singletonList(sku), skuType);
	}

	/**
	 * Purchase app. When done, we callback at given `purchaseListener`.
	 */
	public void purchase(Activity host, List<String> skuList, String skuType) {
		executeService(() -> {
			SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder()
				.setSkusList(skuList)
				.setType(skuType)
				.build();

			billingClient.querySkuDetailsAsync(skuDetailsParams, (billingResult, skuDetailsList) -> {
				if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
					for (SkuDetails skuDetails : skuDetailsList) {
						BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
							.setSkuDetails(skuDetails)
							.build();

						// Start purchase flow
						billingClient.launchBillingFlow(host, billingFlowParams);
					}
				}
			});
		});
	}

	/**
	 * Note that, this method does NOT callback at given `purchaseListener`.
	 * You should handle the result from this method directly.
	 * <p></p>
	 * Query active purchases which user bought via in-app. That is, a purchase which
	 * has been `cancelled` or `expired` will NOT be listed in result.
	 */
	public List<Purchase> queryInAppPurchases() {
		Purchase.PurchasesResult result = queryPurchaseHistories(BillingClient.SkuType.INAPP);

		if (result != null && result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			return result.getPurchasesList();
		}

		return null;
	}

	/**
	 * Note that, this method does NOT callback at given `purchaseListener`.
	 * You should handle the result from this method directly.
	 * <p></p>
	 * Query active purchases which user subscribed. That is, a purchase which
	 * has been `cancelled` or `expired` will NOT be listed in result.
	 */
	public List<Purchase> querySubscriptionPurchases() {
		if (!isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)) {
			return null;
		}
		Purchase.PurchasesResult result = queryPurchaseHistories(BillingClient.SkuType.SUBS);
		if (result != null && result.getResponseCode() == BillingClient.BillingResponseCode.OK) {
			return result.getPurchasesList();
		}
		return null;
	}

	/**
	 * Note that, this method does NOT callback at given `purchaseListener`.
	 * You should handle the result from this method directly.
	 * <p></p>
	 * Query all active purchases which user bought `in-app` or `subscribed`. That is, a purchase which
	 * has been `cancelled` or `expired` will not be listed in result.
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
	 * Async query information for sku (product id).
	 * Note that, this method does NOT callback at given `purchaseListener`.
	 *
	 * @param skuType  BillingClient.SkuType.INAPP or BillingClient.SkuType.SUBS
	 * @param skuList  List of sku which you want to know.
	 * @param callback Even though success or fail, callback will return NonNull list of SkuDetail
	 */
	public void querySkuDetailsAsync(String skuType, List<String> skuList, DkRunner2<BillingResult, List<SkuDetails>> callback) {
		SkuDetailsParams params = SkuDetailsParams.newBuilder()
			.setSkusList(skuList)
			.setType(skuType)
			.build();

		billingClient.querySkuDetailsAsync(params, (billingResult, skuDetailsList) -> {
			if (callback != null) {
				callback.run(billingResult, skuDetailsList);
			}
		});
	}

	/**
	 * Async mark the purchase is invalid item (for eg,. it is not needed check anymore).
	 * Result will be callbacked at given `purchaseListener`.
	 */
	public void revokeAsync(String purchaseToken) {
		if (revokedTokens == null) {
			revokedTokens = new ArraySet<String>();
		}
		if (revokedTokens.contains(purchaseToken)) {
			DkLogs.warning(this, "Skip revoke item which has already revoked");
			return;
		}

		revokedTokens.add(purchaseToken);

		executeService(() -> {
			ConsumeParams consumeParams = ConsumeParams.newBuilder()
				.setPurchaseToken(purchaseToken)
				.build();

			billingClient.consumeAsync(consumeParams, (responseCode, retPurchaseToken) -> {
				if (purchaseListener != null) {
					purchaseListener.onPurchaseRevoked(responseCode.getResponseCode(), retPurchaseToken);
				}
			});
		});
	}

	public void consumeAsync() {
//		ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken().build();
//		ConsumeResponseListener consumeListener;
//		billingClient.consumeAsync(consumeParams, consumeListener);
	}

	public void setPurchaseListener(@Nullable PurchaseListener purchaseListener) {
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

	public boolean isFeatureSupported(String feature) {
		return billingClient.isFeatureSupported(feature).getResponseCode() == BillingClient.BillingResponseCode.OK;
	}

	// region: Private

	private void executeService(Runnable command) {
		if (isConnected()) {
			command.run();
			return;
		}
		// Give a chance to execute the service one more time
		billingClient.startConnection(new BillingClientStateListener() {
			@Override
			public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
				if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
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
			DkLogs.warning(this, "Stop query purchase histories since `billingClient` is null");
			return null;
		}
		return billingClient.queryPurchases(skuType);
	}

	private boolean verifyPurchase(Purchase purchase) {
		try {
			return MySecurity.verifyPurchase(publicKey, purchase.getOriginalJson(), purchase.getSignature());
		}
		catch (Exception e) {
			DkLogs.error(this, e);
		}
		return false;
	}

	// endregion: Private
}
