package com.example.final_exam_109.data

import com.example.final_exam_109.model.Transaction
import java.io.File

class CsvTransactionRepository(private val file: File) : TransactionRepository {

    override fun append(transaction: Transaction) {
        file.appendText(transaction.toCsvLine() + "\n")
    }

    override fun readAll(): List<Transaction> {
        if (!file.exists()) return emptyList()
        return file.readLines()
            .filter { it.isNotBlank() }
            .mapNotNull { Transaction.fromCsvLine(it) }
    }

    override fun clear() {
        if (file.exists()) file.delete()
    }
}