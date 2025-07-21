package com.xai.dosify.feature.iap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class IapViewModel @Inject constructor(
    private val billingClient: BillingClient
) : ViewModel() {

    val isPremium: Flow<Boolean> = callbackFlow {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                    ) { purchasesResult: BillingResult, purchases: List<Purchase> ->
                        if (purchasesResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            trySend(purchases.any { it.isAutoRenewing })
                        } else {
                            trySend(false)
                        }
                    }
                } else {
                    trySend(false)
                }
            }
            override fun onBillingServiceDisconnected() {
                // Retry or close
            }
        })
        awaitClose { billingClient.endConnection() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}