/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.suhadi.demostreamingencryptionexoplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.C.ContentType;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.OkHttpClient;

/** Manages the {@link ExoPlayer}, the IMA plugin and all video playback. */
/* package */ final class PlayerManager implements AdsMediaSource.MediaSourceFactory {

  private final DataSource.Factory manifestDataSourceFactory;
  private final DataSource.Factory mediaDataSourceFactory;

  public static final String AES_ALGORITHM = "AES";
  public static final String AES_TRANSFORMATION = "AES/CTR/NoPadding";

  private static final String ENCRYPTED_FILE_NAME = "encrypted_file.mp3";

  private Cipher mCipher;
  private SecretKeySpec mSecretKeySpec;
  private IvParameterSpec mIvParameterSpec;

  private File mEncryptedFile;

  private SimpleExoPlayer player;
  private long contentPosition;
  String kunci = "[B@41c7b890wqert";

  public PlayerManager(Context context) {
    mEncryptedFile = new File(Environment.getExternalStorageDirectory(), ENCRYPTED_FILE_NAME);

    byte[] key = kunci.getBytes();
    byte[] iv = kunci.getBytes();

    mSecretKeySpec = new SecretKeySpec(key, AES_ALGORITHM);
    mIvParameterSpec = new IvParameterSpec(iv);

    try {
      mCipher = Cipher.getInstance(AES_TRANSFORMATION);
      mCipher.init(Cipher.DECRYPT_MODE, mSecretKeySpec, mIvParameterSpec);
    } catch (Exception e) {
      e.printStackTrace();
    }
    manifestDataSourceFactory =
        new DefaultDataSourceFactory(
            context, Util.getUserAgent(context, context.getString(R.string.application_name)));
    mediaDataSourceFactory =
        new DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.getString(R.string.application_name)),
            new DefaultBandwidthMeter());
  }

  public void init(Context context, PlayerView playerView) {
    // Create a default track selector.
    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    TrackSelection.Factory videoTrackSelectionFactory =
        new AdaptiveTrackSelection.Factory(bandwidthMeter);
    TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

    // Create a player instance.
    player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

    // Bind the player to the view.
    playerView.setPlayer(player);

    // This is the MediaSource representing the content media (i.e. not the ad).
    String contentUrl = context.getString(R.string.content_url);
    MediaSource contentMediaSource = buildMediaSource(Uri.parse(contentUrl));

    // Compose the content media source into a new AdsMediaSource with both ads and content.
//    MediaSource mediaSourceWithAds =
//        new AdsMediaSource(
//            contentMediaSource,
//            /* adMediaSourceFactory= */ this,
//            adsLoader,
//            playerView.getOverlayFrameLayout(),
//            /* eventHandler= */ null,
//            /* eventListener= */ null);

//    DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(handler, null);
// http://192.168.0.24/4041_20170315080846.mp3
//    online
//    MediaSource sampleSource = new ExtractorMediaSource(
//            Uri.parse("http://192.168.0.24/4041_20170315080846.mp3"),
//            new OkHttpDataSourceFactory(
//                    new OkHttpClient(),
//                    "Android.ExoPlayer",
//                    null),
//            new DefaultExtractorsFactory(), null, null);
//    player.seekTo(contentPosition);
//    player.prepare(sampleSource);
//    player.setPlayWhenReady(true);

    //    online encrypt
//    "http://192.168.0.24/3824_20161227003359.mp3"
    MediaSource sampleSource = new ExtractorMediaSource(
            Uri.parse("https://storage.googleapis.com/audiobookchapter/3550_20170116235132.mp3"),
            new OnlineEncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec,
                    new OkHttpClient(),
                    "Android.ExoPlayer",
                    null),
            new DefaultExtractorsFactory(), null, null);
    player.seekTo(contentPosition);
    player.prepare(sampleSource);
    player.setPlayWhenReady(true);

    // Prepare the player with the source.
    //offline
//    DataSource.Factory dataSourceFactory = new EncryptedFileDataSourceFactory(mCipher, mSecretKeySpec, mIvParameterSpec, (TransferListener<? super DataSource>) bandwidthMeter);
//    ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
//    try {
//      Uri uri = Uri.fromFile(mEncryptedFile);
//      MediaSource videoSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
//
//      player.seekTo(contentPosition);
//      player.prepare(videoSource);
//      player.setPlayWhenReady(true);
//    } catch (Exception e) {
//      e.printStackTrace();
//    }
  }

  public void reset() {
    if (player != null) {
      contentPosition = player.getContentPosition();
      player.release();
      player = null;
    }
  }

  public void release() {
    if (player != null) {
      player.release();
      player = null;
    }
//    adsLoader.release();
  }

  // AdsMediaSource.MediaSourceFactory implementation.

  @Override
  public MediaSource createMediaSource(Uri uri) {
    return buildMediaSource(uri);
  }

  @Override
  public int[] getSupportedTypes() {
    // IMA does not support Smooth Streaming ads.
    return new int[] {C.TYPE_DASH, C.TYPE_HLS, C.TYPE_OTHER};
  }

  // Internal methods.

  private MediaSource buildMediaSource(Uri uri) {
    @ContentType int type = Util.inferContentType(uri);
    switch (type) {
      case C.TYPE_DASH:
        return new DashMediaSource.Factory(
                new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                manifestDataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_SS:
        return new SsMediaSource.Factory(
                new DefaultSsChunkSource.Factory(mediaDataSourceFactory), manifestDataSourceFactory)
            .createMediaSource(uri);
      case C.TYPE_HLS:
        return new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
      case C.TYPE_OTHER:
        return new ExtractorMediaSource.Factory(mediaDataSourceFactory).createMediaSource(uri);
      default:
        throw new IllegalStateException("Unsupported type: " + type);
    }
  }

}
