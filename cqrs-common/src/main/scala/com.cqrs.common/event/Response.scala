package com.cqrs.common.event

sealed trait Response

case class EventSuccess(code: String) extends Response