package org.ecodrizzle.de

val charPool = (('A'..'F') + ('0' .. '9'))

fun generateRandomCredentials(credentialLength: Int): String {
    return (1..credentialLength).map{ charPool.random() }.joinToString("")
}

