package com.example.looquiz

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.act_myroom.*
import okhttp3.OkHttpClient
import org.json.JSONObject

class MyRoomActivity : AppCompatActivity() {

    //var quizlist: Array<String>? = null
    var quizlist = ArrayList<Quizlist>()
    var myroom_roomcodenum: String? = null
    var myroom_roomtitle:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_myroom)

        myroom_roomtitle = intent.getStringExtra("title")
        myroom_roomcodenum = intent.getStringExtra("codenum")

        myroom_title.text = myroom_roomtitle
        myroom_codenum.text = myroom_roomcodenum


        val quizlistAdapter = QuizlistAdapter(this, this.myroom_roomcodenum!!,quizlist)
        myroom_rv.adapter=quizlistAdapter

        val quizlistlm = LinearLayoutManager(this)
        myroom_rv.layoutManager =quizlistlm
        myroom_rv.setHasFixedSize(true)


        //멤버조회
        myroom_member.setOnClickListener {

            Asynctask().execute("2",getString(R.string.corplist))
            var builder = AlertDialog.Builder(this)
            builder.setTitle(myroom_roomtitle.toString())

            builder.setNegativeButton("닫기",null)
            //builder.setItems(quizlist,null)
            builder.show()
        }
        myroom_createquiz.setOnClickListener {
            startActivity(Intent(this,MakingQuizActivity::class.java))
            finish()
        }
    }
    inner class Asynctask: AsyncTask<String, Void, String>() {
        var response : String? = null

        override fun doInBackground(vararg params: String): String? {
            var client = OkHttpClient()
            var url = params[0]
            myroom_roomcodenum = params[1]
            response = Okhttp(applicationContext).POST(client,url,CreateJson().json_searchmem(myroom_roomcodenum))

            return response
        }

        override fun onPostExecute(result: String) {


            Log.d("checkcommu",result)
            if(!result.isNullOrEmpty())
                Log.d("checktest",result)

            if(!result[0].equals('{')) { //Json구문이 넘어오지 않을 시 Toast 메세지 출력 후 종료
                Toast.makeText(applicationContext,"네트워크 연결이 좋지 않습니다", Toast.LENGTH_SHORT).show()

                return
            }
            else{
                var json = JSONObject(result)

                if (json.getInt("message") == 1) {
                    var data = json.getJSONObject("data")
                    var uid  = data.getString("uid")
                    Toast.makeText(applicationContext,"${uid}님 환영합니다", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(applicationContext,MainActivity::class.java))
                    finish()
                }
                else {
                    Toast.makeText(applicationContext,"일치하는 정보가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}



