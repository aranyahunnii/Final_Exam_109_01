package com.example.final_exam_109.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.final_exam_109.data.TransactionRepository
import com.example.final_exam_109.model.Transaction
import com.example.final_exam_109.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TransactionUiState(
    val title: String = "",
    val amount: String = "",
    val type: TransactionType = TransactionType.EXPENSE,
    val transactions: List<Transaction> = emptyList(),
    val message: String = ""
) {
    val totalIncome: Double
        get() = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }

    val totalExpense: Double
        get() = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

    // ยอดคงเหลือสุทธิ = รายรับทั้งหมด - รายจ่ายทั้งหมด
    val balance: Double
        get() = totalIncome - totalExpense
}

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    init { loadTransactions() }

    fun onTitleChange(v: String) = _uiState.update { it.copy(title = v) }

    fun onAmountChange(v: String) {
        if (v.all { c -> c.isDigit() || c == '.' } && v.count { c -> c == '.' } <= 1) {
            _uiState.update { it.copy(amount = v) }
        }
    }

    fun onTypeChange(t: TransactionType) = _uiState.update { it.copy(type = t) }

    private fun loadTransactions() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = repository.readAll().reversed()
            _uiState.update { it.copy(transactions = list) }
        }
    }

    fun save() {
        val s = _uiState.value
        val amount = s.amount.toDoubleOrNull()
        if (s.title.isBlank() || amount == null || amount <= 0.0) {
            _uiState.update { it.copy(message = "กรุณากรอกชื่อรายการและจำนวนเงินให้ถูกต้อง") }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            val now = Date()
            // ใช้ Locale.US เพื่อให้ปีเป็น ค.ศ. (2026) ไม่กลายเป็น พ.ศ.
            val date = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
            val time = SimpleDateFormat("HH:mm", Locale.US).format(now)
            repository.append(Transaction(date, time, s.title.trim(), amount, s.type))
            val list = repository.readAll().reversed()
            _uiState.update {
                it.copy(title = "", amount = "", transactions = list,
                    message = "บันทึกข้อมูลเรียบร้อยแล้ว")
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clear()
            _uiState.update { it.copy(transactions = emptyList(), message = "ล้างข้อมูลเรียบร้อยแล้ว") }
        }
    }
}

class TransactionViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        TransactionViewModel(repository) as T
}