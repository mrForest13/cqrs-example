package com.cqrs.example


import com.cqrs.example.db.Id

import scala.util.{Failure, Success}

object AppLauncher extends App with Core with BootedCore with DataBaseLayer {

  db.run(bookDao.findById(Id(1))).onComplete {
    case Success(res) => println(s"WYNIK $res")
    case Failure(th)  => throw th
  }
}
