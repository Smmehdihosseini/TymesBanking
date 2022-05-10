package it.polito.mad.g28.tymes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Advert (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo(name = "datetime") var datetime: String?,
    @ColumnInfo(name = "duration") var duration: String?,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "regularity") var regularity: String?,
)
