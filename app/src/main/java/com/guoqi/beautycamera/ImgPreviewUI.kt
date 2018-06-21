package com.guoqi.beautycamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter
import kotlinx.android.synthetic.main.ui_img_preview.*
import java.io.IOException
import java.io.InputStream


class ImgPreviewUI : AppCompatActivity() {

    val TAG = this.javaClass.simpleName

    private var gpuImage: GPUImage? = null
    var bitmap: Bitmap? = null
    var isOriImg = true//是否是原图

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_img_preview)

        initUI()

        tv_ori.setOnClickListener {
            isOriImg = true
            setOri(bitmap)
        }
        tv_black.setOnClickListener {
            setFilter(bitmap)
        }
    }

    private fun initUI() {
        gpuImage = GPUImage(this)
        gpuImage?.setGLSurfaceView(iv_preview)
        //bitmap = gpuImage?.bitmapWithFilterApplied
        val inputStream: InputStream

        try {
            //加载图片
            inputStream = assets.open("girl.jpg")
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //显示原图
        gpuImage?.setImage(bitmap)
    }

    private fun setFilter(bitmap: Bitmap?) {
        isOriImg = false
        gpuImage?.setImage(bitmap)
        gpuImage?.setFilter(GPUImageGrayscaleFilter())
    }

    private fun setOri(bitmap: Bitmap?) {
        isOriImg = true
        gpuImage?.setImage(bitmap)
        gpuImage?.setFilter(GPUImageFilter())
    }

}
