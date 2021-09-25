package com.example.calculatortask.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calculatortask.R
import com.example.calculatortask.data.models.OperationModel


class AdapterRecyclerOperations : RecyclerView.Adapter<AdapterRecyclerOperations.Holder>() {


    var list : ArrayList<OperationModel> ?= null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_operation, parent , false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val data = list?.get(position)

        holder.apply {
            data?.apply {
                if (data.result == ""){
                    textResult.text = holder.itemView.context.getString(R.string.pending)
                }else{
                  textResult.text = result
                }
                textId.text = "# $id"
                textDate.text = date
                textNumbers.text = numbers
                textOp.text = operator
            }
        }

    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    inner class Holder (view : View) : RecyclerView.ViewHolder(view){

        val textNumbers: TextView = view.findViewById(R.id.text_operation_numbers)
        val textDate: TextView = view.findViewById(R.id.text_date)
        val textOp: TextView = view.findViewById(R.id.text_operation_op)
        val textId: TextView = view.findViewById(R.id.text_operation_id)
        val textResult: TextView = view.findViewById(R.id.text_operation_result)

    }

}