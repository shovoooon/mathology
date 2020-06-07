package com.shovon.mathology.ui

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.shovon.mathology.R
import com.shovon.mathology.model.RegisterResponse
import com.shovon.mathology.network.RetrofitClient
import com.shovon.mathology.utils.isOnline
import com.shovon.mathology.utils.saveData
import com.shovon.mathology.utils.toast
import kotlinx.android.synthetic.main.activity_sign.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit

class SignActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    lateinit var otpId:String
    lateinit var TOKEN: PhoneAuthProvider.ForceResendingToken

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()
    }

    fun processSignIn(view: View) {
        val phone = et_phone.text.toString()
        if (phone.length < 14){
            et_phone.error = "ভুল নম্বর"
            et_phone.requestFocus()
            return
        }

        sendOTP(phone)

    }

    private fun sendOTP(number: String) {
        showProgress()
        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60L, TimeUnit.SECONDS, this,
            object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                override fun onCodeSent(verificationId: String,
                                        token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    otpId = verificationId
                    TOKEN = token

                    Toast.makeText(this@SignActivity, "OTP Sent", Toast.LENGTH_LONG).show()
                }

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    toast(this@SignActivity, "Verification Completed")
                    hideProgress()
                    if (isOnline(this@SignActivity))
                        callRegister(number)
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    toast(this@SignActivity, "Verification Failed")
                    hideProgress()
                }

            })

    }

    private fun callRegister(phone:String) {
        showProgress()
        RetrofitClient.instance.register(phone)
            .enqueue(object :Callback<RegisterResponse>{
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    toast(this@SignActivity, "Something went wrong")
                }

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200)
                        if (!response.body()?.error!!)
                    saveData("phone", phone, this@SignActivity)
                    toast(this@SignActivity, "Sign in successful")
                    startActivity(Intent(this@SignActivity, MainActivity::class.java))
                }

            })
    }

    private fun showProgress() {
        progress_circular.visibility = VISIBLE
    }

    private fun hideProgress() {
        progress_circular.visibility = INVISIBLE
    }
}
