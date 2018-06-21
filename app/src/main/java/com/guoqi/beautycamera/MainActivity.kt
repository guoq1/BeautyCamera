package com.guoqi.beautycamera

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PERMISSIONS_LOCATION = arrayOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")
    val PERMISSIONS_STORAGE = arrayOf("android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE")
    val PERMISSIONS_CAMERA = arrayOf("android.permission.CAMERA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initPermission()
        initClick()
    }

    private fun initClick() {
        //相机预览
        btn_camera.setOnClickListener {
            openCamera()
        }
        //图片预览
        btn_img.setOnClickListener {
            startActivity(Intent(this@MainActivity, ImgPreviewUI::class.java))
        }
    }


    private fun initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestRuntimePermissions(PERMISSIONS_LOCATION + PERMISSIONS_STORAGE + PERMISSIONS_CAMERA, permissionListener)
        }
    }

    /**
     * 申请权限
     */
    private fun requestRuntimePermissions(permissions: Array<String>, listener: PermissionListener) {
        var permissionList = ArrayList<String>()
        // 遍历每一个申请的权限，把没有通过的权限放在集合中
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) !== PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            } else {
                permissionListener?.granted()
            }
        }
        // 申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toTypedArray(), 1000)
        }
    }

    private var permissionListener = object : PermissionListener {
        override fun granted() {
        }

        override fun denied(deniedList: List<String>) {
            if (!deniedList.isEmpty()) {
                AlertDialog.Builder(this@MainActivity).setTitle("获取相关权限被禁用")
                        .setMessage("请在 设置-应用管理-" + getString(R.string.app_name) + "-权限管理 (将相关权限打开)")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("去设置", DialogInterface.OnClickListener { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.parse("""package:$packageName""")
                            startActivity(intent)
                        }).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(this@MainActivity, CameraUI::class.java)
        intent.putExtra("keName", "  张三")
        intent.putExtra("time", "  2018年06月21日 14:24:10")
        intent.putExtra("address", "  山西省太原市小店区1号")
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        if (requestCode == 0) {
            val img_path = data.getStringExtra("img_path")

            val picWidth = data.getIntExtra("pic_width", 0)
            val picHeight = data.getIntExtra("pic_height", 0)
            val intent = Intent(this@MainActivity, ShowPicUI::class.java)
            intent.putExtra("pic_width", picWidth)
            intent.putExtra("pic_height", picHeight)
            intent.putExtra("img_path", img_path)
            intent.putExtra("keName", data.getStringExtra("keName"))
            intent.putExtra("time", data.getStringExtra("time"))
            intent.putExtra("address", data.getStringExtra("address"))
            startActivityForResult(intent, 1)
        }
        if (requestCode == 1) {
            if (data.getStringExtra("imageUrl") != null && data.getStringExtra("imageUrl").isNotEmpty()) {
                var imgPath = data.getStringExtra("imageUrl")//图片路径
                val uri = Uri.parse(imgPath)
                iv_result.setImageURI(uri)
            }
        }
    }
}
