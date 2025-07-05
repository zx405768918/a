package com.example.qq.ui
// Compose 基础

// accompanist pager（记得依赖）

// Coil 图片

// Media3（ExoPlayer 用新版依赖）

// ActivityResult/文件/Uri/Intent
// 网络/JSON/日志
// 支持slot自定义
// 而不是
// 只能改颜色

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ArrowBack
import okhttp3.RequestBody.Companion.toRequestBody
import androidx.compose.foundation.lazy.LazyRow


import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import coil.compose.AsyncImage

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.math.max
import coil.request.ImageRequest
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Chat          // 评论
import androidx.compose.material.icons.filled.Star          // 已收藏
import androidx.compose.material.icons.outlined.StarBorder  // 未收藏
         // Dialog 全屏需要
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.SolidColor

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.text.style.TextAlign
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.window.DialogProperties      // ← 把这一行加上！
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import okhttp3.FormBody
import android.os.Handler
import android.os.Looper
import com.example.qq.ui.VideoItemCard
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.background

import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.material.icons.filled.*

import coil.compose.AsyncImage

import org.json.JSONArray



private val uiThread = Handler(Looper.getMainLooper())
private val client = OkHttpClient()                  // ← loginUser / registerUser 共用






// 评论实体
data class Comment(
    val id: Int,
    val userName: String,
    val avatar: String,
    val content: String,
    val time: String,
    val likecount: Int = 0,
    val subtotal: Int = 0,
    val subcomments: List<Comment> = emptyList(),
    val imgs: List<String> = emptyList(),



    val usertag: String = "", // 用户标签，如"VIP"等



    val location: String = "", // 地理位置

    val isliked: Boolean = false,

    val replies: List<Comment> = emptyList()

)
data class User(
    val id: Int,
    val email: String? = null,
    val avatar: String? = null,
    val signature: String? = null,
    val city: String? = null,
    val hometown: String? = null,
    val name: String?      = null
)
// 视频实体
data class VideoItem(
    val id: Int = 0,            // 增加默认值，兼容不同调用
    val url: String = "",
    var isliked: Boolean = false,
    var likecount: Int = 0,
    var ishidden: Boolean = false,
    var iscollected: Boolean = false,
    var comments: MutableList<Comment> = mutableListOf(),
    val title: String = "",
      val description: String = "",
)



private val NAV_LABELS = listOf("首页", "推荐", "上传", "聊天", "我")
private val defaultVideos = listOf<VideoItem>(
    //VideoItem("https://www.w3school.com.cn/example/html5/mov_bbb.mp4", false, 56),
    //VideoItem("https://media.w3.org/2010/05/sintel/trailer.mp4", false, 120),
    //VideoItem("https://www.w3schools.com/html/movie.mp4", false, 56),
    //VideoItem("https://sf1-cdn-tos.huoshanstatic.com/obj/media-fe/xgplayer_doc_video/mp4/xgplayer-demo-360p.mp4", false, 56),
    //VideoItem("https://vjs.zencdn.net/v/oceans.mp4", false, 56),
    //VideoItem("https://thesanche.org/c2.mp4", false, 56)
)

data class PlaybackState(val position: Long = 0L, val duration: Long = 1L)

@OptIn(ExperimentalFoundationApi::class)

