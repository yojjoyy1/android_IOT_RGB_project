package water.project.app

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.squareup.okhttp.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class RestManager {
    val client = OkHttpClient()
    val domain = "https://us-central1-android-project-c8441.cloudfunctions.net/"
    fun sendApi(url:String,bodyHm: HashMap<String,Any>,restful:String,callback:(String)->Unit)
    {
        if (restful == "POST")
        {
            println("bodyHm:$bodyHm")
            try {
                val json = JSONObject(bodyHm as Map<*, *>)
                val requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"),json.toString())
                val request = Request.Builder()
                    .url(domain+url)
                    .post(requestBody)
                    .build()
                client.newCall(request).enqueue(object:Callback{
                    override fun onFailure(request: Request?, e: IOException?) {
                        println("onFailure:${e.toString()},url:$url,method:POST")
                    }

                    override fun onResponse(response: Response?) {
                        callback(response!!.body().string())
                    }
                })
            }
            catch (e:JSONException)
            {
                e.printStackTrace()
            }
        }
        else
        {
            val request = Request.Builder()
                .url(domain+url)
                .build()
            client.newCall(request).enqueue(object:Callback{
                override fun onFailure(request: Request?, e: IOException?) {
                    println("onFailure:${e.toString()},url:$url,method:GET")
                }

                override fun onResponse(response: Response?) {
                    callback(response!!.toString())
                }

            })
        }
    }
    fun getSelfPic(mContext:Activity,urlStr:String,imageView:ImageView)
    {
        val request = Request.Builder()  // 實例化一個 Builder
            //加上要發送請求的 API 網址
            .url(urlStr)
            //建立 Request
            .build()
        val call = client.newCall(request)
        call.enqueue(object: Callback{

            override fun onFailure(request: Request?, e: IOException?) {
                println("e:${e!!.message}")
            }

            override fun onResponse(response: Response?) {
                try {
                    val bytes = response!!.body()!!.bytes()
                    //把byte字节组装成图片
                    val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                        Log.v(TAG,"onResponse response:${bmp}")
                    mContext.runOnUiThread {
                        if (bmp != null)
                        {
                            val bit = RoundedBitmapDrawableFactory.create(mContext.resources, bmp)
                            bit.cornerRadius = 360f
                            imageView.background = bit
                        }
                    }

                }
                catch (e: JSONException)
                {
                    println("e:${e.message}")
                }
            }
        })
    }
}