package com.example.looquiz

import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.act_mypage.*
import okhttp3.OkHttpClient
import org.json.JSONObject

class MyPageActivity : AppCompatActivity() {

    var corplist: Array<String>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_mypage)
        onPause()
        Log.d("lifecycle","oncreate")

        Asynctask().execute("1",getString(R.string.corplist))

        mypage_icon.setOnClickListener {
            Asynctask().execute("0",getString(R.string.badge))
            startActivity(Intent(this,BadgeList::class.java))
        }
        mypage_btnquizrate.setOnClickListener {
            startActivity(Intent(this,RegionList::class.java))
        }

        mypage_btncorplist.setOnClickListener {

            //Asynctask().execute("1",getString(R.string.corplist))
            var builder = AlertDialog.Builder(this)
            builder.setTitle(" 제휴 리스트 ")

            builder.setNegativeButton("닫기",null)
            builder.setItems(corplist,null)
            builder.show()

        }
    }

    override fun onResume() {
        super.onResume()
        //onStart()
        Log.d("lifecycle","onresume")

    }

    inner class Asynctask: AsyncTask<String, Void, String>() {
        var state = -1 //GET_badge = 0  ,GET_corplist = 1
        var response : String? = null

        override fun doInBackground(vararg params: String): String? {
            state = Integer.parseInt(params[0])
            var client = OkHttpClient()
            var url = params[1]

            if(state == 0){
                response = Okhttp(applicationContext).GET(client,url)

            }

            else {
                response = Okhttp(applicationContext).GET(client, url)
            }

            return response
        }

        override fun onPostExecute(result: String) {

            if(!result.isNullOrEmpty())
                Log.d("checktest",result)

            if(!result[0].equals('{')) { //Json구문이 넘어오지 않을 시 Toast 메세지 출력 후 종료
                Toast.makeText(applicationContext,"네트워크 연결이 좋지 않습니다", Toast.LENGTH_SHORT).show()

                return
            }
            else{
                var json = JSONObject(result)
                if (state == 1){
                    var jsonArray = json.getJSONArray("data")
                    var corpary = Array<String>(jsonArray.length(),{""})
                    for(i in 0 until jsonArray.length()){
                        corpary[i] = jsonArray.get(i).toString()
                    }
                    corplist = corpary
                }
            }
        }
    }
}
