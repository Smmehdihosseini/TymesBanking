package it.polito.mad.g28.tymes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Advert (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "title", defaultValue = "Title") var title: String,
    @ColumnInfo(name = "author", defaultValue = "Author") var author: String,
    @ColumnInfo(name = "location", defaultValue = "Location") var location: String,
    @ColumnInfo(name = "datetime", defaultValue = "Datetime") var datetime: String,
    @ColumnInfo(name = "description", defaultValue = "Description") var description: String,
    @ColumnInfo(name = "price", defaultValue = "Price") var price: String,
    @ColumnInfo(name = "service", defaultValue = "Service") var service: String,
    @ColumnInfo(name = "time", defaultValue = "Time") var time: String,
)
