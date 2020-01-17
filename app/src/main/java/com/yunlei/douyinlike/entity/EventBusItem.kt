package com.yunlei.douyinlike.entity


data class ToolbarStateEvent(var isShow: Boolean)

data class ChangePageEvent(var position: Int)

data class ClearPositionEvent(var isClear: Boolean)