package com.xai.dosify.feature.iap.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PendingPurchasesParams
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
    fun provideBillingClient(@ApplicationContext context: Context): BillingClient {
        val pendingParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts() // For one-time IAP like upgrades
            .build()

        return BillingClient.newBuilder(context)
            .enablePendingPurchases(pendingParams)
            .setListener(PurchasesUpdatedListener { billingResult, purchases ->
                // Handle updates; keep existing if any
            })
            .build()
    }
}