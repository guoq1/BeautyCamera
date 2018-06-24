# BeautyCamera

#### 项目介绍
一键美颜相机


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

