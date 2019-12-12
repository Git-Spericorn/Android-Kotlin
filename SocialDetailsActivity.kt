package com.apps.anurag.myroomdb

import android.app.ActionBar
import android.app.Dialog
import android.databinding.DataBindingUtil
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Gravity
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.apps.anurag.myroomdb.Adapter.SocialAdapter
import com.apps.anurag.myroomdb.DatabaseClient.DatabaseClient
import com.apps.anurag.myroomdb.Interfaces.EditEntryListener
import com.apps.anurag.myroomdb.databinding.ActivitySocialDetailsBinding
import com.apps.anurag.myroomdb.model.SocialUser


class SocialDetailsActivity : BaseActivity() , EditEntryListener {

    //Declaring variables for using globally within this class.
    private lateinit var socialDetailsBinding: ActivitySocialDetailsBinding
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var socialAdapter: SocialAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var socialList : MutableList<SocialUser>
    private lateinit var simpleItemTouchCallback : ItemTouchHelper.Callback
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initializing data binding for this activity.
        socialDetailsBinding = DataBindingUtil.setContentView(this,R.layout.activity_social_details)

        //Setting up recyclerView's layout manager.
        linearLayoutManager = LinearLayoutManager(this)
        socialDetailsBinding.recyclerSocial.layoutManager = linearLayoutManager

        //Initializing the list for setting to recyclerView.
        socialList = mutableListOf()

        //Calling the function that handles swiping gesture for recyclerView items.
        swipingFeature()

        //Calling the function for fetching and displaying datas to recyclerView.
        viewDetails()

        //Showing SnackBar for acknowledging user about swiping feature.
        snackbar =  Snackbar.make(findViewById(R.id.myConstrain),"Swipe Left/Right to remove items",Snackbar.LENGTH_LONG)
        snackbar.setAction("OK") {

            run {

                snackbar.dismiss()
            }
        }
        snackbar.show()

        itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(socialDetailsBinding.recyclerSocial)

    }

    //Function for getting details from DB
    private fun viewDetails(){

        //Creating an AsyncTask for fetching datas in background as DB operations are costly (memory).
        class GetDetails : AsyncTask<Void, Void, List<SocialUser>>() {

            override fun doInBackground(vararg p0: Void?): List<SocialUser>? {

                //Creating a RoomDB client.
                val databaseClient = DatabaseClient(this@SocialDetailsActivity)

                //Fetching a list of data from DB to a list.
                val socialList = databaseClient.getInstance(this@SocialDetailsActivity)?.getSocialDatabase()?.
                    getSocialDao()?.getSocialDetails()
                return socialList
            }

            override fun onPostExecute(result: List<SocialUser>?) {
                super.onPostExecute(result)

                //Checking whether the returned data is null or not.
                if (result != null) {
                    
                    //Initializing the data to the list for setting to recyclerView.
                    socialList = result as MutableList<SocialUser>

                    //Setting adapter for recyclerView.
                    socialAdapter = SocialAdapter(this@SocialDetailsActivity, result,this@SocialDetailsActivity)
                    socialDetailsBinding.recyclerSocial.adapter = socialAdapter
                    socialAdapter.notifyDataSetChanged()

                }else{
                    
                    //Acknowledging user that the data is null.
                    toast("list is null")
                }
            }

        }

        //Executing the AsyncTask.
        val getDetails = GetDetails()
        getDetails.execute()

    }
 
    //Interface method for handling edit click events.
    override fun onEditClicked(id: String) {

        showDialog(id)

    }

    //Function for editing particular entry from DB.
    private fun editEntry(id : String,platformName : String,userName : String){

        //Creating an AsyncTask for editing datas in background as DB operations are costly (memory).
        class EditClass : AsyncTask<Void,Void,Void>(){

            override fun doInBackground(vararg void: Void?): Void? {

                //Creating a RoomDB client.
                val databaseClient = DatabaseClient(this@SocialDetailsActivity)
                
                //Editing the entry in RoomDB based on ID.
                databaseClient.getInstance(this@SocialDetailsActivity)?.getSocialDatabase()
                    ?.getSocialDao()?.updateEntry(id.toInt(),platformName,userName)

            return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)
                //toast("Updated successfully")

                snackbar.setText("Updated successfully").show()

                //Calling the function that fetches and shows datas in recyclerView.
                viewDetails()
            }
        }

        //Executing the AsyncTask.
        val editClass = EditClass()
        editClass.execute()

    }

    //Function that shows dialog prompt while clicking on edit.
    private fun showDialog(id: String){

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_dialog_layout)
        val window = dialog.window
        val wlp = window?.attributes
        wlp?.gravity = Gravity.CENTER
        wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_BLUR_BEHIND.inv())
        window?.attributes = wlp
        dialog.window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        dialog.show()

        //Initializing the views within the dialog prompt.
        val platform : EditText = dialog.findViewById(R.id.etDialogSocial)
        val userName : EditText = dialog.findViewById(R.id.etDialogUser)
        val update : Button = dialog.findViewById(R.id.btDialogUpdate)

        update.setOnClickListener {

            //Calling the edit function.
            editEntry(id,platform.text.toString(),userName.text.toString())
            dialog.dismiss()
        }

    }

    //Function for deleting entry from DB
    private fun deleteEntry(id : Int){

        //Creating an AsyncTask for deleting datas in background as DB operations are costly (memory).
        class DeleteClass : AsyncTask<Void,Void,Void>(){

            override fun doInBackground(vararg p0: Void?): Void? {

              //Creating a RoomDB client.
              val databaseClient = DatabaseClient(this@SocialDetailsActivity)

                 //Deleting entry from RoomDB based on ID.
                 databaseClient.getInstance(this@SocialDetailsActivity)?.
                  getSocialDatabase()?.getSocialDao()?.deleteSocial(id)

                return null
            }

            override fun onPostExecute(result: Void?) {
                super.onPostExecute(result)

                snackbar.setText("Removed").show()

                //Calling the function that fetches and shows datas in recyclerView.
                viewDetails()
            }
        }

        //Executing AsyncTask.
        val deleteEntry = DeleteClass()
        deleteEntry.execute()
    }

    //Function for enabling swipe action for recyclerView
    private fun swipingFeature(){

        simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                Toast.makeText(this@SocialDetailsActivity, "on Move", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView

                //Getting position & ID for the swiped item to delete from RoomDB.
                val position = viewHolder.adapterPosition
                val entryID = socialList[position].getId()

                //Calling the function that handles deleting the entry from RoomDB based on ID.
                deleteEntry(entryID)


            }
        }
    }

}
