package com.example.client.ui.getallstudent

enum class PaginationState {
    REQUEST_INACTIVE, // no ongoing pagination request
    FIRST_LOADING,
    PAGINATING,
    PAGINATION_EXHAUST,
    EMPTY,
}