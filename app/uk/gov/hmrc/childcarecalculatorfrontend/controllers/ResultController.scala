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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ResultsViewModelId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.services.ResultsService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.result
import uk.gov.hmrc.play.bootstrap.controller.FrontendController


@Singleton
class ResultController @Inject()(val appConfig: FrontendAppConfig,
                                 val messagesApi: MessagesApi,
                                 dataCacheConnector: DataCacheConnector,
                                 getData: DataRetrievalAction,
                                 requireData: DataRequiredAction,
                                 resultsService: ResultsService,
                                 utils: Utils) extends FrontendController with I18nSupport {


  def onPageLoad(): Action[AnyContent] = (getData andThen requireData).async { implicit request =>
    resultsService.getResultsViewModel(request.userAnswers).map(model => {

      dataCacheConnector.save[ResultsViewModel](request.sessionId, ResultsViewModelId.toString, model)

      request.userAnswers.location match {
        case Some(_) => Ok(result(appConfig, model, utils))
        case None => Redirect(routes.LocationController.onPageLoad(NormalMode))
      }
    })
      .recover{
        case _ => Redirect(SessionExpiredRouter.route(getClass.getName,"onPageLoad",Some(request.userAnswers),request.uri))
      }
  }
}
