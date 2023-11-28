package jp.ac.it_college.std.s22026.pokemonapisample.api

import io.ktor.client.call.body
import jp.ac.it_college.std.s22026.pokemonapisample.model.NamedApiResourceList

/**
 * PokeAPI の Games カテゴリにあるエンドポイントへのアクセスを実装
 *
 * いま時点では、世代の一覧を取る機能のみ
 */
object Games {
    /**
     * /generation エンドポイントへパラメータなしだと
     * [NamedApiResourceList] 型で取得できる。
     */
    public suspend fun getGenerations(): NamedApiResourceList {
        return ApiClient.get("/generation").body()
    }
}