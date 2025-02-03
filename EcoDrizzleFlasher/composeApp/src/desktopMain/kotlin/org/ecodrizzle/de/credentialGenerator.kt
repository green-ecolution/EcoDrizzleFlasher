package org.ecodrizzle.de

val charPool = (('A'..'Z') + ('0' .. '9'))

fun generateRandomCredentials(credentialLength: Int): String {
    return (1..credentialLength).map{ charPool.random() }.joinToString("")
}

