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

import javax.inject.Singleton

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class PensionNavigation {

  /**
    * Route for parent current and previous year pension
    * @param answers
    * @param year
    * @return
    */
  def yourPensionRoute(answers: UserAnswers, year: String = currentYear) = {
    year match {
      case "CY" => redirectionForParentPensionCY(answers)
      case _ => routes.SessionExpiredController.onPageLoad() //TODO: To be implemented for PY
    }
  }

  private def redirectionForParentPensionCY(answers: UserAnswers) = {

    val youPaidPensionValue = answers.YouPaidPensionCY
    youPaidPensionValue match {
      case Some(true) => routes.HowMuchYouPayPensionController.onPageLoad(NormalMode)
      case Some(false) => routes.YourOtherIncomeThisYearController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

  }
}
