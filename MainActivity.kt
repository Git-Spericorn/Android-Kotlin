package com.anurag.mycoroutines.view

import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.anurag.mycoroutines.R
import com.anurag.mycoroutines.adapter.TracePaymentAdapter
import com.anurag.mycoroutines.databinding.ActivityMainBinding
import com.anurag.mycoroutines.model.moshimodel.IncomingTracePayModel
import com.anurag.mycoroutines.model.moshimodel.TracePayModel
import com.anurag.mycoroutines.viewmodel.MainActivityViewModel
import kotlinx.coroutines.*

class MainActivity : BaseActivity() {

	//Declaring variables for binding & view model.
    private lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     	
     	//Initializing and setting up binding for this activity.
     	activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

     	//Setting layout manager for recyclerView.
        val linearLayoutManager = LinearLayoutManager(this)
        activityMainBinding.recyclerTracePayList.layoutManager = linearLayoutManager

        //Initializing & Setting LifeCycle Owner for ViewModel.
        mainViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        //Creating a Coroutine to run in IO thread for handling network request.
        CoroutineScope(Dispatchers.IO).launch {

            Log.e("ThreadName 0", Thread.currentThread().name)

           //Initializing a list with the response from the network request.
           val payList = mainViewModel.getIncomingTracePaymentList()

	    //Performing a coroutine within the Main Thread.
            withContext(Dispatchers.Main){

                Log.e("ThreadName 1", Thread.currentThread().name)

		//Observing for the changes in the data with the help of ViewModel.   
                payList.observe(this@MainActivity,Observer<TracePayModel>{

                        tracePayModel ->

                    if (tracePayModel != null){

                        if(tracePayModel.DATA != null){

                            if (tracePayModel.DATA?.IncomingTracePayment!!.isNotEmpty()){

                                tracePayModel.DATA?.IncomingTracePayment?.let {
				 
				    //Setting the data to recyclerView if the response is not null.
                                    val paymentList : MutableList<IncomingTracePayModel> = ArrayList()
                                    paymentList.addAll(it)
                                    val tracePaymentAdapter = TracePaymentAdapter(paymentList)
                                    activityMainBinding.recyclerTracePayList.adapter = tracePaymentAdapter
                                    tracePaymentAdapter.notifyDataSetChanged()
                                }

                            }
                        }
                    }
                })
            }


        }




    }
}
