package com.eam.investire.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eam.investire.R
import com.eam.investire.adapter.AdapterMovimentacao.MyViewHolder
import com.eam.investire.model.Movimentacao

/**
 * Created by Jamilton Damasceno
 */
class AdapterMovimentacao(private var movimentacoes: MutableList<Movimentacao?>, var context: Context) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemLista = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movimentacao, parent, false)
        return MyViewHolder(itemLista)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val movimentacao = movimentacoes[position]
        holder.titulo.text = movimentacao!!.descricao
        holder.valor.text = movimentacao.valor.toString()
        holder.categoria.text = movimentacao.categoria
        holder.valor.setTextColor(context.resources.getColor(R.color.colorAccentReceita, null))
        if (movimentacao.tipo == "d") {
            holder.valor.setTextColor(context.resources.getColor(R.color.colorAccent, null))
            val valor = "-" + movimentacao.valor
            holder.valor.text = valor
        }
    }

    override fun getItemCount(): Int {
        return movimentacoes.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titulo: TextView = itemView.findViewById(R.id.textAdapterTitulo)
        var valor: TextView = itemView.findViewById(R.id.textAdapterValor)
        var categoria: TextView = itemView.findViewById(R.id.textAdapterCategoria)

    }
}