package com.example.looquiz


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_region.view.*


class RegionBaseAdapter(val context: Context,val list:List<String?>) : BaseAdapter (){

    override fun getView(position:Int, convertView: View?, parent: ViewGroup?):View{

        val inflater = parent?.context?.
            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.region_custom_view,null)


        val tv = view.findViewById<TextView>(R.id.tv_name)
        val card = view.findViewById<CardView>(R.id.card_view)

        tv.text = list[position]

        card.setOnClickListener{
            Toast.makeText(parent.context,
                "Clicked : ${list[position]}",Toast.LENGTH_SHORT).show()

            val activity  = parent.context as Activity

            val viewGroup = activity.findViewById<ViewGroup>(android.R.id.content)
                .getChildAt(0)

            //클릭하면 이미지뷰가 변하는 코드
            //viewGroup.imageView.setImageResource(R.drawable.suwon)
            var intent = Intent(context,QuizRateActivity::class.java)
            intent.putExtra("cityname",list[position])
            context.startActivity(intent)
        }

        return view
    }
    override fun getItem(position: Int): Any? {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }
}