package com.xai.dosify.feature.iap.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IapModule {

    @Provides
    @Singleton
    fun provideBillingClient(@ApplicationContext context: Context): BillingClient =
        BillingClient.newBuilder(context)
            .setListener(object : PurchasesUpdatedListener {  // Fix: Use object expression
                override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
                    // Handle purchase updates
                }
            })
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enablePrepaidPlans().build())
            .build()
}