@Composable
fun VideoFeed() {
    val ctx = LocalContext.current
    var commentTarget by remember { mutableStateOf<VideoItem?>(null) }
    var showEditProfile by remember { mutableStateOf(false) }
    var avatarUrl by remember { mutableStateOf("") }
    var isLoggedIn by rememberSaveable { mutableStateOf(false) }
    var userName by remember { mutableStateOf("用户名") }
    var signature by remember { mutableStateOf("签名...") }
    var address by remember { mutableStateOf("此处显示地址") }
    var currentTab by remember { mutableStateOf(0) }
    var videoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var myVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var playingUrl by remember { mutableStateOf<String?>(null) }
    var currentPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var playback by remember { mutableStateOf(PlaybackState()) }
    var registerMessage by remember { mutableStateOf("") }
    //var hiddenVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

    //var favorites by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    var myUploadedVideos by remember { mutableStateOf(mutableListOf<VideoItem>()) }
    //var favorites by remember { mutableStateOf(mutableListOf<VideoItem>()) }
    //var favorites by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
    //var favorites by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
   //val favorites = remember { mutableStateListOf<VideoItem>() }
    val favorites = remember { mutableStateListOf<VideoItem>() }

    var currentUserId by rememberSaveable { mutableStateOf(-1) }
    var showRegister by remember { mutableStateOf(false) }
    // 只在 currentTab、videoList 变化时重新排序
    val displayList = when (currentTab) {
        0 -> videoList
        1 -> myUploadedVideos
        2 -> favorites
        else -> videoList
    }



    val realCount = displayList.size
    val virtualCount = 10000
    val initialPage = virtualCount / 2

    val favoriteThumbs = remember { mutableStateListOf<String>() }
    val pagerState = rememberPagerState(initialPage = initialPage)

    LaunchedEffect(Unit) {
        fetchVideoList { videos -> videoList = videos }

    }
    LaunchedEffect(currentTab) {
        if (currentTab == 2) {
            fetchFavorites(currentUserId) { favs ->
                favorites.clear()
                favorites.addAll(favs)
                // 同步 videoList 里所有已收藏状态
                videoList = videoList.map { v ->
                    v.copy(iscollected = favs.any { it.url == v.url })
                }
            }
        }
    }
    // 切tab时跳回中间
    LaunchedEffect(currentTab, realCount) {
        pagerState.scrollToPage(initialPage)
        fetchVideoList { videos -> videoList = videos }
    }
    if (isLoggedIn) {
        LaunchedEffect(userName) {
            // 你的登录接口返回的userId要保存起来，不能只用用户名
            fetchMyVideos(currentUserId) { list -> myVideoList = list }
        }
    }

    Column(Modifier.fillMaxSize()) {
        if (currentTab == 0 || currentTab == 1) {
            if (displayList.isEmpty()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("暂无视频，快来上传吧", color = Color.Gray)
                }
            } else {
                VerticalPager(
                    count = virtualCount,
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    key = { page -> displayList[page % realCount].url }
                ) { page ->
                    val realPage = page % realCount
                    val item = displayList[realPage]
                    VideoPlayerCore(
                        videoItem = item,
                        onComment = { commentTarget = item } ,  // ★ 触发,
                        isActive = (pagerState.currentPage == page),
                        onLike = { /* 点赞 */ },
                        currentUserId = currentUserId,      // 添加这行
                        favorites = favorites,
                        onPlayerReady = { player -> if (pagerState.currentPage == page) currentPlayer = player },
                        onProgress = { pos, dur -> if (pagerState.currentPage == page) playback = PlaybackState(pos, dur) },
                        onCollect = {
                            toggleCollect(item, currentUserId) {
                                // 收藏后立刻拉收藏列表
                                fetchFavorites(currentUserId) { favs ->
                                    favorites.clear()
                                    favorites.addAll(favs)
                                    // 同步 videoList 里所有已收藏状态
                                    videoList = videoList.map { v ->
                                        v.copy(iscollected = favs.any { it.url == v.url })
                                    }
                                }
                            }
                        }
                    )
                }
                // ★ 弹出评论面板
                commentTarget?.let { v ->
                    CommentsSheet(
                        video = v,
                        currentUid = currentUserId,
                        onDismiss = { commentTarget = null }
                    )
                }
            }
            PlayerProgressBar(position = playback.position,
                duration = playback.duration,
                onSeek = { pos -> currentPlayer?.seekTo(pos) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x66000000))
                    .padding(bottom = 4.dp))
        } else if (currentTab == 2) {
            if (!isLoggedIn) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(), contentAlignment = Alignment.Center
                ) {
                    Text("请先登录才能上传视频", color = Color.Red)
                }
            } else {
                UploadVideoScreen(
                    currentUserId = currentUserId,
                    onVideoSelected = { uri ->
                        val newItem = VideoItem(url = uri, isliked = false, likecount = 0)
                        myVideoList = listOf(newItem) + myVideoList
                        videoList = listOf(newItem) + videoList
                        //fetchVideoList { videos -> videoList = videos }
                        currentTab = 0
                    }

                )
            }
        } else if (currentTab == 3) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text("功能开发中：${NAV_LABELS[currentTab]}")
            }
        } else if (currentTab == 4) {
            if (isLoggedIn) {
                UserProfileScreen(
                    avatarUrl  = avatarUrl,          // 头像 URL
                    userName   = userName,
                    bannerUrl  = "",
                    myVideoList = myVideoList,// 顶图 URL
                    location   = "深圳市",
                    onMyVideoListChange = { myVideoList = it },
                    hometown   = "江西省",
                    currentCity= "广州",
                    onBackClick = { currentTab = 0 },  // 切回首页
                    //onTabChange = { tabIdx -> /* … */ },
                    favorites = favorites,
                    signature  = signature,
                    //favoriteThumbs = favorites.map { it.url },
                    onEditClick = { showEditProfile = true },
                    favoriteThumbs =  favorites.map { it.url },
                            videoThumbs =  myVideoList.map { it.url } ,    // ← 这里写 listOf(...),
                            currentUserId = currentUserId,
                    onTabChange = { tabIdx ->
                        if (tabIdx == 1) { // 收藏tab
                            fetchFavorites(currentUserId) { favs ->
                                favorites.clear()
                                favorites.addAll(favs)
                                // 同步 videoList 里所有已收藏状态
                                videoList = videoList.map { v ->
                                    v.copy(iscollected = favs.any { it.url == v.url })
                                }
                            }
                        }
                        // 可选：videoTab/动态tab时分别拉
                    }


                )
                if (showEditProfile) {
                    EditProfileDialog(
                        userId = currentUserId,
                        initAvatar = avatarUrl,      // 头像初始值
                        initName   = userName,       // 名字初始值
                        initSig    = signature,      // 个签初始值
                        onClose = { showEditProfile = false },
                        onSaved = { newUser ->
                            // 保存成功后刷新本地 UI
                            avatarUrl = newUser.avatar ?: avatarUrl
                            userName = newUser.name ?: userName     // 这里改成 name
                            signature = newUser.signature ?: signature
                            showEditProfile = false
                        }

                    )
                }
                LaunchedEffect(currentUserId) {
                    fetchFavorites(currentUserId) { favs ->
                        favorites.clear()
                        favorites.addAll(favs)
                    }
                }
            } else {
                var hiddenVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }

                var showRegister by remember { mutableStateOf(false) }
                if (showRegister) {
                    RegisterScreen(
                        onRegisterSuccess = {
                            registerMessage = "注册成功，请登录";showRegister = false
                        },   // 注册成功，回到登录界面
                        onBack = { showRegister = false }
                    )
                } else {
                    LoginRegisterScreen(
                        registerMessage = registerMessage,
                        onGotoRegister  = {  showRegister = true    },
                        onLoginSuccess  = { user ->
                            currentTab     = 0
                            isLoggedIn     = true
                            currentUserId  = user.id

                            // 这里才有 avatarUrl / signature / userName 的作用域
                            avatarUrl = user.avatar ?: ""
                            signature = user.signature ?: ""
                            userName  = user.name ?: user.email ?: ""

                            favorites.clear()
                            fetchFavorites(user.id) { favs ->
                                favorites.clear(); favorites.addAll(favs)
                            }
                        }
                    )
                }
            }
        } else {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxWidth(), contentAlignment = Alignment.Center
            ) {
                Text("功能开发中：${NAV_LABELS[currentTab]}")
            }
        }

        BottomNavBar(currentTab = currentTab, onTabSelected = { currentTab = it })
    }
}


