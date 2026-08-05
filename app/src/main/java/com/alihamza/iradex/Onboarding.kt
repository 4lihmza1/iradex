package com.alihamza.iradex

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class Choice(
    val title: String,
    val supporting: String,
    val icon: ImageVector
)

@Composable
fun IradexOnboarding(onComplete: (friction: String, goal: String) -> Unit) {
    var page by remember { mutableIntStateOf(0) }
    var friction by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    val totalPages = 6

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(IradexColors.Background)
            .padding(horizontal = 24.dp)
            .padding(top = 18.dp, bottom = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (page > 0) {
                IconButton(onClick = { page-- }) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = IradexColors.Text)
                }
            } else {
                Spacer(Modifier.size(48.dp))
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(totalPages) { index ->
                    Box(
                        Modifier
                            .padding(horizontal = 3.dp)
                            .width(if (index == page) 24.dp else 7.dp)
                            .height(7.dp)
                            .background(
                                if (index <= page) IradexColors.Primary else IradexColors.Border,
                                CircleShape
                            )
                    )
                }
            }
            Text(
                text = "${page + 1}/$totalPages",
                color = IradexColors.Muted,
                style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(48.dp),
                textAlign = TextAlign.End
            )
        }

        AnimatedContent(
            targetState = page,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                (slideInHorizontally { it / 4 } + fadeIn()) togetherWith
                    (slideOutHorizontally { -it / 4 } + fadeOut())
            },
            label = "onboarding"
        ) { current ->
            when (current) {
                0 -> WelcomePage()
                1 -> FrictionPage(friction) { friction = it }
                2 -> GoalPage(goal) { goal = it }
                3 -> MethodPage()
                4 -> PrivacyPage()
                else -> ReadyPage(goal)
            }
        }

        val canContinue = when (page) {
            1 -> friction.isNotBlank()
            2 -> goal.isNotBlank()
            else -> true
        }
        Button(
            onClick = {
                if (page == totalPages - 1) onComplete(friction, goal) else page++
            },
            enabled = canContinue,
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = IradexColors.Primary,
                contentColor = Color.White,
                disabledContainerColor = IradexColors.SurfaceRaised,
                disabledContentColor = IradexColors.Muted
            )
        ) {
            Text(
                if (page == totalPages - 1) "Create my first commitment" else "Continue",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, null, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun WelcomePage() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(116.dp),
            shape = RoundedCornerShape(36.dp),
            color = IradexColors.SurfaceRaised,
            border = BorderStroke(1.dp, IradexColors.Border)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(Modifier.size(76.dp), shape = CircleShape, color = IradexColors.Primary) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.TrackChanges, null, tint = Color.White, modifier = Modifier.size(42.dp))
                    }
                }
            }
        }
        Spacer(Modifier.height(34.dp))
        Text(
            "One intention.\nReal follow-through.",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            color = IradexColors.Text,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Iradex turns the task you keep postponing into one clear commitment you can prove.",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = IradexColors.Muted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FrictionPage(selected: String, onSelect: (String) -> Unit) {
    ChoicePage(
        eyebrow = "LET'S PERSONALIZE IRADEX",
        title = "What usually gets in your way?",
        subtitle = "Choose the pattern that feels most familiar.",
        choices = listOf(
            Choice("I keep starting new things", "My attention moves before I finish.", Icons.Default.Bolt),
            Choice("I postpone the first step", "The task feels bigger than it really is.", Icons.Default.Psychology),
            Choice("I forget what I planned", "Good intentions disappear during the day.", Icons.Default.NotificationsActive)
        ),
        selected = selected,
        onSelect = onSelect
    )
}

@Composable
private fun GoalPage(selected: String, onSelect: (String) -> Unit) {
    ChoicePage(
        eyebrow = "YOUR FIRST FOCUS",
        title = "Where do you want momentum first?",
        subtitle = "We will use this as your default commitment category.",
        choices = listOf(
            Choice("Learning", "Study, courses and new skills", Icons.Default.School),
            Choice("Work", "Projects and professional goals", Icons.Default.WorkOutline),
            Choice("Health", "Movement and wellbeing", Icons.Default.FavoriteBorder),
            Choice("Personal", "Life admin and meaningful habits", Icons.Default.Person)
        ),
        selected = selected,
        onSelect = onSelect
    )
}

