package com.example.primeworld

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import com.example.primeworld.ui.theme.HeroTheme
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.scale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //  6+ éléments
        val heroes = listOf(
            Hero("Fire fox", "Vanguard", 1500, R.drawable.hero1),
            Hero("Cryo", "Fighter", 1000, R.drawable.hero2),
            Hero("Duelist", "Vanguard", 700, R.drawable.hero3),
            Hero("Faceless", "Slayer", 12000, R.drawable.hero4),
            Hero("Arcane Wyrm", "Slayer", 2500, R.drawable.hero5),
            Hero("Archer", "Fighter", 1000, R.drawable.hero6),
            Hero("Bard", "Fighter", 12000, R.drawable.hero7)
        )

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            HeroTheme(darkTheme = isDarkTheme){
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color =  MaterialTheme.colorScheme.background )
                 {
                    HeroListScreen(heroes=heroes,
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { isDarkTheme = it }
                                )
                }
            }
        }
    }
}

@Composable
fun HeroListScreen(heroes: List<Hero>,
                   isDarkTheme: Boolean,
                   onThemeChange: (Boolean) -> Unit) {
    var heroList by remember { mutableStateOf(heroes) }
    var purchasedHeroes by remember { mutableStateOf(setOf<Hero>()) }
    var silver by remember { mutableStateOf(30000.0) }
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // titre
                    Text(
                        text = "CHOOSE YOUR HEROES",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    // Switch
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = onThemeChange
                    )
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Silver:",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${silver.toInt()} ⚜",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier.fillMaxSize()
            .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(heroList) { hero ->
                HeroCard(
                    hero = hero,
                    isPurchased = purchasedHeroes.contains(hero),
                    currentSilver = silver,
                    onBuyClick = {
                        // ajouter le hero dans la list purchasedHeroes
                        if (silver >= hero.price) {
                            purchasedHeroes = purchasedHeroes + hero
                            silver -= hero.price
                        }
                    },
                    onDeleteClick = {
                        // supprimer de la list
                        heroList = heroList.filter { it != hero }
                        purchasedHeroes = purchasedHeroes - hero
                    }
                )
            }
        }
    }
}


    @Composable
    fun HeroCard(
        hero: Hero,
        isPurchased: Boolean,
        currentSilver: Double,
        onBuyClick: () -> Unit,
        onDeleteClick: () -> Unit
    ) {
        val context = LocalContext.current
        val canAfford = currentSilver >= hero.price
        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            isVisible = true
        }
        val scale by animateFloatAsState(
            targetValue = if (isVisible) 1f else 0.8f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp,
                pressedElevation = 4.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isPurchased)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface
            ), shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
        )
        {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                )
                if (isPurchased) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(start = 24.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // IMAGE AVEC 3D
                    Surface(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 16.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        shadowElevation = 8.dp,
                        tonalElevation = 4.dp
                    ) {
                        Box {
                            Image(
                                painter = painterResource(id = hero.imageRes),
                                contentDescription = hero.name,
                                modifier = Modifier.fillMaxSize()

                            )
                            if (isPurchased) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                        )
                                )
                            }
                        }
                    }

                    // COLUMN AVEC INFO
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // NOM DU HERO
                        Column {
                            Text(
                                text = hero.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            )
                            {

                                // NOM DU CLASS
                                Text(
                                    text = hero.heroClass,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))


                            // PRIX EN ARGENT
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                            }
                            Text(
                                text = "${hero.price} ⚜",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    // Boutons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Bouton acheter
                        if (!isPurchased) {
                            Button(
                                onClick = {
                                    if (canAfford) {
                                        Toast.makeText(
                                            context,
                                            "Bought: ${hero.name} for ${hero.price} silver",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onBuyClick()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Need more silver!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                enabled = canAfford,  // ← Bouton

                                modifier = Modifier.height(42.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 6.dp,
                                    pressedElevation = 10.dp,
                                    disabledElevation = 0.dp
                                )
                            ) {
                                Text("Buy", fontSize = 14.sp)
                            }

                            // bouton supprimer
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(
                                        context,
                                        "Deleted: ${hero.name}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onDeleteClick()
                                },
                                modifier = Modifier.height(42.dp),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.error
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text(
                                    "Delete", fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                    // action  en clique buy
                    if (isPurchased) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.primary,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            shadowElevation = 6.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "READY FOR BATTLE",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }
    data class Hero(val name: String, val heroClass: String, val price: Int, val imageRes: Int)
