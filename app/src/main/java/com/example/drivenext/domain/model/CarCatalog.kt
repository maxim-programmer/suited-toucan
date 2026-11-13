package com.example.drivenext.domain.model

object CarCatalog {

    val models: Map<String, List<String>> = linkedMapOf(
        "ВАЗ" to listOf("Granta", "Vesta", "Niva Travel", "Largus", "XRAY"),
        "Mercedes-Benz" to listOf("A-Class", "C-Class", "E-Class", "S-Class", "GLE"),
        "Ford" to listOf("Fiesta", "Focus", "Mondeo", "Kuga", "Mustang"),
        "BMW" to listOf("3 Series", "5 Series", "7 Series", "X3", "X5"),
        "Volkswagen" to listOf("Polo", "Golf", "Passat", "Tiguan", "Jetta")
    )

    val brands: List<String> = models.keys.toList()

    fun modelsOf(brand: String): List<String> = models[brand].orEmpty()
}