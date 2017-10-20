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
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.navigation._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject() (schemes: Schemes, minHoursNav: MinimumHoursNavigation = new MinimumHoursNavigation(),
                           maxEarningsNav: MaximumHoursNavigation = new MaximumHoursNavigation(),
                           selfEmpOrApprNav: SelfEmployedOrApprenticeNavigation = new SelfEmployedOrApprenticeNavigation()) {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> minHoursNav.locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> minHoursNav.costRoute,
    ApprovedProviderId -> minHoursNav.approvedChildCareRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> maxEarningsNav.doYouLiveRoute,
    AreYouInPaidWorkId -> maxEarningsNav.areYouInPaidWorkRoute,
    PaidEmploymentId -> maxEarningsNav.paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> maxEarningsNav.parentWorkHoursRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    PartnerWorkHoursId -> maxEarningsNav.partnerWorkHoursRoute,
    HasYourTaxCodeBeenAdjustedId -> maxEarningsNav.taxCodeAdjustedRoute,
    DoYouKnowYourAdjustedTaxCodeId -> maxEarningsNav.doYouKnowYourAdjustedTaxCodeRoute,
    WhatIsYourTaxCodeId -> maxEarningsNav.whatIsYourTaxCodeRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> maxEarningsNav.partnerTaxCodeAdjustedRoute,
    DoYouKnowYourPartnersAdjustedTaxCodeId -> maxEarningsNav.doYouKnowPartnersTaxCodeRoute,
    WhatIsYourPartnersTaxCodeId -> maxEarningsNav.whatIsYourPartnersTaxCodeRoute,
    YourChildcareVouchersId -> maxEarningsNav.parentsVouchersRoute,
    PartnerChildcareVouchersId -> maxEarningsNav.partnersVouchersRoute,
    EitherGetsVouchersId -> maxEarningsNav.vouchersRoute,
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> maxEarningsNav.doYouGetAnyBenefitsRoute,
    DoYouOrYourPartnerGetAnyBenefitsId -> maxEarningsNav.doYouOrYourPartnerGetAnyBenefitsRoute,
    WhoGetsBenefitsId -> maxEarningsNav.whoGetsBenefitsRoute,
    WhichBenefitsYouGetId -> maxEarningsNav.whichBenefitsYouGetRoute,
    WhichBenefitsPartnerGetId -> maxEarningsNav.whichBenefitsPartnerGetRoute,
    YourAgeId -> maxEarningsNav.yourAgeRoute,
    YourPartnersAgeId -> maxEarningsNav.yourPartnerAgeRoute,
    YourMinimumEarningsId -> maxEarningsNav.yourMinimumEarningsRoute,
    PartnerMinimumEarningsId -> maxEarningsNav.partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId -> maxEarningsNav.areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> maxEarningsNav.partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> maxEarningsNav.yourSelfEmployedRoute,
    PartnerSelfEmployedId -> maxEarningsNav.partnerSelfEmployedRoute,
    YourMaximumEarningsId -> maxEarningsNav.yourMaximumEarningsRoute,
    PartnerMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    EitherOfYouMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode))
  )

