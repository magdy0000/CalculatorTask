package com.example.calculatortask

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.media.RingtoneManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.calculatortask.data.local.MyDao
import com.example.calculatortask.ui.MainActivity
import com.example.calculatortask.utils.Const
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltWorker
class OperationWorker
@AssistedInject
constructor(
    @Assisted private val appContext: Context
    , @Assisted private val  workerParams: WorkerParameters
    ,private val myDao: MyDao) :
    CoroutineWorker(appContext, workerParams) {

    var res = 0.0
    override suspend fun doWork(): Result {

        val id =  inputData.getLong(Const.OPERATION_ID,0)
        val numbers =  inputData.getString(Const.OPERATION_NUMBERS)
        val operator =  inputData.getString(Const.OPERATION_OPERATOR)

        val result = getResult(operator!! , numbers!!)

        myDao.setResult(result , id)

        withContext(Main){
            sendNotification(appContext, "Operation Finished"
                , "The result is $result")
        }


        return Result.success()
    }
    private fun getResult(op: String , numbers : String) : String{

        return when (op) {
            "+" -> {
                add(numbers)
            }
            "-" -> {
                sub(numbers)
            }
            "*" -> {
                multi(numbers)
            }
            else -> {
                div(numbers.replace("0",""))
            }
        }

    }

    private fun add (numbers: String) : String{
        for (number in numbers){
            if (res == 0.0){
                res = number.toString().toDouble()
                continue
            }
            res += number.toString().toDouble()
        }
        return res.toString()
    }
    private fun sub (numbers: String) : String{
        for (number in numbers){
            if (res == 0.0){
                res = number.toString().toDouble()
                continue
            }
            res -= number.toString().toDouble()
        }
        return res.toString()
    }
    private fun multi (numbers: String) : String{

        for (number in numbers){
            if (res == 0.0){
                res = number.toString().toDouble()
                continue
            }
            res *= number.toString().toDouble()
        }
        return res.toString()
    }
    private fun div (numbers: String) : String{

        for (number in numbers){
            if (res == 0.0){
                res = number.toString().toDouble()
                continue
            }

            res /= number.toString().toDouble()
        }
        return res.toString()
    }

    private fun sendNotification(mContext: Context
                                 , title: String
                                 , messageBody: String
                                 ) {
        val sb: Spannable = SpannableString(title)
        sb.setSpan(StyleSpan(Typeface.BOLD), 0, title.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


         var intent = Intent(mContext, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            mContext,
            0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        val notificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "math reminder"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notfication",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "math"
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            channel.vibrationPattern = longArrayOf(0, 1000, 500, 1000)
            notificationManager.createNotificationChannel(channel)
        }
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(mContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setVibrate(longArrayOf(0, 1000, 500, 1000))
            .setStyle(
                NotificationCompat.InboxStyle()
                    .setBigContentTitle(sb)
                    .addLine(messageBody)
            ).setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        // Since android Oreo notification channel is needed.
        notificationManager.notify(System.currentTimeMillis().toInt() /* ID of notification */, notificationBuilder.build())
    }

}
