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

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerStatutoryPayPerWeekForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerStatutoryPayPerWeekId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryPayPerWeek

import scala.concurrent.Future

class PartnerStatutoryPayPerWeekController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>

      val statutoryType = request.userAnswers.partnerStatutoryPayType.getOrElse("")

      val preparedForm = request.userAnswers.partnerStatutoryPayPerWeek match {
        case None => PartnerStatutoryPayPerWeekForm()
        case Some(value) => PartnerStatutoryPayPerWeekForm().fill(value)
      }
      Ok(partnerStatutoryPayPerWeek(appConfig, preparedForm, mode, statutoryType))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>

      val statutoryType = request.userAnswers.partnerStatutoryPayType.getOrElse("")

      PartnerStatutoryPayPerWeekForm().bindFromRequest().fold(
        (formWithErrors: Form[Int]) =>
          Future.successful(BadRequest(partnerStatutoryPayPerWeek(appConfig, formWithErrors, mode, statutoryType))),
        (value) =>
          dataCacheConnector.save[Int](request.sessionId, PartnerStatutoryPayPerWeekId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(PartnerStatutoryPayPerWeekId, mode)(new UserAnswers(cacheMap))))
      )
  }
}