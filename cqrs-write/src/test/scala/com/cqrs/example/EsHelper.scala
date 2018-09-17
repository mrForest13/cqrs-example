package com.cqrs.example

import com.sksamuel.elastic4s.http.Shards
import com.sksamuel.elastic4s.http.index.IndexResponse

object EsHelper {

  val emptyShard = Shards(1, 1, 1)

  val emptyIndexRes = IndexResponse("", "", "", 1L, "", forcedRefresh = true, emptyShard)
}
