package com.alihamza.iradex

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private data class CommitmentCategory(val name: String, val icon: ImageVector)

@Composable
fun PremiumCreateScreen(onBack: () -> Unit, onSave: (Commitment) -> Unit) {
    val context = LocalContext.current
    var task by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(IradexStorage.onboardingGoal(context)) }
    var hour by remember { mutableIntStateOf(18) }
    var minute by remember { mutableIntStateOf(0) }
    var proofMethod by remember { mutableStateOf("Photo of progress") }
    val categories = listOf(
        CommitmentCategory("Learning", Icons.Default.School),
        CommitmentCategory("Work", Icons.Default.WorkOutline),
        CommitmentCategory("Health", Icons.Default.FavoriteBorder),
        CommitmentCategory("Personal", Icons.Default.Person)
    )

    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF151126), IradexColors.Background, IradexColors.Background)
            )
        )
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp).padding(bottom = 34.dp)
        ) {
            CreateHeader(onBack)
            Spacer(Modifier.height(28.dp))
            Text(
                "NEW INTENTION",
                color = IradexColors.PrimarySoft,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "What will you move forward?",
                color = IradexColors.Text,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "One specific step. Small enough to begin, clear enough to prove.",
                color = IradexColors.Muted,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(30.dp))

            SectionLabel("YOUR COMMITMENT", "01")
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = task,
                onValueChange = { if (it.length <= 100) task = it },
                placeholder = { Text("Example: Write 10 lines of Python", color = Color(0xFF777B88)) },
                supportingText = {
                    Text(
                        "${task.length}/100",
                        modifier = Modifier.fillMaxWidth(),
                        color = IradexColors.Muted,
                        textAlign = TextAlign.End
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = IradexColors.Text,
                    unfocusedTextColor = IradexColors.Text,
                    cursorColor = IradexColors.Primary,
                    focusedBorderColor = IradexColors.Primary,
                    unfocusedBorderColor = Color(0xFF343743),
                    focusedContainerColor = Color(0xB3161820),
                    unfocusedContainerColor = Color(0xB3161820)
                )
            )

            Spacer(Modifier.height(26.dp))
            SectionLabel("FOCUS AREA", "02")
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                categories.forEach { item ->
                    CategoryChoice(
                        item = item,
                        selected = category == item.name,
                        onClick = { category = item.name },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            SectionLabel("THE MOMENT", "03")
            Spacer(Modifier.height(10.dp))
            Surface(
                onClick = {
                    TimePickerDialog(
                        context,
                        { _, selectedHour, selectedMinute ->
                            hour = selectedHour
                            minute = selectedMinute
                        },
                        hour,
                        minute,
                        false
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xD9161820),
                border = BorderStroke(1.dp, Color(0xFF343743))
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(48.dp), shape = RoundedCornerShape(16.dp), color = Color(0xFF28213E)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Alarm, null, tint = IradexColors.PrimarySoft)
                        }
                    }
                    Column(Modifier.padding(start = 14.dp)) {
                        Text("ALARM TIME", color = IradexColors.Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text(
                            String.format(Locale.getDefault(), "%02d:%02d", hour, minute),
                            color = IradexColors.Text,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text("Change", color = IradexColors.PrimarySoft, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(26.dp))
            SectionLabel("PROOF OF PROGRESS", "04")
            Spacer(Modifier.height(10.dp))
            ProofChoice(
                title = "Fresh photo",
                supporting = "Best for visible work or handwritten code",
                icon = Icons.Default.CameraAlt,
                selected = proofMethod == "Photo of progress",
                onClick = { proofMethod = "Photo of progress" }
            )
            Spacer(Modifier.height(10.dp))
            ProofChoice(
                title = "Progress note",
                supporting = "Describe the honest step that moved forward",
                icon = Icons.Default.EditNote,
                selected = proofMethod == "Quick progress note",
                onClick = { proofMethod = "Quick progress note" }
            )

            Spacer(Modifier.height(32.dp))
            Button(
                onClick = {
                    onSave(
                        Commitment(
                            task = task.trim(),
                            category = category,
                            alarmHour = hour,
                            alarmMinute = minute,
                            proofMethod = proofMethod
                        )
                    )
                },
                enabled = task.trim().length >= 3,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = IradexColors.Primary,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFF232630),
                    disabledContentColor = Color(0xFF6D707A)
                )
            ) {
                Text("Set my commitment", fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(9.dp))
                Icon(Icons.Default.ArrowForward, null)
            }
            Text(
                "Iradex will call you back at the chosen time.",
                modifier = Modifier.fillMaxWidth().padding(top = 13.dp),
                color = IradexColors.Muted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CreateHeader(onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(46.dp),
            shape = CircleShape,
            color = Color(0x99191B22),
            border = BorderStroke(1.dp, Color(0xFF343743))
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, "Back", tint = IradexColors.Text)
            }
        }
        Text(
            "Create commitment",
            modifier = Modifier.padding(start = 14.dp),
            color = IradexColors.Text,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun SectionLabel(label: String, number: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = IradexColors.Text, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(Modifier.weight(1f))
        Text(number, color = IradexColors.PrimarySoft, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun CategoryChoice(item: CommitmentCategory, selected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Column(
        modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            color = if (selected) Color(0xFF28213E) else Color(0xFF15171D),
            border = BorderStroke(1.dp, if (selected) IradexColors.Primary else Color(0xFF30333C))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(item.icon, item.name, tint = if (selected) IradexColors.PrimarySoft else IradexColors.Muted)
            }
        }
        Text(
            item.name,
            modifier = Modifier.padding(top = 7.dp),
            color = if (selected) IradexColors.Text else IradexColors.Muted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ProofChoice(
    title: String,
    supporting: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = if (selected) Color(0xFF201C36) else Color(0xFF15171D),
        border = BorderStroke(1.dp, if (selected) IradexColors.Primary else Color(0xFF30333C))
    ) {
        Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(44.dp), shape = RoundedCornerShape(14.dp), color = Color(0xFF272A34)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = if (selected) IradexColors.PrimarySoft else IradexColors.Muted)
                }
            }
            Column(Modifier.weight(1f).padding(horizontal = 13.dp)) {
                Text(title, color = IradexColors.Text, style = MaterialTheme.typography.titleMedium)
                Text(supporting, color = IradexColors.Muted, fontSize = 12.sp, lineHeight = 17.sp)
            }
            Surface(
                Modifier.size(26.dp),
                shape = CircleShape,
                color = if (selected) IradexColors.Primary else Color(0xFF242730),
                border = if (selected) null else BorderStroke(1.dp, Color(0xFF444750))
            ) {
                if (selected) Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
