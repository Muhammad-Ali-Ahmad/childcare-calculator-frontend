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

object ParentWorkHoursForm extends FormErrorHelper {

  def parentWorkHoursFormatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[BigDecimal] {

    val decimalRegex = "[0-9]{1,2}(\\.[0-9])?".r

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") | Some("0") => produceError(key, errorKeyBlank)
        case Some(str) if(str.matches(decimalRegex.toString())) =>
          if(BigDecimal(str) > 99.5 && BigDecimal(str) < 100) {
            produceError(key, errorKeyBlank)
          } else Right(BigDecimal(str))
          case _ => produceError(key, errorKeyInvalid)
        }
      }
    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = "workHours.blank", errorKeyInvalid: String = "workHours.invalid"): Form[BigDecimal] =
    Form(single("value" -> of(parentWorkHoursFormatter(errorKeyBlank, errorKeyInvalid))))
}
