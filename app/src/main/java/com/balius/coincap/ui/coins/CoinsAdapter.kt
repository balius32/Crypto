package com.balius.coincap.ui.coins

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.balius.coincap.R
import com.balius.coincap.model.model.Data
import java.text.DecimalFormat

class CoinsAdapter(
    private val context: Context,
    private val coins: List<Data>,
    private val listener :CoinActionListener
) : RecyclerView.Adapter<CoinsAdapter.CoinsVh>(){

    interface CoinActionListener{
        fun onCoinSelect(coinId : String)
    }


    class CoinsVh (itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtName: AppCompatTextView = itemView.findViewById(R.id.txt_name)
        val txtRank: AppCompatTextView = itemView.findViewById(R.id.txt_rank)
        val txtSymbol: AppCompatTextView = itemView.findViewById(R.id.txt_symbol)
        val txtChangePercent: AppCompatTextView = itemView.findViewById(R.id.txt_change_percent)
        val txtPrice: AppCompatTextView = itemView.findViewById(R.id.txt_price)
        val imgDropdown: AppCompatImageView = itemView.findViewById(R.id.img_dropdown)
        val imgDropUp: AppCompatImageView = itemView.findViewById(R.id.img_dropup)
        val layout: ConstraintLayout = itemView.findViewById(R.id.layout)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinsVh {
        val view: View = LayoutInflater.from(context).inflate(R.layout.coins_row, parent, false)
        return CoinsVh(view)
    }

    override fun getItemCount(): Int {
        return coins.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: CoinsVh, position: Int) {
        val coin = coins.get(position)

        holder.txtName.text = coin.name
        holder.txtRank.text = coin.rank
        holder.txtSymbol.text = coin.symbol



        val coinPrice = coin.priceUsd?.toDouble()
        val df = DecimalFormat("#,###.##")
        val formattedNumber = df.format(coinPrice)

        holder.txtPrice.text = "$$formattedNumber"



        val changePrice = coin.changePercent24Hr?.toDouble()


        val truncatedNumber = String.format("%.2f", changePrice).toDouble()


        if (changePrice!! >0){
            holder.imgDropUp.visibility = View.VISIBLE
            holder.imgDropdown.visibility = View.GONE
            holder.txtChangePercent.setTextColor(Color.GREEN)
            holder.txtChangePercent.text = "$truncatedNumber%"

        }else{
            holder.imgDropUp.visibility = View.GONE
            holder.imgDropdown.visibility = View.VISIBLE


            holder.txtChangePercent.setTextColor(Color.RED)

            val formattedNumber = truncatedNumber * -1
            holder.txtChangePercent.text = "$formattedNumber%"

        }


        holder.layout.setOnClickListener{
            listener.onCoinSelect(coin.id.toString())
        }




    }

}