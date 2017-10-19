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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsPY

class YouAnyTheseBenefitsPYViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "youAnyTheseBenefitsPY"

  def createView = () => youAnyTheseBenefitsPY(frontendAppConfig, BooleanForm(), NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => youAnyTheseBenefitsPY(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "YouAnyTheseBenefitsPY view" must {

    behave like normalPage(createView, messageKeyPrefix, "tax_year", "li.income_support", "li.jobseekers_allowance",
      "li.employment_support", "li.pensions", "li.disability", "li.attendance", "li.independance", "li.carers")

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YouAnyTheseBenefitsPYController.onSubmit(NormalMode).url)
  }
}