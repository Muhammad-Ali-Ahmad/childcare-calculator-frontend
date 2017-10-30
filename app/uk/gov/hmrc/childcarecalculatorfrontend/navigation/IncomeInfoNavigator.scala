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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Singleton

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{Identifier, LocationId, PartnerIncomeInfoId, PartnerIncomeInfoPYId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

/**
  * Contains the navigation for current and previous year employment income pages
  */
@Singleton
class IncomeInfoNavigator extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    PartnerIncomeInfoId-> nextPageUrlCY,
    PartnerIncomeInfoPYId -> nextPageUrlPY
  )

  private def nextPageUrlCY(userAnswers: UserAnswers) = {

    val hasPartner = userAnswers.doYouLiveWithPartner.getOrElse(false)
    val paidEmployment = userAnswers.whoIsInPaidEmployment

    val You = YouPartnerBothEnum.YOU.toString
    val Partner = YouPartnerBothEnum.PARTNER.toString
    val Both = YouPartnerBothEnum.BOTH.toString

    if (hasPartner) {
      paidEmployment match {
        case Some(You) => routes.PartnerPaidWorkCYController.onPageLoad(NormalMode)
        case Some(Partner) => routes.ParentPaidWorkCYController.onPageLoad(NormalMode)
        case Some(Both) => routes.EmploymentIncomeCYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    } else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

  private def nextPageUrlPY(userAnswers: UserAnswers) = {

    val hasPartner = userAnswers.doYouLiveWithPartner.getOrElse(false)
    val paidEmployment = userAnswers.whoIsInPaidEmployment

    val You = YouPartnerBothEnum.YOU.toString
    val Partner = YouPartnerBothEnum.PARTNER.toString
    val Both = YouPartnerBothEnum.BOTH.toString

    if(hasPartner) {
      paidEmployment match {
        case Some(You) => routes.PartnerPaidWorkPYController.onPageLoad(NormalMode)
        case Some(Partner) => routes.ParentPaidWorkPYController.onPageLoad(NormalMode)
        case Some(Both) => routes.EmploymentIncomePYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    }else {
      routes.SessionExpiredController.onPageLoad()
    }
  }

}
