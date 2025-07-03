package org.chargepoint.kotlin.station.authorize.database

enum class AccountType(val value:Char, val description:String) {
    PLATINUM('A',"platinum"),
    GOLD('B',"gold"),
    SILVER('C', "silver"),
    BRONZE('D',"bronze"),
    CLASSIC('E',"classic")
}

enum class StationType(val value:Int, val description:String) {
    OWN(0,"own"),
    REGULAR(1,"regular"),
    SUPERIOR(2, "superior"),
    PREMIUM(3,"premium"),
    EXTERNAL(4,"external")
}