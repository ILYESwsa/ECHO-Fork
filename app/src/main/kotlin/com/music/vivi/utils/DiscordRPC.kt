package iad1tya.echo.music.utils

import android.content.Context
import com.dead8309.kizzyrpc.KizzyRPC
import com.dead8309.kizzyrpc.model.Activity
import com.dead8309.kizzyrpc.model.Assets
import com.dead8309.kizzyrpc.model.Metadata
import com.dead8309.kizzyrpc.model.Timestamps
import iad1tya.echo.music.db.entities.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscordRPC(
    val context: Context,
    token: String,
) {
    private val kizzy = KizzyRPC(token)
    private var running = false

    fun isRpcRunning(): Boolean = running

    fun closeRPC() {
        if (running) {
            kizzy.closeRPC()
            running = false
        }
    }

    /**
     * Update Discord Rich Presence with the currently playing song.
     *
     * @param song                    The current Song entity
     * @param currentPlaybackTimeMillis  Current playback position in ms
     * @param playbackSpeed           Playback speed multiplier (used to compute correct end time)
     * @param useDetails              If true, show song title in Details line; otherwise show in State
     * @param status                  Discord status: "online" | "idle" | "dnd"
     * @param button1Text             Label for first button (empty = hidden)
     * @param button1Visible          Whether button 1 is shown
     * @param button2Text             Label for second button (empty = hidden)
     * @param button2Visible          Whether button 2 is shown
     * @param activityType            "listening" | "playing" | "watching" | "competing" | "streaming"
     * @param activityName            Custom activity name override (empty = use default)
     */
    suspend fun updateSong(
        song: Song,
        currentPlaybackTimeMillis: Long,
        playbackSpeed: Float = 1.0f,
        useDetails: Boolean = false,
        status: String = "online",
        button1Text: String = "",
        button1Visible: Boolean = true,
        button2Text: String = "",
        button2Visible: Boolean = true,
        activityType: String = "listening",
        activityName: String = "",
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val now = System.currentTimeMillis()
            val speed = if (playbackSpeed <= 0f) 1.0f else playbackSpeed

            // Compute timestamps based on actual playback position
            val startTime = now - (currentPlaybackTimeMillis / speed).toLong()
            val duration = song.song.duration * 1000L   // duration is stored in seconds
            val endTime = if (duration > 0L) {
                startTime + (duration / speed).toLong()
            } else null

            val artistName = song.artists.joinToString(", ") { it.name }
                .replace(" - Topic", "")
                .ifBlank { "Unknown Artist" }

            val songTitle = song.song.title.ifBlank { "Unknown" }
            val thumbnailUrl = song.song.thumbnailUrl

            // Details line = title, State line = artist  (or reversed if useDetails = false)
            val detailsLine = if (useDetails) songTitle else artistName
            val stateLine  = if (useDetails) artistName else songTitle

            // Activity type int: 0=Playing, 1=Streaming, 2=Listening, 3=Watching, 5=Competing
            val typeInt = when (activityType.lowercase()) {
                "playing"    -> 0
                "streaming"  -> 1
                "listening"  -> 2
                "watching"   -> 3
                "competing"  -> 5
                else         -> 2
            }

            val name = activityName.ifBlank {
                when (typeInt) {
                    0 -> "Echo Music"
                    2 -> "Music"
                    else -> "Echo Music"
                }
            }

            // Build buttons list (only non-blank visible ones)
            val buttons = buildList {
                if (button1Visible && button1Text.isNotBlank()) add(button1Text)
                if (button2Visible && button2Text.isNotBlank()) add(button2Text)
            }

            // Button URLs — YouTube Music deep link for this song
            val ytmUrl = "https://music.youtube.com/watch?v=${song.id}"
            val buttonUrls = buildList {
                if (button1Visible && button1Text.isNotBlank()) add(ytmUrl)
                if (button2Visible && button2Text.isNotBlank()) add(ytmUrl)
            }

            val largeImage = if (!thumbnailUrl.isNullOrBlank()) {
                // Discord supports external images via "mp:" prefix with attachment style,
                // but KizzyRPC handles external URLs directly as largeImage.
                thumbnailUrl
            } else {
                "echo_music_logo"   // fallback — upload this asset in Discord dev portal
            }

            kizzy.setActivity(
                activity = Activity(
                    name = name,
                    details = detailsLine,
                    state = stateLine,
                    type = typeInt,
                    timestamps = Timestamps(
                        start = startTime,
                        end = endTime,
                    ),
                    assets = Assets(
                        largeImage = largeImage,
                        largeText = songTitle,
                        smallImage = "echo_music_logo",
                        smallText = "Echo Music",
                    ),
                    buttons = buttons.ifEmpty { null },
                    metadata = if (buttonUrls.isNotEmpty()) {
                        Metadata(buttonUrls)
                    } else null,
                ),
                status = status,
                since = now,
            )
            running = true
        }
    }

    fun close() {
        closeRPC()
    }

    companion object {
        /**
         * Resolve template variables inside user-defined button text.
         * Supported: {title}, {artist}, {album}
         */
        fun resolveVariables(text: String, song: Song): String {
            val artist = song.artists.joinToString(", ") { it.name }
                .replace(" - Topic", "")
            val album  = song.album?.title ?: ""
            return text
                .replace("{title}",  song.song.title)
                .replace("{artist}", artist)
                .replace("{album}",  album)
        }
    }
}
