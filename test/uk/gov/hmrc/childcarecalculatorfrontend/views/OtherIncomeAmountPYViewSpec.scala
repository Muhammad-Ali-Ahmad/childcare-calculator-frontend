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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.OtherIncomeAmountPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, OtherIncomeAmountPY}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.otherIncomeAmountPY

class OtherIncomeAmountPYViewSpec extends QuestionViewBehaviours[OtherIncomeAmountPY] {

  override val form = new OtherIncomeAmountPYForm(frontendAppConfig).apply()

  val messageKeyPrefix = "otherIncomeAmountPY"

  def createView = () => otherIncomeAmountPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[OtherIncomeAmountPY]) => otherIncomeAmountPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)


  "OtherIncomeAmountPY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.OtherIncomeAmountPYController.onSubmit(NormalMode).url,
      "parentOtherIncomeAmountPY", "partnerOtherIncomeAmountPY")
  }

  "contain the currencySymbol class and £ " in {
    val doc = asDocument(createView())

    assertRenderedByCssSelector(doc, ".currencySymbol")

    val parentCurrencySymbol = doc.getElementById("parentOtherIncomeAmountPY").firstElementSibling().text()
    val partnerCurrencySymbol = doc.getElementById("partnerOtherIncomeAmountPY").firstElementSibling().text()

    parentCurrencySymbol mustBe "£"
    partnerCurrencySymbol mustBe "£"

  }
}



  

