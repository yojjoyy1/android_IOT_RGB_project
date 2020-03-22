package water.project.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class Login :AppCompatActivity(){
    val callbackManager = CallbackManager.Factory.create();
    var loginButton:LoginButton? = null
    var profileTracker:ProfileTracker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton!!.setReadPermissions("email")
        profileTracker = object:ProfileTracker(){
            override fun onCurrentProfileChanged(oldProfile: Profile?, currentProfile: Profile?) {
                println("oldProfile:$oldProfile,currentProfile:$currentProfile")
            }
        }
        // Callback registration
        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            @SuppressLint("CommitPrefEdits")
            override fun onSuccess(loginResult: LoginResult?) { // App code
                /*
                println("onSuccess:${loginResult!!.accessToken.userId}")
                println("onSuccess:${loginResult.accessToken.source.name}")
                println("onSuccess:${loginResult.accessToken.token}")
                println("onSuccess:${loginResult.accessToken.dataAccessExpirationTime}")
                println("onSuccess:${loginResult.accessToken.expires}")
                */
                handleFacebookAccessToken(loginResult!!.accessToken)
            }

            override fun onCancel() { // App code
                println("onCancel")
            }

            override fun onError(exception: FacebookException) { // App code
                println("onError exception:${exception.localizedMessage}")
            }
        })
        checkIsLogin()
        //LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"));
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
    fun checkIsLogin()
    {
        val prf = getSharedPreferences("project", Context.MODE_PRIVATE)
        if (prf.getBoolean("login",false))
        {
            val intent = Intent(this@Login,MainActivity::class.java)
            startActivity(intent)
        }
    }
    @SuppressLint("SimpleDateFormat")
    private fun handleFacebookAccessToken(token: AccessToken) {
        val auth = FirebaseAuth.getInstance()
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential).addOnSuccessListener {
            result ->
            val user = result.user
            val email = user!!.email
            val createTimestamp = user.metadata!!.creationTimestamp
            val lastSignIn = user.metadata!!.lastSignInTimestamp
            val photoUrlStr = user.photoUrl!!.toString()
            val uid = user.uid
            val phone = user.phoneNumber
            val name = user.displayName
            //時間戳記改成正常顯示
            val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
            //最後登入時間
            val date = Date(lastSignIn)
            //帳號新增時間
            val date2 = Date(createTimestamp)
            val bodyHm = HashMap<String,Any>()
            bodyHm["command"] = "login"
            bodyHm["uid"] = uid
            bodyHm["creatDate"] = simpleDateFormat.format(date2)
            bodyHm["displayName"] = name!!
            bodyHm["email"] = email!!
            bodyHm["lastSignInDate"] = simpleDateFormat.format(date)
            bodyHm["photoUrl"] = photoUrlStr
            val restManager = RestManager()
            restManager.sendApi("login",bodyHm,"POST"){
                _ ->
                runOnUiThread {
                    val prf = getSharedPreferences("project", Context.MODE_PRIVATE)
                    prf.edit().putBoolean("login",true).apply()
                    val intent = Intent(this@Login,MainActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}