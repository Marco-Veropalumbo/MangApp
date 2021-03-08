package it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.R
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CartVolume

class MyAdapterCart: RecyclerView.Adapter<MyAdapterCart.MyViewHolder>(){
    private var myList = emptyList<CartVolume>()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_carrello_row, parent, false))
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.volumeTitleCart).text = myList[position].titolo
        holder.itemView.findViewById<TextView>(R.id.volumeNumberCart).text = myList[position].numero.toString()
        holder.itemView.findViewById<TextView>(R.id.finalPriceCart).text = myList[position].prezzo.toString()
    }

    fun setData(newList: List<CartVolume>){
        myList = newList
        notifyDataSetChanged()
    }
}

var Carrello: MutableList<CartVolume>? = ArrayList()