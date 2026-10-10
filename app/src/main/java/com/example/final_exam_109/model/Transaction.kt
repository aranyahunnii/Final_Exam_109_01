package com.example.final_exam_109.model

enum class TransactionType { INCOME, EXPENSE }

data class Transaction(
    val date: String,
    val time: String,
    val title: String,
    val amount: Double,
    val type: TransactionType
) {
    // Object -> บรรทัด CSV
    fun toCsvLine(): String {
        val safeTitle = title.replace("\n", " ").replace("\r", " ")
        return "$date,$time,$safeTitle,$amount,${type.name}"
    }

    companion object {
        // บรรทัด CSV -> Object (คืน null ถ้าบรรทัดเสีย)
        fun fromCsvLine(line: String): Transaction? {
            val p = line.split(",")
            if (p.size < 5) return null
            val title = p.subList(2, p.size - 2).joinToString(",")
            val amount = p[p.size - 2].toDoubleOrNull() ?: return null
            val type = runCatching { TransactionType.valueOf(p.last().trim()) }.getOrNull()
                ?: return null
            return Transaction(p[0], p[1], title, amount, type)
        }
    }
}