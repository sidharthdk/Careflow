package com.careflow.app.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.careflow.app.CareFlowApplication
import com.careflow.app.core.ui.theme.TextSecondary

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val repository = (context.applicationContext as CareFlowApplication).repository
    val viewModel = remember { ProfileViewModel(repository) }
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = uiState.patientName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.patientAge} years",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // My Profile Section
        item {
            SectionTitle(title = "My Profile")
        }
        item {
            SettingsGroup {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = "Name",
                    subtitle = uiState.patientName,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.CalendarMonth,
                    title = "Age",
                    subtitle = "${uiState.patientAge} years",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = "Preferred language",
                    subtitle = uiState.preferredLanguage,
                    onClick = { }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Caregiver Section
        item {
            SectionTitle(title = "Caregiver")
        }
        item {
            SettingsGroup {
                SettingsItem(
                    icon = Icons.Default.PersonAdd,
                    title = "Add a trusted caregiver",
                    subtitle = "Share your care plan with family",
                    onClick = { }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Privacy Section
        item {
            SectionTitle(title = "Privacy")
        }
        item {
            SettingsGroup {
                SettingsItem(
                    icon = Icons.Default.Storage,
                    title = "Local data",
                    subtitle = "All data stored on device",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Security,
                    title = "Permissions",
                    subtitle = "Camera, storage access",
                    onClick = { }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // About Section
        item {
            SectionTitle(title = "About CareFlow")
        }
        item {
            SettingsGroup {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "App version",
                    subtitle = "1.0.0",
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Description,
                    title = "Privacy Policy",
                    subtitle = null,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(start = 56.dp))
                SettingsItem(
                    icon = Icons.Default.Gavel,
                    title = "Terms of Service",
                    subtitle = null,
                    onClick = { }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Disclaimer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "CareFlow is a healthcare companion app. It does not diagnose diseases or prescribe medication. Always consult your doctor for medical decisions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = TextSecondary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        content()
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}
