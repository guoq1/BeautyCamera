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
        //------------颜色调整----------------
        filterList.add("黑白")
        filterList.add("亮度")
        filterList.add("曝光")
        filterList.add("对比度")
        filterList.add("饱和度")
        filterList.add("反色")
        filterList.add("棕褐色")
        filterList.add("阶调曲线")
        filterList.add("灰度系数")
        filterList.add("关卡")
        filterList.add("矩阵转换颜色")
        filterList.add("RGB")
        filterList.add("色调")
        filterList.add("白平衡")
        filterList.add("阴影和高光")
        filterList.add("转换单色")
        filterList.add("指定颜色混合")
        filterList.add("雾霾")
        filterList.add("不透明度")

        //------------图像处理----------------


        filterListAdapter = FilterListAdapter(this, filterList)
        hlv_filter.adapter = filterListAdapter
        hlv_filter.setOnItemClickListener { _, _, i, _ ->
            filterListAdapter.setCheckItem(i)
            when (filterList[i]) {
                "黑白" -> {
                    gpuImage?.setFilter(GPUImageGrayscaleFilter())
                }
                "亮度" -> {//调整后的亮度（-1.0 - 1.0，默认为0.0）
                    gpuImage?.setFilter(GPUImageBrightnessFilter(0.8f))
                }
                "曝光" -> {//调整曝光（-10.0 - 10.0，默认为0.0）
                    gpuImage?.setFilter(GPUImageExposureFilter(1.5f))
                }
                "对比度" -> {//调整后的对比度（0.0 - 4.0，默认值为1.0）
                    gpuImage?.setFilter(GPUImageContrastFilter(2.0f))
                }
                "饱和度" -> {//应用于图像的饱和度或去饱和度（0.0 - 2.0，默认值为1.0）
                    gpuImage?.setFilter(GPUImageSaturationFilter(2.0f))
                }
                "反色" -> {//反转图像的颜色
                    gpuImage?.setFilter(GPUImageColorInvertFilter())
                }
                "棕褐色" -> {//棕褐色调取代正常图像颜色的程度（0.0 - 1.0，默认为1.0）
                    gpuImage?.setFilter(GPUImageSepiaFilter(1.2f))
                }
                "阶调曲线" -> {//根据每个颜色通道的样条曲线调整图像的颜色
                    /*
                    redControlPoints：
                    greenControlPoints：
                    blueControlPoints：
                    rgbCompositeControlPoints：色调曲线采用一系列控制点来定义每个颜色分量的样条曲线，或者复合材料中的所有三个控制点。这些存储为一个NSArray中NSValue包装的CGPoints，其中规范化的X和Y坐标从0到1.默认值是（0,0），（0.5,0.5），（1,1）。*/
                    gpuImage?.setFilter(GPUImageToneCurveFilter())
                }
                "灰度系数" -> {//应用的伽玛调整（0.0 - 3.0，默认值为1.0）
                    gpuImage?.setFilter(GPUImageGammaFilter())
                }
                "关卡" -> {
                    /*类似Photoshop的关卡调整。min，max，minOut和maxOut参数的浮点数在[0，1]范围内。如果您的参数来自Photoshop，范围[0, 255]，您必须先将它们转换为[0，1]。gamma / mid参数是一个float> = 0。这与Photoshop中的值相匹配。如果要将RGB级别应用于各个通道，则需要使用此滤镜两次-首先针对单个通道，然后针对所有通道。*/
                    gpuImage?.setFilter(GPUImageLevelsFilter())
                }
                "矩阵转换颜色" -> {//通过将矩阵应用于图像来转换图像的颜色
                    /*colorMatrix：用于转换图像中每种颜色的4x4矩阵
                    强度：新变换的颜色替换每个像素的原始颜色的程度*/
                    gpuImage?.setFilter(GPUImageColorMatrixFilter())
                }
                "RGB" -> {//调整图像的各个RGB通道
                    /*红色：每个颜色通道乘以的标准化值。范围从0.0到1.0，默认值为1.0。
                    绿色：
                    蓝色：*/
                    gpuImage?.setFilter(GPUImageRGBFilter())
                }
                "色调" -> {//调整图像的色调
                    /*色调：色调角度，以度为单位。默认90度*/
                    gpuImage?.setFilter(GPUImageHueFilter())
                }
                "白平衡" -> {//调整图像的白平衡。
                    /*温度：以ºK调整图像的温度。4000的值非常酷，7000非常温暖。默认值是5000.请注意，4000和5000之间的比例与5000和7000之间的比例几乎相同。
                    色调：通过调整图像的色调。-200的值非常绿，200 非常粉。默认值是0。*/
                    gpuImage?.setFilter(GPUImageWhiteBalanceFilter())
                }
                "阴影和高光" -> {//调整图像的阴影和高光
                    /*阴影：增加以减弱阴影，从0.0到1.0，默认为0.0。
                    亮点：降低高光变暗，从1.0降至0.0，默认为1.0。*/
                    gpuImage?.setFilter(GPUImageHighlightShadowFilter())
                }
                "转换单色" -> {//根据每个像素的亮度将图像转换为单色版本
                    /*强度：特定颜色替换正常图像颜色的程度（0.0 - 1.0，默认为1.0）
                    颜色：作为效果基础的颜色，默认为（0.6,0.45,0.3,1.0）。*/
                    gpuImage?.setFilter(GPUImageMonochromeFilter())
                }
                "指定颜色混合" -> {//使用图像的亮度在两种用户指定的颜色之间混合
                    /*firstColor：第一种颜色和第二种颜色分别指定用什么颜色代替图像的暗区和亮区。默认值为（0.0，0.0，0.5）amd（1.0，0.0，0.0）。
                    secondColor：*/
                    gpuImage?.setFilter(GPUImageFalseColorFilter())
                }
                "雾霾" -> {//用于添加或移除雾霾（类似于UV滤镜）
                    /*firstColor：第一种颜色和第二种颜色分别指定用什么颜色代替图像的暗区和亮区。默认值为（0.0，0.0，0.5）amd（1.0，0.0，0.0）。
                    secondColor：*/
                    gpuImage?.setFilter(GPUImageHazeFilter())
                }
                "不透明度" -> {//调整传入图像的Alpha通道
                    /*不透明度：将每个像素的输入alpha通道乘以（0.0 - 1.0，默认值为1.0）的值，*/
                    gpuImage?.setFilter(GPUImageOpacityFilter())
                }
                "不透明度" -> {//调整传入图像的Alpha通道
                    /*不透明度：将每个像素的输入alpha通道乘以（0.0 - 1.0，默认值为1.0）的值，*/
                    gpuImage?.setFilter(GPUImageChromaKeyBlendFilter())
                }
            }
        }
    }


}
