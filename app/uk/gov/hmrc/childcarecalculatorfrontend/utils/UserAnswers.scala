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

import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._

class UserAnswers(val cacheMap: CacheMap) extends EligibilityChecks {
  def whoGetsVouchers: Option[String] = cacheMap.getEntry[String](WhoGetsVouchersId.toString)

  def vouchers: Option[String] = cacheMap.getEntry[String](VouchersId.toString)

  def getBenefits: Option[Boolean] = cacheMap.getEntry[Boolean](GetBenefitsId.toString)

  def hasYourTaxCodeBeenAdjusted: Option[Boolean] = cacheMap.getEntry[Boolean](HasYourTaxCodeBeenAdjustedId.toString)

  def hasYourPartnersTaxCodeBeenAdjusted: Option[Boolean] = cacheMap.getEntry[Boolean](HasYourPartnersTaxCodeBeenAdjustedId.toString)

  def doYouKnowYourAdjustedTaxCode: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouKnowYourAdjustedTaxCodeId.toString)

  def doesYourEmployerOfferChildcareVouchers: Option[String] = cacheMap.getEntry[String](DoesYourEmployerOfferChildcareVouchersId.toString)

  def doEitherOfYourEmployersOfferChildcareVouchers: Option[String] = cacheMap.getEntry[String](DoEitherOfYourEmployersOfferChildcareVouchersId.toString)

  def partnerWorkHours: Option[Int] = cacheMap.getEntry[Int](PartnerWorkHoursId.toString)

  def parentWorkHours: Option[Int] = cacheMap.getEntry[Int](ParentWorkHoursId.toString)

  def whoIsInPaidEmployment: Option[String] = cacheMap.getEntry[String](WhoIsInPaidEmploymentId.toString)

  def paidEmployment: Option[Boolean] = cacheMap.getEntry[Boolean](PaidEmploymentId.toString)

  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouLiveWithPartnerId.toString)

  def approvedProvider: Option[String] = cacheMap.getEntry[String](ApprovedProviderId.toString)

  def childcareCosts: Option[String] = cacheMap.getEntry[String](ChildcareCostsId.toString)

  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)

  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)

  def location: Option[String] = cacheMap.getEntry[String](LocationId.toString)
}