@Composable
fun VideoPlayerCore(
    videoItem: VideoItem,
    isActive: Boolean,
    favorites: MutableList<VideoItem>,
    currentUserId: Int,                      // 新增
    //favorites: MutableList<VideoItem>,
    onLike: () -> Unit,
    onComment: () -> Unit = {},          // ← 新增：点评论
    onCollect: () -> Unit = {},
    onPlayerReady: (ExoPlayer) -> Unit = {},
    onProgress: (Long, Long) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    // **优化：ExoPlayer 实例和 videoItem.url 绑定，切视频自动释放**
    val exoPlayer = remember(videoItem.url) {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            setMediaItem(MediaItem.fromUri(Uri.parse(videoItem.url)))
            prepare()
        }
    }

    // **优化：只在 isActive 切换时启动协程，释放多余刷新**
    LaunchedEffect(isActive) {
        exoPlayer.playWhenReady = isActive
        if (isActive) onPlayerReady(exoPlayer)
    }
    LaunchedEffect(isActive) {
        while (isActive) {
            onProgress(exoPlayer.currentPosition, max(exoPlayer.duration, 1L))
            delay(300)
        }
    }
    var paused by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(isActive) {
                detectTapGestures { if (isActive) exoPlayer.playWhenReady = !exoPlayer.isPlaying }
            }
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    useController = false
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        // 点赞按钮
        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 80.dp),   // ← bottom=80dp 让整列上移
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
// 每次 favorites 变动都会触发重组
            val isCollected by remember(favorites) {
                derivedStateOf { favorites.any { it.url == videoItem.url } }
            }
            // —— 收藏 ——  （最上）

            Button(
                onClick = {
                    if (currentUserId <= 0) {
                        Toast.makeText(context, "请先登录", Toast.LENGTH_SHORT).show()
                    } else {
                        toggleCollect(videoItem, currentUserId) {
                            fetchFavorites(currentUserId) { favs ->
                                favorites.clear()
                                favorites.addAll(favs)
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCollected) Color.Red else Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(width = 64.dp, height = 36.dp)
            ) {

            }
            // —— 评论 ——

            IconButton(                         // ★ 修改
                onClick = onComment             // 把回调传回宿主
            ) {
                Icon(
                     Icons.Filled.Chat,      // ★ 改成 Chat
                     contentDescription = "评论",
                    tint  = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            // —— 点赞 ——  （最下）
            IconButton(onClick = onLike) {
                Icon(
                    imageVector = if (videoItem.isliked)
                        Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "点赞",
                    tint = if (videoItem.isliked) Color.Red else Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
            Text("${videoItem.likecount}", color = Color.White, fontSize = 12.sp)
        }
    }

    DisposableEffect(videoItem.url) { onDispose { exoPlayer.release() } }
}

@Composable
fun UploadVideoScreen(
    currentUserId: Int,
    onVideoSelected: (String) -> Unit
) {
    val context = LocalContext.current
    var uploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // 关键
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploading = true
            scope.launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it)
                    if (inputStream == null) {
                        uploading = false
                        Toast.makeText(context, "无法读取文件", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    val tempFile =
                        File(context.cacheDir, "upload_${System.currentTimeMillis()}.mp4")
                    inputStream.use { input ->
                        tempFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d("UPLOAD", "uri = $it")
                    Log.d("UPLOAD", "tempFile.exists = ${tempFile.exists()}")

                    // 切到IO线程上传
                    val url = withContext(Dispatchers.IO) {
                        suspendCancellableCoroutine<String> { cont ->
                            uploadVideoFile(tempFile, currentUserId) { result ->
                                cont.resume(result, null)
                            }
                        }
                    }
                    uploading = false
                    if (url.isNotEmpty()) {
                        onVideoSelected(url)
                    } else {
                        Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    uploading = false
                    Toast.makeText(context, "文件处理失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val recordLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && videoUri != null) {
            uploading = true
            val file = File(videoUri!!.path!!)
            uploadVideoFile(file, currentUserId) { url ->
                uploading = false
                if (url.isNotEmpty()) {
                    onVideoSelected(url)
                } else {
                    Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { pickLauncher.launch("video/*") }) {
            Text("从本地选择视频")
        }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val file = File(context.externalCacheDir, "video_${System.currentTimeMillis()}.mp4")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            videoUri = uri
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            recordLauncher.launch(intent)
        }) {
            Text("拍摄新视频")
        }

        if (uploading) {
            Spacer(Modifier.height(24.dp))
            CircularProgressIndicator(color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("上传中...", color = Color.White)
        }
    }
}


@Composable
fun PlayerProgressBar(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (duration <= 0L) 0f else position / duration.toFloat()
    var dragging by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableStateOf(progress) }
    var barWidthPx by remember { mutableStateOf(1f) } // 进度条实际像素宽度

    // 监听布局宽度
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .onGloballyPositioned { layoutCoordinates ->
                barWidthPx = layoutCoordinates.size.width.toFloat()
            }
            .pointerInput(duration, barWidthPx) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragging = true
                        dragProgress = (offset.x / barWidthPx).coerceIn(0f, 1f)
                    },
                    onDrag = { change, _ ->
                        val x = change.position.x.coerceIn(0f, barWidthPx)
                        dragProgress = (x / barWidthPx).coerceIn(0f, 1f)
                    },
                    onDragEnd = {
                        dragging = false
                        onSeek((dragProgress * duration).toLong())
                    },
                    onDragCancel = { dragging = false }
                )
            }
            .pointerInput(duration, barWidthPx) {
                detectTapGestures { offset: Offset ->
                    val ratio = (offset.x / barWidthPx).coerceIn(0f, 1f)
                    onSeek((duration * ratio).toLong())
                }
            }
    ) {
        // 背景条
        Box(
            Modifier
                .fillMaxWidth()
                .height(5.dp)
                .offset(y = -10.dp)
                .align(Alignment.CenterStart)
                .background(Color(0x33FFFFFF), CircleShape)
        )
        // 进度条
        Box(
            Modifier
                .height(5.dp)
                .offset(y = -10.dp)
                .width(with(LocalDensity.current) { ((if (dragging) dragProgress else progress) * barWidthPx).toDp() })
                .align(Alignment.CenterStart)
                .background(Color.White, CircleShape)
        )
        // 拖动圆点
        Box(
            Modifier
                .offset(
                    x = with(LocalDensity.current) { (((if (dragging) dragProgress else progress) * barWidthPx) - 8).toDp() }
                )
                .offset(y = -10.dp)
                .size(16.dp)
                .align(Alignment.CenterStart)
                .background(Color.White, CircleShape)
        )
        // 时间文本
        Text(
            text = "${position.toMMSS()} / ${duration.toMMSS()}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 2.dp)
                .offset(y = 3.dp)
        )
    }
}

private fun Long.toMMSS(): String = "%02d:%02d".format(this / 60000, (this % 60000) / 1000)
@Composable
fun BottomNavBar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NAV_LABELS.forEachIndexed { idx, label ->
            TabButton(
                text = label,
                selected = idx == currentTab,
                modifier = Modifier.weight(1f)   // ✅ 现在 weight 处在 RowScope
            ) { onTabSelected(idx) }
        }
    }
}

@Composable
fun LoginRegisterScreen(
    registerMessage: String,

    onGotoRegister: () -> Unit,

    onLoginSuccess: (User) -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (registerMessage.isNotEmpty()) {
            Text(
                text = registerMessage,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        Text("用户登录", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱", color = Color.White) },
            singleLine = true,
            // 这里不要 visualTransformation
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFF8F7FFF),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码", color = Color.White) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFF8F7FFF),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(Modifier.height(24.dp))
        Row {
            Button(onClick = {
                message = ""
                loginUser(email, password) { ok, user, msg ->
                    if (ok && user != null) {
                        message = "Login successful"
                        onLoginSuccess(user)            // 交给外层处理
                    } else {
                        message = msg
                    }
                }
            }) { Text("登录") }
            Spacer(Modifier.width(24.dp))
            Button(onClick = onGotoRegister) {
                Text("注册")
            }
        }
        Spacer(Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(message, color = if (message == "Login successful") Color.Green else Color.Red)
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("手机号注册", color = Color.White, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("邮箱", color = Color.White) },
            singleLine = true,
            // 这里不要 visualTransformation
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFF8F7FFF),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码", color = Color.White) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color.White,
                focusedBorderColor = Color(0xFF8F7FFF),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                loading = true
                message = ""
                registerUser(email, password) { result ->
                    loading = false
                    if (result.code == 200) {

                        onRegisterSuccess()
                    } else {
                        message = result.msg ?: "注册失败"
                    }
                }
            },
            enabled = !loading
        ) {
            Text(if (loading) "注册中..." else "注册")
        }
        Spacer(Modifier.height(16.dp))
        if (message.isNotEmpty()) {
            Text(message, color = if (message == "注册成功") Color.Green else Color.Red)
        }
        // 可选：返回登录按钮
        TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
            Text("返回登录", color = Color.Gray)
        }
    }
}

