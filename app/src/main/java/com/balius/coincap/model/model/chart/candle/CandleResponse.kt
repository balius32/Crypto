package com.balius.coincap.model.model.chart.candle

data class CandleResponse(
    val s: String,    // Assuming "s" is always present and of type String
    val t: List<Long>,    // List of timestamps (Unix time)
    val c: List<Double>,    // List of closing prices
    val o: List<Double>,    // List of opening prices
    val h: List<Double>,    // List of highest prices
    val l: List<Double>,    // List of lowest prices
    val v: List<Double>    // List of volumes



)