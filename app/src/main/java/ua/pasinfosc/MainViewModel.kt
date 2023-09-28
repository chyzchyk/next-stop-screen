package ua.pasinfosc

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.datasource.FileDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ua.pasinfosc.domain.entities.Stop
import ua.pasinfosc.domain.usecases.GetBusIdUseCase
import ua.pasinfosc.domain.usecases.GetFilesAdUseCase
import ua.pasinfosc.domain.usecases.GetTimeForAdUseCase
import ua.pasinfosc.utils.CustomEventLogger
import ua.pasinfosc.utils.pasinfoscLog

class MainViewModel(application: Application) : AndroidViewModel(application), KoinComponent {

    private val getAdVideos: GetFilesAdUseCase by inject()
    private val getBusIdUseCase: GetBusIdUseCase by inject()
    private val getTimeForAdUseCase: GetTimeForAdUseCase by inject()

    private val _routeId = MutableStateFlow<String?>(null)
    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    private val _finalStop = MutableStateFlow<String?>(null)
    private val _busKeyList = MutableStateFlow<List<String>>(emptyList())

    val routeId: StateFlow<String?> get() = _routeId
    val stops: StateFlow<List<Stop>> get() = _stops
    val finalStop: StateFlow<String?> get() = _finalStop

    var playSoundListener: ((String) -> Unit)? = null
    private var lastPlayedSound: String? = null

    private val _adShown = MutableStateFlow(false)
    val adShown: StateFlow<Boolean> get() = _adShown

    private lateinit var adList: List<Uri>
    private var lastAdIndex = -1

    private val playerListener = object : Listener {
        @SuppressLint("SwitchIntDef") // i don't need to handle idle state
        @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_READY -> {
                    _adShown.value = true
                }
                Player.STATE_ENDED -> {
                    _adShown.value = false
                    newAdWaitingJob = viewModelScope.launch {
                        delay(getTimeForAdUseCase())
                        if (isActive) {
                            adPlayer.setMediaSource(
                                ProgressiveMediaSource.Factory(FileDataSource.Factory())
                                    .createMediaSource(
                                        MediaItem.fromUri(
                                            adList[getNewAdIndex()].also(
                                                ::pasinfoscLog
                                            )
                                        )
                                    )
                            )
                            adPlayer.prepare()
                        }
                    }
                }
            }
        }
    }
    val adPlayer: ExoPlayer = ExoPlayer.Builder(application).build().apply {
        playWhenReady = true
        addListener(playerListener)
        addAnalyticsListener(CustomEventLogger())
    }

    private var newAdWaitingJob: Job? = null

    private var alreadyInitialized = false

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun init(context: Context) {
        pasinfoscLog("init")
        if (alreadyInitialized) return
        alreadyInitialized = true

        _routeId.value = ""
        viewModelScope.launch {
            adList = getAdVideos().filter { it.checkIfVideo(context) }

            pasinfoscLog("bus api init")
            BusApi.init(
                context,
                getBusIdUseCase()
            ) { finalStopName, routeId, stops, endless, sound ->
                _finalStop.value = finalStopName
                _routeId.value = routeId
                _stops.value = stops.run {
                    if (endless) stops + stops.subList(
                        fromIndex = 0,
                        toIndex = stops.indexOfFirst {
                            it.state == Stop.State.CURRENT || it.state == Stop.State.NEXT
                        }
                    ) else stops
                }

                pasinfoscLog("sound $sound")
                pasinfoscLog("last sound $lastPlayedSound")
                if (sound != lastPlayedSound && sound.isNotEmpty()) {
                    newAdWaitingJob?.cancel()
                    newAdWaitingJob = null
                    adPlayer.pause()
                    _adShown.value = false
                    newAdWaitingJob = viewModelScope.launch {
                        delay(getTimeForAdUseCase())
                        if (isActive) {
                            _adShown.value = true
                            adPlayer.play()
                        }
                    }

                    lastPlayedSound = sound
                    playSoundListener?.invoke(sound)
                }
            }
//            pasinfoscLog("schedule ad")
//            newAdWaitingJob = viewModelScope.launch {
//                delay(getTimeForAdUseCase().also(::pasinfoscLog))
//                if (isActive && adList.isNotEmpty()) {
//                    adPlayer.setMediaSource(
//                        ProgressiveMediaSource.Factory(FileDataSource.Factory())
//                            .createMediaSource(MediaItem.fromUri(adList[getNewAdIndex()].also(::pasinfoscLog)))
//                    )
//                    adPlayer.prepare()
//                }
//            }
        }
    }

    private fun Uri.checkIfVideo(context: Context): Boolean {
        return if (ContentResolver.SCHEME_CONTENT == scheme) {
            context.contentResolver.getType(this)
        } else {
            MimeTypeMap
                .getSingleton()
                .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(toString()))
        }?.startsWith("video") == true
    }

    private fun getNewAdIndex(): Int {
        lastAdIndex++
        return if (lastAdIndex in adList.indices) lastAdIndex else {
            lastAdIndex = 0; 0
        }
    }
}