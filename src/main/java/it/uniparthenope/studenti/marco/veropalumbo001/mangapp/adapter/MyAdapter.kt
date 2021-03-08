package it.uniparthenope.studenti.marco.veropalumbo001.mangapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.R
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.CartVolume
import it.uniparthenope.studenti.marco.veropalumbo001.mangapp.model.ReceivedVolumes

class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var myList = emptyList<ReceivedVolumes>()

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener { v: View ->
                val temp1 = itemView.findViewById<TextView>(R.id.volumeTitle).text.toString()
                val temp2 = itemView.findViewById<TextView>(R.id.volumeNumber).text.toString().toInt()
                val temp3 = itemView.findViewById<TextView>(R.id.finalPrice).text.toString().toFloat()
                val temp4 = CartVolume(temp1, temp2, temp3)
                Carrello?.add(temp4)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false))
    }

    override fun getItemCount(): Int {
        return myList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.volumeTitle).text = myList[position].TITOLO
        holder.itemView.findViewById<TextView>(R.id.volumeNumber).text = myList[position].NUMERO.toString()
        holder.itemView.findViewById<TextView>(R.id.publisherName).text = myList[position].NOMEA
        holder.itemView.findViewById<TextView>(R.id.publicationDate).text = myList[position].DATA_DI_PUBBLICAZIONE
        holder.itemView.findViewById<TextView>(R.id.finalPrice).text = myList[position].PREZZO_FINALEV.toString()
        holder.itemView.findViewById<TextView>(R.id.rimanenti).text = myList[position].RIMANENTI.toString()
    }

    fun setData(newList: List<ReceivedVolumes>){
        myList = newList
        notifyDataSetChanged()
    }
}