package com.usher.demo.view.seat

import com.twigcodes.ui.SeatSelectionView
import com.twigcodes.ui.SeatSelectionView2

object DataUtil {
    private const val MIN_ROW_COUNT = 4
    private const val MAX_ROW_COUNT = 10
    private const val MIN_COLUMN_COUNT = 8
    private const val MAX_COLUMN_COUNT = 20

    data class ServerConfig(var data: List<List<String>>, var columnCount: Int)
    data class SeatConfig(var data: ArrayList<ArrayList<SeatSelectionView.Status>>, var columnCount: Int)

    private fun makeServerColumn() = (MIN_COLUMN_COUNT..MAX_COLUMN_COUNT).random()

    private fun makeServerData(columnCount: Int) =
            Array((MIN_ROW_COUNT..MAX_ROW_COUNT).random()) {
                Array(columnCount) {
                    when ((0..6).random()) {
                        in 0..3 -> "unsold"
                        4, 5 -> "sold"
                        else -> "disabled"
                    }
                }.toList()
            }.toList()

    private fun makeServerConfig(): ServerConfig {
        val columnCount = makeServerColumn()
        val data = makeServerData(columnCount)

        return ServerConfig(data, columnCount)
    }

    fun makeSeatConfig(): SeatConfig {
        val serverConfig = makeServerConfig()
        val data = ArrayList(
                serverConfig.data.map { row ->
                    row.map { column ->
                        when (column) {
                            "unsold" -> SeatSelectionView.Status.IDLE
                            "sold" -> SeatSelectionView.Status.SELECTED
                            else -> SeatSelectionView.Status.DISABLED
                        }
                    }
                }.map { row -> ArrayList(row) }
        )

        return SeatConfig(data, serverConfig.columnCount)
    }

    /**
     * 1. 把ServerData由服务器定义的字段转为客户端定义的枚举
     * 2. 把ServerData内部的List转为ArrayList
     * 3. 把ServerData这个List转为ArrayList
     */
    fun makeSeatData(): ArrayList<ArrayList<SeatSelectionView2.Status>> =
            ArrayList(
                    makeServerData(makeServerColumn()).map { row ->
                        row.map { column ->
                            when (column) {
                                "unsold" -> SeatSelectionView2.Status.IDLE
                                "sold" -> SeatSelectionView2.Status.SELECTED
                                else -> SeatSelectionView2.Status.DISABLED
                            }
                        }
                    }.map { row -> ArrayList(row) }
            )
}