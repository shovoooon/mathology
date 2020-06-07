package com.shovon.mathology.ui

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shovon.mathology.R
import com.shovon.mathology.model.DashboardResponse
import com.shovon.mathology.model.PayoutResponse
import com.shovon.mathology.network.RetrofitClient
import com.shovon.mathology.utils.getData
import com.shovon.mathology.utils.toast
import kotlinx.android.synthetic.main.activity_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardActivity : AppCompatActivity() {

    private lateinit var phone: String
    private var mBal: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        phone = getData("phone", this).toString()
        val bal = getData("bal", this).toString()
        if (bal != "null"){
            tv_bal.text = bal
        }
        tv_phone.text = phone

        loadDashboard()



    }

    private fun loadDashboard() {
        progress_circular.visibility = VISIBLE

        class LoadDashboard():AsyncTask<Void, Void, Void>(){
            override fun doInBackground(vararg params: Void?): Void? {
                RetrofitClient.instance.dashboard(phone)
                    .enqueue(object :Callback<DashboardResponse>{
                        override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                            progress_circular.visibility = INVISIBLE
                            toast(this@DashboardActivity, getString(R.string.error_msg))
                        }

                        override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                            progress_circular.visibility = INVISIBLE

                            FirebaseCrashlytics.getInstance().log(response.code().toString())
                            FirebaseCrashlytics.getInstance().log(response.body()?.bal!!)

                            if (response.code() == 200)
                                if (!response.body()?.error!!)
                                    mBal = response.body()?.bal!!
                            tv_bal.text = mBal
                        }

                    })
                return null
            }

        }

        LoadDashboard().execute()
    }

    fun sendRequest(view: View) {
        val amount = et_amount.text.toString()
        val method = et_method.text.toString()

        if (et_amount.text.toString().isEmpty()) {
            et_amount.error = "এমাউন্ট দিন"
            et_amount.requestFocus()
            return
        }

        if (method.isNullOrEmpty()) {
            et_method.error = "মেথড দিন"
            et_method.requestFocus()
            return
        }

        if (amount.toFloat() > mBal.toFloat()) {
            et_amount.error = "পর্যাপ্ত ব্যালেন্স নাই"
            et_amount.requestFocus()
            return
        }


        progress_circular.visibility = VISIBLE

        RetrofitClient.instance.payout(phone, amount, method)
            .enqueue(object : Callback<PayoutResponse> {
                override fun onFailure(call: Call<PayoutResponse>, t: Throwable) {
                    progress_circular.visibility = INVISIBLE
                    toast(this@DashboardActivity, getString(R.string.error_msg))
                }

                override fun onResponse(call: Call<PayoutResponse>, response: Response<PayoutResponse>) {
                    progress_circular.visibility = INVISIBLE
                    if (response.code() == 200)
                        toast(this@DashboardActivity, response.body()?.message.toString())
                    loadDashboard()
                }

            })


    }
}
