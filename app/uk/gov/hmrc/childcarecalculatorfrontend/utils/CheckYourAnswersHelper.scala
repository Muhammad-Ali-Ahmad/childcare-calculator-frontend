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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.CheckMode
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def yourChildcareVouchers: Option[AnswerRow] = userAnswers.yourChildcareVouchers map {
    x => AnswerRow("yourChildcareVouchers.checkYourAnswersLabel", s"yourChildcareVouchers.$x", true, routes.YourChildcareVouchersController.onPageLoad(CheckMode).url)
  }

  def partnerChildcareVouchers: Option[AnswerRow] = userAnswers.partnerChildcareVouchers map {
    x => AnswerRow("partnerChildcareVouchers.checkYourAnswersLabel", s"partnerChildcareVouchers.$x", true, routes.PartnerChildcareVouchersController.onPageLoad(CheckMode).url)
  }

  def whatIsYourTaxCode: Option[AnswerRow] = userAnswers.whatIsYourTaxCode map {
    x => AnswerRow("whatIsYourTaxCode.checkYourAnswersLabel", s"$x", false, routes.WhatIsYourTaxCodeController.onPageLoad(CheckMode).url)
  }

  def whoGetsBenefits: Option[AnswerRow] = userAnswers.whoGetsBenefits map {
    x => AnswerRow("whoGetsBenefits.checkYourAnswersLabel", s"whoGetsBenefits.$x", true, routes.WhoGetsBenefitsController.onPageLoad(CheckMode).url)
  }

  def doYouOrYourPartnerGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouOrYourPartnerGetAnyBenefits map {
    x => AnswerRow("doYouOrYourPartnerGetAnyBenefits.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(CheckMode).url)
  }

  def whatsYourAge: Option[AnswerRow] = userAnswers.whatsYourAge map {
    x => AnswerRow("whatsYourAge.checkYourAnswersLabel", s"whatsYourAge.$x", true, routes.WhatsYourAgeController.onPageLoad(CheckMode).url)
  }

  def doYouGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouGetAnyBenefits map {
    x => AnswerRow("doYouGetAnyBenefits.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouGetAnyBenefitsController.onPageLoad(CheckMode).url)
  }

  def doYouKnowYourPartnersAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourPartnersAdjustedTaxCode map {
    x => AnswerRow("doYouKnowYourPartnersAdjustedTaxCode.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(CheckMode).url)
  }

  def areYouInPaidWork: Option[AnswerRow] = userAnswers.areYouInPaidWork map {
    x => AnswerRow("areYouInPaidWork.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.AreYouInPaidWorkController.onPageLoad(CheckMode).url)
  }

  def whoGetsVouchers: Option[AnswerRow] = userAnswers.whoGetsVouchers map {
    x => AnswerRow("whoGetsVouchers.checkYourAnswersLabel", s"whoGetsVouchers.$x", true, routes.WhoGetsVouchersController.onPageLoad(CheckMode).url)
  }

  def eitherGetsVouchers: Option[AnswerRow] = userAnswers.eitherGetsVouchers map {
    x => AnswerRow("eitherGetsVouchers.checkYourAnswersLabel", s"vouchers.$x", true, routes.EitherGetsVouchersController.onPageLoad(CheckMode).url)
  }

  def hasYourTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourTaxCodeBeenAdjusted map {
    x => AnswerRow("hasYourTaxCodeBeenAdjusted.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)
  }

  def hasYourPartnersTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourPartnersTaxCodeBeenAdjusted map {
    x => AnswerRow("hasYourPartnersTaxCodeBeenAdjusted.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)
  }

  def doYouKnowYourAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourAdjustedTaxCode map {
    x => AnswerRow("doYouKnowYourAdjustedTaxCode.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(CheckMode).url)
  }

  def partnerWorkHours: Option[AnswerRow] = userAnswers.partnerWorkHours map {
    x => AnswerRow("partnerWorkHours.checkYourAnswersLabel", s"$x", false, routes.PartnerWorkHoursController.onPageLoad(CheckMode).url)
  }

  def parentWorkHours: Option[AnswerRow] = userAnswers.parentWorkHours map {
    x => AnswerRow("parentWorkHours.checkYourAnswersLabel", s"$x", false, routes.ParentWorkHoursController.onPageLoad(CheckMode).url)
  }

  def whoIsInPaidEmployment: Option[AnswerRow] = userAnswers.whoIsInPaidEmployment map {
    x => AnswerRow("whoIsInPaidEmployment.checkYourAnswersLabel", s"whoIsInPaidEmployment.$x", true, routes.WhoIsInPaidEmploymentController.onPageLoad(CheckMode).url)
  }

  def paidEmployment: Option[AnswerRow] = userAnswers.paidEmployment map {
    x => AnswerRow("paidEmployment.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PaidEmploymentController.onPageLoad(CheckMode).url)
  }

  def doYouLiveWithPartner: Option[AnswerRow] = userAnswers.doYouLiveWithPartner map {
    x => AnswerRow("doYouLiveWithPartner.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouLiveWithPartnerController.onPageLoad(CheckMode).url)
  }

  def approvedProvider: Option[AnswerRow] = userAnswers.approvedProvider map {
    x => AnswerRow("approvedProvider.checkYourAnswersLabel", s"approvedProvider.$x", true, routes.ApprovedProviderController.onPageLoad(CheckMode).url)
  }

  def childcareCosts: Option[AnswerRow] = userAnswers.childcareCosts map {
    x => AnswerRow("childcareCosts.checkYourAnswersLabel", s"childcareCosts.$x", true, routes.ChildcareCostsController.onPageLoad(CheckMode).url)
  }

  def childAgedThreeOrFour: Option[AnswerRow] = userAnswers.childAgedThreeOrFour map {
    x => AnswerRow("childAgedThreeOrFour.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url)
  }

  def childAgedTwo: Option[AnswerRow] = userAnswers.childAgedTwo map {
    x => AnswerRow("childAgedTwo.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildAgedTwoController.onPageLoad(CheckMode).url)
  }

  def location: Option[AnswerRow] = userAnswers.location map {
    x => AnswerRow("location.checkYourAnswersLabel", s"location.$x", true, routes.LocationController.onPageLoad(CheckMode).url)
  }

}
