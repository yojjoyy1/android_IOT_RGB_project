package water.project.app.infoAdapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import water.project.app.Login
import water.project.app.MainActivity
import water.project.app.R

class infoCellAdapter(var mContext:Context,var ary_items:ArrayList<Any>):BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var conver = convertView
        println("position:$position,count:${ary_items.size}")
        if (position != ary_items.size)
        {
            if (conver == null)
            {
                val inf = LayoutInflater.from(mContext)
                conver = inf.inflate(R.layout.layout_info_listview,null)
            }
            val title = conver!!.findViewById<TextView>(R.id.title)
            val data = ary_items[position] as HashMap<String,Any>
            title.text = data["title"] as String
            val des = conver.findViewById<TextView>(R.id.des)
            des.text = data["des"] as String
            return  conver
        }
        else
        {
            if (conver == null)
            {
                val inf = LayoutInflater.from(mContext)
                conver = inf.inflate(R.layout.layout_info_cell,null)
            }
            val logoutBtn = conver!!.findViewById<Button>(R.id.logout_btn)
            logoutBtn.setOnClickListener(logoutListener())
            return conver
        }
    }

    override fun getItem(position: Int): Any {
        return ary_items[position - 1]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return ary_items.size + 1
    }
    fun logoutListener():View.OnClickListener
    {
        return View.OnClickListener {
            AlertDialog.Builder(mContext)
                .setTitle("是否要登出")
                .setPositiveButton("確定"){
                        dialog, _ ->
                    dialog.dismiss()
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    val prf = mContext.getSharedPreferences("project", Context.MODE_PRIVATE)
                    prf.edit().putBoolean("login",false).apply()
                    val intent = Intent(mContext, Login::class.java)
                    mContext.startActivity(intent)
                }
                .setNegativeButton("取消",null)
                .show()
        }
    }
}