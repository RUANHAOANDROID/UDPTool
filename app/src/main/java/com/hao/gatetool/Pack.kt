package com.hao.gatetool

/**
 *@date: 2022/12/1
 *@author: 锅得铁
 *  验票\开闸\过闸\
 */
object Pack {
    fun qrMsg(qrcode: String): String {
        return "\$F384605470533333459544638111/@${qrcode}\\@\$E"
    }

    fun passedMsg(): String {
        return "open gate passed"
    }
    fun openMsg():String{
        return  "";
    }
}