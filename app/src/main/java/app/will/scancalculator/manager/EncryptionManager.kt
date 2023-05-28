package app.will.scancalculator.manager

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

class EncryptionManager @Inject constructor() {

    companion object {
        private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
        private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
        private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
        private const val PROVIDER = "AndroidKeyStore"
        private const val KEYSTORE_ALIAS = "secret-key"
        private val CHARSET = charset("UTF-8")
    }

    fun encryptData(payload: String): ByteArray {
        cipher.init(Cipher.ENCRYPT_MODE, getKey())
        return byteArrayOf(cipher.iv.size.toByte()) + cipher.iv + cipher.doFinal(
            payload.toByteArray(
                CHARSET
            )
        )
    }

    fun decryptData(encryptedData: ByteArray): String {
        val ivLength = encryptedData[0]
        val encryptedDataOffset = ivLength + 1
        cipher.init(
            Cipher.DECRYPT_MODE,
            getKey(),
            GCMParameterSpec(128, encryptedData.copyOfRange(1, encryptedDataOffset))
        )
        return cipher.doFinal(encryptedData.copyOfRange(encryptedDataOffset, encryptedData.size))
            .toString(CHARSET)
    }

    private val cipher by lazy {
        Cipher.getInstance(TRANSFORMATION)
    }

    private val keyStore by lazy {
        KeyStore.getInstance(PROVIDER).apply {
            load(null)
        }
    }

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey ?: createKey()
    }

    private fun createKey(): SecretKey {
        return KeyGenerator.getInstance(ALGORITHM).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                ).setBlockModes(BLOCK_MODE).setEncryptionPaddings(PADDING)
                    .setUserAuthenticationRequired(false).setRandomizedEncryptionRequired(true)
                    .build()
            )
        }.generateKey()
    }
}