fun loginUser(
    email: String,
    password: String,
    callback: (ok: Boolean, user: User?, msg: String) -> Unit
) {
    // 1️⃣ 组织 JSON 请求体
    val json = JSONObject()
        .put("email", email)
        .put("password", password)
        .toString()

    val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())

    // 2️⃣ 构造请求
    val req = Request.Builder()
        .url("https://thesanche.org/api/auth/login")
        .post(body)
        .build()

    // 3️⃣ 异步发送
    client.newCall(req).enqueue(object : Callback {

        override fun onFailure(call: Call, e: IOException) {
            callback(false, null, e.message ?: "网络错误")
        }

        override fun onResponse(call: Call, response: Response) {
            response.use { resp ->
                val txt = resp.body?.string().orEmpty()
                try {
                    val obj = JSONObject(txt)
                    if (obj.optInt("code") == 200) {
                        val u = obj.getJSONObject("user")
                        val user = User(
                            id        = u.getInt("id"),
                            name      = u.optString("name",      null),
                            email     = u.optString("email",     null),
                            avatar    = u.optString("avatar",    null),
                            signature = u.optString("signature", null),
                            city      = u.optString("city",      null),
                            hometown  = u.optString("hometown",  null)
                        )
                        callback(true, user, "登录成功")
                    } else {
                        callback(false, null, obj.optString("msg", "登录失败"))
                    }
                } catch (e: Exception) {
                    callback(false, null, "解析失败: ${e.message}")
                }
            }
        }
    })
}

// 这里需要你实际对接 CloudBase 的注册云函数
// 伪代码，实际项目请用 SDK 或 Retrofit 调用 HTTP 接口
data class ResultData(val code: Int, val msg: String?)

fun registerUser(email: String, password: String, callback: (ResultData) -> Unit) {
    val url = "https://thesanche.org/api/auth/register"

    val client = OkHttpClient()
    val json = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()
    val mediaType = "application/json; charset=utf-8".toMediaType()
    val body = RequestBody.create(mediaType, json)
    val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(ResultData(-1, "Network error"))
        }

        override fun onResponse(call: Call, response: Response) {
            val bodyStr = response.body?.string() ?: ""
            Log.d("API", "API 返回内容: $bodyStr")
            try {
                val json = JSONObject(bodyStr)
                val code = json.optInt("code", -1)
                val msg = json.optString("msg", "未知错误")
                if (code == 200) {
                    callback(ResultData(200, "注册成功"))
                } else {
                    callback(ResultData(code, msg))
                }
            } catch (e: Exception) {
                callback(ResultData(-1, "解析错误：" + e.message))
            }

        }
    })
}


//private fun Long.toMMSS(): String = "%02d:%02d".format(this / 60000, (this % 60000) / 1000)

data class Dimens(
    val bannerHeight: Dp = 110.dp,                 // 顶部大图高度
    val starBtnSize: Dp = 48.dp,                  // 右上收藏按钮
    val sectionMargin: Dp = 16.dp,                // 各区块左右内边距
    val sectionGap: Dp = 12.dp,                   // 区块之间竖向间隔
    val editBtnGap: Dp = 12.dp,                   // 用户名与“修改”间距
    val infoLabelWidth: Dp = 60.dp,               // 信息行 label 固定宽
    val signatureLines: Int = 3,                  // 签名最大行数
    val tabHeight: Dp = 48.dp,                    // Tab 按钮高度
    val gridThumbGap: Dp = 2.dp,                  // 九宫格间距
    val gridColumns: Int = 4,                     // 九宫格列数
    val gridMinHeight: Dp = 300.dp                // 九宫格最小高度
)

data class Strings(
    val editHint: String = "修改信息",
    val tabs: List<String> = listOf("动态", "收藏", "我的", "隐藏")
)

