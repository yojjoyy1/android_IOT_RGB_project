package water.project.app

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.viewpager.widget.ViewPager
import com.facebook.AccessToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.kaopiz.kprogresshud.KProgressHUD
import org.json.JSONObject
import org.w3c.dom.Text
import com.squareup.okhttp.*
import kotlinx.android.synthetic.main.layout_info.*
import water.project.app.infoAdapter.infoCellAdapter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() ,View.OnClickListener{

    val restManager = RestManager()
    var viewPager:CustomViewPager? = null
    var mViewList:ArrayList<View>? = null
    var accessToken = AccessToken.getCurrentAccessToken()
    var titleText:TextView? = null
    var firebase: DatabaseReference? = null
    var textViewR:TextView? = null
    var textViewG:TextView? = null
    var textViewB:TextView? = null
    var rgbImg:ImageView? = null
    var powerSwitch:Switch? = null
    var editR:EditText? = null
    var editG:EditText? = null
    var editB:EditText? = null
    var index1:ImageView? = null
    var index2:ImageView? = null
    var index3:ImageView? = null
    var index1TextView:TextView? = null
    var index2TextView:TextView? = null
    var index3TextView:TextView? = null
    var kpr:KProgressHUD? = null
    var sendRGB:ImageView? = null
    var rgbHm:HashMap<String,Any>? = null
    var pictureImageView:ImageView? = null
    var infoListView:ListView? = null
    var infoCellAdapter:infoCellAdapter? = null
    var photoUrlStr:String? = null
    var moodImageView:ImageView? = null
    var marqueeTextView:TextView? = null
    var maskImageView:ImageView? = null
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        kpr = KProgressHUD.create(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setDetailsLabel("")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
            .show();
        kpr!!.dismiss()
        rgbHm = HashMap<String,Any>()
        index1 = findViewById(R.id.index1)
        index1!!.setColorFilter(resources.getColor(R.color.red))
        index1!!.setOnClickListener(this)
        index1TextView = findViewById(R.id.index1_textView)
        index1TextView!!.setTextColor(resources.getColor(R.color.red))
        index2 = findViewById(R.id.index2)
        index2!!.setOnClickListener(this)
        index2TextView = findViewById(R.id.index2_textView)
        index3 = findViewById(R.id.index3)
        index3!!.setOnClickListener(this)
        index3TextView = findViewById(R.id.index3_textView)
        viewPager = findViewById<CustomViewPager>(R.id.home_viewpager)
        titleText = findViewById<TextView>(R.id.title)
        titleText!!.text = resources.getString(R.string.home)
        firebase = FirebaseDatabase.getInstance().reference
        initData()
        viewPager!!.adapter = ViewPagerAdatper(mViewList!!)
        viewPager!!.setPagingEnabled(false)
        println("accessToken:$accessToken")
        firebase!!.child("arduino/rgb").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                println("cancel:${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val hm = p0.value as HashMap<String,Any>
                    val r = hm["r"] as Long
                    val g = hm["g"] as Long
                    val b = hm["b"] as Long
                    textViewR!!.text = "$r"
                    textViewG!!.text = "$g"
                    textViewB!!.text = "$b"
                    val rgbColor = Color.rgb(r.toInt(),g.toInt(),b.toInt())
                    rgbImg!!.setColorFilter(rgbColor)
                    powerSwitch!!.isChecked = !(r.toInt() == 0 &&
                            g.toInt() == 0 &&
                            b.toInt() == 0)
                    println("switch:${powerSwitch!!.isChecked}")
                    println("hm:$hm")
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val ani = AlphaAnimation(0.0f,1.0f)
        ani.duration = 4000
        ani.repeatCount = -1
        maskImageView!!.startAnimation(ani)
        firebase!!.child("notification").addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                println("cancel:${p0.message}")
            }

            override fun onDataChange(p0: DataSnapshot) {
                val dic = p0.value as HashMap<String,Any>
                val sec = dic["sec"] as Long
                val secDurLong = sec * 1000
                val str = dic["msg"] as String
                textViewAnimat(secDurLong,str)
            }

        })
    }
    override fun onClick(v: View?) {
        when(v)
        {
            index1 ->
            {
                titleText!!.text = resources.getString(R.string.home)
                viewPager!!.currentItem = 0
                index1!!.setColorFilter(resources.getColor(R.color.red))
                index1TextView!!.setTextColor(resources.getColor(R.color.red))
                index2!!.setColorFilter(R.color.black)
                index2TextView!!.setTextColor(resources.getColor(R.color.black))
                index3!!.setColorFilter(R.color.black)
                index3TextView!!.setTextColor(resources.getColor(R.color.black))
            }
            index2 ->
            {
                titleText!!.text = resources.getString(R.string.features)
                viewPager!!.currentItem = 1
                index1!!.setColorFilter(resources.getColor(R.color.black))
                index1TextView!!.setTextColor(resources.getColor(R.color.black))
                index2!!.setColorFilter(resources.getColor(R.color.red))
                index2TextView!!.setTextColor(resources.getColor(R.color.red))
                index3!!.setColorFilter(resources.getColor(R.color.black))
                index3TextView!!.setTextColor(resources.getColor(R.color.black))
            }
            index3 ->
            {
                titleText!!.text = resources.getString(R.string.info)
                viewPager!!.currentItem = 2
                index1!!.setColorFilter(resources.getColor(R.color.black))
                index1TextView!!.setTextColor(resources.getColor(R.color.black))
                index2!!.setColorFilter(resources.getColor(R.color.black))
                index2TextView!!.setTextColor(resources.getColor(R.color.black))
                index3!!.setColorFilter(resources.getColor(R.color.red))
                index3TextView!!.setTextColor(resources.getColor(R.color.red))
            }
            sendRGB ->
            {
                if(editR!!.text.isNotEmpty() || editG!!.text.isNotEmpty() || editB!!.text.isNotEmpty())
                {
                    if(checkEdit(editR!!) || checkEdit(editG!!) || checkEdit(editB!!))
                    {
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("不可大於255")
                            .setPositiveButton("確定",null)
                            .show()
                    }
                    else
                    {
                        kpr!!.show()
                        if (editR!!.text.isNotEmpty())
                        {
                            rgbHm!!["r"] = editR!!.text.toString().toInt()
                        }
                        if (editG!!.text.isNotEmpty())
                        {
                            rgbHm!!["g"] = editG!!.text.toString().toInt()
                        }
                        if (editB!!.text.isNotEmpty())
                        {
                            rgbHm!!["b"] = editB!!.text.toString().toInt()
                        }
                        restManager.sendApi("setRGB",rgbHm!!,"POST"){
                            _ ->
                            runOnUiThread {
                                editR!!.text = "".toEditable()
                                editG!!.text = "".toEditable()
                                editB!!.text = "".toEditable()
                                rgbHm = HashMap<String,Any>()
                                kpr!!.dismiss()
                            }
                        }
                    }
                }
                else
                {
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("至少輸入一種")
                        .setPositiveButton("確定",null)
                        .show()
                }
            }
            powerSwitch ->
            {
                println("powerSwitch:${powerSwitch!!.isChecked}")
                kpr!!.show()
                if (powerSwitch!!.isChecked)
                {
                    rgbHm!!["r"] = 0
                    rgbHm!!["g"] = 180
                    rgbHm!!["b"] = 0
                    restManager.sendApi("setRGB",rgbHm!!,"POST"){
                        _ ->
                        runOnUiThread {
                            kpr!!.dismiss()
                        }
                    }
                }
                else
                {
                    rgbHm!!["r"] = 0
                    rgbHm!!["g"] = 0
                    rgbHm!!["b"] = 0
                    restManager.sendApi("setRGB",rgbHm!!,"POST"){
                            _ ->
                        runOnUiThread {
                            kpr!!.dismiss()
                        }
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun initData()
    {
        mViewList = ArrayList<View>()
        val inf = LayoutInflater.from(this)
        val view1 = inf.inflate(R.layout.layout_home,null)
        pageView1FindId(view1)
        val view2 = inf.inflate(R.layout.layout_features,null)
        pageView2FindId(view2)
        val view3 = inf.inflate(R.layout.layout_info,null)
        pageView3FindId(view3)
        mViewList!!.add(view1)
        mViewList!!.add(view2)
        mViewList!!.add(view3)
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun pageView1FindId(view:View)
    {
        textViewR = view.findViewById<TextView>(R.id.RGB_R)
        textViewR!!.text = "0"
        textViewG = view.findViewById<TextView>(R.id.RGB_G)
        textViewG!!.text = "0"
        textViewB = view.findViewById<TextView>(R.id.RGB_B)
        textViewB!!.text = "0"
        rgbImg = view.findViewById<ImageView>(R.id.rgb_imageView)
        moodImageView = view.findViewById(R.id.moodImageView)
        val animat = AnimationDrawable()
        animat.addFrame(resources.getDrawable(R.drawable.sad),3000)
        animat.addFrame(resources.getDrawable(R.drawable.happy),1000)
        animat.isOneShot = false
        moodImageView!!.setImageDrawable(animat)
        animat.start()
        marqueeTextView = view.findViewById(R.id.marquee_textView)
        maskImageView = view.findViewById(R.id.mask_imageView)
    }
    fun textViewAnimat(dur:Long,textStr:String)
    {
        marqueeTextView!!.text = textStr
        val metric = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metric)
        val width = metric.widthPixels
        val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 0.0f, width.toFloat())
        animator.duration = dur
        animator.addUpdateListener {
            marqueeTextView!!.translationX = it.animatedValue as Float
        }
        animator.repeatCount = -1
        animator.start()
    }
    fun pageView2FindId(view:View)
    {
        powerSwitch = view.findViewById<Switch>(R.id.power_switch)
        powerSwitch!!.setOnClickListener(this)
        editR = view.findViewById<EditText>(R.id.edit_r)
        editG = view.findViewById<EditText>(R.id.edit_g)
        editB = view.findViewById<EditText>(R.id.edit_b)
        sendRGB = view.findViewById(R.id.sendRGB_ImageView)
        sendRGB!!.setOnClickListener(this)
    }
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun pageView3FindId(view:View)
    {
        pictureImageView = view.findViewById(R.id.info_picture)
        infoListView = view.findViewById(R.id.info_listview)
        val bodyHm = HashMap<String,Any>()
        bodyHm["command"] = "get"
        bodyHm["uid"] = FirebaseAuth.getInstance().currentUser!!.uid
        val infoArray = ArrayList<Any>()
        restManager.sendApi("login",bodyHm,"POST"){
            res ->
            println("res:$res")
            val json = JSONObject(res)
            if (json.has("displayName"))
            {
                val hm = HashMap<String,Any>()
                val displayName = json.getString("displayName")
                hm["title"] = "姓名:"
                hm["des"] = displayName
                infoArray.add(hm)
            }
            if (json.has("email"))
            {
                val hm = HashMap<String,Any>()
                val email = json.getString("email")
                hm["title"] = "信箱:"
                hm["des"] = email
                infoArray.add(hm)
            }
            if (json.has("creatDate"))
            {
                val hm = HashMap<String,Any>()
                val creatDate = json.getString("creatDate")
                hm["title"] = "帳號創辦時間:"
                hm["des"] = creatDate
                infoArray.add(hm)
            }
            if (json.has("lastSignInDate"))
            {
                val hm = HashMap<String,Any>()
                val lastSignInDate = json.getString("lastSignInDate")
                hm["title"] = "最後登入時間:"
                hm["des"] = lastSignInDate
                infoArray.add(hm)
            }
            photoUrlStr = json.getString("photoUrl")
            if(infoArray.size == 4)
            {
                runOnUiThread {
                    restManager.getSelfPic(this@MainActivity,photoUrlStr!!,pictureImageView!!)
                    infoCellAdapter = infoCellAdapter(this,infoArray)
                    infoListView!!.adapter = infoCellAdapter
                }
            }
        }
    }
    fun checkEdit(edit:EditText):Boolean
    {
        return if (edit.text.isNotEmpty()) {
            edit.text.toString().toInt() > 255
        } else {
            false
        }
    }
    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

}
