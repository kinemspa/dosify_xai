package com.xai.dosify.feature.iap.di

import android.content.Context
import com.android.billingclient.api.BillingClient
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
        return BillingClient.newBuilder(context)
            .enablePendingPurchases() // Enables pending purchases for one-time products
            .enableAutoServiceReconnection() // Recommended for v8+; auto-reconnects if disconnected
            .setListener(PurchasesUpdatedListener { billingResult, purchases ->
                // Handle updates; keep existing if any
            })
            .build()
    }
}