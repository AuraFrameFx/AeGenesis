package AeGenesis.securecomm.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import AeGenesis.securecomm.crypto.CryptoManager
import AeGenesis.securecomm.keystore.SecureKeyStore
import AeGenesis.securecomm.protocol.SecureChannel
import javax.inject.Singleton

/**
 * Hilt module that provides dependencies for the secure communications module.
 */
@Module
@InstallIn(SingletonComponent::class)
object SecureCommModule {

    @Provides
    @Singleton
    fun provideSecureKeyStore(
        @ApplicationContext context: Context
    ): SecureKeyStore {
        return SecureKeyStore(context)
    }

    @Provides
    @Singleton
    fun provideCryptoManager(
        @ApplicationContext context: Context,
        secureKeyStore: SecureKeyStore
    ): CryptoManager {
        return CryptoManager(context)
    }

    @Provides
    @Singleton
    fun provideSecureChannel(
        cryptoManager: CryptoManager
    ): SecureChannel {
        return SecureChannel(cryptoManager)
    }
}
