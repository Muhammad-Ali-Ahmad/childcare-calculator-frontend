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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import javax.inject.Inject

import play.api.i18n.Messages

class IncomeSummary @Inject()(utils: Utils) {
  def load(userAnswers: UserAnswers)(implicit messages: Messages): Map[String, String] = {
    val result: Map[String, String] = Map()
    lazy val parentIncome = loadParentIncome(userAnswers, _: Map[String, String])
    lazy val parentPension = loadHowMuchYouPayPension(userAnswers, _: Map[String, String])
    lazy val parentOtherIncome = loadYourOtherIncome(userAnswers, _: Map[String, String])
    lazy val parentBenefitsIncome = loadYourBenefitsIncome(userAnswers, _: Map[String, String])
    (parentIncome andThen parentPension andThen parentOtherIncome andThen parentBenefitsIncome) (result)
  }

  private def loadParentIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    userAnswers.parentEmploymentIncomeCY.foldLeft(result)((result, income) => result + (Messages("incomeSummary.yourIncome") -> s"£${utils.valueFormatter(income)}"))
  }

  private def loadHowMuchYouPayPension(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.YouPaidPensionCY,result,(Messages("incomeSummary.paidIntoPension") -> Messages("site.no")),Messages("incomeSummary.pensionPaymentsAmonth"),userAnswers.howMuchYouPayPension)
  }

  private def loadYourOtherIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.yourOtherIncomeThisYear,result,(Messages("incomeSummary.otherIncome") -> Messages("site.no")),Messages("incomeSummary.yourOtherIncome"),userAnswers.yourOtherIncomeAmountCY)
  }

  private def loadYourBenefitsIncome(userAnswers: UserAnswers, result: Map[String, String])(implicit messages: Messages) = {
    loadSectionAmount(userAnswers.youAnyTheseBenefits,result,(Messages("incomeSummary.incomeFromBenefits") -> Messages("site.no")),Messages("incomeSummary.yourBenefitsIncome"),userAnswers.youBenefitsIncomeCY)
  }

  private def loadSectionAmount(conditionToCheckAmount: Option[Boolean], result: Map[String,String], conditionNotMet: (String,String), textForIncome: String, incomeSection: Option[BigDecimal])(implicit messages: Messages) = {
    conditionToCheckAmount match {
      case Some(conditionMet) => {
        if (conditionMet) {
          incomeSection.foldLeft(result)((result, income) => result + (textForIncome -> s"£${utils.valueFormatter(income)}"))
        }
        else {
          result + conditionNotMet
        }
      }
      case _ => result + conditionNotMet
    }
  }
}