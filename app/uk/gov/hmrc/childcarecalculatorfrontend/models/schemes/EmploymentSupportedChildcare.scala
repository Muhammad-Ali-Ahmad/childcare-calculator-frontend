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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotEligible, YesNoUnsureEnum, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{You, Partner, Both}


class EmploymentSupportedChildcare extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {
    val No = YesNoUnsureEnum.NO.toString
    val NotSure = YesNoUnsureEnum.NOTSURE.toString


    val hasParentChildcareCosts: Boolean = answers.childcareCosts.contains(YesNoNotYetEnum.YES.toString)
    val hasPartnerChildcareVouchers = answers.partnerChildcareVouchers.fold(false)(x => !(x.equals(No)|| x.equals(NotSure)))

    val hasParentChildcareVouchers = answers.yourChildcareVouchers.fold(false)(x => !(x.equals(No)|| x.equals(NotSure)))

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val whoInPaidEmployment = answers.whoIsInPaidEmployment
    val bothChildcareVouchers = answers.whoGetsVouchers

    if (hasPartner) {
      whoInPaidEmployment match {
        case Some(You) => {
          getEligibility(hasParentChildcareCosts && hasParentChildcareVouchers)
        }
        case Some(Partner) => {
          getEligibility(hasParentChildcareCosts && hasPartnerChildcareVouchers)
        }
        case Some(_) => {
          getEligibility(hasParentChildcareCosts && bothChildcareVouchers.contains(Both))
        }
        case _ => NotEligible
      }
    } else {

       getEligibility(hasParentChildcareCosts && hasParentChildcareVouchers)
    }
  }

  private def getEligibility(f: Boolean): Eligibility = f match {
    case true => Eligible
    case false => NotEligible
  }
}
