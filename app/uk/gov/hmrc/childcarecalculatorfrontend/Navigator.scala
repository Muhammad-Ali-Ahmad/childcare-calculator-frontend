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

package uk.gov.hmrc.childcarecalculatorfrontend

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{CheckMode, Mode, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> (ua => locationRoute(ua)),
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ExpectChildcareCostsController.onPageLoad(NormalMode)),
    ExpectChildcareCostsId -> (_ => routes.FreeHoursInfoController.onPageLoad),
    DoYouLiveWithPartnerId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> (_ => routes.PaidEmploymentController.onPageLoad(NormalMode)),
    PaidEmploymentId -> (_ => routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)),
    WhoIsInPaidEmploymentId -> (ua => workHoursRoute(ua)),
    PartnerWorkHoursId -> (ua => partnerWorkHoursRoute(ua))
  )

  private def workHoursRoute(answers: UserAnswers) = {
    answers.whoIsInPaidEmployment match {
      case Some("you") =>
        routes.ParentWorkHoursController.onPageLoad(NormalMode)
      case Some("partner") =>
        routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case Some("both") =>
        routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerWorkHoursRoute(answers: UserAnswers) = {
    if(answers.whoIsInPaidEmployment.contains("both")) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.WhatToTellTheCalculatorController.onPageLoad()
    }
  }

  private def locationRoute(answers: UserAnswers) = answers.location match {
    case Some("northernIreland") => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    case Some(l) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(

  )

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = mode match {
    case NormalMode =>
      routeMap.getOrElse(id, _ => routes.WhatToTellTheCalculatorController.onPageLoad())
    case CheckMode =>
      editRouteMap.getOrElse(id, _ => routes.CheckYourAnswersController.onPageLoad())
  }
}
