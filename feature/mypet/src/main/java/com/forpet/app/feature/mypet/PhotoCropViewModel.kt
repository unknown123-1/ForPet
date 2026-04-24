package com.forpet.app.feature.mypet

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.forpet.app.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class PhotoCropViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val route: Route.PhotoCrop = savedStateHandle.toRoute()
    val sourceUri: String = route.uri
    val petId: Long = route.petId

    private val _savedFilePath = MutableStateFlow<String?>(null)
    val savedFilePath: StateFlow<String?> = _savedFilePath.asStateFlow()

    /**
     * 사용자가 조정한 zoom/pan 상태를 기반으로 원형 크롭 영역을 추출하여 저장합니다.
     * EXIF 회전 정보를 반영하여 카메라 촬영 이미지도 올바르게 처리합니다.
     */
    fun savePhoto(
        viewWidthPx: Float,
        viewHeightPx: Float,
        cropRadiusPx: Float,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(sourceUri)

                val rotationDegrees = context.contentResolver.openInputStream(uri)?.use { input ->
                    val exif = ExifInterface(input)
                    when (exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL,
                    )) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                } ?: 0f

                val raw = context.contentResolver.openInputStream(uri)?.use {
                    BitmapFactory.decodeStream(it)
                } ?: return@launch

                val original = if (rotationDegrees != 0f) {
                    val matrix = Matrix().apply { setRotate(rotationDegrees) }
                    Bitmap.createBitmap(raw, 0, 0, raw.width, raw.height, matrix, true)
                } else raw

                val originalWidth = original.width.toFloat()
                val originalHeight = original.height.toFloat()

                val fitScale = minOf(viewWidthPx / originalWidth, viewHeightPx / originalHeight)
                val totalScale = fitScale * scale

                val cropLeft = (-cropRadiusPx - offsetX) / totalScale + originalWidth / 2f
                val cropTop = (-cropRadiusPx - offsetY) / totalScale + originalHeight / 2f
                val cropRight = (cropRadiusPx - offsetX) / totalScale + originalWidth / 2f
                val cropBottom = (cropRadiusPx - offsetY) / totalScale + originalHeight / 2f

                val left = cropLeft.coerceIn(0f, originalWidth).toInt()
                val top = cropTop.coerceIn(0f, originalHeight).toInt()
                val right = cropRight.coerceIn(0f, originalWidth).toInt()
                val bottom = cropBottom.coerceIn(0f, originalHeight).toInt()

                if (right <= left || bottom <= top) return@launch

                val cropped = Bitmap.createBitmap(original, left, top, right - left, bottom - top)
                val output = Bitmap.createScaledBitmap(cropped, OUTPUT_SIZE, OUTPUT_SIZE, true)

                val fileName = if (petId == 0L) "pet_temp_photo.jpg" else "pet_${petId}_photo.jpg"
                val outFile = File(context.filesDir, fileName)
                FileOutputStream(outFile).use { fos ->
                    output.compress(Bitmap.CompressFormat.JPEG, 90, fos)
                }

                _savedFilePath.value = outFile.absolutePath
            } catch (_: Exception) {
            }
        }
    }

    companion object {
        private const val OUTPUT_SIZE = 400
    }
}
