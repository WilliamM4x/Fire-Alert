package com.DWSENAI.firealert

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.DWSENAI.firealert.R.layout.activity_main
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val CHANNEL_ID = "canal_id_test"
    private val idnotifica = 101
    var botaovl: ToggleButton? = null
    var botaotm: Button? = null
    var bt: Button? = null
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        val myRef3 = database.getReference("FireAlert/note3")
        val getnote3 = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var texto = StringBuilder()
                createNotificationChannel()
                val not3 = dataSnapshot.getValue()
                texto.append(not3)
                if (not3 == true) {
                    notifyUser2()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        var stats3 = myRef3.addValueEventListener(getnote3).toString()


        val myRef2 = database.getReference("FireAlert/note")
        val getnote = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var texto = StringBuilder()
                createNotificationChannel()
                val not = dataSnapshot.getValue()
                texto.append(not)
                if (not == true) {
                    notifyUser()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        var stats2 = myRef2.addValueEventListener(getnote).toString()

        val myRef = database.getReference("FireAlert/valvula")
        val getdata = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var texto = StringBuilder()

                val hert = dataSnapshot.getValue()
                texto.append(hert)
                if (hert == true) {
                    textView5.setText("Lig")
                } else {
                    textView5.setText("Dslg")
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        var stats = myRef.addValueEventListener(getdata).toString()

        botaovl = simpleToggleButton1
        botaovl!!.setOnClickListener {
            val st = "${botaovl!!.text}"
            val status = """ Alterar para : ${botaovl!!.text}""".trimIndent()
            //display the current state of toggle button's
            Toast.makeText(applicationContext, status, Toast.LENGTH_SHORT).show()
            bt = button
            bt!!.setOnClickListener() {
                if (st == "ON") {
                    myRef.setValue(true)
                } else {
                    myRef.setValue(0)
                }
            }
        }

        botaotm = this.bntime
        botaotm!!.setOnClickListener {
            val tm = editTextNumberDecimal.text.toString().toInt()           //transformei em texto, string, número//
            val time = database.getReference("FireAlert/time")
            time.setValue(tm)
            val tdefi = database.getReference("FireAlert/tdefine")
            tdefi.setValue(true)
            Toast.makeText(applicationContext, "Timer definido", Toast.LENGTH_SHORT).show()
        }

        val bn:CheckBox? =this.findViewById(R.id.checkBox)
           bn!!.setOnClickListener {
            val checked: Boolean = bn.isChecked()
            if(checked==true){
                val auto = database.getReference("FireAlert/auto")
                auto.setValue(true)
                Toast.makeText(applicationContext, "Sistema em modo automático", Toast.LENGTH_SHORT).show()}
            else{
                val auto = database.getReference("FireAlert/auto")
                auto.setValue(0)
                Toast.makeText(applicationContext, "Sistema modo automático desativado", Toast.LENGTH_SHORT).show()
            }
        }
    }
                    private fun createNotificationChannel() {
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val name = "notificacao teste"
                        val descriptionText = "notificaco descricao"
                        val importance = NotificationManager.IMPORTANCE_HIGH
                        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                            description = descriptionText
                        }
                        val notificationManager: NotificationManager =
                            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.createNotificationChannel(channel)
                      }
                    }

                  private fun notifyUser() {
                    val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("State Valvula")
                        .setContentText("A vavula foi acionada!")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        with(NotificationManagerCompat.from(this)) {
                        notify(idnotifica, mBuilder.build())
                    }
                  }
                 private fun notifyUser2() {
                    val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                     .setContentTitle("Time out")
                     .setContentText("O tempo de cocção finalizado. Novo tempo pode ser definido!")
                     .setSmallIcon(R.mipmap.ic_launcher)
                     .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                     with(NotificationManagerCompat.from(this)) {
                     notify(idnotifica, mBuilder.build())
                   }
                 }
}



