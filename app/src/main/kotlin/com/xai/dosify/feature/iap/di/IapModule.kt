package com.xai.dosify.feature.iap.di

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
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
            .setListener { billingResult: BillingResult, purchases: List<Purchase>? ->
                // Handle purchase updates
            }
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enablePrepaidPlans().build())  // Fix: Add param for 8.0.0
            .build()
}