//package com.pierrejacquier.todoboard.features.boards.adapter
//
//import android.support.v4.util.SparseArrayCompat
//import android.support.v7.widget.RecyclerView
//import android.view.ViewGroup
//import com.pierrejacquier.todoboard.commons.adapter.AdapterConstants
//import com.pierrejacquier.todoboard.commons.adapter.ViewType
//import com.pierrejacquier.todoboard.commons.adapter.ViewTypeDelegateAdapter
//import com.pierrejacquier.todoboard.database.boards.Board
//
//class BoardsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private val items: ArrayList<ViewType>
//    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
//    private val loadingItem = object: ViewType {
//        override fun getViewType() = AdapterConstants.LOADING
//    }
//
//    init {
//        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
//        delegateAdapters.put(AdapterConstants.BOARD, BoardsDelegateAdapter())
//        items = ArrayList()
////        items.add(loadingItem)
//    }
//
//    override fun getItemCount(): Int {
//        return items.size
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
//    }
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, this.items[position])
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return this.items[position].getViewType()
//    }
//
//    fun addBoards(boards: List<Board>) {
//        val initPosition = items.size - 1
//        items.removeAt(initPosition)
//        notifyItemRemoved(initPosition)
//
//        // insert boards and the loading at the end of the list
//        items.addAll(boards)
//        items.add(loadingItem)
//        notifyItemRangeChanged(initPosition, items.size + 1 /* plus loading item */)
//    }
//
//    fun clearAndAddBoards(boards: List<Board>) {
////        items.clear()
////        notifyItemRangeRemoved(0, getLastPosition())
//
//        items.addAll(boards)
//        notifyItemRangeInserted(0, items.size)
//    }
//
//    fun getBoards(): List<Board> {
//        return items
//                .filter { it.getViewType() == AdapterConstants.BOARD }
//                .map { it as Board }
//    }
//
//    private fun getLastPosition() = if (items.lastIndex == -1) 0 else items.lastIndex
//
//}
