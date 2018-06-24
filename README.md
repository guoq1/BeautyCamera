# BeautyCamera

#### 项目介绍
一键美颜相机
---
title: gpuImage实现美颜水印相机和简单的图片处理
date: 2018-06-23
categories: 水印相机
tags:
  - gpuImage
  - 美颜
  - ps
  - 图片处理
  - 水印相机
  - 滤镜
  - 图片
comments: false
---

# gpuImage实现美颜水印相机和图片滤镜


>@Author GQ 2018年06月23日
>
>项目中用到水印相机,还要美颜功能,记录一下实现过程,又看了一部分gpuImage文档的图片滤镜
>
>https://github.com/BradLarson/GPUImage
>
>https://github.com/Dean1990/MagicCamera 这个项目没跑起来 - -b



<!--more-->



## 效果图

![1](https://ws2.sinaimg.cn/large/006tNc79ly1fsm65vkcp0j308c0ett9s.jpg)

![2](https://ws2.sinaimg.cn/large/006tNc79ly1fsm65w6fp9j308c0ett9o.jpg)

![3](https://ws1.sinaimg.cn/large/006tNc79ly1fsm65x3xznj308c0etdg6.jpg)



## 使用gpuImage的滤镜实现相机预览

- 美颜滤镜

```java
gpuImage = GPUImage(this)
gpuImage?.setGLSurfaceView(surfaceView)

magicFilterGroup = GPUImageFilterGroup()
magicFilterGroup?.addFilter(GPUImageBeautyFilter())

noMagicFilterGroup = GPUImageFilterGroup()
noMagicFilterGroup?.addFilter(GPUImageFilter())

//设置滤镜
gpuImage?.setFilter(if (isInMagic) magicFilterGroup else noMagicFilterGroup)
```

- 使用GLSurfaceView显示相机预览

```xml
 <android.opengl.GLSurfaceView
            android:id="@+id/surfaceView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```

- 设置图片尺寸

```java
parameters.setPictureSize(pictureSize.width, pictureSize.height)
```

- 设置预览尺寸

```java
parameters.setPreviewSize(closelySize.width, closelySize.height)
```

- 连续对焦

```java
//连续对焦
if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
}
```





## 使用Camera.parameters设置

- 不同分辨率下预览变形问题
- 预览分辨率与照片分辨率自适应最接近尺寸
- 前后置摄像头分辨率问题



## 设置水印

- 方法一: 使用canvas将原图片和水印图片重新画出来

```java
//不同分辨率导致水印的大小不同(暂未解决)
private fun getView2Bitmap(): Bitmap {
    ll_waterMarker!!.isDrawingCacheEnabled = true
    var waterBitmapCache = ll_waterMarker.drawingCache
    var waterBitmap = Bitmap.createBitmap(waterBitmapCache, 0, 0, ll_waterMarker.width, ll_waterMarker.height)
    ll_waterMarker.destroyDrawingCache()
    var oriBitmap = (iv_img.drawable as BitmapDrawable).bitmap
    var newb = Bitmap.createBitmap(oriBitmap.width, oriBitmap.height, Bitmap.Config.ARGB_8888);
    var canvas = Canvas(newb)
    canvas.drawBitmap(oriBitmap, 0f, 0f, null)
    canvas.drawBitmap(waterBitmap, 0f, (oriBitmap.height - waterBitmap.height).toFloat() - SizeUtil.dp2px(this, 40f), null)
    canvas.save(Canvas.ALL_SAVE_FLAG);
    canvas.restore();
    return newb
}
```



- 方法二: 截取Layout直接当做绘制好的带水印图片

```java
//截取layout保存图片,会有黑边
private fun getScreenPhoto(waterPhoto: RelativeLayout?): Bitmap {
    waterPhoto!!.isDrawingCacheEnabled = true
    waterPhoto.buildDrawingCache()
    var bitmap: Bitmap? = waterPhoto.drawingCache
    val width = waterPhoto.width
    val height = waterPhoto.height
    val bitmap1 = Bitmap.createBitmap(bitmap!!, 0, 0, width, height)
    waterPhoto.destroyDrawingCache()
return bitmap1
}
```



## 更多请[查看demo](https://gitee.com/madaigou/BeautyCamera)



## gpuImage的一些滤镜

"GPUImageFastBlurFilter"                               【模糊】
"GPUImageGaussianBlurFilter"                       【高斯模糊】
"GPUImageGaussianSelectiveBlurFilter"        【高斯模糊，选择部分清晰】
"GPUImageBoxBlurFilter"                                【盒状模糊】
"GPUImageTiltShiftFilter"                                【条纹模糊，中间清晰，上下两端模糊】
"GPUImageMedianFilter.h"                             【中间值，有种稍微模糊边缘的效果】
"GPUImageBilateralFilter"                               【双边模糊】
"GPUImageErosionFilter"                                【侵蚀边缘模糊，变黑白】
"GPUImageRGBErosionFilter"                         【RGB侵蚀边缘模糊，有色彩】
"GPUImageDilationFilter"                               【扩展边缘模糊，变黑白】
"GPUImageRGBDilationFilter"                        【RGB扩展边缘模糊，有色彩】
"GPUImageOpeningFilter"                             【黑白色调模糊】
"GPUImageRGBOpeningFilter"                      【彩色模糊】
"GPUImageClosingFilter"                               【黑白色调模糊，暗色会被提亮】
"GPUImageRGBClosingFilter"                        【彩色模糊，暗色会被提亮】
"GPUImageLanczosResamplingFilter"          【Lanczos重取样，模糊效果】
"GPUImageNonMaximumSuppressionFilter"     【非最大抑制，只显示亮度最高的像素，其他为黑】
"GPUImageThresholdedNonMaximumSuppressionFilter" 【与上相比，像素丢失更多】
"GPUImageCrosshairGenerator"              【十字】
"GPUImageLineGenerator"                       【线条】
"GPUImageTransformFilter"                     【形状变化】
"GPUImageCropFilter"                              【剪裁】
"GPUImageSharpenFilter"                        【锐化】
"GPUImageUnsharpMaskFilter"               【反遮罩锐化】
"GPUImageSobelEdgeDetectionFilter"           【Sobel边缘检测算法(白边，黑内容，有点漫画的反色效果)】
"GPUImageCannyEdgeDetectionFilter"          【Canny边缘检测算法（比上更强烈的黑白对比度）】
"GPUImageThresholdEdgeDetectionFilter"    【阈值边缘检测（效果与上差别不大）】
"GPUImagePrewittEdgeDetectionFilter"         【普瑞维特(Prewitt)边缘检测(效果与Sobel差不多，貌似更平滑)】
"GPUImageXYDerivativeFilter"                        【XYDerivative边缘检测，画面以蓝色为主，绿色为边缘，带彩色】
"GPUImageHarrisCornerDetectionFilter"       【Harris角点检测，会有绿色小十字显示在图片角点处】
"GPUImageNobleCornerDetectionFilter"      【Noble角点检测，检测点更多】
"GPUImageShiTomasiFeatureDetectionFilter" 【ShiTomasi角点检测，与上差别不大】
"GPUImageMotionDetector"                             【动作检测】
"GPUImageHoughTransformLineDetector"      【线条检测】
"GPUImageParallelCoordinateLineTransformFilter" 【平行线检测】
"GPUImageLocalBinaryPatternFilter"        【图像黑白化，并有大量噪点】
"GPUImageLowPassFilter"                          【用于图像加亮】
"GPUImageHighPassFilter"                        【图像低于某值时显示为黑】
"GPUImageSketchFilter"                          【素描】
"GPUImageThresholdSketchFilter"         【阀值素描，形成有噪点的素描】
"GPUImageToonFilter"                             【卡通效果（黑色粗线描边）】
"GPUImageSmoothToonFilter"                【相比上面的效果更细腻，上面是粗旷的画风】
"GPUImageKuwaharaFilter"                     【桑原(Kuwahara)滤波,水粉画的模糊效果；处理时间比较长，慎用】
"GPUImageMosaicFilter"                         【黑白马赛克】
"GPUImagePixellateFilter"                       【像素化】
"GPUImagePolarPixellateFilter"              【同心圆像素化】
"GPUImageCrosshatchFilter"                  【交叉线阴影，形成黑白网状画面】
"GPUImageColorPackingFilter"              【色彩丢失，模糊（类似监控摄像效果）】
"GPUImageVignetteFilter"                        【晕影，形成黑色圆形边缘，突出中间图像的效果】
"GPUImageSwirlFilter"                               【漩涡，中间形成卷曲的画面】
"GPUImageBulgeDistortionFilter"            【凸起失真，鱼眼效果】
"GPUImagePinchDistortionFilter"            【收缩失真，凹面镜】
"GPUImageStretchDistortionFilter"         【伸展失真，哈哈镜】
"GPUImageGlassSphereFilter"                  【水晶球效果】
"GPUImageSphereRefractionFilter"         【球形折射，图形倒立】
"GPUImagePosterizeFilter"                 【色调分离，形成噪点效果】
"GPUImageCGAColorspaceFilter"      【CGA色彩滤镜，形成黑、浅蓝、紫色块的画面】
"GPUImagePerlinNoiseFilter"              【柏林噪点，花边噪点】
"GPUImage3x3ConvolutionFilter"      【3x3卷积，高亮大色块变黑，加亮边缘、线条等】
"GPUImageEmbossFilter"                   【浮雕效果，带有点3d的感觉】
"GPUImagePolkaDotFilter"                 【像素圆点花样】
"GPUImageHalftoneFilter"                  【点染,图像黑白化，由黑点构成原图的大致图形】
混合模式 Blend
"GPUImageMultiplyBlendFilter"            【通常用于创建阴影和深度效果】
"GPUImageNormalBlendFilter"               【正常】
"GPUImageAlphaBlendFilter"                 【透明混合,通常用于在背景上应用前景的透明度】
"GPUImageDissolveBlendFilter"             【溶解】
"GPUImageOverlayBlendFilter"              【叠加,通常用于创建阴影效果】
"GPUImageDarkenBlendFilter"               【加深混合,通常用于重叠类型】
"GPUImageLightenBlendFilter"              【减淡混合,通常用于重叠类型】
"GPUImageSourceOverBlendFilter"       【源混合】
"GPUImageColorBurnBlendFilter"          【色彩加深混合】
"GPUImageColorDodgeBlendFilter"      【色彩减淡混合】
"GPUImageScreenBlendFilter"                【屏幕包裹,通常用于创建亮点和镜头眩光】
"GPUImageExclusionBlendFilter"            【排除混合】
"GPUImageDifferenceBlendFilter"          【差异混合,通常用于创建更多变动的颜色】
"GPUImageSubtractBlendFilter"            【差值混合,通常用于创建两个图像之间的动画变暗模糊效果】
"GPUImageHardLightBlendFilter"         【强光混合,通常用于创建阴影效果】
"GPUImageSoftLightBlendFilter"           【柔光混合】
"GPUImageChromaKeyBlendFilter"       【色度键混合】
"GPUImageMaskFilter"                           【遮罩混合】
"GPUImageHazeFilter"                            【朦胧加暗】
"GPUImageLuminanceThresholdFilter" 【亮度阈】
"GPUImageAdaptiveThresholdFilter"     【自适应阈值】
"GPUImageAddBlendFilter"                    【通常用于创建两个图像之间的动画变亮模糊效果】
"GPUImageDivideBlendFilter"                 【通常用于创建两个图像之间的动画变暗模糊效果】

