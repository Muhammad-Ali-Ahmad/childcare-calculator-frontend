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
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, Schemes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants

@Singleton
class Navigator @Inject() (schemes: Schemes) {

  val You = YouPartnerBothEnum.YOU.toString
  val Partner = YouPartnerBothEnum.PARTNER.toString
  val Both = YouPartnerBothEnum.BOTH.toString

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> costRoute,
    ApprovedProviderId -> approvedChildCareRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> doYouLiveRoute,
    AreYouInPaidWorkId -> areYouInPaidWorkRoute,
    PaidEmploymentId -> paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> workHoursRoute,
    PartnerWorkHoursId -> partnerWorkHoursRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    HasYourTaxCodeBeenAdjustedId -> taxCodeAdjustedRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> partnerTaxCodeAdjustedRoute,
    DoYouKnowYourAdjustedTaxCodeId -> DoYouKnowYourAdjustedTaxCodeRoute,
    YourChildcareVouchersId -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    PartnerChildcareVouchersId -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    EitherGetsVouchersId -> vouchersRoute,
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> doYouGetAnyBenefitsRoute,
    DoYouOrYourPartnerGetAnyBenefitsId -> doYouOrYourPartnerGetAnyBenefitsRoute
  )

  private def locationRoute(answers: UserAnswers) = {
    val Ni = LocationEnum.NORTHERNIRELAND.toString
    answers.location match {
      case Some(Ni) => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      case Some(_) => routes.ChildAgedTwoController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def doYouLiveRoute(answers: UserAnswers) = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      routes.PaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }
  }

  private def areYouInPaidWorkRoute(answers: UserAnswers) = {
    if (answers.areYouInPaidWork.contains(true)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def paidEmploymentRoute(answers: UserAnswers) = {
    if(answers.paidEmployment.contains(true)){
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.FreeHoursResultController.onPageLoad()
    }
  }

  private def workHoursRoute(answers: UserAnswers) = {
    answers.whoIsInPaidEmployment match {
      case Some(You) => routes.ParentWorkHoursController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case Some(Both) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def partnerWorkHoursRoute(answers: UserAnswers) = {
    if(answers.whoIsInPaidEmployment.contains(Both)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
    }
  }

  private def DoYouKnowYourAdjustedTaxCodeRoute(answers: UserAnswers):Call = {
    if(answers.doYouKnowYourAdjustedTaxCode.contains(true)) {
      routes.WhatIsYourTaxCodeController.onPageLoad(NormalMode)
    } else if (answers.hasPartnerInPaidWork && answers.doYouKnowYourAdjustedTaxCode.contains(false)) {
      routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
    } else if ((!answers.hasPartnerInPaidWork) && answers.doYouKnowYourAdjustedTaxCode.contains(false)) {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    } else routes.SessionExpiredController.onPageLoad()
  }

  private def costRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString
    answers.childcareCosts match {
      case Some(No) =>
        if (answers.isEligibleForMaxFreeHours == Eligible) {
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      case Some(_) => routes.ApprovedProviderController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def approvedChildCareRoute(answers: UserAnswers) = {
    val No = YesNoUnsureEnum.NO.toString

    answers.approvedProvider match {
      case Some(No) => {
        if(answers.isEligibleForMaxFreeHours == Eligible){
          routes.FreeHoursInfoController.onPageLoad()
        } else {
          routes.FreeHoursResultController.onPageLoad()
        }
      }
      case Some(_) => if(answers.isEligibleForFreeHours == Eligible) routes.FreeHoursInfoController.onPageLoad()
      else routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def taxCodeAdjustedRoute(answers: UserAnswers): Call =
    (answers.hasPartnerInPaidWork, answers.hasYourTaxCodeBeenAdjusted) match {
      case (true, Some(false)) => routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      case (_, Some(true)) => routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
      case (false, Some(false)) => routes.YourChildcareVouchersController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  private def partnerTaxCodeAdjustedRoute(answers: UserAnswers): Call = {
    if (answers.hasYourPartnersTaxCodeBeenAdjusted.contains(true)) {
      routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
    } else if (answers.hasYourPartnersTaxCodeBeenAdjusted.contains(false)) {
      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private def parentsVouchersRoute(answers: UserAnswers) = {
    answers.yourChildcareVouchers match {
      case Some(_) => {
        if(answers.doYouLiveWithPartner.contains(true)) {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        } else {
          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def vouchersRoute(answers: UserAnswers) = {
    val Yes = YesNoUnsureEnum.YES.toString

    answers.eitherGetsVouchers match {
      case Some(Yes) => if(answers.doYouLiveWithPartner.contains(true)) {
        if(answers.whoIsInPaidEmployment.contains(YouPartnerBothEnum.BOTH.toString)) {
          routes.WhoGetsVouchersController.onPageLoad(NormalMode)
        } else {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      } else {
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
      }
      case Some(_) => {
        if(answers.doYouLiveWithPartner.contains(true)) {
          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        } else {
          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
  }

  private def doYouGetAnyBenefitsRoute(answers: UserAnswers) = {
    if(answers.doYouGetAnyBenefits.contains(false)){
      routes.WhatsYourAgeController.onPageLoad(NormalMode)
    } else {
      //TODO: Go to new Which benefits do you get checkbox page
      routes.WhatToTellTheCalculatorController.onPageLoad()
    }
  }

  private def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers) = {
    if(answers.doYouOrYourPartnerGetAnyBenefits.contains(false)){
      routes.WhatsYourAgeController.onPageLoad(NormalMode)
    } else {
      routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
    }
  }

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map(
  )

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = {
    answers =>
      mode match {
        case NormalMode =>
          routeMap.getOrElse(id, (_: UserAnswers) => routes.WhatToTellTheCalculatorController.onPageLoad())(answers)
        case CheckMode =>
          editRouteMap.getOrElse(id, (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad())(answers)
      }
  }
}
