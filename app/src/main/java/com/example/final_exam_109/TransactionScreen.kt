package com.example.final_exam_109.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.final_exam_109.data.CsvTransactionRepository
import com.example.final_exam_109.model.Transaction
import com.example.final_exam_109.model.TransactionType
import com.example.final_exam_109.viewmodel.TransactionUiState
import com.example.final_exam_109.viewmodel.TransactionViewModel
import com.example.final_exam_109.viewmodel.TransactionViewModelFactory
import java.io.File
import java.util.Locale

private fun money(v: Double): String = String.format(Locale.US, "%,.2f", v)

// ================= Stateful =================
@Composable
fun TransactionScreen() {
    val context = LocalContext.current.applicationContext
    val factory = remember {
        TransactionViewModelFactory(
            CsvTransactionRepository(File(context.filesDir, "transactions.csv"))
        )
    }
    val viewModel: TransactionViewModel = viewModel(factory = factory)
    val state by viewModel.uiState.collectAsState()

    TransactionContent(
        state = state,
        onTitleChange = viewModel::onTitleChange,
        onAmountChange = viewModel::onAmountChange,
        onTypeChange = viewModel::onTypeChange,
        onSave = viewModel::save,
        onClear = viewModel::clearAll
    )
}

// ================= Stateless =================
@Composable
fun TransactionContent(
    state: TransactionUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BgTop, BgBottom)))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().safeDrawingPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Header() }
            item { BalanceCard(state) }
            item {
                FormCard(
                    state = state,
                    onTitleChange = onTitleChange,
                    onAmountChange = onAmountChange,
                    onTypeChange = onTypeChange,
                    onSave = onSave,
                    onClear = onClear
                )
            }
            item { HistoryHeader(count = state.transactions.size) }
            if (state.transactions.isEmpty()) {
                item { EmptyHint() }
            } else {
                items(state.transactions) { TransactionRow(it) }
            }
            item { Spacer(Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun Header() {
    Column(Modifier.padding(top = 8.dp)) {
        Text(
            "🌸 ระบบบันทึกรายรับ-รายจ่าย",
            fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = PinkDark
        )
        Text("บันทึกลงไฟล์ .CSV ในเครื่องของคุณ", fontSize = 13.sp, color = PinkPrimary)
    }
}

@Composable
private fun BalanceCard(state: TransactionUiState) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(PinkPrimary, PinkSoft)))
            .padding(22.dp)
    ) {
        Column {
            Text("ยอดคงเหลือสุทธิ", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            Text(
                "฿ ${money(state.balance)}",
                color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryChip("↑ รายรับรวม", state.totalIncome, Modifier.weight(1f))
                SummaryChip("↓ รายจ่ายรวม", state.totalExpense, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: Double, modifier: Modifier = Modifier) {
    Column(
        modifier
            .background(Color.White.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
        Text("฿ ${money(value)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

@Composable
private fun FormCard(
    state: TransactionUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (TransactionType) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PinkPrimary,
        unfocusedBorderColor = PinkBorder,
        focusedLabelColor = PinkPrimary,
        cursorColor = PinkPrimary
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                label = { Text("ชื่อรายการ (เช่น ค่าอาหาร, เงินเดือน)") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.amount,
                onValueChange = onAmountChange,
                label = { Text("จำนวนเงิน (บาท)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TypeOption(
                    label = "รายจ่าย",
                    selected = state.type == TransactionType.EXPENSE,
                    color = ExpenseRed, bg = ExpenseBg,
                    onClick = { onTypeChange(TransactionType.EXPENSE) },
                    modifier = Modifier.weight(1f)
                )
                TypeOption(
                    label = "รายรับ",
                    selected = state.type == TransactionType.INCOME,
                    color = IncomeGreen, bg = IncomeBg,
                    onClick = { onTypeChange(TransactionType.INCOME) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSave,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    modifier = Modifier.weight(1f).height(52.dp)
                ) { Text("บันทึกข้อมูล", fontWeight = FontWeight.Bold) }

                OutlinedButton(
                    onClick = onClear,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, PinkPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PinkPrimary),
                    modifier = Modifier.height(52.dp)
                ) { Text("ล้างข้อมูล", fontWeight = FontWeight.Bold) }
            }
            if (state.message.isNotEmpty()) {
                Text("✿ ${state.message}", color = PinkDark, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun TypeOption(
    label: String,
    selected: Boolean,
    color: Color,
    bg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) bg else Color.White)
            .border(1.5.dp, if (selected) color else PinkBorder, RoundedCornerShape(16.dp))
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = color,
                unselectedColor = Color(0xFFB07A90)
            )
        )
        Spacer(Modifier.width(6.dp))
        Text(label, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun HistoryHeader(count: Int) {
    Row(
        Modifier.fillMaxWidth().padding(top = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("ประวัติรายการ (จากไฟล์ CSV)", fontWeight = FontWeight.Bold, color = PinkDark, fontSize = 16.sp)
        Text("$count รายการ", color = PinkPrimary, fontSize = 13.sp)
    }
}

@Composable
private fun EmptyHint() {
    Box(Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
        Text("ยังไม่มีรายการ เริ่มบันทึกรายการแรกได้เลย 🌷", color = PinkDark.copy(alpha = 0.7f))
    }
}

@Composable
private fun TransactionRow(t: Transaction) {
    val isIncome = t.type == TransactionType.INCOME
    val mainColor = if (isIncome) IncomeGreen else ExpenseRed
    val softColor = if (isIncome) IncomeBg else ExpenseBg

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(44.dp).clip(CircleShape).background(softColor),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isIncome) "↑" else "↓", color = mainColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(t.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text("${t.date} ${t.time}", fontSize = 12.sp, color = Color(0xFF9C7A8A))
            }
            Text(
                (if (isIncome) "+ ฿ " else "- ฿ ") + money(t.amount),
                color = mainColor, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp
            )
        }
    }
}