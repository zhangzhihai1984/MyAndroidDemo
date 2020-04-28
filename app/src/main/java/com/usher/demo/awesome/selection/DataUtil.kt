package com.usher.demo.awesome.selection

import com.twigcodes.ui.SeatSelectionView

object DataUtil {
    private const val MIN_ROW_COUNT = 4
    private const val MAX_ROW_COUNT = 10
    private const val MIN_COLUMN_COUNT = 8
    private const val MAX_COLUMN_COUNT = 20

    data class ServerData(var data: List<List<String>>, var columnCount: Int)
    data class SeatConfig(var data: ArrayList<ArrayList<SeatSelectionView.Status>>, var columnCount: Int)

    private fun makeServerData(): ServerData {
        val columnCount = (MIN_COLUMN_COUNT..MAX_COLUMN_COUNT).random()
        val data = Array((MIN_ROW_COUNT..MAX_ROW_COUNT).random()) {
            Array(columnCount) {
                when ((0..6).random()) {
                    in 0..3 -> "unsold"
                    4, 5 -> "sold"
                    else -> "disabled"
                }
            }.toList()
        }.toList()

        return ServerData(data, columnCount)
    }


    /**
     * 1. 把mockServerData[i][j]数据由服务器定义的字段转为客户端定义的枚举
     * 2. 把mockServerData内部的List转为ArrayList
     * 3. 把mockServerData这个List转为ArrayList
     */
    fun makeSeatConfig(): SeatConfig {
        val serverData = makeServerData()
        val data = ArrayList(
                serverData.data.map { row ->
                    row.map { column ->
                        when (column) {
                            "unsold" -> SeatSelectionView.Status.IDLE
                            "sold" -> SeatSelectionView.Status.SELECTED
                            else -> SeatSelectionView.Status.DISABLED
                        }
                    }
                }.map { row -> ArrayList(row) }
        )

        return SeatConfig(data, serverData.columnCount)
    }

}