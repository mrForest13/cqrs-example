package com.cqrs.example.handler.model

sealed trait Command

final case class AddAuthor(firstName: String, lastName: String) extends Command
