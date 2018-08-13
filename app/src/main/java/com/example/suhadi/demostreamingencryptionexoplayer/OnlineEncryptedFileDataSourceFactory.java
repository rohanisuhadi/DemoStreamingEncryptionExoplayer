package com.example.suhadi.demostreamingencryptionexoplayer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.HttpDataSource.BaseFactory;
import com.google.android.exoplayer2.upstream.TransferListener;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.CacheControl;
import okhttp3.Call;

public class OnlineEncryptedFileDataSourceFactory extends BaseFactory {

    @NonNull
    private final Call.Factory callFactory;
    @Nullable
    private final String userAgent;
    @Nullable
    private final TransferListener<? super DataSource> listener;
    @Nullable
    private final CacheControl cacheControl;
    private Cipher mCipher;
    private SecretKeySpec mSecretKeySpec;
    private IvParameterSpec mIvParameterSpec;

    /**
     * @param callFactory A {@link Call.Factory} (typically an {@link okhttp3.OkHttpClient}) for use
     *     by the sources created by the factory.
     * @param userAgent An optional User-Agent string.
     * @param listener An optional listener.
     */
    public OnlineEncryptedFileDataSourceFactory(
            Cipher cipher,
            SecretKeySpec secretKeySpec,
            IvParameterSpec ivParameterSpec,
            @NonNull Call.Factory callFactory,
            @Nullable String userAgent,
            @Nullable TransferListener<? super DataSource> listener) {
        this(cipher,secretKeySpec,ivParameterSpec,callFactory, userAgent, listener, null);
    }

    /**
     * @param callFactory A {@link Call.Factory} (typically an {@link okhttp3.OkHttpClient}) for use
     *     by the sources created by the factory.
     * @param userAgent An optional User-Agent string.
     * @param listener An optional listener.
     * @param cacheControl An optional {@link CacheControl} for setting the Cache-Control header.
     */
    public OnlineEncryptedFileDataSourceFactory(
            Cipher cipher, SecretKeySpec secretKeySpec, IvParameterSpec ivParameterSpec,
            @NonNull Call.Factory callFactory,
            @Nullable String userAgent,
            @Nullable TransferListener<? super DataSource> listener,
            @Nullable CacheControl cacheControl) {

        this.mCipher = cipher;
        this.mSecretKeySpec = secretKeySpec;
        this.mIvParameterSpec = ivParameterSpec;
        this.callFactory = callFactory;
        this.userAgent = userAgent;
        this.listener = listener;
        this.cacheControl = cacheControl;
    }

    @Override
    protected OnlineEncryptedFileDataSource createDataSourceInternal(HttpDataSource.RequestProperties defaultRequestProperties) {
        return new OnlineEncryptedFileDataSource(mCipher,mSecretKeySpec,mIvParameterSpec,callFactory, userAgent, null, listener, cacheControl,
                defaultRequestProperties);
    }
}
