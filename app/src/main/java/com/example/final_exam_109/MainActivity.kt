package com.example.final_exam_109

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.final_exam_109.ui.PinkLedgerTheme
import com.example.final_exam_109.ui.TransactionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ไอคอนแถบสถานะเป็นสีเข้ม อ่านง่ายบนพื้นหลังชมพูอ่อน
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            PinkLedgerTheme {
                TransactionScreen()
            }
        }
    }
}