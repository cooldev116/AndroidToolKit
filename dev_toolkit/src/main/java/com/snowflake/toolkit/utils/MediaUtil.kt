package com.snowflake.toolkit.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.snowflake.toolkit.manger.ToolKitManager
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

/**
 * @description:媒体工具类
 * @author:Melon
 * @date:2025/9/9
 */
object MediaUtil {

    private val context by lazy {
        ToolKitManager.instance.getContext()
    }

    /**
     * @description 保存图片到相册
     * @param file 要保存到相册图片文件
     * @param displayName 图片名称
     * @return
     * @author Melon
     * @time 2025/9/9 9:57
     */
    fun saveImageToGallery(
        file: File,
        displayName: String,
        relativePath: String = Environment.DIRECTORY_PICTURES,
        mimeType: String = FileUtil.getMimeType(file),
        description: String = ""
    ): Uri? {
        var uri: Uri? = null
        try {
            val bis = BufferedInputStream(FileInputStream(file))
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis())
            values.put(MediaStore.Images.Media.DESCRIPTION, description)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
            } else {
                values.put(
                    MediaStore.MediaColumns.DATA,
                    Environment.getExternalStorageDirectory().path + "/" + relativePath + "/" + displayName
                )
            }
            uri =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val bos = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytes = bis.read(buffer)
                    while (bytes >= 0) {
                        bos.write(buffer, 0, bytes)
                        bos.flush()
                        bytes = bis.read(buffer)
                    }
                    bos.close()
                }
            }
            bis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    /**
     * @description 保存视频到相册
     * @author Melon
     * @time 2025/9/10 16:18
     */
    fun saveVideoToGallery(
        context: Context,
        file: File,
        displayName: String,
        mimeType: String = FileUtil.getMimeType(file),
        relativePath: String = Environment.DIRECTORY_MOVIES,
        description: String = ""
    ): Uri? {
        var uri: Uri? = null
        try {
            val bis = BufferedInputStream(FileInputStream(file))
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000) // 秒级
                put(MediaStore.Video.Media.DESCRIPTION, description)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                    // 可选：保存视频时常和大小，提升媒体库体验
                    put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis())
                    put(MediaStore.Video.Media.IS_PENDING, 1) // 先标记写入中
                } else {
                    put(
                        MediaStore.MediaColumns.DATA,
                        Environment.getExternalStorageDirectory().path + "/" + relativePath + "/" + displayName
                    )
                }
            }

            uri =
                context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val bos = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytes = bis.read(buffer)
                    while (bytes >= 0) {
                        bos.write(buffer, 0, bytes)
                        bytes = bis.read(buffer)
                    }
                    bos.flush()
                    bos.close()
                }

                // Android Q 及以上：写入完成后清除 IS_PENDING
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear()
                    values.put(MediaStore.Video.Media.IS_PENDING, 0)
                    context.contentResolver.update(uri, values, null, null)
                }
            }
            bis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    /**
     * @description 将Bitmap保存到相册
     * @param bitmap 需要保存的Bitmap
     * @param fileName 图片名称
     * @return
     * @author Melon
     * @time 2025/11/13 17:45
     */
    fun saveBitmapToGallery(
        context: Context,
        bitmap: Bitmap,
        fileName: String = "IMG_${System.currentTimeMillis()}.jpg"
    ): Uri? {
        val resolver = context.contentResolver
        var uri: Uri? = null
        val outputStream: OutputStream?

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10及以上使用 MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/MyApp"
                    ) // 可自定义文件夹
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                outputStream = uri?.let { resolver.openOutputStream(it) }

                outputStream?.let {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                    it.flush()
                    it.close()
                }

                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                uri?.let { resolver.update(it, contentValues, null, null) }

            } else {
                // Android 7.0 - 9.0 保存到外部公有目录
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/MyApp"
                val file = File(picturesDir)
                if (!file.exists()) file.mkdirs()

                val imageFile = File(file, fileName)
                outputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                // 通知系统刷新相册
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }
}