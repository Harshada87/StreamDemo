package com.example.streamdemo.ui.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.example.streamdemo.data.model.Video
import com.example.streamdemo.data.model.VideoType

fun Video.toMediaItem(authToken: String?): MediaItem {

    // Step 1: Start a new form and write the video's address (the manifest URL)
    val mediaItemBuilder = MediaItem.Builder().setUri(url)

    // Step 2: Fill in the rest depending on the type of video
    when (val videoType = type) {

        // HLS and Live: tell the player "this is an HLS manifest (.m3u8)"
        VideoType.Hls, VideoType.Live -> {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }

        // DASH: tell the player "this is a DASH manifest (.mpd)"
        VideoType.Dash -> {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
        }

        // DRM (Digital Rights Management): DASH manifest + "how to get the key"
        is VideoType.Drm -> {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)

            // Show your ID at the front desk: the login token as a header
            val licenseRequestHeaders: Map<String, String> =
                if (authToken != null) mapOf("Authorization" to "Bearer $authToken")
                else emptyMap()

            // Fill in the DRM section of the form
            val drmConfiguration = MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID) // which lock: Widevine
                .setLicenseUri(videoType.licenseUrl)                                   // where the front desk is
                .setLicenseRequestHeaders(licenseRequestHeaders)                       // your ID
                .build()

            mediaItemBuilder.setDrmConfiguration(drmConfiguration)
        }
    }

    // Step 3: Submit the form
    return mediaItemBuilder.build()
}