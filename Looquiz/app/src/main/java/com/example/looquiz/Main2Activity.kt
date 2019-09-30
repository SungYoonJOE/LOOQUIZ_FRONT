package com.example.looquiz

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.dia_hint.view.*
import kotlinx.android.synthetic.main.dia_quiz.view.*
import okhttp3.OkHttpClient
import org.json.JSONObject

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var gMap: GoogleMap? = null
    var locManager: LocationManager? = null
    //lateinit var desLng: LatLng
    lateinit var userLoc: Location
    var distance = 0.0
    var quizList:Array<String>? = null

    var quest1 = LatLng(37.575840, 126.977009)

    var permission_list = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    )
    var regionList: Array<String>? = null
    lateinit var regionListBuilder: AlertDialog.Builder

    var checkAllQuiz = mutableSetOf<Int>()

    var markerOpt1 = MarkerOptions().position(LatLng(37.575840, 126.977009))//광화문
    var markerOpt2 = MarkerOptions().position(LatLng(37.5834703,126.9752437)) //신무문
    var markerOpt3 = MarkerOptions().position(LatLng(37.5760892,126.9793858)) //동십자각
    var markerOpt4 = MarkerOptions().position(LatLng(37.5785225,126.9769845))//근정전
    var markerOpt5 = MarkerOptions().position(LatLng(37.5790885,126.977044))//사정전
    var markerOpt6 = MarkerOptions().position(LatLng(37.580314, 126.978075)) //자경전
    var markerOpt7 = MarkerOptions().position(LatLng(37.5798388,126.975708))//경회루
    var markerOpt8 = MarkerOptions().position(LatLng(37.5825542,126.9741758)) //태원전
    var markerOpt9 = MarkerOptions().position(LatLng(37.583146, 126.977261)) //건청궁
    var markerOpt10 = MarkerOptions().position(LatLng(37.5759947, 126.9746318))//국립고궁박물관

    var marker1:Marker? = null
    var marker2:Marker? = null
    var marker3:Marker? = null
    var marker4:Marker? = null
    var marker5:Marker? = null
    var marker6:Marker? = null
    var marker7:Marker? = null
    var marker8:Marker? = null
    var marker9:Marker? = null
    var marker10:Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main2)
        setSupportActionBar(toolbar)

        var colorAlpha = Color.parseColor("#80FFFFFF") //app bar 투명도 설정을 위한 컬러 코드 정수화
        toolbar.setBackgroundColor(colorAlpha) //app bar 투명도 설정을 위한 배경색 임시 설정 이 두개 코드 지워도댐


        val intent = getIntent()
        region.text = intent.getStringExtra("regionName")
        //var regionName = intent.getStringExtra("regionName")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0)
        } else {
            init()
        }

        //로그아웃
        var navigationView = findViewById<NavigationView>(R.id.nav_view)
        var view:View = navigationView.getHeaderView(0)
        var userid=view.findViewById<TextView>(R.id.uid)

        /*var uid:String? = null
        uid = intent.getStringExtra("uid")
        userid.text = "${uid}" + " 님"*/

        /*var btn_logout = view.findViewById<Button>(R.id.btn_logout)
        btn_logout.setOnClickListener {
            Asynctask().execute("6",getString(R.string.logout))
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_LONG).show()
            startActivity(Intent(this,SignInActivity::class.java))
            finish()
        }*/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.popup_menu, menu)

        var search_item:MenuItem? = menu?.findItem(R.id.app_bar_search)
        var search_view= search_item?.actionView as android.support.v7.widget.SearchView

        search_view.queryHint = "참가코드를 입력해주세요."

        search_view.setOnQueryTextListener(object: android.support.v7.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextSubmit(search_word: String?): Boolean {
                //방 참여 통신
                Asynctask().execute("1",getString(R.string.enter_room), search_word.toString())
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_seoul -> {
                showRegionList(item.title.toString())
                return true
            }

            R.id.action_gyeongsang -> { //경주
                showRegionList(item.title.toString())
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showRegionList(region: String){
        regionListBuilder = AlertDialog.Builder(this)
        regionListBuilder.setTitle(region+" 리스트")
        Asynctask().execute("4", getString(R.string.cityname), region)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_mypage -> {
                startActivity(Intent(this, MyPageActivity::class.java))
            }
            R.id.nav_room -> {
                startActivity(Intent(this, RoomChoose::class.java))
            }
            R.id.nav_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return
            }
        }
        init()
    }

    fun init() {
        var callback = MapReadyCallback()
        var mapFragment = supportFragmentManager.findFragmentById(R.id.regionmapview) as SupportMapFragment
        mapFragment.getMapAsync(callback)
    }

    fun makeQuizListDia(regionName: String?, quizIdList: List<Int>){
        var quizListBuilder = AlertDialog.Builder(this@Main2Activity)
        var quizListDiaView = layoutInflater.inflate(R.layout.dia_quizlist, null)
        var quizListBuilder2:AlertDialog = quizListBuilder.create()

        var idList = arrayOfNulls<String>(quizIdList.size)
        for(i in 0 .. quizIdList.size-1){
            idList[i] = quizIdList[i].toString()
        }

        var quizChoiceListener = object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Log.d("which ", ""+which)
                Log.d("idList which", ""+idList[which])
                quizListBuilder2.dismiss()
                Asynctask().execute("1", getString(R.string.before_quiz), idList[which])

            }
        }
        quizListDiaView.region_name.text = regionName
        quizListBuilder.setCustomTitle(quizListDiaView)
        quizListBuilder.setSingleChoiceItems(idList, -1, quizChoiceListener)
        quizListBuilder.setPositiveButton("닫기", null)
        quizListBuilder.show()
    }

    fun makeQuizListDia2(regionName: String?, quizIdMapList: MutableList<Pair<Int, String>>){
        var quizListBuilder = AlertDialog.Builder(this@Main2Activity)
        var quizListDiaView = layoutInflater.inflate(R.layout.dia_quizlist, null)
        var quizListBuilder2:AlertDialog = quizListBuilder.create()

        var idList = arrayOfNulls<String>(quizIdMapList.size)
        for(i in 0 .. quizIdMapList.size-1){
            idList[i] = quizIdMapList[i].second
        }
        var idMapList:MutableList<Pair<Int, String>> = quizIdMapList

        var quizChoiceListener = object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                quizListBuilder2.dismiss()
                Asynctask().execute("1", getString(R.string.before_quiz), idMapList[which].first.toString())

            }
        }
        quizListDiaView.region_name.text = regionName
        quizListBuilder.setCustomTitle(quizListDiaView)
        quizListBuilder.setSingleChoiceItems(idList, -1, quizChoiceListener)
        quizListBuilder.setPositiveButton("닫기", null)
        quizListBuilder.show()
    }
    //현재위치 측정
    fun getMyLocation() {
        locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }

        var location = locManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        var location2 = locManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (location != null) {
            setMyLocation(location)
        } else {
            if (location2 != null) {
                setMyLocation(location2)
            }
        }
        var listener = GetMyLocationListener()

        if (locManager?.isProviderEnabled(LocationManager.GPS_PROVIDER)!! == true) {
            locManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 15f, listener)
        } else if (locManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER)!! == true) {
            locManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 15f, listener)
        }
    }

    inner class MapReadyCallback : OnMapReadyCallback {
        override fun onMapReady(p0: GoogleMap?) {
            gMap = p0
            getMyLocation()
            gMap!!.setPadding(20,190,10,0)
            Log.d("현위치 >>", "userLoc ${userLoc}")

            p0?.animateCamera(CameraUpdateFactory.zoomTo(15f))
            p0?.moveCamera(CameraUpdateFactory.newLatLng(quest1))
            marker1 = p0?.addMarker(markerOpt1)
            marker1?.title = "광화문"
            marker2 = p0?.addMarker(markerOpt2)
            marker2?.title = "신무문"
            marker3 = p0?.addMarker(markerOpt3)
            marker3?.title = "동십자각"
            marker4 = p0?.addMarker(markerOpt4)
            marker4?.title = "근정전"
            marker5 = p0?.addMarker(markerOpt5)
            marker5?.title = "사정전"
            marker6 = p0?.addMarker(markerOpt6)
            marker6?.title = "자경전"
            marker7 = p0?.addMarker(markerOpt7)
            marker7?.title = "경회루"
            marker8 = p0?.addMarker(markerOpt8)
            marker8?.title = "태원전"
            marker9 = p0?.addMarker(markerOpt9)
            marker9?.title = "건청궁"
            marker10 = p0?.addMarker(markerOpt10)
            marker10?.title = "국립 고궁 박물관"

            Asynctask().execute("0", getString(R.string.region_quizlist), intent.getStringExtra("regionName"))

            p0?.setOnInfoWindowClickListener {
                distance = getDistance(userLoc, it.position)
                Log.d("최종 거리계산 >> ", "${distance}km")

                //var mark_taglist = mutableListOf<Int>()
                var marker_taglist =  mutableListOf<Pair<Int, String>>()
                //               if(distance<= 70) {
                when (it) {
                    marker1 -> {
                        //mark_taglist = marker1?.tag as MutableList<Int>
                        marker_taglist = marker1?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size > 1){
                            makeQuizListDia2(marker1?.title, marker_taglist)
                            //makeQuizListDia(marker1?.title, mark_taglist)
                        }else{
                            makeQuiz(marker1?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker2 -> {
                        marker_taglist = marker2?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker2?.title, marker_taglist)
                        }else{
                            makeQuiz(marker2?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker3 -> {
                        marker_taglist = marker3?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size > 1){
                            makeQuizListDia2(marker3?.title, marker_taglist)
                            //makeQuizListDia(marker1?.title, marker_taglist)
                        }else{
                            makeQuiz(marker3?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker4 -> {
                        marker_taglist = marker4?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker4?.title, marker_taglist)
                        }else{
                            makeQuiz(marker4?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker5 -> {
                        marker_taglist = marker5?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker5?.title, marker_taglist)
                        }else{
                            makeQuiz(marker5?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker6 -> {
                        marker_taglist = marker6?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker6?.title, marker_taglist)
                        }else{
                            makeQuiz(marker6?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker7 -> {
                        marker_taglist = marker7?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker7?.title, marker_taglist)
                        }else{
                            makeQuiz(marker7?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker8 -> {
                        marker_taglist = marker8?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker8?.title, marker_taglist)
                        }else{
                            makeQuiz(marker8?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker9 -> {
                        marker_taglist = marker9?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker9?.title, marker_taglist)
                        }else{
                            makeQuiz(marker9?.title, marker_taglist.get(0).first)
                        }
                    }
                    marker10 -> {
                        marker_taglist = marker10?.tag as MutableList<Pair<Int, String>>
                        if(marker_taglist.size>1){
                            makeQuizListDia2(marker10?.title, marker_taglist)
                        }else{
                            makeQuiz(marker10?.title, marker_taglist.get(0).first)
                        }
                    }
                }
//                }else Toast.makeText(applicationContext, "문제를 풀 수 없습니다.", Toast.LENGTH_LONG).show()
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    return
                }
                if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                    return
                }
            }
            p0?.isMyLocationEnabled = true
            p0?.mapType = GoogleMap.MAP_TYPE_NORMAL //기본
        }
    }

    fun makeQuiz(regionName: String?, quizId: Int) {
        Asynctask().execute("1",getString(R.string.before_quiz), quizId.toString())
    }

    fun getDistance(nowLoc: Location, latlng2: LatLng): Double {
        var startLoc = nowLoc
        val endLoc = Location("PointB")
        endLoc.latitude = latlng2.latitude
        endLoc.longitude = latlng2.longitude
        //var distance = startLoc.distanceTo(endLoc).toDouble()
//      Log.d("거리계산>>", "${distance}")
        return startLoc.distanceTo(endLoc).toDouble()
    }

    fun setMyLocation(location: Location) {
        var userLng = LatLng(location.latitude, location.longitude) //현위치
        userLoc = location
        //distance = getDistance(location, desLng)

        var update1 = CameraUpdateFactory.newLatLng(userLng)
        var update2 = CameraUpdateFactory.zoomTo(15f)

        gMap?.moveCamera(update1)
        gMap?.animateCamera(update2)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return
            }
        }
        gMap?.isMyLocationEnabled = true
        gMap?.mapType = GoogleMap.MAP_TYPE_NORMAL //기본
    }

    inner class GetMyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location?) {
            setMyLocation(location!!)
            locManager?.removeUpdates(this)
        }
        override fun onProviderDisabled(provider: String?) {
        }
        override fun onProviderEnabled(provider: String?) {
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }
    }

    inner class Asynctask: AsyncTask<String, Void, String>() {
        var state = -1 //POST_regionQuizList= 0 , POST_quizQuestion = 1 POST_takeQuiz = 2, POST_getUserBadge = 3
        var response:String? = null // state 4 전체 지역 리스트 조회, state 5 방 참가 state 6 로그아웃

        override fun doInBackground(vararg params: String?): String? {
            state = Integer.parseInt(params[0])
            var client = OkHttpClient()
            var url = params[1]

            if(state == 0){ //퀴즈 리스트 조회
                var rname = params[2]
                response = Okhttp(applicationContext).POST(client, url, CreateJson().json_regionquizlist(rname))
            } else if(state == 1){ //퀴즈 문제 조회
                var quizId = params[2].toString().toInt()
                response = Okhttp(applicationContext).POST(client, url,CreateJson().json_beforequiz(quizId))
            } else if( state ==2){
                var rname = params[2]
                var quiz_result = params[3].toString().toInt()
                response = Okhttp(applicationContext).POST(client, url, CreateJson().json_afterquiz(rname, quiz_result))
            }else if(state == 3){
                var rname = params[2]
                response = Okhttp(applicationContext).POST(client, url, CreateJson().json_getbadge(rname))
            } else if(state == 4){
                var cityname = params[2]
                url = url + "${cityname}"
                Log.d("확인2", url)
                response = Okhttp(applicationContext).GET(client, url)
            } else if(state == 5){
                var codenum = params[2]
                response = Okhttp(applicationContext).POST(client, url, CreateJson().json_enterroom(codenum))
            }
            Log.d("통신 결과 response >> ", response)
            return response
        }

        override fun onPostExecute(result: String) {
            if(!result.isNullOrEmpty()) Log.d("checktest", result)

            if(!result[0].equals('{')) { //Json구문이 넘어오지 않을 시 Toast 메세지 출력 후 종료
                Toast.makeText(applicationContext,"네트워크 연결이 좋지 않습니다", Toast.LENGTH_SHORT).show()
                return
            } else{
                var json = JSONObject(result)
                if(state == 0){
                    if(json.getInt("message") == 1){
                        var jsonArray = json.getJSONArray("data")

                        var tag1List = mutableListOf<Pair<Int, String>>()
                        var tag2List = mutableListOf<Pair<Int, String>>()
                        var tag3List = mutableListOf<Pair<Int, String>>()
                        var tag4List = mutableListOf<Pair<Int, String>>()
                        var tag5List = mutableListOf<Pair<Int, String>>()
                        var tag6List = mutableListOf<Pair<Int, String>>()
                        var tag7List = mutableListOf<Pair<Int, String>>()
                        var tag8List = mutableListOf<Pair<Int, String>>()
                        var tag9List = mutableListOf<Pair<Int, String>>()
                        var tag10List = mutableListOf<Pair<Int, String>>()

                        for(i in 0..jsonArray.length()-1){
                            Log.d("obj", ""+jsonArray.getJSONObject(i))
                            var jsonObj = jsonArray.getJSONObject(i)

                            if(jsonObj.get("dname") == marker1?.title){
                                tag1List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker1?.tag = tag1List

                            } else if(jsonObj.get("dname") == marker2?.title){
                                tag2List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker2?.tag = tag2List

                            } else if(jsonObj.get("dname") == marker3?.title){
                                tag3List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker3?.tag = tag3List

                            } else if(jsonObj.get("dname") == marker4?.title){
                                tag4List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker4?.tag = tag4List

                            } else if(jsonObj.get("dname") == marker5?.title){
                                tag5List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker5?.tag = tag5List

                            } else if(jsonObj.get("dname") == marker6?.title){
                                tag6List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker6?.tag = tag6List

                            } else if(jsonObj.get("dname") == marker7?.title){
                                tag7List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker7?.tag = tag7List

                            } else if(jsonObj.get("dname") == marker8?.title){
                                tag8List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker8?.tag = tag8List

                            } else if(jsonObj.get("dname") == marker9?.title){
                                tag9List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker9?.tag = tag9List

                            } else if(jsonObj.get("dname") == marker10?.title){
                                tag10List.add(Pair(jsonObj.get("qid").toString().toInt(), jsonObj.get("qname").toString()))
                                marker10?.tag = tag10List
                            }
                        }
                    } else{ //state=0, message = 0
                        Log.d("Fail 0", " 0 네트워크 연결이 좋지 않음")
                        Toast.makeText(applicationContext, "네트워크 연결이 좋지 않습니다.", Toast.LENGTH_LONG).show()
                    }
                } else if(state == 1){
                    if (json.getInt("message") == 1){
                        var jsonObject = json.getJSONObject("data")

                        var quizBuilder = AlertDialog.Builder(this@Main2Activity)
                        var diaQuizView = layoutInflater.inflate(R.layout.dia_quiz, null)
                        var quizBuilder2: AlertDialog = quizBuilder.create()

                        var checkListener = object :DialogInterface.OnClickListener{
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                if (checkAllQuiz.size == 7){
                                    var quiz_completion_builder = AlertDialog.Builder(this@Main2Activity)
                                    var quiz_completion_view = layoutInflater.inflate(R.layout.dia_completion, null)

                                    var quiz_badge_listener = object :DialogInterface.OnClickListener{
                                        override fun onClick(dialog: DialogInterface?, which: Int) {
                                            Asynctask().execute("3", getString(R.string.get_badge), region.text.toString())
                                        }
                                    }
                                    quiz_completion_builder.setCustomTitle(quiz_completion_view)
                                    quiz_completion_builder.setPositiveButton("확인", quiz_badge_listener)
                                    quiz_completion_builder.show()
                                }
                            }
                        }
                        var quizAnsBuilder = AlertDialog.Builder(this@Main2Activity)
                        var quizAnsView = layoutInflater.inflate(R.layout.dia_ans, null)
                        quizAnsView.findViewById<TextView>(R.id.region_name).text = jsonObject.get("dname").toString()
                        quizAnsBuilder.setView(quizAnsView)
                        quizAnsBuilder.setPositiveButton("확인", checkListener)

                        var quizWAnsBuilder = AlertDialog.Builder(this@Main2Activity)
                        var quizWAnsView = layoutInflater.inflate(R.layout.dia_wans, null)
                        quizWAnsView.findViewById<TextView>(R.id.region_name).text = jsonObject.get("dname").toString()
                        quizWAnsBuilder.setView(quizWAnsView)
                        quizWAnsBuilder.setPositiveButton("닫기", null)

                        var hintBuilder = AlertDialog.Builder(this@Main2Activity)
                        var diaHintView = layoutInflater.inflate(R.layout.dia_hint, null)
                        hintBuilder.setView(diaHintView)
                        hintBuilder.setPositiveButton("확인", null)

                        var btnHint: Button = diaQuizView.findViewById<Button>(R.id.btn_hint)
                        btnHint.setOnClickListener {
                            quizBuilder2.dismiss()
                            hintBuilder.show()
                        }
                        var selectItem = -1
                        var choiceListener = object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                selectItem = which
                            }
                        }
                        var diaListListener = object : DialogInterface.OnClickListener {
                            override fun onClick(dialog: DialogInterface?, which: Int) {
                                if (selectItem == -1) {
                                    Toast.makeText(this@Main2Activity, "항목을 선택해주세요", Toast.LENGTH_LONG).show()
                                    quizBuilder2.dismiss()
                                    makeQuiz(jsonObject.get("dname").toString(), jsonObject.get("qid").toString().toInt())
                                } else {
                                    quizBuilder2.dismiss()
                                    //정답일 경우
                                    if (selectItem+1 == jsonObject.get("answer").toString().toInt()){
                                        quizAnsBuilder.show()
                                        Asynctask().execute("2", getString(R.string.after_quiz), region.text.toString(), "1")
                                        checkAllQuiz?.add(jsonObject.get("qid").toString().toInt())
                                    }else{ //오답일 경우
                                        quizWAnsBuilder.show()
                                        checkAllQuiz?.remove(jsonObject.get("qid").toString().toInt())
                                        Asynctask().execute("2", getString(R.string.after_quiz), region.text.toString(), "0")
                                    }
                                }
                            }
                        }

                        diaQuizView.region_question.text= jsonObject.getString("qname")
                        quizList = arrayOf(jsonObject.getString("qcontent1"), jsonObject.getString("qcontent2"),
                            jsonObject.getString("qcontent3"), jsonObject.getString("qcontent4"), jsonObject.getString("qcontent5"))
                        diaHintView.quiz_hint.text = jsonObject.getString("hcontent")

                        diaQuizView.findViewById<TextView>(R.id.region_name).text = jsonObject.get("dname").toString()
                        quizBuilder.setCustomTitle(diaQuizView)
                        quizBuilder.setSingleChoiceItems(quizList, -1, choiceListener)
                        quizBuilder.setPositiveButton("확인", diaListListener)
                        quizBuilder.setNegativeButton("취소", null)
                        quizBuilder.show()
                    } else {
                        Log.d("Fail 1", " 1 네트워크 연결이 좋지 않음")
                        Toast.makeText(applicationContext, "1 네트워크 연결이 좋지 않습니다.", Toast.LENGTH_LONG).show()
                    }
                } else if(state == 2){
                    if (json.getInt("message").equals(1)) {
                    } else {
                        Toast.makeText(applicationContext, "다시 시도해주십시오.", Toast.LENGTH_SHORT).show()
                    }
                }else if(state == 3){
                    if (json.getInt("message").equals(1)) {
                        Toast.makeText(applicationContext, "뱃지가 생성되었습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(applicationContext, "다시 시도해주십시오.", Toast.LENGTH_SHORT).show()
                    }
                } else if(state == 4){
                    if (json.getInt("message") == 1) {
                        var jsonArray = json.getJSONArray("data")
                        var regList = Array<String>(jsonArray.length(), { "" })

                        for (i in 0 until jsonArray.length()) {
                            regList?.set(i, jsonArray.get(i).toString())
                        }

                        regionList = regList
                        regionListBuilder.setItems(regionList, null)
                        //regionListBuilder.setSingleChoiceItems(regionList, -1, null)
                        regionListBuilder.setNegativeButton("닫기", null)
                        regionListBuilder.show()

                    } else { //message == 0
                        Toast.makeText(applicationContext, "네트워크 연결이 좋지 않습니다.", Toast.LENGTH_LONG).show()
                    }
                } else if(state == 5){
                    if (json.getInt("message") == 1) {
                        Toast.makeText(applicationContext, "성공적으로 가입되었습니다.", Toast.LENGTH_LONG).show()
                        startActivity(Intent(applicationContext, RoomChoose::class.java))
                    } else { //message == 0
                        Toast.makeText(applicationContext, "참가코드를 올바르게 입력해주세요", Toast.LENGTH_LONG).show()
                    }
                }

            }

        }
    }
}