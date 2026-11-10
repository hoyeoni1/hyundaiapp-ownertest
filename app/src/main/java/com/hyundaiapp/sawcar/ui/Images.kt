package com.hyundaiapp.sawcar.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.hyundaiapp.sawcar.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** 생성 이미지는 전부 1536×1024 (docs/image-list.md). 화면 비율(≈1.4)에 맞춰 양옆을 조금 잘라 채운다 */
val IMG_SIZE = IntSize(1536, 1024)

/** 상황별 이미지. const로 두지 않는다(비-final R 필드와 섞이면 런타임 NoSuchMethodError, 11-09 실측) */
object Img {
    // 정면: 실내등 × 김 × 전조등
    val F_DARK_FOG100: Int get() = R.drawable.f_dark_fog100
    val F_DARK_FOG75: Int get() = R.drawable.f_dark_fog75
    val F_DARK_FOG50: Int get() = R.drawable.f_dark_fog50
    val F_DARK_FOG25: Int get() = R.drawable.f_dark_fog25
    val F_DARK_FOG0: Int get() = R.drawable.f_dark_fog0
    val F_LIT_FOG100: Int get() = R.drawable.f_lit_fog100
    val F_LIT_FOG75: Int get() = R.drawable.f_lit_fog75
    val F_LIT_FOG50: Int get() = R.drawable.f_lit_fog50
    val F_LIT_FOG25: Int get() = R.drawable.f_lit_fog25
    val F_LIT_FOG0: Int get() = R.drawable.f_lit_fog0
    val F_DARK_BEAM_FOG100: Int get() = R.drawable.f_dark_beam_fog100
    val F_DARK_BEAM_FOG0: Int get() = R.drawable.f_dark_beam_fog0
    val F_LIT_BEAM_FOG100: Int get() = R.drawable.f_lit_beam_fog100
    val F_LIT_BEAM_FOG0: Int get() = R.drawable.f_lit_beam_fog0
    val F_DAWN1: Int get() = R.drawable.f_dawn1
    val F_DAWN2: Int get() = R.drawable.f_dawn2
    val F_DAWN3: Int get() = R.drawable.f_dawn3

    // 왼쪽·오른쪽
    val L_DARK: Int get() = R.drawable.l_dark
    val L_LIT: Int get() = R.drawable.l_lit
    val R_DARK: Int get() = R.drawable.r_dark
    val R_LIT: Int get() = R.drawable.r_lit

    // 핸들 확대
    val S_LIT: Int get() = R.drawable.s_lit
    val S_DARK: Int get() = R.drawable.s_dark
    val S_THERMAL25: Int get() = R.drawable.s_thermal25
    val S_THERMAL50: Int get() = R.drawable.s_thermal50
    val S_THERMAL75: Int get() = R.drawable.s_thermal75
    val S_THERMAL100: Int get() = R.drawable.s_thermal100
    val S_DARK_GLOW: Int get() = R.drawable.s_dark_glow
    val S_DARK_GLOW_FADE: Int get() = R.drawable.s_dark_glow_fade

    // 룸미러 확대
    val M_EMPTY: Int get() = R.drawable.m_empty
    val M_MONSTER1: Int get() = R.drawable.m_monster1
    val M_MONSTER2: Int get() = R.drawable.m_monster2
    val M_MONSTER3: Int get() = R.drawable.m_monster3
    val M_MONSTER4: Int get() = R.drawable.m_monster4
    val M_MONSTER5: Int get() = R.drawable.m_monster5
    val M_RED_EMPTY: Int get() = R.drawable.m_red_empty
    val M_RED_MONSTER1: Int get() = R.drawable.m_red_monster1
    val M_RED_MONSTER2: Int get() = R.drawable.m_red_monster2
    val M_RED_MONSTER3: Int get() = R.drawable.m_red_monster3
    val M_RED_MONSTER4: Int get() = R.drawable.m_red_monster4
    val M_RED_MONSTER5: Int get() = R.drawable.m_red_monster5

