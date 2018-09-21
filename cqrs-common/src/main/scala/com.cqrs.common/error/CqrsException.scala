package com.cqrs.common.error

sealed trait CqrsException

case class ConflictException(msg: String) extends Exception(msg) with CqrsException
case class NotFoundException(msg: String) extends Exception(msg) with CqrsException
case class EsException(msg: String)       extends Exception(msg) with CqrsException
