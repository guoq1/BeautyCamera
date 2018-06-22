package com.guoqi.beautycamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.*
import kotlinx.android.synthetic.main.ui_img_preview.*
import java.io.IOException
import java.io.InputStream


class ImgPreviewUI : AppCompatActivity() {

    val TAG = this.javaClass.simpleName

    private var gpuImage: GPUImage? = null
    var bitmap: Bitmap? = null
    private lateinit var filterListAdapter: FilterListAdapter
    private var filterList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ui_img_preview)

        initUI()

        initData()

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

    private fun initData() {
        btn_ori.setOnClickListener {
            gpuImage?.setFilter(GPUImageFilter())
            filterListAdapter.setCheckItem(-1)
        }
        filterList.add("黑白")
        filterList.add("亮度")
        filterList.add("曝光")
        filterList.add("对比度")
        filterList.add("饱和度")
        filterList.add("反色")
        filterList.add("色阶")
        filterList.add("色调")
        filterListAdapter = FilterListAdapter(this, filterList)
        hlv_filter.adapter = filterListAdapter
        hlv_filter.setOnItemClickListener { _, _, i, _ ->
            filterListAdapter.setCheckItem(i)
            when (filterList[i]) {
                "黑白" -> {
                    gpuImage?.setFilter(GPUImageGrayscaleFilter())
                }
                "亮度" -> {
                    gpuImage?.setFilter(GPUImageBrightnessFilter(0.8f))
                }
                "曝光" -> {
                    gpuImage?.setFilter(GPUImageExposureFilter(1.5f))
                }
                "对比度" -> {
                    gpuImage?.setFilter(GPUImageContrastFilter(2.0f))
                }
                "饱和度" -> {
                    gpuImage?.setFilter(GPUImageSaturationFilter(2.0f))
                }
                "反色" -> {
                    gpuImage?.setFilter(GPUImageColorInvertFilter())
                }
                "色阶" -> {
                    gpuImage?.setFilter(GPUImageSepiaFilter(1.2f))
                }
                "色调" -> {
                    gpuImage?.setFilter(GPUImageToneCurveFilter())
                }
            }
        }
    }


}
