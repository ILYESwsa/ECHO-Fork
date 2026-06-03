package iad1tya.echo.music.ui.screens.settings.integrations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import iad1tya.echo.music.LocalPlayerAwareWindowInsets
import iad1tya.echo.music.R
import iad1tya.echo.music.constants.*
import iad1tya.echo.music.ui.component.IconButton
import iad1tya.echo.music.ui.utils.backToMain
import iad1tya.echo.music.utils.rememberPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscordSettings(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    // ── Preferences ────────────────────────────────────────────────────────────
    var enableRpc       by rememberPreference(EnableDiscordRPCKey,      false)
    var token           by rememberPreference(DiscordTokenKey,          "")
    var useDetails      by rememberPreference(DiscordUseDetailsKey,     false)
    var status          by rememberPreference(DiscordStatusKey,         "online")
    var activityType    by rememberPreference(DiscordActivityTypeKey,   "listening")
    var activityName    by rememberPreference(DiscordActivityNameKey,   "")
    var advancedMode    by rememberPreference(DiscordAdvancedModeKey,   false)
    var btn1Text        by rememberPreference(DiscordButton1TextKey,    "")
    var btn1Visible     by rememberPreference(DiscordButton1VisibleKey, true)
    var btn2Text        by rememberPreference(DiscordButton2TextKey,    "")
    var btn2Visible     by rememberPreference(DiscordButton2VisibleKey, true)

    // ── Local UI state ─────────────────────────────────────────────────────────
    var tokenVisible        by remember { mutableStateOf(false) }
    var showInfoDialog      by remember { mutableStateOf(false) }
    var showStatusMenu      by remember { mutableStateOf(false) }
    var showActivityMenu    by remember { mutableStateOf(false) }

    // ── Info dialog ────────────────────────────────────────────────────────────
    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            title = { Text(stringResource(R.string.discord_integration)) },
            text  = { Text(stringResource(R.string.discord_information)) },
        )
    }

    // ── Layout ─────────────────────────────────────────────────────────────────
    Column(
        Modifier
            .windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(
                    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(
            Modifier.windowInsetsPadding(
                LocalPlayerAwareWindowInsets.current.only(WindowInsetsSides.Top)
            )
        )
        Spacer(Modifier.height(72.dp)) // top-bar clearance

        // ── Enable toggle ──────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text  = stringResource(R.string.enable_discord_rpc),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text  = "Discord Rich Presence",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Switch(
                    checked         = enableRpc,
                    onCheckedChange = { enableRpc = it },
                )
            }
        }

        AnimatedVisibility(visible = enableRpc) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Token field ────────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text  = stringResource(R.string.discord_token),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text  = stringResource(R.string.discord_token_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value               = token,
                            onValueChange       = { token = it },
                            modifier            = Modifier.fillMaxWidth(),
                            placeholder         = { Text(stringResource(R.string.discord_token_hint)) },
                            singleLine          = true,
                            visualTransformation = if (tokenVisible)
                                VisualTransformation.None
                            else
                                PasswordVisualTransformation(),
                            keyboardOptions     = KeyboardOptions(keyboardType = KeyboardType.Password),
                            trailingIcon        = {
                                IconButton(onClick = { tokenVisible = !tokenVisible }) {
                                    Icon(
                                        painter            = painterResource(
                                            if (tokenVisible) R.drawable.visibility_off
                                            else             R.drawable.visibility
                                        ),
                                        contentDescription = null,
                                    )
                                }
                            },
                        )
                    }
                }

                // ── Display options ────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text  = "Display",
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Spacer(Modifier.height(4.dp))

                        // Swap title/artist lines
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.discord_use_details))
                                Text(
                                    text  = stringResource(R.string.discord_use_details_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked         = useDetails,
                                onCheckedChange = { useDetails = it },
                            )
                        }

                        HorizontalDivider(Modifier.padding(vertical = 4.dp))

                        // Status dropdown
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(stringResource(R.string.discord_status))
                            Box {
                                TextButton(onClick = { showStatusMenu = true }) {
                                    Text(
                                        when (status) {
                                            "idle" -> stringResource(R.string.discord_status_idle)
                                            "dnd"  -> stringResource(R.string.discord_status_dnd)
                                            else   -> stringResource(R.string.discord_status_online)
                                        }
                                    )
                                }
                                DropdownMenu(
                                    expanded        = showStatusMenu,
                                    onDismissRequest = { showStatusMenu = false },
                                ) {
                                    listOf(
                                        "online" to stringResource(R.string.discord_status_online),
                                        "idle"   to stringResource(R.string.discord_status_idle),
                                        "dnd"    to stringResource(R.string.discord_status_dnd),
                                    ).forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text    = { Text(label) },
                                            onClick = { status = value; showStatusMenu = false },
                                            leadingIcon = if (status == value) ({
                                                Icon(painterResource(R.drawable.check), null)
                                            }) else null,
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(Modifier.padding(vertical = 4.dp))

                        // Activity type dropdown
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(stringResource(R.string.discord_activity_type))
                            Box {
                                TextButton(onClick = { showActivityMenu = true }) {
                                    Text(
                                        when (activityType) {
                                            "playing"   -> stringResource(R.string.discord_activity_type_playing)
                                            "watching"  -> stringResource(R.string.discord_activity_type_watching)
                                            else        -> stringResource(R.string.discord_activity_type_listening)
                                        }
                                    )
                                }
                                DropdownMenu(
                                    expanded        = showActivityMenu,
                                    onDismissRequest = { showActivityMenu = false },
                                ) {
                                    listOf(
                                        "listening" to stringResource(R.string.discord_activity_type_listening),
                                        "playing"   to stringResource(R.string.discord_activity_type_playing),
                                        "watching"  to stringResource(R.string.discord_activity_type_watching),
                                    ).forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text    = { Text(label) },
                                            onClick = { activityType = value; showActivityMenu = false },
                                            leadingIcon = if (activityType == value) ({
                                                Icon(painterResource(R.drawable.check), null)
                                            }) else null,
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(Modifier.padding(vertical = 4.dp))

                        // Custom activity name
                        OutlinedTextField(
                            value         = activityName,
                            onValueChange = { activityName = it },
                            modifier      = Modifier.fillMaxWidth(),
                            label         = { Text(stringResource(R.string.discord_activity_name)) },
                            placeholder   = { Text(stringResource(R.string.discord_activity_name_hint)) },
                            singleLine    = true,
                        )
                    }
                }

                // ── Advanced mode (buttons) ────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(stringResource(R.string.discord_advanced_mode))
                                Text(
                                    text  = stringResource(R.string.discord_advanced_mode_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Switch(
                                checked         = advancedMode,
                                onCheckedChange = { advancedMode = it },
                            )
                        }

                        AnimatedVisibility(visible = advancedMode) {
                            Column(Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text  = stringResource(R.string.discord_variables_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )

                                // Button 1
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedTextField(
                                        value         = btn1Text,
                                        onValueChange = { btn1Text = it },
                                        modifier      = Modifier.weight(1f),
                                        label         = { Text(stringResource(R.string.discord_button1_text)) },
                                        singleLine    = true,
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text  = stringResource(R.string.discord_button_visible),
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                        Switch(
                                            checked         = btn1Visible,
                                            onCheckedChange = { btn1Visible = it },
                                        )
                                    }
                                }

                                // Button 2
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    OutlinedTextField(
                                        value         = btn2Text,
                                        onValueChange = { btn2Text = it },
                                        modifier      = Modifier.weight(1f),
                                        label         = { Text(stringResource(R.string.discord_button2_text)) },
                                        singleLine    = true,
                                    )
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text  = stringResource(R.string.discord_button_visible),
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                        Switch(
                                            checked         = btn2Visible,
                                            onCheckedChange = { btn2Visible = it },
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // ── TopAppBar ──────────────────────────────────────────────────────────────
    TopAppBar(
        title = { Text(stringResource(R.string.discord_rpc_settings)) },
        navigationIcon = {
            IconButton(
                onClick     = navController::navigateUp,
                onLongClick = navController::backToMain,
            ) {
                Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
            }
        },
        actions = {
            IconButton(onClick = { showInfoDialog = true }) {
                Icon(painterResource(R.drawable.info), contentDescription = null)
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
