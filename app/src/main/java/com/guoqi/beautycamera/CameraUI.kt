package com.guoqi.beautycamera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.guoqi.beautycamera.ImageUtil.APK_FILE
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup
import kotlinx.android.synthetic.main.ui_camera.*
import java.io.File

class CameraUI : AppCompatActivity(), View.OnClickListener {

    val TAG = this.javaClass.simpleName

    val SUCCESS = 0
    val ERROR = 2

    private var mCamera: Camera? = null
    private var mCameraId = 1 //默认使用前置摄像头

    //屏幕宽高
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    //闪光灯模式 0:关闭 1: 开启 2: 自动
    private var light_num = 0
    //延迟时间
    private var delay_time: Int = 0
    private var delay_time_temp: Int = 0
    private var is_camera_delay: Boolean = false
    private var picHeight: Int = 0

    private lateinit var saveBitmap: Bitmap//拍照结果Bitmap
    private lateinit var img_path_magic: String//拍照图片路径

    //美颜
    private var gpuImage: GPUImage? = null
    private var magicFilterGroup: GPUImageFilterGroup? = null
    private var noMagicFilterGroup: GPUImageFilterGroup? = null
    private var isInMagic: Boolean = false

    private var isPreviewing: Boolean = false //是否预览

    private val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            val what = msg.what
            when (what) {
                SUCCESS -> {
                    if (delay_time > 0) {
                        camera_delay_time_text!!.text = "" + delay_time
                    }

                    try {
                        if (delay_time == 0) {
                            capture()
                            is_camera_delay = false
                            camera_delay_time_text!!.visibility = View.GONE
                        }
                    } catch (e: Exception) {
                        return
                    }

                }

                ERROR -> is_camera_delay = false
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ui_camera)

