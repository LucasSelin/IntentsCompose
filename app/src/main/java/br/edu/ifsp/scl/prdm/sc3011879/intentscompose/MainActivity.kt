package br.edu.ifsp.scl.prdm.sc3011879.intentscompose

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.ifsp.scl.prdm.sc3011879.intentscompose.ui.theme.IntentsComposeTheme

sealed class Screen(val route: String) {
    object Home : Screen(route = "home_screen")
    object AddWord : Screen(route = "add_word_screen")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navHostController = rememberNavController()

            IntentsComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainNavHost(
                        navHostController = navHostController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainNavHost(navHostController: NavHostController, modifier: Modifier) {
    NavHost(
        navController = navHostController,
        startDestination = Screen.Home.route,
    ) {
        composable(
            route = Screen.Home.route,
        ) { backStackEntry ->
            val newWord by backStackEntry.savedStateHandle
                .getStateFlow("newWord", "")
                .collectAsState()

            HomeScreen(
                newWord = newWord,
                modifier = modifier,
                onWordConcatenated = {
                    backStackEntry.savedStateHandle["newWord"] = ""
                },
                onAddWordClick = {
                    navHostController.navigate(
                        route = "${Screen.AddWord.route}?currentText=${Uri.encode(it)}"
                    )
                }
            )
        }

        composable(
            route = "${Screen.AddWord.route}?currentText={currentText}",
            arguments = listOf(
                navArgument("currentText") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            AddWordScreen(
                currentText = backStackEntry.arguments?.getString("currentText") ?: "",
                modifier = modifier,
                onConcatenateClick = {
                    navHostController.previousBackStackEntry?.savedStateHandle?.set("newWord", it)
                    navHostController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun HomeScreen(
    newWord: String,
    modifier: Modifier,
    onWordConcatenated: () -> Unit,
    onAddWordClick: (String) -> Unit
) {
    var currentText by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(newWord) {
        if (newWord.isNotEmpty()) {
            currentText = if (currentText.isEmpty()) newWord else "$currentText $newWord"
            onWordConcatenated()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentText,
            onValueChange = {},
            readOnly = true,
            label = { Text("String atual") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onAddWordClick(currentText) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar palavra")
        }
        Button(
            onClick = { currentText = "" },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reiniciar")
        }
    }
}

@Composable
fun AddWordScreen(currentText: String, modifier: Modifier, onConcatenateClick: (String) -> Unit) {
    var word by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentText,
            onValueChange = {},
            readOnly = true,
            label = { Text("String atual") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = word,
            onValueChange = { word = it },
            label = { Text("Nova palavra") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { onConcatenateClick(word.trim()) },
            enabled = word.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Concatenar")
        }
    }
}