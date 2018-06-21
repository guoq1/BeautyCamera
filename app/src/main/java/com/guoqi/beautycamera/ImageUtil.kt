package com.guoqi.beautycamera

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object ImageUtil {

    val APK_FILE = "BeautyCamera"

    /**
     * 保存图片为JPEG
     *
     * @param bitmap
     * @param path
     */
    fun saveJPGE(bitmap: Bitmap, path: String, quality: Int): Boolean {
        val file = File(path)
        val tempPath = File(file.parent)
        if (!tempPath.exists()) {
            tempPath.mkdirs()
        }
        try {
            val out = FileOutputStream(file)
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush()
                out.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

}