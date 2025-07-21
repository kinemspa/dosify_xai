package com.xai.dosify.feature.iap.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
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
    fun provideBillingClient(@ApplicationContext context: Context): BillingClient {
        val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
            // Handle updates; keep existing if any
        }

        return BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases() // Enables pending purchases for one-time products
            .build()
    }
}