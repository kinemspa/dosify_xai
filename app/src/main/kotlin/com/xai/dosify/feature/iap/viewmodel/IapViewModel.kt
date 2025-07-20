package com.xai.dosify.feature.iap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IapViewModel @Inject constructor(
    private val billingClient: BillingClient
) : ViewModel() {

    val isPremium: Flow<Boolean> = flow {
        viewModelScope.launch {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(result: BillingResult) {
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        val queryParams = QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.SUBS)
                            .build()
                        val purchasesResult = billingClient.queryPurchasesAsync(queryParams).await()  // Fix: await in launch
                        val purchases = purchasesResult.purchasesList  // Fix: from PurchasesResult
                        emit(purchases.any { it.isAutoRenewing })
                    } else {
                        emit(false)
                    }
                }
                override fun onBillingServiceDisconnected() {
                    // Retry
                }
            })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
}