package com.example.looquiz

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.skt.Tmap.TMapGpsManager
import com.skt.Tmap.TMapPoint
import com.skt.Tmap.TMapView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TMapGpsManager.onLocationChangedCallback  {

    private lateinit var mContext: Context
    private var m_bTrackingMode = true

    private lateinit var tMapGpsManager: TMapGpsManager
    private lateinit var tmap: TMapView

    //tMap API Key
    //private val tMapKey = resources.getString(R.string.tmapkey)
    private val tMapKey = ""
    private var mMarkerID=0

    private var mTMapPoint = arrayListOf<TMapPoint>()
    private var mTArrayMarkerID = arrayListOf<String>()
    private var mapPointList = arrayListOf<MapPoint>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)


        //지도 조회
        tmap = TMapView(applicationContext)
        tmap.setSKTMapApiKey(tMapKey)
        tmapview.addView(tmap)

        //사용자 현재 위치
        mContext = this

        addPoint()

        //현재 보는 방향
        tmap.setCompassMode(true)

        //현위치 아이콘 표시
        tmap.setIconVisibility(true)

        //줌레벨
        tmap.setZoom(15f)
        //tmap.mapType(TMapView.MAPTYPE_STANDARD)
        tmap.setLanguage(TMapView.LANGUAGE_KOREAN)


        tMapGpsManager = TMapGpsManager(this)

        //tMapGpsManager.minTime(1000)
        //tMapGpsManager.minDistance(5)
        //tMapGpsManager.provider(NETWORK_PROVIDER)

        //tMapGpsManager.OpenGps()


        //화면 중심을 현재위치로 이동
        tmap.setTrackingMode(true)
        tmap.setSightVisible(true)
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
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            /*R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }*/
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    //TMap
    override fun onLocationChange(p0: Location?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        /*if (m_bTrackingMode){
            tmap.setLocationPoint(p0.longitude, p0.latitude)
        }*/
    }

    fun addPoint(){
        //mapPointList.add(MapPoint("경복궁",37.5903217,  126.9644442))
    }
}