        initUI()
        initData()
    }

    private fun initUI() {
        setWaterText()
        //拍照按钮
        iv_capture!!.setOnClickListener(this)

        //关闭相机界面按钮
        camera_close!!.setOnClickListener(this)

        //top 的view
        home_custom_top_relative!!.alpha = 0.5f

        //前后摄像头切换
        camera_frontback!!.setOnClickListener(this)

        //延迟拍照时间
        camera_delay_time!!.setOnClickListener(this)

        //切换正方形时候的动画
        homeCustom_cover_top_view!!.alpha = 0.5f
        homeCustom_cover_bottom_view!!.alpha = 0.5f

        //拍照时动画
        home_camera_cover_top_view!!.alpha = 1f
        home_camera_cover_bottom_view!!.alpha = 1f

        //闪光灯
        flash_light!!.setOnClickListener(this)

        //开启美颜
        gpuImage = GPUImage(this)
        gpuImage?.setGLSurfaceView(surfaceView)

        magicFilterGroup = GPUImageFilterGroup()
        magicFilterGroup?.addFilter(GPUImageBeautyFilter())

        noMagicFilterGroup = GPUImageFilterGroup()
        noMagicFilterGroup?.addFilter(GPUImageFilter())

        ACache.get(this).getAsObject("isInMagic")?.let {
            isInMagic = it as Boolean
            setMagic(isInMagic)
        }

        gpuImage?.setFilter(if (isInMagic) magicFilterGroup else noMagicFilterGroup)

        iv_magic.setOnClickListener {
            isInMagic = !isInMagic
            setMagic(isInMagic)
            gpuImage?.setFilter(if (isInMagic) magicFilterGroup else noMagicFilterGroup)
        }
    }

    private fun setMagic(isInMagic: Boolean) {
        if (isInMagic) {
            iv_magic.setImageResource(R.mipmap.ic_magic_yes)
            tv_magic.text = "开启美颜"
            ACache.get(this).put("isInMagic", true)
        } else {
            iv_magic.setImageResource(R.mipmap.ic_magic_no)
            tv_magic.text = "关闭美颜"
            ACache.get(this).put("isInMagic", false)
        }
    }

    /**
     * 设置水印
     */
    private fun setWaterText() {
        tv_name.text = intent.getStringExtra("keName")
        tv_time.text = intent.getStringExtra("time")
        tv_address.text = intent.getStringExtra("address")
    }

    private fun initData() {
        val dm = resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        Log.e(TAG, """screenWidth = $screenWidth""")
        Log.e(TAG, """screenHeight = $screenHeight""")
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_capture -> if (isPreviewing) {
                if (delay_time == 0) {
                    when (light_num) {
                        0 ->
                            //关闭
                            CameraUtil.getInstance().turnLightOff(mCamera)
                        1 -> CameraUtil.getInstance().turnLightOn(mCamera)
                        2 ->
                            //自动
                            CameraUtil.getInstance().turnLightAuto(mCamera)
                    }
                    capture()
                } else {
                    camera_delay_time_text.visibility = View.VISIBLE
                    camera_delay_time_text.text = delay_time.toString()
                    is_camera_delay = true
                    Thread(Runnable {
                        while (delay_time > 0) {
                            //按秒数倒计时
                            try {
                                Thread.sleep(1000)
                            } catch (e: InterruptedException) {
                                mHandler.sendEmptyMessage(ERROR)
                                return@Runnable
                            }

                            delay_time--
                            mHandler.sendEmptyMessage(SUCCESS)
                        }
                    }).start()
                }
            }


        //前后置摄像头拍照
            R.id.camera_frontback -> switchCamera()

        //退出相机界面 释放资源
            R.id.camera_close -> {
                if (is_camera_delay) {
                    Toast.makeText(this@CameraUI, "正在拍照请稍后...", Toast.LENGTH_SHORT).show()
                    return
                }
                finish()
            }

        //闪光灯
            R.id.flash_light -> {
                if (mCameraId == 1) {
                    //前置
                    Toast.makeText(this, "请切换为后置摄像头开启闪光灯", Toast.LENGTH_SHORT).show()
                    return
                }
                val parameters = mCamera!!.parameters
                when (light_num) {
                    0 -> {
                        //打开
                        light_num = 1
                        flash_light!!.setImageResource(R.mipmap.btn_camera_flash_on)
                        parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH//开启
                        mCamera!!.parameters = parameters
                    }
                    1 -> {
                        //自动
                        light_num = 2
                        parameters.flashMode = Camera.Parameters.FLASH_MODE_AUTO
                        mCamera!!.parameters = parameters
                        flash_light!!.setImageResource(R.mipmap.btn_camera_flash_auto)
                    }
                    2 -> {
                        //关闭
                        light_num = 0
                        //关闭
                        parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                        mCamera!!.parameters = parameters
                        flash_light!!.setImageResource(R.mipmap.btn_camera_flash_off)
                    }
                }
            }

        //延迟拍照时间
            R.id.camera_delay_time -> when (delay_time) {
                0 -> {
                    delay_time = 3
                    delay_time_temp = delay_time
                    camera_delay_time!!.setImageResource(R.mipmap.btn_camera_timing_3)
                }

                3 -> {
                    delay_time = 5
                    delay_time_temp = delay_time
                    camera_delay_time!!.setImageResource(R.mipmap.btn_camera_timing_5)
                }

                5 -> {
                    delay_time = 10
                    delay_time_temp = delay_time
                    camera_delay_time!!.setImageResource(R.mipmap.btn_camera_timing_10)
                }

                10 -> {
                    delay_time = 0
                    delay_time_temp = delay_time
                    camera_delay_time!!.setImageResource(R.mipmap.btn_camera_timing_0)
                }
            }
        }
    }

    private fun switchCamera() {
        releaseCamera()
        mCameraId = (mCameraId + 1) % Camera.getNumberOfCameras()
        initCamera(mCameraId)
    }


    override fun onResume() {
        super.onResume()
        initCamera(mCameraId)
    }

    private fun initCamera(cameraId: Int) {
        ll_water.visibility = View.VISIBLE
        try {
            if (mCamera == null) {
                mCameraId = cameraId
                mCamera = Camera.open(cameraId)

                //设置pictureSize和preViewSize问题
                setupCamera(mCamera)

                if (!isPreviewing) {
                    isPreviewing = true
                    gpuImage?.setUpCamera(mCamera, if (cameraId == 0) 90 else 270, cameraId > 0, false)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onPause() {
        super.onPause()
        releaseCamera()
    }


    /**
     * 拍照
     */
    private fun capture() {
        ll_water.visibility = View.GONE
        Toast.makeText(this, "图片正在处理中...", Toast.LENGTH_SHORT).show()
        mCamera?.takePicture(null, null, Camera.PictureCallback { data, camera ->
            val cTime = System.currentTimeMillis().toString()
            val photoNameOri = cTime + "_ori.jpeg"
            val photoNameMagic = cTime + "_magic.jpeg"
            img_path_magic = Environment.getExternalStorageDirectory().absolutePath + File.separator + APK_FILE + File.separator + photoNameMagic

            var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            saveBitmap = CameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, bitmap)
            SaveTask().execute()


            //保存美颜照片
//            gpuImage?.setImage(bitmap)
//            gpuImage?.saveToPictures(Environment.getExternalStorageDirectory().absolutePath + File.separator + APK_FILE + CACHE, photoNameMagic) {
            //camera.startPreview();
            //surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY;
//            };

//            var result = gpuImage?.getBitmapWithFilterApplied(bitmap)
//            //旋转图片90度
//            result = ImageUtil.rotateBitmap(result, ImageUtil.readPictureDegree(img_path_magic) - 90)
//            BitmapUtils.saveJPGE_After(context, result, img_path_magic, 100)

            //普通照片
//            val img_path_ori = Environment.getExternalStorageDirectory().absolutePath + File.separator + APK_FILE + CACHE + File.separator + photoNameOri
//            BitmapUtils.saveJPGE_After(context, saveBitmap, img_path_ori, 100)

            if (!bitmap.isRecycled) {
                bitmap.recycle()
            }

//            if (!saveBitmap.isRecycled) {
//                saveBitmap.recycle()
//            }

//            if (!result!!.isRecycled) {
//                result.recycle()
//            }
        })
    }


    /**
     * 设置
     */
    private fun setupCamera(camera: Camera?) {
        try {
            val parameters = camera!!.parameters

            if (parameters.supportedFocusModes.contains(
                            Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            }

            //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
            val previewSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.supportedPreviewSizes, 800)
            Log.e("SIZE", "previewSize = " + previewSize.width + " | " + previewSize.height)
            parameters.setPreviewSize(previewSize.width, previewSize.height)

            val pictrueSize = CameraUtil.getInstance().getPropSizeForHeight(parameters.supportedPictureSizes, 800)
            Log.e("SIZE", "pictureSize = " + pictrueSize.width + " | " + pictrueSize.height)
            parameters.setPictureSize(pictrueSize.width, pictrueSize.height)

            camera.parameters = parameters

            /**
             * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
             * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width previewSize.width才是surfaceView的高度 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小></previewSize.width>
             *
             */
            picHeight = screenWidth * pictrueSize.width / pictrueSize.height

//            val params = FrameLayout.LayoutParams(screenWidth, screenHeight/*screenWidth * pictrueSize.width / pictrueSize.height*/)
            //这里当然可以设置拍照位置 比如居中
//            params.gravity = Gravity.CENTER;
            //修正预览拉长效果
            var params = changePreviewSize(camera, picHeight, screenWidth)
            Log.e(TAG, "surfaceView展现的尺寸:" + params.height + "*" + params.width)
            surfaceView!!.layoutParams = params

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 修改相机的预览尺寸，调用此方法就行
     *
     * @param camera     相机实例
     * @param viewWidth  预览的surfaceView的宽
     * @param viewHeight 预览的surfaceView的高
     */
    private fun changePreviewSize(camera: Camera, viewWidth: Int, viewHeight: Int): FrameLayout.LayoutParams {
        var parameters = camera.parameters;
        var sizeList = parameters.supportedPreviewSizes
        var closelySize: Camera.Size? = null//储存最合适的尺寸
        Log.e(TAG, "camera拍照的尺寸 :" + viewWidth + " * " + viewHeight)
        for (size in sizeList) {
            Log.e(TAG, "camera支持的preview尺寸 :" + size.width + " * " + size.height)
            //先查找preview中是否存在与surfaceview相同宽高的尺寸
            if ((size.width == viewWidth) && (size.height == viewHeight)) {
                closelySize = size;
            }
        }
        if (closelySize == null) {
            // 得到与传入的宽高比最接近的size
            var reqRatio = (viewWidth.toFloat()) / viewHeight;
            var curRatio: Float
            var deltaRatio: Float;
            var deltaRatioMin = Float.MAX_VALUE;
            for (size in sizeList) {
                if (size.width < 1024) continue;//1024表示可接受的最小尺寸，否则图像会很模糊，可以随意修改
                curRatio = (size.width.toFloat()) / size.height;
                deltaRatio = Math.abs(reqRatio - curRatio);
                if (deltaRatio < deltaRatioMin) {
                    deltaRatioMin = deltaRatio;
                    Log.e("changePreviewSize", "最接近pic尺寸的预览尺寸比例:" + deltaRatioMin)
                    Log.e("changePreviewSize", "最接近pic尺寸的预览尺寸:" + size.width + "*" + size.height)
                    closelySize = size;
                }
            }
        }

        //下面是调整预览时的尺寸
        //修复华为mate7前置摄像头预览为(1280*720=1.777),不够屏幕宽度(1920*1080=1.777),预览图象太小问题
        if (closelySize!!.height < screenWidth) {
            Log.e(TAG, "摄像头预览尺寸(" + closelySize?.width + "*" + closelySize?.height + ") < 屏幕宽度(" + screenHeight + "*" + screenWidth + ")")

            //以屏幕宽度为比例,应当显示的预览高度为1920*1080 ,然而屏幕高度获取为1812 < 应预览高度1920
            var shouldPreviewHeight = (screenWidth * closelySize.width) / closelySize.height
            if (screenHeight < shouldPreviewHeight) {
                //此时应当拿高度来做比例
                closelySize?.width = screenHeight
                closelySize?.height = (screenHeight * closelySize.height) / closelySize.width
            } else {
                closelySize?.width = screenWidth
                closelySize?.height = shouldPreviewHeight
            }
            Log.e("changePreviewSize", "调整预览尺寸为：" + closelySize?.width + "*" + closelySize?.height);
        }

        //空白占位高度 = screenHeight - 预览高度closelySize.height - 顶部栏
        Log.e(TAG, "空白高度 : " + screenHeight + "-" + closelySize?.width + " = " + (screenHeight - closelySize!!.width))
        //如果屏幕预览高度占不满屏幕,则顶部空出操作栏高度
        var hasBlankHeight = SizeUtil.px2dp(this, (screenHeight - closelySize!!.width).toFloat() / 2) > 44
        if (hasBlankHeight) {
            val Params = RelativeLayout.LayoutParams(screenWidth, SizeUtil.dp2px(this, 44f)/*SizeUtil.px2dp(this, (screenHeight - closelySize.width).toFloat() / 2)*/)
            homeCustom_cover_top_view!!.layoutParams = Params
        } else {
            val Params = RelativeLayout.LayoutParams(screenWidth, 0)
            homeCustom_cover_top_view!!.layoutParams = Params
        }

        //设置相机可接受的尺寸
        parameters.setPreviewSize(closelySize!!.width, closelySize!!.height)
        camera.parameters = parameters

        Log.e("changePreviewSize", "预览尺寸最终修改为：" + closelySize?.width + "*" + closelySize?.height);
        return FrameLayout.LayoutParams(closelySize.height, closelySize.width)
    }


    /**
     * 释放相机资源
     */
    private fun releaseCamera() {
        if (mCamera != null) {
            gpuImage?.deleteImage()
            isPreviewing = false
            mCamera!!.setPreviewCallback(null)
            mCamera!!.stopPreview()
            mCamera!!.release()
            mCamera = null
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class SaveTask : AsyncTask<Void, Void, Bitmap>() {
        override fun doInBackground(vararg p0: Void?): Bitmap {
            var gpuImage = GPUImage(this@CameraUI)
            gpuImage.setImage(saveBitmap)
            gpuImage.setFilter(if (isInMagic) GPUImageBeautyFilter() else GPUImageFilter())
            val result = gpuImage.getBitmapWithFilterApplied(saveBitmap)
            ImageUtil.saveJPGE(result, img_path_magic, 100)
            return result!!
        }

        override fun onPostExecute(result: Bitmap?) {
            super.onPostExecute(result)

            val intent = Intent()
            intent.putExtra("img_path", img_path_magic)
            intent.putExtra("pic_width", screenWidth)
            intent.putExtra("pic_height", picHeight)
            intent.putExtra("keName", tv_name.text.toString())
            intent.putExtra("time", tv_time.text.toString())
            intent.putExtra("address", tv_address.text.toString())
            setResult(RESULT_OK, intent)
            finish()

        }
    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        super.onBackPressed()
    }
}


