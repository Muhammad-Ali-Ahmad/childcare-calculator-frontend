/*
 * Copyright 2019 HM Revenue & Customs
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

import org.joda.time.LocalDate
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildStartEducationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.DateViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childStartEducation

class ChildStartEducationViewSpec extends DateViewBehaviours[LocalDate] {

  val messageKeyPrefix = "childStartEducation"

  val validBirthday = new LocalDate(LocalDate.now.minusYears(17).getYear, 2, 1)

  def createView = () => childStartEducation(frontendAppConfig, ChildStartEducationForm(validBirthday), NormalMode, 0, "Foo")(fakeRequest, messages)

  def createViewUsingForm = (form: Form[LocalDate]) => childStartEducation(frontendAppConfig, form, NormalMode, 0, "Foo")(fakeRequest, messages)

  val form = ChildStartEducationForm(validBirthday)

  "ChildStartEducation view" must {

    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      title = messages(s"$messageKeyPrefix.title"),
      heading = Some(messages(s"$messageKeyPrefix.heading", "Foo"))
    )

    behave like pageWithBackLink(createView)

    behave like pageWithDateFields(createViewUsingForm, messageKeyPrefix, routes.AboutYourChildController.onSubmit(NormalMode, 0).url, "date")
  }
}
