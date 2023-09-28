package ua.pasinfosc

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ua.pasinfosc.domain.entities.Stop
import ua.pasinfosc.ui.theme.PasinfoscTheme
import ua.pasinfosc.utils.hasPermission
import ua.pasinfosc.utils.outputFile
import java.util.*


class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()
    private var soundNotificationPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PasinfoscTheme {
//                val adShown = viewModel.adShown.collectAsState()
//                AdPlayer()
//                if (!adShown.value)
                Content()
            }
        }
        initializePlayer()
        setupWindow()
        initSoundPlayListener()
        setupBootCompletedListener()
    }

    private fun setupBootCompletedListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !Settings.canDrawOverlays(applicationContext)
        ) {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(myIntent)
        }
        registerReceiver(
            BootCompletedReceiver(),
            IntentFilter().apply {
                addCategory("android.intent.category.DEFAULT")
                addAction("android.intent.action.BOOT_COMPLETED")
                addAction("android.intent.action.QUICKBOOT_POWERON")
                addAction("android.intent.action.LOCKED_BOOT_COMPLETED")
                addAction("com.htc.intent.action.QUICKBOOT_POWERON")
            }
        )
    }

    @Composable
    private fun AdPlayer() {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    player = viewModel.adPlayer
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                    )
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    }

    private fun initializePlayer() {
        soundNotificationPlayer = ExoPlayer.Builder(this).build()
    }

    @androidx.media3.common.util.UnstableApi
    public override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    @androidx.media3.common.util.UnstableApi
    public override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    @androidx.media3.common.util.UnstableApi
    public override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    @androidx.media3.common.util.UnstableApi
    public override fun onResume() {
        super.onResume()
        if ((Util.SDK_INT <= 23 || soundNotificationPlayer == null)) {
            initializePlayer()
        }
        checkPermissions()
    }

    private fun releasePlayer() {
        soundNotificationPlayer?.release()
        soundNotificationPlayer = null
    }

    private fun initSoundPlayListener() {
        viewModel.playSoundListener = {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(outputFile(it)))
            soundNotificationPlayer?.also { exoPlayer ->
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = true
                exoPlayer.prepare()
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) viewModel.init(this)
            else startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
        } else {
            if (hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) viewModel.init(this)
            else requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 99)
        }
    }

    private fun setupWindow() {
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).hide(WindowInsetsCompat.Type.systemBars())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    @Composable
    private fun Content() {
        val routeId = viewModel.routeId.collectAsState()
        val stops = viewModel.stops.collectAsState()
        val finalStop = viewModel.finalStop.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    routeId = routeId.value.toString(),
                    finalStopName = if (stops.value.firstOrNull {
                            it.state == Stop.State.CURRENT || it.state == Stop.State.NEXT
                        } == null) {
                        "Пошук маршруту"
                    } else
                        finalStop.value ?: "Пошук маршруту",
                    estimatedTime = -1 // TODO
                )
            },
            content = {
                StopsPreview(stops.value)
            }
        )
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun TopAppBar(
        routeId: String,
        finalStopName: String,
        estimatedTime: Int,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .background(Color.Red)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = routeId,
                fontSize = 48.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(12.dp)
                    .background(Color.White)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = finalStopName,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
                    modifier = Modifier.basicMarquee()
                )
//                Text(
//                    text = "${stringResource(R.string.estimated_time)}: " +
//                            stringResource(R.string.minutes, estimatedTime),
//                    fontSize = 28.sp,
//                    fontWeight = FontWeight.Medium,
//                    color = Color.White,
//                )
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    private fun StopsPreview(stopsArg: List<Stop>) = BoxWithConstraints {
        val tenth = maxHeight / 10
        val stops = remember(stopsArg) {
            println(stopsArg)

            if (stopsArg.firstOrNull { it.state == Stop.State.CURRENT || it.state == Stop.State.NEXT } != null) {
                stopsArg.subList(
                    fromIndex = stopsArg.indexOfFirst {
                        it.state == Stop.State.CURRENT || it.state == Stop.State.NEXT
                    },
                    toIndex = stopsArg.size
                )
            } else emptyList()
        }

        val timelineStartOffset = 96.dp
        val timelineWeight = 16.dp
        val stationCircleRadius = 24.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {
            for (i in 3 downTo 0) stops.getOrNull(i)?.let { stop ->
                Box(
                    modifier = Modifier
                        .height((if (i == 0) 4 else 2) * tenth)
                        .fillMaxWidth()
                ) {
                    if (i == 0) Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .background(Color(0xFFE6E6E6), RoundedCornerShape(28.dp))
                    )
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "", // TODO
                            modifier = Modifier.width(timelineStartOffset),
                            fontSize = 28.sp,
                            color = Color.Black
                        )
                        Box(contentAlignment = Alignment.Center) {
                            // TODO сделать обрывающийся таймлайн с конца
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(timelineWeight)
                                    .padding(top = if (stops.size - i <= 1) tenth * if (i == 0) 2 else 1 else 0.dp)
                                    .background(Color.Black)
                            )
                            Box(
                                modifier = Modifier
                                    .size(stationCircleRadius * 2)
                                    .background(Color.LightGray, CircleShape)
                                    .border(timelineWeight * 0.7f, Color.Black, CircleShape)
                            )
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            if (i == 0) {
                                Text(
                                    text = if (stop.state == Stop.State.CURRENT) {
                                        stringResource(R.string.stop)
                                    } else {
                                        stringResource(R.string.next_stop)
                                    },
                                    fontSize = 28.sp,
                                    color = Color.Black
                                )
                            }
                            Text(
                                text = stop.name,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                maxLines = 1,
                                modifier = Modifier.basicMarquee()
                            )
                        }
                    }
                }
            }
        }
    }
}