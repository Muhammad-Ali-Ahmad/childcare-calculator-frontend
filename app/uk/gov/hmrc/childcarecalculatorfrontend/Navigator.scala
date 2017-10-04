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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject()() {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> (ua => locationRoute(ua)),
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> (ua => costRoute(ua)),
    DoYouLiveWithPartnerId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    HasYourTaxCodeBeenAdjustedId -> (ua => taxCodeAdjustedRoute(ua)),
    HasYourPartnersTaxCodeBeenAdjustedId -> (ua => partnerTaxCodeAdjustedRoute(ua))

  )

  private def costRoute(answers: UserAnswers): Call = answers.childcareCosts match {
    case Some("no") => {
      if(answers.isEligibleForFreeHours == Eligible && answers.location.contains("england") && answers.childAgedThreeOrFour.getOrElse(false)) {
        routes.FreeHoursInfoController.onPageLoad()
      } else if(answers.isEligibleForFreeHours == Eligible) {//TODO - go to Free hours results page
        routes.PaidEmploymentController.onPageLoad(NormalMode)
      } else {//TODO - go to Free hours results page
        routes.PaidEmploymentController.onPageLoad(NormalMode)
      }
    }
    case Some(_) => routes.ApprovedProviderController.onPageLoad(NormalMode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def taxCodeAdjustedRoute(answers: UserAnswers): Call = {
    if (answers.hasPartnerInPaidWork) {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    } else if ((!answers.hasPartnerInPaidWork) && answers.hasYourTaxCodeBeenAdjusted.contains(false)) {
      routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
    } else if ((!answers.hasPartnerInPaidWork) && answers.hasYourTaxCodeBeenAdjusted.contains(true)) {
      routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerTaxCodeAdjustedRoute(answers: UserAnswers): Call = {
      if (answers.hasYourPartnersTaxCodeBeenAdjusted.contains(true)) {
        routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
      } else if (answers.hasPartnerInPaidWork && answers.hasYourPartnersTaxCodeBeenAdjusted.contains(false)) {
        routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
      } else if (!answers.hasPartnerInPaidWork && answers.hasYourPartnersTaxCodeBeenAdjusted.contains(false)) {
        routes.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(NormalMode)
      } else {
        routes.SessionExpiredController.onPageLoad()
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