    // 사이드미러 확대
    val LM_DARK: Int get() = R.drawable.lm_dark
    val LM_FLASH: Int get() = R.drawable.lm_flash
    val RM_DARK: Int get() = R.drawable.rm_dark

    // 시작·성공
    val TITLE: Int get() = R.drawable.title
    val END_DOOR: Int get() = R.drawable.end_door

    // 단계 묶음(순서대로 섞는다)
    val DARK_FOG: List<Int> get() = listOf(F_DARK_FOG100, F_DARK_FOG75, F_DARK_FOG50, F_DARK_FOG25, F_DARK_FOG0)
    val LIT_FOG: List<Int> get() = listOf(F_LIT_FOG100, F_LIT_FOG75, F_LIT_FOG50, F_LIT_FOG25, F_LIT_FOG0)
    val THERMAL: List<Int> get() = listOf(S_LIT, S_THERMAL25, S_THERMAL50, S_THERMAL75, S_THERMAL100)
    val REAR: List<Int> get() = listOf(M_EMPTY, M_MONSTER1, M_MONSTER2, M_MONSTER3, M_MONSTER4, M_MONSTER5)
    val REAR_RED: List<Int> get() = listOf(M_RED_EMPTY, M_RED_MONSTER1, M_RED_MONSTER2, M_RED_MONSTER3, M_RED_MONSTER4, M_RED_MONSTER5)

    // 미리 읽기 묶음
    val FRONT_ALL: List<Int> get() = DARK_FOG + LIT_FOG + listOf(F_DARK_BEAM_FOG100, F_DARK_BEAM_FOG0, F_LIT_BEAM_FOG100, F_LIT_BEAM_FOG0, F_DAWN1, F_DAWN2, F_DAWN3)
    val STEERING_ALL: List<Int> get() = THERMAL + listOf(S_DARK, S_DARK_GLOW, S_DARK_GLOW_FADE)
    val REAR_ALL: List<Int> get() = REAR + REAR_RED
}

/**
 * 진행률 t(0~1)로 단계 이미지들 사이를 섞는다: 아래 장은 불투명, 다음 장은 t의 소수 부분만큼.
 * 결과는 [ImageStack]에 그대로 준다.
 */
fun seq(ids: List<Int>, t: Float): List<Pair<Int, Float>> {
    if (ids.size == 1) return listOf(ids[0] to 1f)
    val pos = t.coerceIn(0f, 1f) * (ids.size - 1)
    val i = pos.toInt().coerceIn(0, ids.size - 2)
    val f = pos - i
    val out = arrayListOf(ids[i] to 1f)
    if (f > 0.005f) out += ids[i + 1] to f
    return out
}

/** 디코딩한 이미지 캐시. 한 장 6MB(ARGB)라 LRU로 묶고, 시점에 들어갈 때 그 시점의 장들을 미리 읽는다 */
object ImageCache {
    private const val TAG = "SawCarUi"
    private val lru = object : LruCache<Int, ImageBitmap>(budgetBytes()) {
        override fun sizeOf(key: Int, value: ImageBitmap): Int = value.width * value.height * 4
    }
    private val io = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun budgetBytes(): Int {
        val max = Runtime.getRuntime().maxMemory()
        return (max / 2).coerceAtMost(240L * 1024 * 1024).toInt()
    }

    @Synchronized
    fun get(context: Context, id: Int): ImageBitmap {
        lru.get(id)?.let { return it }
        val t0 = System.currentTimeMillis()
        val opts = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
        }
        val bmp = runCatching { BitmapFactory.decodeResource(context.resources, id, opts) }
            .onFailure { Log.e(TAG, "decode failed id=$id", it) }
            .getOrNull()
            ?: Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888).apply { eraseColor(0xFF07090C.toInt()) }
        val img = bmp.asImageBitmap()
        lru.put(id, img)
        val name = runCatching { context.resources.getResourceEntryName(id) }.getOrDefault("$id")
        Log.i(TAG, "img $name ${bmp.width}x${bmp.height} ${System.currentTimeMillis() - t0}ms cache=${lru.size() / 1024 / 1024}MB/${lru.maxSize() / 1024 / 1024}MB")
        return img
    }

    fun prefetch(context: Context, ids: List<Int>) {
        val app = context.applicationContext
        io.launch { ids.forEach { if (lru.get(it) == null) get(app, it) } }
    }
}