/* ---------- ② 主屏 ---------- */



    @Composable
    fun UserProfileScreen(
        avatarUrl: String,
        userName: String,
        bannerUrl: String,
        onMyVideoListChange: (List<VideoItem>) -> Unit,
        onBackClick: () -> Unit = {},
        favorites: MutableList<VideoItem>,
        favoriteThumbs: List<String>,
        onTabChange: (Int) -> Unit = {},
        //favoriteThumbs = favorites.map { it.url },
        location: String,
        hometown: String,
        currentUserId: Int,
        signature: String,
        videoThumbs: List<String>,
        currentCity: String,
        // 事件回调
        onStarClick: () -> Unit = {},
        onEditClick: () -> Unit = {},
        tabIndex: Int = 0,
        myVideoList: List<VideoItem>,

        // 可外部注入的尺寸/文案
        dimens: Dimens = Dimens(),
        strings: Strings = Strings()
    ) {
        var previewItem by remember { mutableStateOf<VideoItem?>(null) }
        var hiddenVideoList by remember { mutableStateOf<List<VideoItem>>(emptyList()) }
        val context = LocalContext.current
        val activity = context as? Activity
        //var previewUrl by remember { mutableStateOf<String?>(null) }
        var innerTab by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // ← 添加返回按钮
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "返回"
                )
            }
            /* --- 顶部大图 & 收藏按钮 --- */
            Box {
                // —— 顶部 banner —— //
                Image(
                    painter = rememberAsyncImagePainter(bannerUrl),
                    contentDescription = "Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimens.bannerHeight)
                        .padding(top = dimens.sectionMargin)
                )

                // —— 右上收藏按钮 —— //
                IconButton(
                    onClick = onStarClick,
                    modifier = Modifier
                        .size(dimens.starBtnSize)
                        .align(Alignment.BottomEnd)
                        .padding(end = dimens.sectionMargin, bottom = dimens.sectionGap)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = "收藏",
                        tint = MaterialTheme.colorScheme.primary)
                }

                // ---------- 这里是新增的头像 ---------- //
                Box(
                    modifier = Modifier
                        .size(80.dp)                            // 头像尺寸
                        .align(Alignment.BottomStart)           // 贴在 banner 左下
                        .offset(x = 16.dp, y = 40.dp)           // 微调位置（往下沉一点）
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape) // 白色描边
                        .background(Color(0xFFEFEFEF))          // 占位底色
                        .clickable { onEditClick() }            // 点头像也能编辑
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier
                                .fillMaxSize(0.6f)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            /* --- 用户名 + “修改信息” --- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(
                    start = dimens.sectionMargin,
                    top = dimens.sectionGap
                )
            ) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Spacer(Modifier.width(dimens.editBtnGap))              // ← 间距参数
                TextButton(onClick = onEditClick) {
                    Text(strings.editHint, style = MaterialTheme.typography.labelLarge)
                }
            }

            Divider(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimens.sectionMargin,
                        vertical = dimens.sectionGap
                    )
            )

            /* --- 位置 / 籍贯 / 现居地 --- */
            Column(Modifier.padding(horizontal = dimens.sectionMargin)) {
                InfoRow("位置", location, dimens)
                InfoRow("籍贯", hometown, dimens)
                InfoRow("现居地", currentCity, dimens)
            }

            /* --- 签名 --- */
            OutlinedTextField(
                value = signature,
                onValueChange = {},
                label = { Text("签名") },
                maxLines = dimens.signatureLines,                      // ← 行数参数
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = dimens.sectionMargin,
                        vertical = dimens.sectionGap
                    )
            )

            Divider(Modifier.fillMaxWidth())

            /* --- 顶部 Tabs --- */
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimens.sectionGap),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                strings.tabs.forEachIndexed { i, t ->
                    TabButton(
                        text = t,
                        selected = tabIndex == i,
                        modifier = Modifier
                            .height(dimens.tabHeight)                  // ← 高度参数
                            .weight(1f),                               // 均分宽度
                        //onClick = { onTabChange(i) }
                    ) { innerTab = i }
                }
            }
            //var previewUrl by remember { mutableStateOf<String?>(null) }
            Divider(Modifier.fillMaxWidth())
            when (innerTab) {
                /*** 动态 / 隐藏：原占位即可 ***/
                0 -> ProfileTabContent(strings.tabs[innerTab])
                3 -> { // 隐藏
                    // 只拉一次，或者每次都拉，看你需求
                    LaunchedEffect(currentUserId, innerTab) {
                        if (currentUserId > 0 && innerTab == 3) { // 只有在切到隐藏 tab 时才拉
                            fetchHiddenVideos(currentUserId) { list -> hiddenVideoList = list }
                        }
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(dimens.gridColumns),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(dimens.gridThumbGap),
                        horizontalArrangement = Arrangement.spacedBy(dimens.gridThumbGap),
                        verticalArrangement = Arrangement.spacedBy(dimens.gridThumbGap)
                    ) {
                        items(hiddenVideoList) { item ->
                            Box {
                                /* 缩略图本身 */
                                VideoThumbnail(
                                    url = item.url,
                                    onClick = { previewItem = item }     // 仍可点进全屏
                                )

                                /* 红色“取消隐藏”按钮 —— 右上角 */

                            }
                        }
                    }
                }

                // 收藏/九宫格可滚动
                1 -> LazyVerticalGrid(
                    columns = GridCells.Fixed(dimens.gridColumns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(dimens.gridThumbGap),
                    horizontalArrangement = Arrangement.spacedBy(dimens.gridThumbGap),
                    verticalArrangement = Arrangement.spacedBy(dimens.gridThumbGap)
                ) {
                    items(favorites) { item ->
                        VideoThumbnail(
                            url = item.url,
                            onClick = { previewItem = item }
                        )
                    }
                }

                // ← “我的”页，只显示自己上传的视频
                2 -> LazyVerticalGrid(
                    columns = GridCells.Fixed(dimens.gridColumns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(dimens.gridThumbGap),
                    horizontalArrangement = Arrangement.spacedBy(dimens.gridThumbGap),
                    verticalArrangement = Arrangement.spacedBy(dimens.gridThumbGap)
                ) {
                    items(myVideoList) { item ->

                        VideoThumbnail(url = item.url, onClick = {
                            Log.d("PROFILE", "Clicked video url: ${item.url}")
                            previewItem = item
                        })



                    }
                }

                // 如果你要展示收藏列表，用 favorites:
                // items(favorites) { item ->
                //     VideoThumbnail(url = item.url, onClick = { previewUrl = item.url })
                // }
            }

            if (previewItem != null) {
                Text(
                    "当前预览url: ${previewItem!!.url}",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        /* --- 视频九宫格 --- */
        previewItem?.let { item ->
            FullscreenPlayerDialog(
                                 videoItem      = item,
                                currentUserId  = currentUserId,
                                 favorites      = favorites,
                                 onDismiss      = { previewItem = null },

                                 // ① 点“隐藏”/“取消隐藏”
                                  onHide = if (innerTab == 1) null else { video ->
                                        fetchMyVideos(currentUserId)   { onMyVideoListChange(it) }
                                          fetchHiddenVideos(currentUserId){ hiddenVideoList = it }
                                      },

                                // ② 发送评论
                                 onSendComment = { txt, imgs ->
                                        sendComment(item, currentUserId, txt, imgs) {            // 发送后刷新
                                                  fetchComments(item){ /* 列表已在 video.comments 里 */ }
                                            }
                                       },

                                    // ③ 点赞 / 取消赞
                                   onToggleLike = {
                                       toggleLike(item, currentUserId)
                                   }
                                     )
        }

    }
/* ---------- ③ 复用小组件 ---------- */

// 单行信息
@Composable
private fun InfoRow(label: String, value: String, dimens: Dimens) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "$label：",
            modifier = Modifier.width(dimens.infoLabelWidth),      // ← label 固定宽
            fontWeight = FontWeight.Medium
        )
        Text(value, color = Color.Gray, maxLines = 1)
    }
}

// Tab 按钮
@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = if (selected)
                MaterialTheme.colorScheme.primary else Color.Gray
        )
    ) { Text(text) }
}

// 视频缩略图
@Composable
private fun VideoThumbnail(
    url: String,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray)
            .clickable { onClick() }           // 点击回调
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build()
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center)
        )
    }
}


// Tab 内容占位
@Composable
fun ProfileTabContent(text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = Color.Gray)
    }
}

    fun uploadVideoFile(file: File, userId: Int, callback: (String) -> Unit) {
        val client = OkHttpClient()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody("video/mp4".toMediaType()))
            .addFormDataPart("userid", userId.toString())   // 关键！
            .build()

        val request = Request.Builder()
            .url("https://thesanche.org/api/video/upload")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback("")
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string() ?: ""
                try {
                    val url = JSONObject(bodyStr).optString("url", "")
                    callback(url)
                } catch (e: Exception) {
                    callback("")
                }
            }
        })
    }

