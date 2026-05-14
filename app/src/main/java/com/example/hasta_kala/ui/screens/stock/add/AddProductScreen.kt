package com.example.hasta_kala.ui.screens.stock.add

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hasta_kala.ui.theme.ManropeFontFamily

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen(
    productId: Int? = null,
    onBack: () -> Unit,
    viewModel: AddProductViewModel
) {
    val isSaved by viewModel.isSaved.collectAsState()
    val productToEdit by viewModel.product.collectAsState()
    val existingCats by viewModel.existingCategories.collectAsState()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf("") }
    var sku by remember { mutableStateOf("") }
    var mrp by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var stockQty by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    
    val selectedColors = remember { mutableStateListOf<String>() }
    val selectedSizes = remember { mutableStateListOf<String>() }
    
    var colorInput by remember { mutableStateOf("") }
    var sizeInput by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    LaunchedEffect(productId) {
        if (productId != null && productId != 0) {
            viewModel.loadProduct(productId)
        }
    }

    LaunchedEffect(productToEdit) {
        productToEdit?.let { p ->
            name = p.name
            sku = p.sku
            mrp = p.mrp.toString()
            sellingPrice = p.sellingPrice.toString()
            stockQty = p.stockQty.toString()
            category = p.category
            imageUri = if (p.imageUri.isNotEmpty()) Uri.parse(p.imageUri) else null
            selectedColors.clear()
            selectedColors.addAll(p.colors)
            selectedSizes.clear()
            selectedSizes.addAll(p.sizes)
        }
    }

    LaunchedEffect(isSaved) {
        if (isSaved) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId != null && productId != 0) "Edit Product" else "Add New Product", color = Color.White, fontFamily = ManropeFontFamily, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1F0F09))
            )
        },
        containerColor = Color(0xFF1F0F09)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2D1B14))
                    .clickable { galleryLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp).background(Color.Black.copy(alpha = 0.5f), CircleShape).padding(8.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color(0xFFF95E14), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Add Product Image", color = Color(0xFFE3BFB2), fontSize = 14.sp)
                    }
                }
            }

            // Form Fields
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )

            OutlinedTextField(
                value = sku,
                onValueChange = { sku = it },
                label = { Text("SKU (Optional)") },
                placeholder = { Text("Auto-generated if empty") },
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = mrp,
                    onValueChange = { mrp = it },
                    label = { Text("MRP") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = outlinedColors()
                )
                OutlinedTextField(
                    value = sellingPrice,
                    onValueChange = { sellingPrice = it },
                    label = { Text("Sale Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    colors = outlinedColors()
                )
            }

            OutlinedTextField(
                value = stockQty,
                onValueChange = { stockQty = it },
                label = { Text("Stock Quantity") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = outlinedColors()
            )

            // Dynamic Category
            var categoryExpanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { 
                        category = it
                        categoryExpanded = true
                    },
                    label = { Text("Category (Type or Select)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors(),
                    trailingIcon = {
                        IconButton(onClick = { categoryExpanded = !categoryExpanded }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFFF95E14))
                        }
                    }
                )
                DropdownMenu(
                    expanded = categoryExpanded && existingCats.isNotEmpty(),
                    onDismissRequest = { categoryExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f).background(Color(0xFF2D1B14)),
                    properties = androidx.compose.ui.window.PopupProperties(focusable = false)
                ) {
                    existingCats.filter { it.contains(category, ignoreCase = true) }.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat, color = Color.White) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // Colors
            AttributeSection(
                title = "Colors",
                input = colorInput,
                onInputChange = { colorInput = it },
                selectedItems = selectedColors,
                onAdd = { 
                    if (colorInput.isNotBlank()) {
                        selectedColors.add(colorInput)
                        colorInput = ""
                    }
                },
                onRemove = { selectedColors.remove(it) }
            )

            // Sizes
            AttributeSection(
                title = "Sizes",
                input = sizeInput,
                onInputChange = { sizeInput = it },
                selectedItems = selectedSizes,
                onAdd = { 
                    if (sizeInput.isNotBlank()) {
                        selectedSizes.add(sizeInput)
                        sizeInput = ""
                    }
                },
                onRemove = { selectedSizes.remove(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotBlank() && sellingPrice.isNotBlank() && category.isNotBlank()) {
                        if (productId != null && productId != 0 && productToEdit != null) {
                            viewModel.updateProduct(
                                productToEdit!!.copy(
                                    name = name,
                                    sku = sku,
                                    mrp = mrp.toDoubleOrNull() ?: 0.0,
                                    sellingPrice = sellingPrice.toDoubleOrNull() ?: 0.0,
                                    stockQty = stockQty.toIntOrNull() ?: 0,
                                    category = category,
                                    imageUri = imageUri?.toString() ?: "",
                                    colors = selectedColors.toList(),
                                    sizes = selectedSizes.toList()
                                )
                            )
                        } else {
                            viewModel.addProduct(
                                name = name,
                                sku = sku,
                                mrp = mrp.toDoubleOrNull() ?: 0.0,
                                sellingPrice = sellingPrice.toDoubleOrNull() ?: 0.0,
                                stockQty = stockQty.toIntOrNull() ?: 0,
                                category = category,
                                imageUri = imageUri?.toString() ?: "",
                                colors = selectedColors.toList(),
                                sizes = selectedSizes.toList()
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF95E14))
            ) {
                Text(if (productId != null && productId != 0) "Update Product" else "Save Product", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AttributeSection(
    title: String,
    input: String,
    onInputChange: (String) -> Unit,
    selectedItems: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, color = Color(0xFFF95E14), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                placeholder = { Text("Add $title (e.g. Red, XL)") },
                modifier = Modifier.weight(1f),
                colors = outlinedColors(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onAdd() })
            )
            IconButton(
                onClick = onAdd,
                modifier = Modifier.background(Color(0xFFF95E14), RoundedCornerShape(8.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            selectedItems.forEach { item ->
                InputChip(
                    selected = true,
                    onClick = { },
                    label = { Text(item) },
                    trailingIcon = {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp).clickable { onRemove(item) }
                        )
                    },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = Color(0xFF39251E),
                        labelColor = Color.White,
                        trailingIconColor = Color.White
                    ),
                    border = null
                )
            }
        }
    }
}

@Composable
fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFFF95E14),
    unfocusedBorderColor = Color(0xFF5A4138),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    focusedLabelColor = Color(0xFFF95E14),
    unfocusedLabelColor = Color(0xFFE3BFB2),
    cursorColor = Color(0xFFF95E14)
)
