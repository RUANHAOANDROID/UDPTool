package com.hao.gatetool

/**
 *@date: 2022/12/1
 *@author: 锅得铁
 *#
 */
object Pack {
    fun crtQrMsg(qrcode: String): String {
        return "\$F384605470533333459544638111/@${qrcode}\\@\$E"
    }

    fun crtPassedMsg(): String {
        return "open gate passed"
    }
}