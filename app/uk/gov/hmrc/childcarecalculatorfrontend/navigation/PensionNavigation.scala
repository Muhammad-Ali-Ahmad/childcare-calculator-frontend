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
class PensionNavigation @Inject()  (utils: Utils = new Utils()){

  def yourPensionRouteCY(answers: UserAnswers) = {

    val youPaidPensionValue = answers.YouPaidPensionCY
    youPaidPensionValue match {
      case Some(true) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def partnerPensionRouteCY(answers: UserAnswers) = {

    val partnerPaidPensionValue = answers.PartnerPaidPensionCY
    partnerPaidPensionValue match {
      case Some(true) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def bothPensionRouteCY(answers: UserAnswers) = {

    val bothPaidPensionValue = answers.bothPaidPensionCY
    bothPaidPensionValue match {
      case Some(true) => routes.WhoPaysIntoPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def whoPaysPensionRouteCY(answers: UserAnswers) = {

    val WhoPaysPensionValue = answers.whoPaysIntoPension
    WhoPaysPensionValue match {
      case Some(You) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HowMuchPartnerPayPensionController.onPageLoad(NormalMode)
      case Some(Both) => routes.HowMuchBothPayPensionController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def howMuchYouPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchYouPayPensionValue = answers.howMuchYouPayPension

    utils.getCallOrSessionExpired(howMuchYouPayPensionValue,
                            routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode))
  }

  def howMuchPartnerPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchPartnerPayPensionValue = answers.howMuchPartnerPayPension

    utils.getCallOrSessionExpired(howMuchPartnerPayPensionValue,
      routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(NormalMode))
  }

  def howMuchBothPayPensionRouteCY(answers: UserAnswers) = {
    val howMuchBothPayPensionValue = answers.howMuchBothPayPension

    utils.getCallOrSessionExpired(howMuchBothPayPensionValue,
      routes.BothOtherIncomeThisYearController.onPageLoad(NormalMode))
 }

  def yourPensionRoutePY(answers: UserAnswers) = {

    val youPaidPensionPYValue = answers.youPaidPensionPY
    youPaidPensionPYValue match {
      case Some(true) => routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def partnerPensionRoutePY(answers: UserAnswers) = {

    val partnerPaidPensionPYValue = answers.partnerPaidPensionPY
    partnerPaidPensionPYValue match {
      case Some(true) => routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.PartnerAnyOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def bothPensionRoutePY(answers: UserAnswers) = {

    val bothPaidPensionPYValue = answers.bothPaidPensionPY
    bothPaidPensionPYValue match {
      case Some(true) => routes.WhoPaidIntoPensionPYController.onPageLoad(NormalMode)
      case Some(false) => routes.BothOtherIncomeLYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }

  def whoPaysPensionRoutePY(answers: UserAnswers) = {

    val whoPaidIntoPensionPYValue = answers.whoPaidIntoPensionPY
    whoPaidIntoPensionPYValue match {
      case Some(You) => routes.HowMuchYouPayPensionPYController.onPageLoad(NormalMode)
      case Some(Partner) => routes.HowMuchPartnerPayPensionPYController.onPageLoad(NormalMode)
      case Some(Both) => routes.HowMuchBothPayPensionPYController.onPageLoad(NormalMode)
      case _ => utils.sessionExpired
    }
  }
}
