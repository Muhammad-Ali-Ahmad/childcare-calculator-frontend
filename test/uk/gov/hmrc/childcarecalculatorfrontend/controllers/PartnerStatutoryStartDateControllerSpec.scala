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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{Json, JsNumber}
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerStatutoryStartDateForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerStatutoryStartDateId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryStartDate

class PartnerStatutoryStartDateControllerSpec extends ControllerSpecBase {

  val statutoryType = "maternity"

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new PartnerStatutoryStartDateController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString(form: Form[LocalDate] = PartnerStatutoryStartDateForm()) =
    partnerStatutoryStartDate(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages).toString

  "PartnerStatutoryStartDate Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode, statutoryType)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(PartnerStatutoryStartDateId.toString -> Json.toJson(new LocalDate(2017, 2, 1)))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode, statutoryType)(fakeRequest)

      contentAsString(result) mustBe viewAsString(PartnerStatutoryStartDateForm().fill(new LocalDate(2017, 2, 1)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )

      val result = controller().onSubmit(NormalMode, statutoryType)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = PartnerStatutoryStartDateForm().bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode, statutoryType)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, statutoryType)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", statutoryType))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, statutoryType)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
