package com.example.practice1005
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.practice1005.R

class MainActivity : AppCompatActivity() {
    var mit:Int = 0
    var mkeyguardManager : KeyguardManager? =null //控制開鎖上鎖的管理員
    var mFingerprintManager : FingerprintManager?=null //控制指紋辨識的管理員
    var CancellationSignal: CancellationSignal?=null //接收取消訊息的物件

    mkeyguardManager = getSystemService(Activity.KEYGUARD_SERVICE) as KeyguardManager //定義控制開鎖上鎖的管理員
    mFingerprintManager = getSystemService(FingerprintManager::class.java) //指紋辨識器的管理員

    check()

    super.onCreate(savedInstanceState)

    val button:Button = findViewById(R.id.button)
    val button2:Button = findViewById(R.id.button2)
    val button3:Button = findViewById(R.id.button3)
//        val button4:Button = findViewById(R.id.button4)

    val old_password:EditText = findViewById(R.id.old_password)
    val new_password: EditText = findViewById(R.id.new_password)
    val new_password_confirm: EditText = findViewById(R.id.new_passwd_confirm)

    val input_passwd:EditText = findViewById(R.id.input_passwd)

    val preference = PreferenceManager.getDefaultSharedPreferences(this)
    val passwd = preference.edit() //要有一個編輯器

    if(preference.getString("data","") == ""){
        passwd.putString("data", "0000") //用編輯器把輸入框的文字轉為字串，存入「data」的標籤
        passwd.apply()  //套用編輯器的變更
    }

    button1.setOnClickListener(){

        if(old_passwd.text.toString() != preference.getString("data","")){
            Toast.makeText(this, "輸入的舊密碼不正確", Toast.LENGTH_SHORT).show()
        }else{
            if(new_passwd.text.toString() != new_passwd_confirm.text.toString()){
                Toast.makeText(this, "請確認新密碼輸入正確", Toast.LENGTH_SHORT).show()
            }else if(new_passwd.text.toString() == ""){
                Toast.makeText(this, "新密碼不得為空值", Toast.LENGTH_SHORT).show()
            }else{
                passwd.putString("data",new_passwd.text.toString()) //用編輯器把輸入框的文字轉為字串，存入「data」的標籤
                passwd.apply()  //套用編輯器的變更
                Toast.makeText(this, "密碼更改成功", Toast.LENGTH_SHORT).show()
            }
        }
    }
    button2.setOnClickListener(){
        if(input_passwd.text.toString() == preference.getString("data","")){
            startActivity(Intent(this@MainActivity,MainActivity2::class.java))
            wrong=0
        }else{
            Toast.makeText(this, "密碼錯誤", Toast.LENGTH_SHORT).show()
        }
    }
    button3.setOnClickListener(){

        Toast.makeText(this, "請感應指紋辨識器", Toast.LENGTH_SHORT).show()



        check() //開始檢查設備和設定
        CancellationSignal = CancellationSignal()
        mFingerprintManager!!.authenticate(null, CancellationSignal,0,
            object: FingerprintManager.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    //Toast.makeText(this@MainActivity, "$errorCode $errString", Toast.LENGTH_SHORT).show() //如果辨識錯誤印出錯誤資訊
                }

                override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    //指紋辨識通過後，接下來要做的事情可以打在這邊，可以用Intent切換到下一個畫面
                    Toast.makeText(this@MainActivity, "指紋辨識成功", Toast.LENGTH_SHORT).show()
                    //接下來要做的事情可以打在這邊，可以用在App內設置密碼做為備案
                    mit=0
                    startActivity(Intent(this@MainActivity,MainActivity2::class.java))
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    //指紋辨識成功後，接下來要做的事情可以打在這邊，可以用Intent切換到下一個畫面
                    mit++
                    if(mit>5){
                        mit=1
                    }
                    Toast.makeText(this@MainActivity, "無法識別的指紋,錯誤次數"+mit+"次", Toast.LENGTH_SHORT).show()
                    //如何建立新的Activity並使用Intent切換的教學：  https://ithelp.ithome.com.tw/articles/10216549
                }
            },null)
    }

}
