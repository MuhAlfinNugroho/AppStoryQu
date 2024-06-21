package com.alfin.appstoryqu

import com.alfin.appstoryqu.Respon.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        return List(101) { index ->
            ListStoryItem(
                id = "",
                name = "Muhammad Alfin Nugroho",
                description = "Deskripsi untuk cerita ke-${index + 1}",
                photoUrl = "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/certificate_logo.png",
                createdAt = "Tanggal ${index + 1}",
                lat = -6.9055799,
                lon = 109.6522581
            )
        }
    }
}