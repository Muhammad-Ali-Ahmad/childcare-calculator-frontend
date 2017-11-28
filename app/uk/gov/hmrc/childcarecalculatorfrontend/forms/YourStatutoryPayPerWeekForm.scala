/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

object YourStatutoryPayPerWeekForm extends FormErrorHelper {

  def yourStatutoryPayPerWeekFormatter(errorKeyBlank: String, errorKeyDecimal: String, errorKeyNonNumeric: String) = new Formatter[Int] {

    val intRegex = """^(\d+)$""".r
    val decimalRegex = """^(\d*\.\d*)$""".r

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) => s.trim.replace(",", "") match {
          case intRegex(str) => Right(str.toInt)
          case decimalRegex(_) => produceError(key, errorKeyDecimal)
          case _ => produceError(key, errorKeyNonNumeric)
        }
      }
    }

    def unbind(key: String, value: Int) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = "error.required", errorKeyDecimal: String = "error.integer", errorKeyNonNumeric: String = "error.non_numeric"): Form[Int] =
    Form(single("value" -> of(yourStatutoryPayPerWeekFormatter(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric))))
}