/**
 *
 * to do sample project
 *
 */

package model

// Topページのviewvalue
case class ViewValueError(
  title:    String,
  cssSrc:   Seq[String],
  jsSrc:    Seq[String],
  message:  String,
) extends ViewValueCommon

