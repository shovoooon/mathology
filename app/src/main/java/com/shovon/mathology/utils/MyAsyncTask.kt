package com.shovon.mathology.utils

import android.content.Context
import android.os.AsyncTask
import com.shovon.mathology.model.AddBalResponse
import com.shovon.mathology.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Itz Shovon on 5/6/2020
 */
class MyAsyncTask(mContext: Context, mPhone:String) : AsyncTask<Void, Void, Void>() {

    private var phone = ""
    private var context: Context

    init {
        this.phone = mPhone
        this.context = mContext
    }

    override fun doInBackground(vararg params: Void?): Void? {

        RetrofitClient.instance.addBal(phone)
            .enqueue(object :Callback<AddBalResponse>{
                override fun onFailure(call: Call<AddBalResponse>, t: Throwable) {
                    toast(context, "কিছু একটা গোলযোগ হয়েছে")
                }

                override fun onResponse(call: Call<AddBalResponse>, response: Response<AddBalResponse>) {
                    if (response.code() == 200)
                        if (!response.body()?.error!!)
                            toast(context, "আপনি টাকা পেয়েছেন")
                }

            })
        return null
    }
}
