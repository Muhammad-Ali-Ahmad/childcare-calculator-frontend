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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourOtherIncomeAmountPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.BigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourOtherIncomeAmountPY

class YourOtherIncomeAmountPYViewSpec extends BigDecimalViewBehaviours {

  val messageKeyPrefix = "yourOtherIncomeAmountPY"

  def createView = () => yourOtherIncomeAmountPY(frontendAppConfig, new YourOtherIncomeAmountPYForm(frontendAppConfig).apply(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => yourOtherIncomeAmountPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  val form = new YourOtherIncomeAmountPYForm(frontendAppConfig).apply()

  "YourOtherIncomeAmountPY view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like bigDecimalPage(createViewUsingForm, messageKeyPrefix, routes.YourOtherIncomeAmountPYController.onSubmit(NormalMode).url)
  }
}
