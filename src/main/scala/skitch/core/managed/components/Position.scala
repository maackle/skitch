package skitch.core.managed.components

import skitch.vector.{vec2, vec}
import skitch.Types._

import skitch.core.{components => plain, AutoTransform2D, AutoTransform, Render, managed}

trait Position extends plain.Position with AutoTransform

trait Position2D extends plain.Position2D with AutoTransform2D

trait PositionXY extends plain.PositionXY with AutoTransform2D