//  def isYouPartnerOrBoth(who: Option[String]): String = {
//    who match {
//      case Some(You) => You
//      case Some(Partner) => Partner
//      case Some(Both) => Both
//      case _ => You
//    }
//  }
//
//  private def whichBenefitsYouGetRoute(answers: UserAnswers) = {
//    isYouPartnerOrBoth(answers.whoGetsBenefits) match {
//      case You => routes.YourAgeController.onPageLoad(NormalMode)
//      case Both => routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def whichBenefitsPartnerGetRoute(answers: UserAnswers) = {
//    isYouPartnerOrBoth(answers.whoGetsBenefits) match {
//      case Partner => routes.YourPartnersAgeController.onPageLoad(NormalMode)
//      case Both => routes.YourAgeController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def doYouLiveRoute(answers: UserAnswers) = {
//    if (answers.doYouLiveWithPartner.contains(true)) {
//      routes.PaidEmploymentController.onPageLoad(NormalMode)
//    } else {
//      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def areYouInPaidWorkRoute(answers: UserAnswers) = {
//    if (answers.areYouInPaidWork.contains(true)) {
//      routes.ParentWorkHoursController.onPageLoad(NormalMode)
//    } else {
//      routes.FreeHoursResultController.onPageLoad()
//    }
//  }
//
//  private def paidEmploymentRoute(answers: UserAnswers) = {
//    if(answers.paidEmployment.contains(true)){
//      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
//    } else {
//      routes.FreeHoursResultController.onPageLoad()
//    }
//  }
//
//  private def workHoursRoute(answers: UserAnswers) = {
//    answers.whoIsInPaidEmployment match {
//      case Some(You) => routes.ParentWorkHoursController.onPageLoad(NormalMode)
//      case Some(Partner) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
//      case Some(Both) => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def partnerWorkHoursRoute(answers: UserAnswers) = {
//    if(answers.whoIsInPaidEmployment.contains(Both)) {
//      routes.ParentWorkHoursController.onPageLoad(NormalMode)
//    } else {
//      routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def taxCodeAdjustedRoute(answers: UserAnswers): Call = {
//    answers.hasYourTaxCodeBeenAdjusted match {
//      case Some(true) => routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
//      case Some(false) =>
//        if (answers.hasBothInPaidWork) {
//          routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
//        } else {
//          routes.YourChildcareVouchersController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def DoYouKnowYourAdjustedTaxCodeRoute(answers: UserAnswers): Call = {
//    answers.doYouKnowYourAdjustedTaxCode match {
//      case Some(true) => routes.WhatIsYourTaxCodeController.onPageLoad(NormalMode)
//      case Some(false) =>
//        if (answers.hasPartnerInPaidWork | answers.hasBothInPaidWork) {
//          routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
//        } else {
//          routes.YourChildcareVouchersController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def whatIsYourTaxCodeRoute(answers: UserAnswers): Call = {
//    if (answers.doYouLiveWithPartner.contains(true)) {
//      answers.whoIsInPaidEmployment match {
//        case Some(Both) => routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
//        case Some(You) => routes.YourChildcareVouchersController.onPageLoad(NormalMode)
//        case _ => routes.SessionExpiredController.onPageLoad()
//      }
//    } else {
//      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def partnerTaxCodeAdjustedRoute(answers: UserAnswers): Call = {
//    answers.hasYourPartnersTaxCodeBeenAdjusted match {
//      case Some(true) => routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
//      case Some(false) =>
//        if (answers.hasBothInPaidWork) {
//          routes.EitherGetsVouchersController.onPageLoad(NormalMode)
//        } else {
//          routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def doYouKnowPartnersTaxCodeRoute(answers: UserAnswers): Call =
//    answers.doYouKnowYourPartnersAdjustedTaxCode match {
//      case Some(true) => routes.WhatIsYourPartnersTaxCodeController.onPageLoad(NormalMode)
//      case Some(false) =>
//        if(answers.hasPartnerInPaidWork) {
//          routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
//        } else {
//          routes.EitherGetsVouchersController.onPageLoad(NormalMode)
//        }
//      case None => routes.SessionExpiredController.onPageLoad()
//    }
//
//  private def whatIsYourPartnersTaxCodeRoute(answers: UserAnswers): Call = {
//    if (answers.hasBothInPaidWork) {
//      routes.EitherGetsVouchersController.onPageLoad(NormalMode)
//    } else if (answers.hasPartnerInPaidWork) {
//      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
//    } else {
//      routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def parentsVouchersRoute(answers: UserAnswers) = {
//    answers.yourChildcareVouchers match {
//      case Some(_) =>
//        if(answers.doYouLiveWithPartner.contains(true)) {
//          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
//        } else {
//          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def partnersVouchersRoute(answers: UserAnswers) = {
//    answers.partnerChildcareVouchers match {
//      case Some(_) =>
//        if(answers.doYouLiveWithPartner.contains(true)) {
//          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
//        } else {
//          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def vouchersRoute(answers: UserAnswers): Call = {
//    val Yes = YesNoUnsureEnum.YES.toString
//
//    answers.eitherGetsVouchers match {
//      case Some(Yes) => if(answers.doYouLiveWithPartner.contains(true)) {
//        if(answers.whoIsInPaidEmployment.contains(Both)) {
//          routes.WhoGetsVouchersController.onPageLoad(NormalMode)
//        } else {
//          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
//        }
//      } else {
//        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
//      }
//      case Some(_) =>
//        if(answers.doYouLiveWithPartner.contains(true)) {
//          routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
//        } else {
//          routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
//        }
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def doYouGetAnyBenefitsRoute(answers: UserAnswers) = {
//    if(answers.doYouGetAnyBenefits.contains(false)){
//      routes.YourAgeController.onPageLoad(NormalMode)
//    } else {
//      routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers) = {
//    answers.doYouOrYourPartnerGetAnyBenefits match {
//      case Some(false) =>
//        if(answers.hasPartnerInPaidWork) {
//          routes.YourPartnersAgeController.onPageLoad(NormalMode)
//        }else{
//          routes.YourAgeController.onPageLoad(NormalMode)
//        }
//      case Some(true) => routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def whoGetsBenefitsRoute(answers: UserAnswers) = {
//    answers.whoGetsBenefits match {
//      case Some(You) | Some(Both) => routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
//      case Some(Partner) => routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def yourAgeRoute(answers: UserAnswers) = {
//    if (answers.doYouLiveWithPartner.contains(true)) {
//      answers.whoIsInPaidEmployment match {
//        case Some(Both) => routes.YourPartnersAgeController.onPageLoad(NormalMode)
//        case Some(You) => routes.YourMinimumEarningsController.onPageLoad(NormalMode)
//        case _ => routes.SessionExpiredController.onPageLoad()
//      }
//    } else {
//      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def yourPartnerAgeRoute(answers: UserAnswers) = {
//    if(answers.hasBothInPaidWork){
//      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
//    } else if(answers.hasPartnerInPaidWork){
//      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
//    } else {
//      routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def yourMinimumEarningsRoute(answers: UserAnswers) = {
//    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
//    val areYouInPaidWork = answers.areYouInPaidWork.getOrElse(true)
//    val whoIsInPaidEmp = answers.whoIsInPaidEmployment
//    val hasMinimumEarnings = answers.yourMinimumEarnings
//
//    (hasMinimumEarnings, hasPartner, areYouInPaidWork, whoIsInPaidEmp) match {
//      case (Some(true), false, true, _) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//      case (Some(true), true, _, Some(You)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//      case (Some(true), true, _, Some(Both)) => routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
//      case (Some(false), false, true, _) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case (Some(false), true, _ , Some(You)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case (Some(false), true, _ , Some(Both)) => routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
//  private def partnerMinimumEarningsRoute(answers: UserAnswers) = {
//    val yourMinEarnings = answers.yourMinimumEarnings
//    val partnerMinEarnings = answers.partnerMinimumEarnings
//
//    (yourMinEarnings, partnerMinEarnings) match {
//      case (Some(true), Some(true)) => routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
//      case (Some(false), Some(true)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case (Some(false), Some(false)) => routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case (Some(true), Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case (_, Some(true)) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
//      case (_, Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//      case _ => routes.SessionExpiredController.onPageLoad()
//    }
//  }
//
// private def yourSelfEmployedRoute(answers: UserAnswers) = {
//    val yourMinEarnings = answers.yourMinimumEarnings
//    val partnerMinEarnings = answers.partnerMinimumEarnings
//
//    if (answers.doYouLiveWithPartner.contains(true)) {
//      if (answers.whoIsInPaidEmployment.contains(You) | answers.whoIsInPaidEmployment.contains(Partner)) {
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//      } else {
//        (yourMinEarnings, partnerMinEarnings) match {
//          case (Some(false), Some(false)) => routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
//          case (Some(true), Some(false)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//          case (Some(false), Some(true)) => routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
//          case _ => routes.SessionExpiredController.onPageLoad()
//        }
//      }
//    } else {
//      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//  }
//
//  private def partnerSelfEmployedRoute(answers: UserAnswers) = {
//    val yourMinEarnings = answers.yourMinimumEarnings
//    val partnerMinEarnings = answers.partnerMinimumEarnings
//
//    if (answers.hasPartnerInPaidWork) {
//      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    } else if (answers.hasBothInPaidWork) {
//      (yourMinEarnings, partnerMinEarnings) match {
//        case (Some(false), Some(false)) => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//        case (Some(true), Some(false)) => routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//        case _ => routes.SessionExpiredController.onPageLoad()
//      }
//    } else {
//      routes.SessionExpiredController.onPageLoad()
//    }
//  }

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
