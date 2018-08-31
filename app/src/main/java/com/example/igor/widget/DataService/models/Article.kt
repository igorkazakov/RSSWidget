package com.example.igor.widget.DataService.models

import android.database.Cursor
import android.os.Parcel
import android.os.Parcelable

data class Article(
        var id: String? = null,
        var title: String? = null,
        var author: String? = null,
        var link: String? = null,
        var description: String? = null,
        var content: String? = null,
        var category: String? = null,
        var disable: Int = 0) : Parcelable {

    constructor(cursor: Cursor) : this() {

        id = cursor.getInt(0).toString()
        title = cursor.getString(1)
        author = cursor.getString(2)
        link = cursor.getString(3)
        description = cursor.getString(4)
        content = cursor.getString(5)
        category = cursor.getString(6)
        disable = cursor.getInt(7)
    }

    constructor(parcel: Parcel) : this() {
        readFromParcel(parcel)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeString(link)
        parcel.writeString(description)
        parcel.writeString(content)
        parcel.writeString(category)
        parcel.writeInt(disable)
    }

    private fun readFromParcel(parcel: Parcel) {

        id = parcel.readString()
        title = parcel.readString()
        author = parcel.readString()
        link = parcel.readString()
        description = parcel.readString()
        content = parcel.readString()
        category = parcel.readString()
        disable = parcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Article> {
        override fun createFromParcel(parcel: Parcel): Article {
            return Article(parcel)
        }

        override fun newArray(size: Int): Array<Article?> {
            return arrayOfNulls(size)
        }
    }
}