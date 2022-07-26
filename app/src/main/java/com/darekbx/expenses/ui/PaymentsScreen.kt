package com.darekbx.expenses.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.darekbx.expenses.model.Payment
import com.darekbx.expenses.viewmodel.ExpensesViewModel

@Composable
fun PaymentsScreen(
    expensesViewModel: ExpensesViewModel = hiltViewModel()
) {
    val payments by expensesViewModel.loadAllPayments().observeAsState(initial = emptyList())
    LazyColumn(Modifier.padding(8.dp)) {
        items(payments, { payment: Payment -> payment.uid!! }) { payment ->
            Card(
                modifier = Modifier.padding(8.dp),
                elevation = 4.dp
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        PaymentText(payment.amount)
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = "Spent: %.2fzł".format(payment.summaryExpenses()),
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                    Column {
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = payment.timestamp,
                            style = MaterialTheme.typography.caption
                        )
                        Text(
                            modifier = Modifier.padding(end = 8.dp),
                            text = "Expenses: ${payment.expensesCount()}",
                            style = MaterialTheme.typography.caption
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun PaymentText(amount: Double) {
    // Based on: https://saket.me/compose-custom-text-spans/
    var onDraw: DrawScope.() -> Unit by remember { mutableStateOf({}) }
    val text = buildAnnotatedString {
        append("Payment: ")
        withAnnotation("amount", annotation = "ignored") {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                append("%.2fzł".format(amount))
            }
        }
    }

    val animationProgress by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Text(
        modifier = Modifier
            .padding(start = 8.dp)
            .drawBehind { onDraw() },
        text = text,
        style = MaterialTheme.typography.h6,
        onTextLayout = { layoutResult ->

            val annotation = text.getStringAnnotations("amount", 0, text.length).first()
            onDraw = {
                for (i in annotation.start until annotation.end) {
                    val textBounds = layoutResult.getBoundingBox(i)
                    val underline = textBounds.copy(
                        top = textBounds.bottom - 3.sp.toPx() * (animationProgress + 0.3F)
                    )
                    drawRect(
                        color = Color(
                            1F - animationProgress,
                            animationProgress * 0.75F,
                             animationProgress * 0.95F
                        ),
                        topLeft = underline.topLeft,
                        size = underline.size
                    )
                }
            }
        }
    )
}

@Preview(name = "Payment text", backgroundColor = 0xFFFFFF)
@Composable
fun PaymentTextPreview() {
    PaymentText(amount = 13460.99)
}
