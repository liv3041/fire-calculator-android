package com.toonandtools.firecalculator.Data

data class DataItem(
    val viewType:Int
){
    var ItemList:List<ChildItem>? = null
    constructor(viewType: Int,pokemon:List<ChildItem>):this(viewType){
        this.ItemList = pokemon
    }


    data class ChildItem(val text:String = "")


}