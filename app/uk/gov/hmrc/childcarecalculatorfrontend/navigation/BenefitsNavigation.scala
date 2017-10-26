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

/**
  * Contains the navigation for current and previous year pension pages
  */

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.{Inject, Singleton}

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

@Singleton
class BenefitsNavigation @Inject()(utils: Utils = new Utils()){

  def yourBenefitsRouteCY(answers: UserAnswers) = {

    val youAnyTheseBenefitsValue = answers.youAnyTheseBenefits
    youAnyTheseBenefitsValue match {
      case Some(true) => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(false) => routes.YourStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def partnerBenefitsRouteCY(answers: UserAnswers) = {

    val partnerAnyTheseBenefitsCYValue = answers.partnerAnyTheseBenefitsCY
    partnerAnyTheseBenefitsCYValue match {
      case Some(true) => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def bothBenefitsRouteCY(answers: UserAnswers) = {

    val bothAnyTheseBenefitsCYValue = answers.bothAnyTheseBenefitsCY
    bothAnyTheseBenefitsCYValue match {
      case Some(true) => routes.WhosHadBenefitsController.onPageLoad(NormalMode)
      case Some(false) => routes.BothStatutoryPayCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def whosHadBenefitsRouteCY(answers: UserAnswers) = {

    val whosHadBenefitsValue = answers.whosHadBenefits
    whosHadBenefitsValue match {
      case Some(You) => routes.YouBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.PartnerBenefitsIncomeCYController.onPageLoad(NormalMode)
      case Some(Both) => routes.BenefitsIncomeCYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def yourBenefitsIncomeRouteCY(answers: UserAnswers) = {
    val youBenefitsIncomeCYValue = answers.youBenefitsIncomeCY

    utils.getCallOrSessionExpired(youBenefitsIncomeCYValue,
                            routes.YourStatutoryPayCYController.onPageLoad(NormalMode))
  }

  def partnerBenefitsIncomeRouteCY(answers: UserAnswers) = {
    val partnerBenefitsIncomeCYValue = answers.partnerBenefitsIncomeCY

    utils.getCallOrSessionExpired(partnerBenefitsIncomeCYValue,
      routes.PartnerStatutoryPayCYController.onPageLoad(NormalMode))
  }

  /*def howMuchBothPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchBothPayPensionValue = answers.howMuchBothPayPension

    utils.getCallOrSessionExpired(howMuchBothPayPensionValue,
      routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode))
 }*/

}
