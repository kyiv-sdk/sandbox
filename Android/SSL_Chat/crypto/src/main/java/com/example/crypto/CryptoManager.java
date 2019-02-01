package com.example.crypto;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class CryptoManager {
    private static final CryptoManager ourInstance = new CryptoManager();
    private final String KEY_ALIAS = "key_for_password";
    private final String KEY_STORE = "AndroidKeyStore";
    private final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private KeyStore mKeyStore;
    private KeyPairGenerator mKeyPairGenerator;
    private Cipher mCipher;

    public static CryptoManager getInstance() {
        return ourInstance;
    }

    private CryptoManager() {
        this.mKeyStore = null;
        this.mKeyPairGenerator = null;
        this.mCipher = null;

        prepare();
    }

    private boolean prepare(){
        return getKeyStore() && getCipher() && getKey();
    }

    private boolean getKeyStore(){
        if (mKeyStore != null) return true;
        try {
            mKeyStore = KeyStore.getInstance(KEY_STORE);
            mKeyStore.load(null);
            return true;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean getCipher(){
        if (mCipher != null) return true;
        try {
            mCipher = Cipher.getInstance(TRANSFORMATION);
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean getKey(){
        try {
            return mKeyStore.containsAlias(KEY_ALIAS) || generateNewKey();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean getKeyPairGenerator(){
        if (mKeyPairGenerator != null) return true;
        try {
            mKeyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean generateNewKey(){
        if (getKeyPairGenerator()){
            try {
                Log.i("--------MY_LOG--------", "generating new key");
                mKeyPairGenerator.initialize(new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                        .setUserAuthenticationRequired(true)
                        .setUserAuthenticationValidityDurationSeconds(1)
                        .build());
                mKeyPairGenerator.generateKeyPair();
                return true;
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean initCipher(int mode){
        try {
            mKeyStore.load(null);
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    initEncodeCipher();
                    break;
                case Cipher.DECRYPT_MODE:
                    initDecodeCipher();
                    break;
                default:
                    return false;
            }
            return true;
        } catch (InvalidKeyException e) {
            Log.i("--------MY_LOG--------", "invalid key detected");
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }  catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            deleteInvalidKey();
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void deleteInvalidKey() {
        if (getKeyStore()) {
            try {
                mKeyStore.deleteEntry(KEY_ALIAS);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
    }

    private void initDecodeCipher() throws InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        PrivateKey key = (PrivateKey) mKeyStore.getKey(KEY_ALIAS, null);
        mCipher.init(Cipher.DECRYPT_MODE, key);
    }

    protected void initEncodeCipher() throws KeyStoreException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey key = mKeyStore.getCertificate(KEY_ALIAS).getPublicKey();

        PublicKey unrestricted = KeyFactory.getInstance(key.getAlgorithm()).generatePublic(new X509EncodedKeySpec(key.getEncoded()));
        OAEPParameterSpec spec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA1, PSource.PSpecified.DEFAULT);

        mCipher.init(Cipher.ENCRYPT_MODE, unrestricted, spec);
    }

    public String encode(String inputString) {
        try {
            if (prepare() && initCipher(Cipher.ENCRYPT_MODE)) {
                byte[] bytes = mCipher.doFinal(inputString.getBytes());
                return Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    public String decode(String encodedString) {
        try {
            prepare();
            initDecodeCipher();
            byte[] bytes = Base64.decode(encodedString, Base64.NO_WRAP);
            return new String(mCipher.doFinal(bytes));
        } catch (IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            deleteInvalidKey();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }
}
