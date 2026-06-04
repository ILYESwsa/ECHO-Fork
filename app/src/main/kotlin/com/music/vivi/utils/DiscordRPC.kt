package iad1tya.echo.music.utils

import android.content.Context
import com.my.kizzyrpc.KizzyRPC
import com.my.kizzyrpc.model.Activity
import com.my.kizzyrpc.model.Assets
import com.my.kizzyrpc.model.Metadata
import com.my.kizzyrpc.model.Timestamps
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

            val startTime = now - (currentPlaybackTimeMillis / speed).toLong()
            val duration = song.song.duration * 1000L
            val endTime = if (duration > 0L) {
                startTime + (duration / speed).toLong()
            } else null

            val artistName = song.artists.joinToString(", ") { it.name }
                .replace(" - Topic", "")
                .ifBlank { "Unknown Artist" }

            val songTitle = song.song.title.ifBlank { "Unknown" }
            val thumbnailUrl = song.song.thumbnailUrl

            val detailsLine = if (useDetails) songTitle else artistName
            val stateLine  = if (useDetails) artistName else songTitle

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
                    else -> "Music"
                }
            }

            val buttons = buildList {
                if (button1Visible && button1Text.isNotBlank()) add(button1Text)
                if (button2Visible && button2Text.isNotBlank()) add(button2Text)
            }

            val ytmUrl = "https://music.youtube.com/watch?v=${song.id}"
            val buttonUrls = buildList {
                if (button1Visible && button1Text.isNotBlank()) add(ytmUrl)
                if (button2Visible && button2Text.isNotBlank()) add(ytmUrl)
            }

            val largeImage = if (!thumbnailUrl.isNullOrBlank()) {
                thumbnailUrl
            } else {
                "echo_music_logo"
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
