package com.guoqi.beautycamera

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import com.guoqi.beautycamera.ImageUtil.APK_FILE
import kotlinx.android.synthetic.main.ui_show_pic.*
import java.io.File


class ShowPicUI : AppCompatActivity() {

    private var picWidth: Int = 0
    private var picHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.ui_show_pic)

        initUI()
        initClick()
    }

    private fun initUI() {
        picWidth = intent.getIntExtra("pic_width", 0)
        picHeight = intent.getIntExtra("pic_height", 0)
        Log.e("SIZE", """picWidth = $picWidth""")
        Log.e("SIZE", """picHeight = $picHeight""")
        var path = intent.getStringExtra("img_path")
        var name = intent.getStringExtra("keName")
        var time = intent.getStringExtra("time")
        var address = intent.getStringExtra("address")
        tv_name.text = name
        tv_time.text = time
        tv_address.text = address
        iv_img.setImageURI(Uri.parse(path))
        iv_img.layoutParams = RelativeLayout.LayoutParams(picWidth, picHeight)

        val options = BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options)
        options.outWidth
        options.outHeight
        iv_img.layoutParams = RelativeLayout.LayoutParams(options.outWidth, options.outHeight)

    }

    private fun initClick() {
        tv_ok.setOnClickListener {
            rl_option.visibility = View.GONE
            val bitmap = getView2Bitmap()
            val rootPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + APK_FILE + File.separator
            var waterFilePath = rootPath + "water_" + System.currentTimeMillis().toString() + ".jpeg"
            ImageUtil.saveJPGE(bitmap, waterFilePath, 100)
            if (waterFilePath.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra("imageUrl", waterFilePath)
                intent.putExtra("address", tv_address.text.toString())
                Log.e("imageUrl", waterFilePath)
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this@ShowPicUI, "图片保存失败", Toast.LENGTH_SHORT).show()
            }
        }
        tv_cancel.setOnClickListener {
            finish()
        }
    }

    //截取layout
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

    //截取照片相片
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


}