@Composable
private fun ChoicePage(
    eyebrow: String,
    title: String,
    subtitle: String,
    choices: List<Choice>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 38.dp)
    ) {
        Text(eyebrow, color = IradexColors.PrimarySoft, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(12.dp))
        Text(title, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge, color = IradexColors.Text)
        Spacer(Modifier.height(12.dp))
        Text(subtitle, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge, color = IradexColors.Muted)
        Spacer(Modifier.height(28.dp))
        choices.forEach { choice ->
            val isSelected = selected == choice.title
            Surface(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp).clickable { onSelect(choice.title) },
                shape = RoundedCornerShape(22.dp),
                color = if (isSelected) Color(0xFF211E3B) else IradexColors.Surface,
                border = BorderStroke(1.dp, if (isSelected) IradexColors.Primary else IradexColors.Border)
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(Modifier.size(46.dp), shape = RoundedCornerShape(15.dp), color = IradexColors.SurfaceRaised) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(choice.icon, null, tint = if (isSelected) IradexColors.PrimarySoft else IradexColors.Muted)
                        }
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
                        Text(choice.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = IradexColors.Text)
                        Text(choice.supporting, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = IradexColors.Muted)
                    }
                    AnimatedVisibility(isSelected) {
                        Surface(Modifier.size(28.dp), shape = CircleShape, color = IradexColors.Primary) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MethodPage() {
    StoryPage(
        eyebrow = "THE IRADEX LOOP",
        title = "Progress, not perfection.",
        subtitle = "The alarm does not ask you to finish everything. It asks you to make one visible step.",
        rows = listOf(
            Triple(Icons.Default.TrackChanges, "Commit", "Choose one specific task and a time."),
            Triple(Icons.Default.NotificationsActive, "Act", "Iradex calls you back when the moment arrives."),
            Triple(Icons.Default.CameraAlt, "Prove", "Add a photo or note showing genuine progress.")
        )
    )
}

@Composable
private fun PrivacyPage() {
    StoryPage(
        eyebrow = "PRIVATE BY DEFAULT",
        title = "Your progress stays yours.",
        subtitle = "This prototype stores your commitments, history and proof locally on your phone.",
        rows = listOf(
            Triple(Icons.Default.Lock, "Local storage", "No account and no cloud upload."),
            Triple(Icons.Default.Shield, "Safety first", "Every alarm always includes an Emergency stop."),
            Triple(Icons.Default.NotificationsActive, "Only needed access", "We explain each alarm permission before requesting it.")
        )
    )
}

@Composable
private fun StoryPage(
    eyebrow: String,
    title: String,
    subtitle: String,
    rows: List<Triple<ImageVector, String, String>>
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 38.dp)
    ) {
        Text(eyebrow, color = IradexColors.PrimarySoft, style = androidx.compose.material3.MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(12.dp))
        Text(title, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge, color = IradexColors.Text)
        Spacer(Modifier.height(12.dp))
        Text(subtitle, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge, color = IradexColors.Muted)
        Spacer(Modifier.height(30.dp))
        rows.forEach { (icon, heading, copy) ->
            Row(Modifier.fillMaxWidth().padding(bottom = 24.dp), verticalAlignment = Alignment.Top) {
                Surface(Modifier.size(48.dp), shape = RoundedCornerShape(16.dp), color = IradexColors.SurfaceRaised) {
                    Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = IradexColors.PrimarySoft) }
                }
                Column(Modifier.padding(start = 16.dp)) {
                    Text(heading, style = androidx.compose.material3.MaterialTheme.typography.titleMedium, color = IradexColors.Text)
                    Spacer(Modifier.height(4.dp))
                    Text(copy, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium, color = IradexColors.Muted)
                }
            }
        }
    }
}

@Composable
private fun ReadyPage(goal: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.size(112.dp), shape = CircleShape, color = Color(0xFF1C3328)) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, tint = IradexColors.Success, modifier = Modifier.size(54.dp))
            }
        }
        Spacer(Modifier.height(34.dp))
        Text("You are ready.", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge, color = IradexColors.Text)
        Spacer(Modifier.height(14.dp))
        Text(
            "Start with one small ${goal.lowercase().ifBlank { "personal" }} commitment. Iradex will handle the reminder; you only need to handle the next step.",
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            color = IradexColors.Muted,
            textAlign = TextAlign.Center
        )
    }
}
