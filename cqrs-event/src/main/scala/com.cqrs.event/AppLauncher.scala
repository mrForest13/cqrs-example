package com.cqrs.event

object AppLauncher
    extends App
    with Core
    with BootedCore
    with DatabaseLayer
    with ServiceLayer
    with EventHandlerLayer