fun fetchVideoList(callback: (List<VideoItem>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://thesanche.org/api/video/list")
        .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            uiThread.post { callback(emptyList()) }
        }
        override fun onResponse(call: Call, response: Response) {
            val bodyStr = response.body?.string() ?: ""
            try {
                val jsonArr = JSONObject(bodyStr).optJSONArray("list")
                val list = mutableListOf<VideoItem>()
                if (jsonArr != null) {
                    for (i in 0 until jsonArr.length()) {
                        val obj = jsonArr.getJSONObject(i)
                        list.add(
                            VideoItem(
                                id          = obj.optInt("id"),
                                url         = obj.optString("url"),
                                title       = obj.optString("title"),
                                description = obj.optString("description"),
                                iscollected = false,
                                likecount   = obj.optInt("likecount", 0)
                            )
                        )
                    }
                }
                uiThread.post { callback(list) }
            } catch (e: Exception) {
                uiThread.post { callback(emptyList()) }
            }
        }
    })
}

    fun fetchMyVideos(userId: Int, callback: (List<VideoItem>) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://thesanche.org/api/video/my?userid=$userId")

            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(emptyList())
            }

            override fun onResponse(call: Call, response: Response) {
                val bodyStr = response.body?.string() ?: ""
                try {
                    val jsonArr = JSONObject(bodyStr).optJSONArray("list")
                    val list = mutableListOf<VideoItem>()
                    if (jsonArr != null) {
                        for (i in 0 until jsonArr.length()) {
                            val obj = jsonArr.getJSONObject(i)
                            list.add(VideoItem(id = obj.optInt("id"),
                                url = obj.optString("url"),
                                title = obj.optString("title"),
                                description = obj.optString("description")))
                        }
                    }
                    uiThread.post { callback(list) }
                } catch (e: Exception) {
                    uiThread.post { callback(emptyList()) }
                }
            }
        })
    }

@Composable
private fun FullscreenPlayerDialog(

    videoItem: VideoItem,
    currentUserId: Int,
    favorites: MutableList<VideoItem>,
    onDismiss: () -> Unit,
    onHide: ((VideoItem) -> Unit)? = null,
    onSendComment: (String, List<String>) -> Unit = { _, _ -> },
    onToggleLike: () -> Unit = {}
) {

    var showComments by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var localHidden by remember { mutableStateOf(videoItem.ishidden) }
    LaunchedEffect(videoItem.url) {
        fetchComments(videoItem) { comments = videoItem.comments }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding()
        ) {
            /* —— 播放器 —— */
            VideoPlayerCore(
                videoItem      = videoItem,
                isActive       = true,
                onLike         = onToggleLike,
                currentUserId  = currentUserId,
                favorites      = favorites,
                onComment      = { showComments = true }
            )

            /* —— 关闭按钮 —— */
            IconButton(
                onClick = onDismiss,                       // 只关闭 Dialog
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)     // 右上角最外侧
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "关闭",
                    tint = Color.White
                )
            }
            if (onHide != null) {
                IconButton(
                    onClick = {
                        val targetHide = !localHidden            // 取反
                        setVideoHidden(videoItem.id, currentUserId, targetHide) { ok ->
                            if (ok) {
                                videoItem.ishidden = targetHide  // 更新模型
                                localHidden = targetHide         // 立即换色
                                onHide?.invoke(videoItem)        // 交给外层刷新列表
                            } else {
                                Toast.makeText(context, "操作失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 16.dp, end = 72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.VisibilityOff,
                        contentDescription = if (localHidden) "取消隐藏" else "隐藏",
                        tint = if (localHidden) Color.Red else Color.Yellow   // 红=已隐藏
                    )
                }
            }
            /* —— 底部评论栏 —— */
            if (showComments) {
                CommentsSheet(                    // ← 同样改名
                    video        = videoItem,
                    currentUid   = currentUserId,
                    onDismiss    = { showComments = false }
                )
            }
        }
    }
}


suspend fun uploadImage(
    context: Context,
    uri: Uri
): String = withContext(Dispatchers.IO) {

    val file = context.contentResolver.openInputStream(uri)!!.use { input ->
        val tmp = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
        tmp.outputStream().use { output -> input.copyTo(output) }
        tmp
    }

    val reqBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "file",
            file.name,
            file.asRequestBody("image/jpeg".toMediaType())
        )
        .build()

    val req = Request.Builder()
        .url("https://thesanche.org/api/upload/image")
        .post(reqBody)
        .build()

    client.newCall(req).execute().use { resp ->
        JSONObject(resp.body!!.string()).optString("url", "")
    }
}

// 发送评论
fun postCommentToServer(ctx: Context, videoUrl: String, content: String) {
    // TODO 这里写真正的网络请求，下面仅示范本地 Toast
    Toast.makeText(ctx, "已发表：$content", Toast.LENGTH_SHORT).show()
}

// 点赞/取消点赞
fun toggleLike(video: VideoItem, userId: Int, onOk: () -> Unit = {}) {
    OkHttpClient().newCall(
        Request.Builder()
            .url("https://thesanche.org/api/video/like")
            .post(
                FormBody.Builder()
                    .add("video", video.url)
                    .add("uid", userId.toString())
                    .build()
            )
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) { onOk() }
        override fun onResponse(call: Call, resp: Response) {
            if (resp.isSuccessful) {
                video.isliked = !video.isliked
                if (video.isliked) video.likecount++ else if (video.likecount > 0) video.likecount--
            }
            onOk()
        }
    })
    Log.d("LIKE", "video.url=${video.url}, userId=$userId")
}
fun sendComment(
    video: VideoItem,
    uid:   Int,
    text:  String,
    imgs:  List<String>,
    cb:    (Boolean) -> Unit
) {
    val json = JSONObject()
        .put("video",   video.url)
        .put("uid",     uid)
        .put("content", text)
        .put("imgs",    JSONArray(imgs))
        .toString()

    val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
    val req  = Request.Builder()
        .url("https://thesanche.org/api/comment/add")
        .post(body)
        .build()

    client.newCall(req).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) = cb(false)
        override fun onResponse(call: Call, res: Response) {
            cb(res.isSuccessful)
        }
    })
}

fun fetchComments(video: VideoItem, onDone: () -> Unit = {}) {
    OkHttpClient().newCall(
        Request.Builder()
            .url("https://thesanche.org/api/comment/list?video=${Uri.encode(video.url)}")
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) = onDone()

        override fun onResponse(call: Call, resp: Response) {
            val arr  = JSONObject(resp.body!!.string()).getJSONArray("list")
            val list = mutableListOf<Comment>()

            for (i in 0 until arr.length()) {
                val o        = arr.getJSONObject(i)               // ← 先拿当前对象
                val imgList  = mutableListOf<String>()            // 准备装图片

                val imgsArr = o.optJSONArray("imgs")              // 解析 imgs 数组
                for (j in 0 until (imgsArr?.length() ?: 0)) {
                    imgList += imgsArr!!.optString(j)
                }

                list += Comment(
                    id       = o.optInt("id"),
                    userName = o.optString("userName"),
                    avatar   = o.optString("avatar"),
                    content  = o.optString("content"),
                    time     = o.optString("createdat"),
                    imgs     = imgList                            // ★ 带图评论
                )
            }

            video.comments = list
            onDone()
        }
    })
}

