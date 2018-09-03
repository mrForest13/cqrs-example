package com.cqrs.example.utils

sealed trait CqrsEsException

case class ConflictException(msg: String) extends Exception(msg) with CqrsEsException
case class NotFoundException(msg: String) extends Exception(msg) with CqrsEsException
case class EsException(msg: String)       extends Exception(msg) with CqrsEsException