/** 이미지 비율 좌표 ↔ 화면 비율 좌표(탭 판정·표시 자리) */
class Mapping(private val src: Rect, private val img: IntSize) {
    fun x(fracX: Float): Float = (fracX * img.width - src.left) / src.width
    fun y(fracY: Float): Float = (fracY * img.height - src.top) / src.height
    fun rect(r: Rect): Rect = Rect(x(r.left), y(r.top), x(r.right), y(r.bottom))
    fun srcX(canvasFracX: Float): Float = (src.left + canvasFracX * src.width) / img.width
    fun srcY(canvasFracY: Float): Float = (src.top + canvasFracY * src.height) / img.height
}

/** 화면을 꽉 채우도록 이미지에서 잘라 올 부분(가운데 맞춤) */
fun coverSrcRect(img: IntSize, canvas: Size): Rect {
    var w = img.width.toFloat()
    var h = w * canvas.height / canvas.width
    if (h > img.height) { h = img.height.toFloat(); w = h * canvas.width / canvas.height }
    val left = (img.width - w) / 2
    val top = (img.height - h) / 2
    return Rect(left, top, left + w, top + h)
}

fun DrawScope.drawCover(image: ImageBitmap, alpha: Float = 1f) {
    val src = coverSrcRect(IntSize(image.width, image.height), size)
    drawImage(
        image = image,
        srcOffset = IntOffset(src.left.roundToInt(), src.top.roundToInt()),
        srcSize = IntSize(src.width.roundToInt().coerceAtLeast(1), src.height.roundToInt().coerceAtLeast(1)),
        dstOffset = IntOffset.Zero,
        dstSize = IntSize(size.width.roundToInt().coerceAtLeast(1), size.height.roundToInt().coerceAtLeast(1)),
        alpha = alpha,
    )
}

private class StackLayer(val id: Int, val alpha: Animatable<Float, AnimationVector1D>)

/**
 * 이미지 겹침. `targets`는 아래부터 위로 (이미지, 알파). 첫 장(바닥)은 바로 나타나고, 나머지는 알파를 향해 움직이며,
 * 목록에서 빠진 장은 맨 위에서 서서히 사라진다 → 상태가 바뀔 때마다 자연스러운 크로스페이드.
 */
@Composable
fun ImageStack(targets: List<Pair<Int, Float>>, modifier: Modifier = Modifier, fadeOutMs: Int = 450, fadeInMs: Int = 300) {
    val context = LocalContext.current
    val layers = remember { mutableStateListOf<StackLayer>() }
    LaunchedEffect(targets, fadeOutMs, fadeInMs) {
        val want = LinkedHashMap<Int, Float>()
        targets.forEach { (id, a) -> want[id] = a.coerceIn(0f, 1f) }
        val baseId = targets.firstOrNull()?.first
        want.keys.forEach { id ->
            if (layers.none { it.id == id }) layers.add(StackLayer(id, Animatable(if (id == baseId) 1f else 0f)))
        }
        val ordered = want.keys.mapNotNull { k -> layers.firstOrNull { it.id == k } } + layers.filter { it.id !in want }
        if (ordered != layers.toList()) { layers.clear(); layers.addAll(ordered) }
        ordered.forEach { l ->
            val t = want[l.id] ?: 0f
            launch {
                if (l.alpha.value != t) l.alpha.animateTo(t, tween(if (t == 0f) fadeOutMs else fadeInMs))
                if (t == 0f) layers.remove(l)
            }
        }
    }
    Canvas(modifier.fillMaxSize()) {
        for (l in layers) {
            val a = l.alpha.value
            if (a <= 0.004f) continue
            drawCover(ImageCache.get(context, l.id), a)
        }
    }
}

/** 한 장을 화면에 꽉 채운다(시작·성공 화면 배경) */
@Composable
fun CoverImage(id: Int, modifier: Modifier = Modifier) = ImageStack(listOf(id to 1f), modifier)