fun toggleCollect(video: VideoItem, userId: Int, onOk: () -> Unit = {}) {
    OkHttpClient().newCall(
        Request.Builder()
            .url("https://thesanche.org/api/favorite/toggle")
            .post(
                FormBody.Builder()
                    .add("video", video.url)
                    .add("uid", userId.toString())
                    .build()
            )
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {}
        override fun onResponse(call: Call, resp: Response) {
            video.iscollected = !video.iscollected
            onOk()
        }
    })
    Log.d("收藏调试", "video.url=${video.url}, userId=$userId")

}
fun fetchFavorites(userId: Int, callback: (List<VideoItem>) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://thesanche.org/api/video/fav?userid=$userId")
        .build()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(emptyList())
        }

        override fun onResponse(call: Call, response: Response) {
            val bodyStr = response.body?.string() ?: ""
            try {
                val jsonArr = JSONObject(bodyStr).optJSONArray("list")
                val list = mutableListOf<VideoItem>()
                if (jsonArr != null) {
                    for (i in 0 until jsonArr.length()) {
                        val obj = jsonArr.getJSONObject(i)
                        list.add(VideoItem(url = obj.getString("url"), iscollected = true))
                    }
                }
                uiThread.post { callback(list) }
            } catch (e: Exception) {
                uiThread.post { callback(emptyList()) }
            }
        }
    })
}
@Composable
fun VideoItemCard(video: VideoItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = video.url)
        Text(text = "Likes: ${video.likecount}")
        // 可根据需要添加缩略图、收藏按钮等
    }
}
fun setVideoHidden(videoId: Int, userId: Int, hide: Boolean, onResult: (Boolean) -> Unit) {
    Log.d("隐藏调试", "videoId=$videoId, userId=$userId, hide=$hide")
    val url = "https://thesanche.org/api/video/hide"
    val body = FormBody.Builder()
        .add("videoid", videoId.toString())
        .add("userid", userId.toString())
        .add("hide", if (hide) "1" else "0")
        .build()
    OkHttpClient().newCall(
        Request.Builder().url(url).post(body).build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("隐藏调试", "网络错误: ${e.message}")
            uiThread.post { onResult(false) }
        }

        override fun onResponse(call: Call, resp: Response) {
            val bodyStr = resp.body?.string() ?: ""
            Log.d("隐藏调试", "响应: $bodyStr")
            uiThread.post { onResult(resp.isSuccessful) }
        }
    })
}
fun fetchHiddenVideos(userId: Int, callback: (List<VideoItem>) -> Unit) {
    val url = "https://thesanche.org/api/video/hidden?userid=$userId"
    OkHttpClient().newCall(
        Request.Builder().url(url).build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) { callback(emptyList()) }
        override fun onResponse(call: Call, response: Response) {
            val bodyStr = response.body?.string() ?: ""
            try {
                val jsonArr = JSONObject(bodyStr).optJSONArray("list")
                val list = mutableListOf<VideoItem>()
                if (jsonArr != null) {
                    for (i in 0 until jsonArr.length()) {
                        val obj = jsonArr.getJSONObject(i)
                        list.add(
                            VideoItem(
                                id = obj.optInt("id"),
                                url = obj.optString("url"),
                                title = obj.optString("title"),
                                description = obj.optString("description"),
                                ishidden    = true
                                // ... 其它字段
                            )
                        )
                    }
                }
                uiThread.post { callback(list) }
            } catch (e: Exception) { uiThread.post { callback(emptyList()) } }
        }
    })
}
@Composable
fun EditProfileDialog(
    userId: Int,
    initAvatar: String,
    initName: String,
    initSig: String,
    onClose: () -> Unit,
    onSaved: (User) -> Unit
) {
    var name by remember { mutableStateOf(initName) }
    var sig  by remember { mutableStateOf(initSig) }

    // ① 头像 URL 用 String 就够了
    var curAvatarUrl by remember { mutableStateOf(initAvatar) }
    var avatarUri    by remember { mutableStateOf<Uri?>(null) }

    val ctx = LocalContext.current

    // ② 选图 launcher —— 在回调里同步更新本地 state
    val pickImg = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        avatarUri    = uri
        curAvatarUrl = uri?.toString() ?: curAvatarUrl
    }

    Dialog(onDismissRequest = onClose) {
        Surface(shape = RoundedCornerShape(12.dp)) {
            Column(Modifier.padding(20.dp)) {

                /* 头像选择 */
                Box(
                    Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.LightGray, CircleShape)   // ① 加个灰色描边
                        .clickable { pickImg.launch("image/*") }
                        .background(Color(0xFFEFEFEF))                // ② 没头像时给个底色
                ) {
                    if (curAvatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = curAvatarUrl,
                            contentDescription = null,
                            modifier = Modifier.matchParentSize()
                        )
                    } else {
                        Icon(                                     // ③ 放个占位图标
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.fillMaxSize(0.6f)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("昵称") })
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = sig,  onValueChange = { sig  = it }, label = { Text("签名") })

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    TextButton(onClick = onClose) { Text("取消") }
                    Spacer(Modifier.width(8.dp))

                    Button(
                        onClick = {                // ← 只有这一层 lambda 给 Button
                            updateProfile(
                                userId      = userId,
                                avatarFile  = avatarUri?.let { copyUriToTempFile(ctx, it) },
                                name        = name,
                                signature   = sig,
                                city        = ""
                            ) { ok, msg ->         // ← 这是 updateProfile 的回调，不影响 Button
                                if (ok) {
                                    onSaved(
                                        User(
                                            id        = userId,
                                            name      = name,
                                            avatar    = curAvatarUrl,
                                            signature = sig
                                        )
                                    )
                                } else {
                                    Toast.makeText(ctx, msg ?: "保存失败", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}


fun updateProfile(
    userId: Int,
    avatarFile: File?,             // null = 不改头像
    name: String,
    signature: String,
    city: String,
    onDone: (Boolean, String?) -> Unit
) {
    val client = OkHttpClient()
    val body = MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("userid", userId.toString())
        .addFormDataPart("name", name)
        .addFormDataPart("signature", signature)
        .addFormDataPart("city", city)
    if (avatarFile != null) {
        body.addFormDataPart(
            "avatar", avatarFile.name,
            avatarFile.asRequestBody("image/*".toMediaType())
        )
    }
    client.newCall(
        Request.Builder()
            .url("https://thesanche.org/api/user/update")
            .post(body.build())
            .build()
    ).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) =
            onDone(false, "网络错误")
        override fun onResponse(call: Call, resp: Response) {
            val ok = resp.isSuccessful && JSONObject(resp.body!!.string()).optInt("code") == 200
            onDone(ok, if (ok) null else "保存失败")
        }
    })
}
fun copyUriToTempFile(ctx: Context, uri: Uri): File {
    val input = ctx.contentResolver.openInputStream(uri)!!
    val file  = File(ctx.cacheDir, "avatar_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { output -> input.copyTo(output) }
    return file
}

@Composable
fun CommentItem(
    comment: Comment,
    onReply: (Comment) -> Unit = {},
    onLike: (Comment) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 头像
            AsyncImage(
                model = comment.avatar,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // 用户名和标签
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.userName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF576B95)
                    )
                    // 可添加用户标签
                    if (comment.usertag.isNotBlank()) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = comment.usertag,
                            fontSize = 11.sp,
                            color = Color(0xFF999999),
                            modifier = Modifier
                                .background(
                                    Color(0xFFF0F0F0),
                                    RoundedCornerShape(2.dp)
                                )
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }

                // 评论内容
                if (comment.content.isNotBlank()) {
                    Text(
                        text = comment.content,
                        fontSize = 15.sp,
                        color = Color(0xFF111111),
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 20.sp
                    )
                }

                // 时间和地点
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = comment.time,
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                    if (comment.location.isNotBlank()) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = comment.location,
                            fontSize = 12.sp,
                            color = Color(0xFF999999)
                        )
                    }
                    Spacer(Modifier.weight(1f))

                    // 右侧操作按钮
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 回复按钮
                        Text(
                            text = "回复",
                            fontSize = 12.sp,
                            color = Color(0xFF999999),
                            modifier = Modifier.clickable { onReply(comment) }
                        )

                        // 点赞
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { onLike(comment) }
                        ) {
                            Icon(
                                imageVector = if (comment.isliked) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                tint = if (comment.isliked) Color(0xFFFF6B6B) else Color(0xFF999999),
                                modifier = Modifier.size(16.dp)
                            )
                            if (comment.likecount > 0) {
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "${comment.likecount}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF999999)
                                )
                            }
                        }

                        // 更多按钮
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // 展开回复
                if (comment.subtotal > 0) {
                    Text(
                        text = "展开${comment.subtotal}条回复 ∨",
                        fontSize = 12.sp,
                        color = Color(0xFF576B95),
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { onReply(comment) }
                    )
                }
            }
        }
    }
}

