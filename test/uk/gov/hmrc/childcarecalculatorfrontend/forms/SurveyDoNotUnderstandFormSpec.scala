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

class SurveyDoNotUnderstandFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyInvalid = "invalid"

  "SurveyDoNotUnderstand Form" must {

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(SurveyDoNotUnderstandForm(errorKeyBlank, errorKeyInvalid), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(SurveyDoNotUnderstandForm(errorKeyBlank, errorKeyInvalid), emptyForm, expectedError)
    }

  }
}
