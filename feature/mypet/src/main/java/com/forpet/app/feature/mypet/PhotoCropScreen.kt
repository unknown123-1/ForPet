package com.forpet.app.feature.mypet

import android.app.Activity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.forpet.app.core.designsystem.theme.White

@Composable
fun PhotoCropScreen(
    onPhotoSaved: (String) -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: PhotoCropViewModel = hiltViewModel(),
) {
    val savedFilePath by viewModel.savedFilePath.collectAsStateWithLifecycle()

    LaunchedEffect(savedFilePath) {
        savedFilePath?.let { path -> onPhotoSaved(path) }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            val wasLightStatusBars = controller.isAppearanceLightStatusBars
            val wasLightNavBars = controller.isAppearanceLightNavigationBars
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
            onDispose {
                controller.isAppearanceLightStatusBars = wasLightStatusBars
                controller.isAppearanceLightNavigationBars = wasLightNavBars
            }
        }
    }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    var intrinsicWidth by remember { mutableFloatStateOf(0f) }
    var intrinsicHeight by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val density = LocalDensity.current
        val viewWidthPx = with(density) { maxWidth.toPx() }
        val viewHeightPx = with(density) { maxHeight.toPx() }
        val cropRadiusPx = minOf(viewWidthPx, viewHeightPx) * 0.42f
        val cropDiameter = cropRadiusPx * 2f

        val fitScaleValue by remember {
            derivedStateOf {
                if (intrinsicWidth <= 0f || intrinsicHeight <= 0f) 1f
                else minOf(viewWidthPx / intrinsicWidth, viewHeightPx / intrinsicHeight)
            }
        }

        val minScale by remember {
            derivedStateOf {
                if (intrinsicWidth <= 0f || intrinsicHeight <= 0f) return@derivedStateOf 1f
                val displayW = intrinsicWidth * fitScaleValue
                val displayH = intrinsicHeight * fitScaleValue
                maxOf(cropDiameter / displayW, cropDiameter / displayH).coerceAtLeast(1f)
            }
        }

        /**
         * 주어진 scale에서 offset 허용 범위를 반환.
         *
         * 유도:
         *   이미지 왼쪽 edge (screen) = viewW/2 + offsetX - imgW*totalS/2
         *   이 edge가 cropCenter - cropR 이하여야 함 →
         *   offsetX ≤ (imgW * totalS - cropDiameter) / 2 = maxOX
         */
        fun maxOffsetFor(s: Float): Offset {
            if (intrinsicWidth <= 0f || intrinsicHeight <= 0f) return Offset(1e6f, 1e6f)
            val totalS = fitScaleValue * s
            return Offset(
                x = ((intrinsicWidth * totalS) - cropDiameter).coerceAtLeast(0f) / 2f,
                y = ((intrinsicHeight * totalS) - cropDiameter).coerceAtLeast(0f) / 2f,
            )
        }

        LaunchedEffect(minScale) {
            if (scale < minScale) {
                scale = minScale
                val mo = maxOffsetFor(minScale)
                offset = Offset(
                    offset.x.coerceIn(-mo.x, mo.x),
                    offset.y.coerceIn(-mo.y, mo.y),
                )
            }
        }

        val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
            val newScale = (scale * zoomChange).coerceIn(minScale * 0.75f, 8f)
            val mo = maxOffsetFor(newScale)
            scale = newScale
            offset = Offset(
                (offset.x + offsetChange.x).coerceIn(-mo.x, mo.x),
                (offset.y + offsetChange.y).coerceIn(-mo.y, mo.y),
            )
        }

        LaunchedEffect(transformableState.isTransformInProgress) {
            if (!transformableState.isTransformInProgress && scale < minScale) {
                val startScale = scale
                val startOX = offset.x
                val startOY = offset.y
                val mo = maxOffsetFor(minScale)
                val targetOX = startOX.coerceIn(-mo.x, mo.x)
                val targetOY = startOY.coerceIn(-mo.y, mo.y)

                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium,
                    ),
                ) { progress, _ ->
                    scale = lerp(startScale, minScale, progress)
                    offset = Offset(
                        lerp(startOX, targetOX, progress),
                        lerp(startOY, targetOY, progress),
                    )
                }
            }
        }

        AsyncImage(
            model = viewModel.sourceUri,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            onSuccess = { result: AsyncImagePainter.State.Success ->
                intrinsicWidth = result.result.drawable.intrinsicWidth.toFloat()
                intrinsicHeight = result.result.drawable.intrinsicHeight.toFloat()
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                )
                .transformable(state = transformableState),
        )

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            drawRect(Color.Black.copy(alpha = 0.55f))
            drawCircle(
                color = Color.Black,
                radius = cropRadiusPx,
                blendMode = BlendMode.DstOut,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = cropRadiusPx,
                style = Stroke(width = 2.dp.toPx()),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            TextButton(
                onClick = onNavigateUp,
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Text(text = "취소", color = White, fontSize = 16.sp)
            }
            TextButton(
                onClick = {
                    viewModel.savePhoto(
                        viewWidthPx = viewWidthPx,
                        viewHeightPx = viewHeightPx,
                        cropRadiusPx = cropRadiusPx,
                        scale = scale,
                        offsetX = offset.x,
                        offsetY = offset.y,
                    )
                },
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Text(
                    text = "완료",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PhotoCropScreenPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            TextButton(
                onClick = {},
                modifier = Modifier.align(Alignment.TopStart),
            ) {
                Text(text = "취소", color = White, fontSize = 16.sp)
            }
            TextButton(
                onClick = {},
                modifier = Modifier.align(Alignment.TopEnd),
            ) {
                Text(
                    text = "완료",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen },
        ) {
            val radius = minOf(size.width, size.height) * 0.42f
            drawRect(Color.Black.copy(alpha = 0.55f))
            drawCircle(
                color = Color.Black,
                radius = radius,
                blendMode = BlendMode.DstOut,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = radius,
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}
