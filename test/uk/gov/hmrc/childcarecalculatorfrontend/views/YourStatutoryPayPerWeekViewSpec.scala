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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YourStatutoryPayPerWeekForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.BigDecimalViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourStatutoryPayPerWeek

class YourStatutoryPayPerWeekViewSpec extends BigDecimalViewBehaviours {

  val messageKeyPrefix = "yourStatutoryPayPerWeek"

  val statutoryType = "maternity"

  val form = YourStatutoryPayPerWeekForm(statutoryType)

  def createView = () => yourStatutoryPayPerWeek(frontendAppConfig, YourStatutoryPayPerWeekForm(statutoryType), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[BigDecimal]) => yourStatutoryPayPerWeek(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  def createViewWithStatutoryType = (statutoryType: String) => yourStatutoryPayPerWeek(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)


  "YourStatutoryPayPerWeek view" must {
    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.title", statutoryType))
      )

    behave like pageWithBackLink(createView)

    behave like bigDecimalPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YourStatutoryPayPerWeekController.onSubmit(NormalMode).url,
      messageDynamicValue = Some(statutoryType.toString))
  }

  "show correct statutory pay type" in {
    val doc = asDocument(createViewWithStatutoryType(statutoryType))

    //assertContainsText(doc, messagesApi(s"$messageKeyPrefix.hint"))
    assertContainsText(doc, messagesApi(s"$messageKeyPrefix.heading", statutoryType))
  }
}
