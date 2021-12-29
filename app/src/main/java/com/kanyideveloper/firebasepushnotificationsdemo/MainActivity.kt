package com.kanyideveloper.firebasepushnotificationsdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.kanyideveloper.firebasepushnotificationsdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

const val TOPIC = "/topics/myTopic"

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseService.sharedPreferences = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            binding.edtToken.setText(it.token)
        }

        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)



        binding.buttonSend.setOnClickListener {
            val title = binding.edtTitle.text.toString()
            val message = binding.edtMessage.text.toString()
            val recipientToken = binding.edtToken.text.toString()

            if (title.isNotEmpty() && message.isNotEmpty() && recipientToken.isNotEmpty()) {

                // Sending to a specific device

                PushNotification(
                    NotificationData(title, message),
                    recipientToken
                ).also {
                    sendNotification(it)
                }

                // Sending Notification to a given Topic

                /*PushNotification(
                        NotificationData(title,message),
                        TOPIC
                    ).also {
                        sendNotification(it)
                    }*/
            }
        }
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d(TAG, "response: ${Gson().toJson(response)}")
                } else {
                    Log.d(TAG, response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.d(TAG, e.toString())
            }
        }
}