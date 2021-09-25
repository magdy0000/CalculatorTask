package com.example.calculatortask.utils

import java.text.ParsePosition

object Helper {


    fun getOperator (position: Int): String {

        return when (position) {
            0 -> "+"
            1 -> {
                "-"
            }
            2 -> {
                "/"
            }
            else -> {
               "*"
            }
        }
    }

    fun getTime (position: Int): Int {

        return when (position) {
            1 -> 5
            2 -> {
                30
            }
            3 -> {
                60
            }
            4 ->{
                90
            }
            else -> {
                120
            }
        }
    }
}