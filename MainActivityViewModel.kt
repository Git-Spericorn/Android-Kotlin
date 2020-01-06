package com.anurag.mycoroutines.viewmodel

import android.util.Log
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.anurag.mycoroutines.adapter.TracePaymentAdapter
import com.anurag.mycoroutines.api.Api
import com.anurag.mycoroutines.model.moshimodel.IncomingTracePayModel
import com.anurag.mycoroutines.model.moshimodel.TracePayModel
import com.anurag.mycoroutines.utils.AppConstants
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Anurag on 08,November,2019
 */
class MainActivityViewModel : ViewModel() {

    private var paymentMutableList : MutableLiveData<TracePayModel> = MutableLiveData()

    fun getIncomingTracePaymentList() : MutableLiveData<TracePayModel>{

            Api.apiMethods.getIncomingListAsync(AppConstants.CONTENT_TYPE,AppConstants.BEARER+
                    "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6Imt1dHR1QG1haWxpbmF0b3IuY29tIiwicGFzc3dvcmQiOiIkYXJnb24yaSR2PTE5JG09MTAyNDAwLHQ9MixwPTgkTVRGak5qUTRNMkl6TlRjd05ESmhZemsyWXpFd05UZzFPVEZqWVRjMk1ETSRNMERGRVM0Vk9lMHJtSHZYcXJkNnBRIiwiaWF0IjoxNTc0NjcyMDQ4LCJleHAiOjE1NzQ3NTg0NDh9.UcGugydBxcbtIGkY2ARAIw4OcSup3tTYEAyuMC_fyVpL8Dzs9L_6h36lOVHGl_vC3Ls8ndGgipoxPCCfNrpdDQ",
                "2755", "1", "2670","","").enqueue(object : Callback<TracePayModel> {

                    override fun onResponse(call: Call<TracePayModel>, response: Response<TracePayModel>) {

                        response.let {

                            it.body()?.let { resBody ->

                                if (resBody.isSuccess){

                                    paymentMutableList.value = response.body()
                                }
                            }
                        }

                    }

                    override fun onFailure(call: Call<TracePayModel>, t: Throwable) {

                        t.printStackTrace()
                    }
                })

        return paymentMutableList
    }

}
