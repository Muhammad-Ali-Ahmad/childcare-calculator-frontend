/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class PartnerEmploymentIncomeCYFormSpec extends FormSpec {


  val errorKeyBlank = partnerEmploymentIncomeBlankErrorKey
  val errorKeyInvalid = partnerEmploymentIncomeInvalidErrorKey

  "PartnerEmploymentIncomeCY Form" must {

    "bind positive numbers" in {
      val form = PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number up to the threshold of 999999.99" in {
      val form = PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "999999.99"))
      form.get shouldBe 999999.99
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid), emptyForm, expectedError)
    }

    "fail to bind numbers below the threshold" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(PartnerEmploymentIncomeCYForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "0.9"), expectedError)
    }
  }
}
