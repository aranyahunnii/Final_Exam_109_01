package com.example.final_exam_109.data

import com.example.final_exam_109.model.Transaction

interface TransactionRepository {
    fun append(transaction: Transaction)
    fun readAll(): List<Transaction>
    fun clear()
}