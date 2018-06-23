package com.guoqi.beautycamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import jp.co.cyberagent.android.gpuimage.*
import kotlinx.android.synthetic.main.ui_img_preview.*
import java.io.IOException
import java.io.InputStream

/**
 * https://github.com/BradLarson/GPUImage
 */
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
        filterList.add("2D3D")
        filterList.add("锐化")
        filterList.add("高斯模糊")
        filterList.add("框模糊")
        filterList.add("双边模糊")
        filterList.add("卷积核")
        filterList.add("膨胀")
        filterList.add("膨胀2")

        //------------混合模式----------------


        //------------视觉效果----------------
        filterList.add("半色调")
        filterList.add("黑白交叉")
        filterList.add("草图")
        filterList.add("卡通")
        filterList.add("降噪")
        filterList.add("浮雕")
        filterList.add("简单图象")
        filterList.add("旋转失真")
        filterList.add("凸起")
        filterList.add("折射")
        filterList.add("折射2")
        filterList.add("渐晕")
        filterList.add("抽象")
        filterList.add("模拟CGA")

        filterListAdapter = FilterListAdapter(this, filterList)
        hlv_filter.adapter = filterListAdapter
        hlv_filter.setOnItemClickListener { _, _, i, _ ->
            filterListAdapter.setCheckItem(i)
            when (filterList[i]) {
            //------------颜色调整----------------
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

            //------------图像处理----------------

                "2D3D" -> {//这适用于图像的任意二维或三维变换
                    /*affineTransform：这需要一个CGAffineTransform来调整二维图像
                    transform3D：这需要CATransform3D来处理3D图像
                    ignoreAspectRatio：默认情况下，转换图像的高宽比保持不变，但可以将其设置为YES以使转换与高宽比无关*/
                    gpuImage?.setFilter(GPUImageTransformFilter())
                }
                "锐化" -> {//锐化图像
                    /*清晰度：适用的清晰度调整（-4.0 - 4.0，默认为0.0）*/
                    gpuImage?.setFilter(GPUImageSharpenFilter(2.0f))
                }
                "高斯模糊" -> {//硬件优化的可变半径高斯模糊
                    /*texelSpacingMultiplier：texels间隔的乘数，范围从0.0开始，默认为1.0。调整这可能会稍微增加模糊强度，但会在结果中引入伪影。强烈建议先使用其他参数，然后再触摸此参数。
                    blurRadiusInPixels：用于模糊的半径（以像素为单位），默认值为2.0。这调整了高斯分布函数中的西格玛变量。
                    blurRadiusAsFractionOfImageWidth：
                    blurRadiusAsFractionOfImageHeight：设置这些属性将允许模糊半径根据图像的大小进行缩放
                    blurPasses：依次模糊输入图像的次数。通过越多，过滤器越慢。*/
                    gpuImage?.setFilter(GPUImageGaussianBlurFilter(2.0f))
                }
                "框模糊" -> {//硬件优化的可变半径框模糊
                    /*texelSpacingMultiplier：texels间隔的乘数，范围从0.0开始，默认为1.0。调整这可能会稍微增加模糊强度，但会在结果中引入伪影。强烈建议先使用其他参数，然后再触摸此参数。
                    blurRadiusInPixels：用于模糊的半径（以像素为单位），默认值为2.0。这调整了高斯分布函数中的西格玛变量。
                    blurRadiusAsFractionOfImageWidth：
                    blurRadiusAsFractionOfImageHeight：设置这些属性将允许模糊半径根据图像的大小进行缩放
                    blurPasses：依次模糊输入图像的次数。通过越多，过滤器越慢。*/
                    gpuImage?.setFilter(GPUImageBoxBlurFilter(2.0f))
                }
                "双边模糊" -> {//双边模糊，尝试在保留锐利边缘的同时模糊相似的颜色值
                    /*texelSpacingMultiplier：texel读取间隔的乘数，范围从0.0开始，默认值为4.0
                    distanceNormalizationFactor：中心颜色和样本颜色之间距离的标准化因子，默认值为8.0。*/
                    gpuImage?.setFilter(GPUImageBilateralFilter(3.0f))
                }
                "卷积核" -> {//针对图像运行3x3卷积核
                    /*卷积核：卷积核是应用于像素及其周围8个像素的值的3×3矩阵。该矩阵按行优先顺序指定，左上角像素为one.one，右下角为three.three。如果矩阵中的值不等于1.0，则图像可能变亮或变暗。*/
                    gpuImage?.setFilter(GPUImage3x3ConvolutionFilter())
                }
                "膨胀" -> {//执行图像膨胀操作，其中矩形邻域中红色通道的最大强度用于此像素的强度。要初始化的矩形区域的半径在初始化时指定，范围为1-4像素。这是为了与灰度图像一起使用，并且它扩展了明亮的区域。
                    gpuImage?.setFilter(GPUImageDilationFilter())
                }
                "膨胀2" -> {//这与GPUImageDilationFilter相同，只不过它对所有颜色通道都有效，而不仅仅是红色通道。
                    gpuImage?.setFilter(GPUImageRGBDilationFilter())
                }
            //------------混合模式----------------
                "颜色替换" -> {//选择性地用第二张图像替换第一张图像中的颜色
                    /*thresholdSensitivity：颜色匹配需要与要替换的目标颜色存在多少距离（默认值为0.4）
                    平滑：融合色彩匹配的顺利程度（默认值为0.1*/
                    gpuImage?.setFilter(GPUImageChromaKeyBlendFilter())
                }
                "混合" -> {//应用两个图像的混合混合
                    /*混合：第二个图像覆盖第一个图像的程度（0.0 - 1.0，默认为0.5）*/
                    gpuImage?.setFilter(GPUImageDissolveBlendFilter())
                }

            //------------视觉效果----------------
                "半色调" -> {//为图像应用半色调效果，如新闻打印
                    /*fractionalWidthOfAPixel：网点的宽度和高度的一部分（0.0 - 1.0，默认值为0.05）*/
                    gpuImage?.setFilter(GPUImageHalftoneFilter())
                }
                "黑白交叉" -> {//将图像转换为黑白交叉阴影图案
                    /*crossHatchSpacing：图像的分数宽度，用作交叉影线的间距。默认值是0.03。
                    lineWidth：交叉线的相对宽度。默认值是0.003。*/
                    gpuImage?.setFilter(GPUImageCrosshatchFilter())
                }
                "草图" -> {//将视频转换为草图。这只是倒置颜色的索贝尔边缘检测滤波器
                    /*texelWidth：
                    texelHeight：这些参数影响检测到的边缘的可见性
                    edgeStrength：调整滤镜的动态范围。较高的值会导致较强的边缘，但可以使强度色彩空间饱和。默认值是1.0。*/
                    gpuImage?.setFilter(GPUImageSketchFilter())
                }
                "卡通" -> {//它使用Sobel边缘检测在对象周围放置黑色边框，然后对图像中的颜色进行量化，使图像具有卡通般的质量。
                    gpuImage?.setFilter(GPUImageToonFilter())
                }
                "降噪" -> {//它使用与GPUImageToonFilter相似的过程，只有它在高斯模糊之前具有高斯模糊以消除噪音。
                    /*texelWidth：
                    texelHeight：这些参数影响检测到的边缘的可见性
                    blurRadiusInPixels：基础高斯模糊的半径。默认值是2.0。
                    阈值：边缘检测的灵敏度，较低的值更敏感。范围从0.0到1.0，默认值为0.2
                    quantizationLevels：在最终图像中表示的颜色层数。默认值是10.0*/
                    gpuImage?.setFilter(GPUImageSmoothToonFilter())
                }
                "浮雕" -> {//在图像上应用浮雕效果
                    /*强度：压花的强度，从0.0到4.0，1.0为正常水平*/
                    gpuImage?.setFilter(GPUImageEmbossFilter())
                }
                "简单图象" -> {//这会将颜色动态范围减少到指定的步骤数，从而形成卡通般简单的图像阴影。
                    /*colorLevels：减少图像空间的颜色级别数。范围从1到256，默认值为10。*/
                    gpuImage?.setFilter(GPUImagePosterizeFilter(15))
                }
                "旋转失真" -> {//在图像上创建旋转失真
                    /*半径（radius）：应用变形的中心半径，默认值为0.5
                    center（中心）：图像的中心（从0到1.0的归一化坐标）关于哪个扭曲，默认为（0.5,0.5）
                    angle（角度）：适用于图像的扭曲量，默认值为1.0*/
                    gpuImage?.setFilter(GPUImageSwirlFilter())
                }
                "凸起" -> {//在图像上创建凸起变形
                    /*半径：从中心开始应用失真的半径，默认值为0.25
                    center（中心）：图像的中心（从0到1.0的归一化坐标），关于哪个要扭曲，默认为（0.5,0.5）
                    比例：应用的失真量，从-1.0到1.0，默认值为0.5*/
                    gpuImage?.setFilter(GPUImageBulgeDistortionFilter())
                }
                "折射" -> {//通过玻璃球模拟折射
                    /*中心：应用失真的中心，默认值为（0.5,0.5）
                    半径：失真半径，范围从0.0到1.0，默认值为0.25
                    refractiveIndex：球体的折射率，默认值为0.71*/
                    gpuImage?.setFilter(GPUImageSphereRefractionFilter())
                }
                "折射2" -> {//与GPUImageSphereRefractionFilter相同，只有图像不反转，并且玻璃边缘有一点结霜
                    /*中心：应用失真的中心，默认值为（0.5,0.5）
                    半径：失真半径，范围从0.0到1.0，默认值为0.25
                    refractiveIndex：球体的折射率，默认值为0.71*/
                    gpuImage?.setFilter(GPUImageGlassSphereFilter())
                }
                "渐晕" -> {//执行渐晕效果，淡化图像的边缘
                    /*vignetteCenter：tex coords中的小插曲的中心（CGPoint），默认为0.5,0.5
                    vignetteColor：用于装饰图案（GPUVector3）的颜色，默认为黑色
                    vignetteStart：从晕影效果开始的中心开始的标准化距离，默认值为0.5
                    vignetteEnd：距离晕影效果结束的中心的标准化距离，默认值为0.75*/
                    gpuImage?.setFilter(GPUImageVignetteFilter())
                }
                "抽象" -> {//科威拉图像抽象，从Kyprianidis等人的工作中得出。人。在GPU Pro集合的出版物“GPU上的各向异性科威拉滤波”中。这会产生一幅类似油画的图像，但其计算量非常大，因此在iPad 2上渲染帧可能需要几秒钟。这可能最适合用于静止图像。
                    /*半径：以整数指定应用滤镜时从中心像素出发测试的像素数，缺省值为4.较高的值会创建更抽象的图像，但代价是处理时间要大得多。*/
                    gpuImage?.setFilter(GPUImageKuwaharaFilter())
                }
                "模拟CGA" -> {//模拟CGA监视器的颜色空间
                    gpuImage?.setFilter(GPUImageCGAColorspaceFilter())
                }
            }
        }
    }


}
