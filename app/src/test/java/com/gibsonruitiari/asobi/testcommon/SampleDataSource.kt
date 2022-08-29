package com.gibsonruitiari.asobi.testcommon

import com.gibsonruitiari.asobi.data.datamodels.SManga

val sampleComicList = buildList {
    add(generateSMangaObjectWhenGivenParams("The Boys", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2018/02/The-Boys-1-72-2006-2012-Chronological-Order.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("The Guardians of the Galaxy", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2022/07/Guardians-of-the-Galaxy-by-Al-Ewing-Vol.-2-Here-We-Make-Our-Stand-TPB-2021-1.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("The Marvels Vol 1", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2018/02/The-Boys-1-72-2006-2012-Chronological-Order.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("The Avengers", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2018/02/The-Boys-1-72-2006-2012-Chronological-Order.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("The GoodAsian", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2018/02/The-Boys-1-72-2006-2012-Chronological-Order.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("The BlackHammer", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2022/07/Guardians-of-the-Galaxy-by-Al-Ewing-Vol.-2-Here-We-Make-Our-Stand-TPB-2021-1.jpg?fit=400%2C615&ssl=1", comicLink = "https://getcomics.info/other-comics/the-boys-1-72-2006-2012-chronological-order/"))
    add(generateSMangaObjectWhenGivenParams("Batman and The Avengers", thumbnailLink ="https://i0.wp.com/getcomics.info/share/uploads/2022/06/Avengers-57-2022.webp?fit=400%2C607&ssl=1", comicLink = "https://getcomics.info/marvel/avengers-57-2022/"))


}

private fun generateSMangaObjectWhenGivenParams(name:String,
                                                comicLink:String,thumbnailLink:String): SManga {
    val sMangaImpl = SManga.create()
    sMangaImpl.comicLink = comicLink
    sMangaImpl.comicName=name
    sMangaImpl.comicThumbnailLink= thumbnailLink
    return sMangaImpl
}