/**
 * 评论列表
 */
@Composable
fun CommentsList(
    list: List<Comment>,
    onReply: (Comment) -> Unit = {},
    onLike: (Comment) -> Unit = {},
    onSendComment: (String) -> Unit = {},
    onSelectImage: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // 标题栏
        Surface(
            color = Color.White,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "3.1万条评论",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF111111)
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF999999),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* 添加评论 */ }
                )
                Spacer(Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = Color(0xFF999999),
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { /* 分享 */ }
                )
            }
        }

        // 评论列表
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(list) { comment ->
                CommentItem(comment, onReply, onLike)
                Divider(
                    color = Color(0xFFF0F0F0),
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }


    }
}
/**
 * 底部评论输入框
 */







/* ──────────────────────────────── 评论弹层 ──────────────────────────────── */

@Composable
fun CommentsSheet(
    video: VideoItem,
    currentUid: Int,
    onDismiss: () -> Unit,
    onSent: () -> Unit = {}
) {
    var refreshing by remember { mutableStateOf(false) }

    /* 拉取评论 */
    val refresh = {
        refreshing = true
        fetchComments(video) { refreshing = false }
    }

    LaunchedEffect(video.id) { refresh() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        windowInsets = WindowInsets(0.dp)
    ) {
        /* ─── 标题栏 ─── */
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("全部评论 · ${video.comments.size.formatCount()}", fontWeight = FontWeight.SemiBold)
        }

        /* ─── 列表 ─── */
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 60.dp)     // 预留输入栏高度
        ) {
            items(video.comments) { c -> CommentCell(c) }
        }

        /* ─── 输入栏 ─── */
        CommentInputBar(
            placeholder = "善语结善缘，恶言伤人心",
            onSend = { text, imgs ->
                sendComment(video, currentUid, text, imgs) {
                    refresh()
                    onSent()
                }
            }
        )
    }
}
/* ────────────────────────── 输入栏（多图） ────────────────────────── */

@Composable
fun CommentInputBar(
    placeholder: String = "善语结善缘，恶言伤人心",
    onSend: (String, List<String>) -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var text by remember { mutableStateOf("") }
    val imgs = remember { mutableStateListOf<String>() }

    val pickImgs = rememberLauncherForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            scope.launch {
                val url = uploadImage(ctx, uri)        // ← 你自己的封装
                if (url.isNotBlank()) imgs += url
            }
        }
    }

    Column(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .background(Color.White)
    ) {
        if (imgs.isNotEmpty()) {
            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp),
                Arrangement.spacedBy(6.dp)
            ) {
                imgs.forEachIndexed { idx, url ->
                    Box {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { imgs.removeAt(idx) }
                        )
                    }
                }
            }
        }

        Row(
            Modifier
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 36.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(18.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                cursorBrush = SolidColor(Color.Black),
                decorationBox = { inner ->
                    if (text.isEmpty()) {
                        Text(placeholder, color = Color(0xFFAAAAAA), fontSize = 14.sp)
                    }
                    inner()
                }
            )

            IconButton(onClick = { pickImgs.launch("image/*") }) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray)
            }
            IconButton(
                enabled = text.isNotBlank() || imgs.isNotEmpty(),
                onClick = {
                    onSend(text.trim(), imgs.toList())
                    text = ""; imgs.clear()
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = null,
                    tint = if (text.isNotBlank() || imgs.isNotEmpty())
                        MaterialTheme.colorScheme.primary
                    else Color.Gray
                )
            }
        }
    }
}

/* ────────────────────────── 单条评论 cell ────────────────────────── */

@Composable
private fun CommentCell(c: Comment) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = c.avatar,
                contentDescription = null,
                modifier = Modifier.size(36.dp).clip(CircleShape)
            )
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(c.userName, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(c.time, color = Color.Gray, fontSize = 11.sp)
            }
            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.Gray)
            Spacer(Modifier.width(2.dp))
            Text(c.likecount.formatCount(), color = Color.Gray, fontSize = 11.sp)
        }

        if (c.content.isNotBlank()) {
            Text(
                c.content,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 44.dp, top = 4.dp, end = 8.dp)
            )
        }

        if (c.imgs.isNotEmpty()) {
            Row(
                Modifier
                    .padding(start = 44.dp, top = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                Arrangement.spacedBy(6.dp)
            ) {
                c.imgs.forEach { url ->
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).clip(RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}


/* ---------- 小工具 ---------- */
private fun Int.formatCount(): String = when {
    this >= 10000 -> " %.1f万".format(this / 10000f)
    else          -> toString()
}
