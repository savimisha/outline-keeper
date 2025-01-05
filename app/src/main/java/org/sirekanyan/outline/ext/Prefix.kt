package org.sirekanyan.outline.ext

fun addPrefixToKey(url: String, prefix: String) =
    if (prefix.isEmpty()) {
        url
    } else {
        "$url&prefix=$prefix"
    }