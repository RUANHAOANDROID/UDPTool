package com.hao.gatetool

/**
 *@date: 2022/12/1
 *@author: 锅得铁
 *  验票\开闸\过闸\
 */
object Pack {
    //验票
    fun qrMsg(qrcode: String, sn: String = "284605470533333459544638"): String {
        return "\$F${sn}111/@${qrcode}\\@\$E"
    }

    //过闸
    fun passedMsg(sn: String = "284605470533333459544638"): String {
        return "\$F${sn}P21/@ok\\@\$E"
    }

}