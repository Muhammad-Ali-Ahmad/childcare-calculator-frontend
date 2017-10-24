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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{unknownErrorKey, whichBenefitsYouGetErrorKey}

object WhichBenefitsYouGetForm extends FormErrorHelper {

  def WhichBenefitsYouGetFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, whichBenefitsYouGetErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[Set[String]] =
    Form(single("value" -> set(of(WhichBenefitsYouGetFormatter))))

  def options = Map(
    "whichBenefitsYouGet.incomeBenefits"             -> WhichBenefitsEnum.INCOMEBENEFITS.toString,
    "whichBenefitsYouGet.disabilityBenefits"         -> WhichBenefitsEnum.DISABILITYBENEFITS.toString,
    "whichBenefitsYouGet.highRateDisabilityBenefits" -> WhichBenefitsEnum.HIGHRATEDISABILITYBENEFITS.toString,
    "whichBenefitsYouGet.carersAllowance"            -> WhichBenefitsEnum.CARERSALLOWANCE.toString
  )

  def optionIsValid(value: String): Boolean = options.values.toSeq.contains(value)
}
