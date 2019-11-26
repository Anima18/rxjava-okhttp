package com.anima.networkrequest

/**
 * Created by jianjianhong on 19-11-7
 */
interface DataPagingCallback<T> {
    fun onSuccess(dataList: List<T>, total: Int)
    fun onFailure(message: String)
}