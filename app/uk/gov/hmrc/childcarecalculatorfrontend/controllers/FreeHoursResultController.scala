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

import com.google.inject.Inject
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.FreeHours
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CheckYourAnswersHelper, ChildcareConstants, UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResult
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class FreeHoursResultController @Inject()(appConfig: FrontendAppConfig,
                                          override val messagesApi: MessagesApi,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction,
                                          utils: Utils,
                                          freeHours: FreeHours) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      val location = utils.getOrException(request.userAnswers.location, Some("freeHoursController"), Some("location"))
      val checkYourAnswersHelper = new CheckYourAnswersHelper(request.userAnswers)
      val eligibility = freeHours.eligibility(request.userAnswers)

      val sections = Seq(AnswerSection(None, Seq(
        checkYourAnswersHelper.location,
        checkYourAnswersHelper.childAgedTwo,
        checkYourAnswersHelper.childAgedThreeOrFour,
        checkYourAnswersHelper.childcareCosts
      ).flatten))

      val livingWithPartner =request.userAnswers.doYouLiveWithPartner.fold(false)(c => c)
      val paidEmployment = checkIfInEmployment(request.userAnswers)

    Ok(freeHoursResult(appConfig, location, eligibility, sections,paidEmployment,livingWithPartner))
  }

  private def checkIfInEmployment(userAnswers: UserAnswers) = {
    if (userAnswers.areYouInPaidWork.isDefined) {
      userAnswers.areYouInPaidWork.getOrElse(false)
    } else {
      userAnswers.whoIsInPaidEmployment.fold(true)(whoInPaidEmployment => whoInPaidEmployment match {
        case ChildcareConstants.neither => false
        case _ => true
      })
    }
  }
}
