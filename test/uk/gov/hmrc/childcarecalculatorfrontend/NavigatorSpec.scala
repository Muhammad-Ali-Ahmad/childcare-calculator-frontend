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

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{Scheme, Schemes}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class NavigatorSpec extends SpecBase with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator(new Schemes())

  case object UnknownIdentifier extends Identifier

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(userAnswers()) mustBe routes.WhatToTellTheCalculatorController.onPageLoad()
      }

      "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
        val answers = spy(userAnswers())
        when(answers.location) thenReturn Some("england") thenReturn Some("wales") thenReturn Some("scotland")

        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
        val answers = spy(userAnswers())
        when(answers.location) thenReturn Some("northernIreland")
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Child Aged Two" in {
        navigator.nextPage(ChildAgedTwoId, NormalMode)(spy(userAnswers())) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(spy(userAnswers())) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
      }

      "from childcare costs" when {
        "go to expect approved childcare cost when you have childcare cost or not yet decided" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("yes") thenReturn Some("notYet")

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
        }

        "go to results page when you are not eligible for free hours and don't have the child care cost" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedThreeOrFour) thenReturn Some(false)
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to free hours results page when you are eligible for free hours, have child aged 3 or 4 years and no child care cost for non-england" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedThreeOrFour) thenReturn Some(true)
          when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northernIreland")
          when(answers.isEligibleForFreeHours) thenReturn Eligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to free hours info page when you are eligible for free hours, have child aged 3 or 4 years and no child care cost for england" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedThreeOrFour) thenReturn Some(true)
          when(answers.location) thenReturn Some("england")
          when(answers.isEligibleForFreeHours) thenReturn Eligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
        }

        "go to results page when you are eligible for free hours, have child aged 2 and don't have the child care cost" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedTwo) thenReturn Some(true)
          when(answers.childAgedThreeOrFour) thenReturn Some(false)
          when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("england")
          when(answers.isEligibleForFreeHours) thenReturn Eligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to free hours info for england and results page for non-england if you are eligible for free hours, have child 2 & 3 or 4 years, don't have childcare cost & lives in wales, scotland" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedTwo) thenReturn Some(true)
          when(answers.childAgedThreeOrFour) thenReturn Some(true)
          when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("england")
          when(answers.isEligibleForFreeHours) thenReturn Eligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
        }

        "go to free hours info page if you are eligible for free hours, have child aged 3 or 4 years and don't have childcare cost and lives in england" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedThreeOrFour) thenReturn Some(true)
          when(answers.location) thenReturn Some("england")
          when(answers.isEligibleForMaxFreeHours) thenReturn Eligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
        }

        "go to free hours results page if you are eligible for free hours, have child aged 3 or 4 years and don't have childcare cost and lives in non-england" in {
          val answers = spy(userAnswers())
          when(answers.childcareCosts) thenReturn Some("no")
          when(answers.childAgedThreeOrFour) thenReturn Some(true)
          when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northernIreland")
          when(answers.isEligibleForMaxFreeHours) thenReturn NotEligible

          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

      }

      "WILL YOUR CHILDCARE COSTS BE WITH AN APPROVED PROVIDER" when {
        "go to free hours results from approved provider when they are eligible for free hours, no approved childcare provider and" +
          "location is not england" in {
          val answers = spy(userAnswers())
          when(answers.isEligibleForFreeHours) thenReturn Eligible
          when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northernIreland")
          when(answers.approvedProvider) thenReturn Some("no")
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to free hours info page from approved provider when they are eligible for free hours, location is england and " +
          "don't have approved child care" in {
          val answers = spy(userAnswers())
          when(answers.isEligibleForFreeHours) thenReturn Eligible
          when(answers.location) thenReturn Some("england")
          when(answers.approvedProvider) thenReturn Some("no")
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to free hours results from approved provider when they are not eligible for free hours and no approved childcare provider" in {
          val answers = spy(userAnswers())
          when(answers.isEligibleForFreeHours) thenReturn NotEligible
          when(answers.approvedProvider) thenReturn Some("no")
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }

        "go to fre hours info page from approved provider when they are eligible for free hours and could be eligible for more" in {
          val answers = spy(userAnswers())
          when(answers.isEligibleForFreeHours) thenReturn Eligible
          when(answers.approvedProvider) thenReturn Some("notYet") thenReturn Some("yes")
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
        }

        "go to partner page from approved provider when we don't know if they are eligible for free hours or other schemes yet" in {
          val answers = spy(userAnswers())
          when(answers.isEligibleForFreeHours) thenReturn NotDetermined
          when(answers.approvedProvider) thenReturn Some("notYet") thenReturn Some("yes")
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
          navigator.nextPage(ApprovedProviderId, NormalMode)(answers) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
        }
      }

      "Has Your Tax Code Been Adjusted" when {
        "single user will be taken to DoYouKnowYourAdjustedTaxCode screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn false
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
        }

        "single user will be taken to DoesYourEmployerOfferChildcareVouchers screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn false
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
        }

        "user with a partner in paid work will be taken to HasYourPartnerTaxCodeBeenAdjusted screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        }

        "user with a partner in paid work will be taken to DoYouKnowYourAdjustedTaxCode screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
        }
      }

      "Has Your Partners Tax Code Been Adjusted" when {
        "user with partner will be taken to DoYouKnowYourPartnersAdjustedTaxCode screen from HasYourPartnersTaxCodeBeenAdjusted when yes is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourPartnersTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourPartnersTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
        }

        "user with partner will be taken to EitherGetsVouchers screen from HasYourPartnersTaxCodeBeenAdjusted when no is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourPartnersTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourPartnersTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.EitherGetsVouchersController.onPageLoad(NormalMode)
        }
      }

      "Do you know your adjusted tax code" when {
        "when users answer yes they are taken to WhatIsYourAdjustedTaxCode screen from DoYouKnowYourAdjustedTaxCode when yes is selected" in {
          val answers = spy(userAnswers())
          when(answers.doYouKnowYourAdjustedTaxCode) thenReturn Some(true)
          navigator.nextPage(DoYouKnowYourAdjustedTaxCodeId, NormalMode)(answers) mustBe routes.WhatIsYourTaxCodeController.onPageLoad(NormalMode)
        }

        "user with partner will be taken to DoYouKnowYourPartnersAdjustedTaxCode screen from DoYouKnowYourAdjustedTaxCode when no is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.doYouKnowYourAdjustedTaxCode) thenReturn Some(false)
          navigator.nextPage(DoYouKnowYourAdjustedTaxCodeId, NormalMode)(answers) mustBe routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
        }

        "single user will be taken to DoesYourEmployerOfferChildcareVouchersController screen from DoYouKnowYourAdjustedTaxCode when no is selected" in {
          val answers = spy(userAnswers())
          when(answers.hasPartnerInPaidWork) thenReturn false
          when(answers.doYouKnowYourAdjustedTaxCode) thenReturn Some(false)
          navigator.nextPage(DoYouKnowYourAdjustedTaxCodeId, NormalMode)(answers) mustBe routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
        }
      }

      "go to are you in paid work from do you live with partner when user selects No" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        navigator.nextPage(DoYouLiveWithPartnerId, NormalMode)(answers) mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
      }

      "Go to free hours results" when {
        "user selects 'No' from are you in paid work" in {
          val answers = spy(userAnswers())
          when(answers.areYouInPaidWork) thenReturn Some(false)
          navigator.nextPage(AreYouInPaidWorkId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }
        "user selects 'No' from paid employment" in {
          val answers = spy(userAnswers())
          when(answers.paidEmployment) thenReturn Some(false)
          navigator.nextPage(PaidEmploymentId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        }
      }

      "go to do you live with partner from free hours info page" in {
        navigator.nextPage(FreeHoursInfoId, NormalMode)(spy(userAnswers())) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }

      "go to paid employment from do you live with partner when user selects yes" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        navigator.nextPage(DoYouLiveWithPartnerId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
      }

      "go to who is in paid employment from paid employment when user answers yes" in {
        val answers = spy(userAnswers())
        when(answers.paidEmployment) thenReturn Some(true)
        navigator.nextPage(PaidEmploymentId, NormalMode)(answers) mustBe routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
      }

      "Go to Partner work hours" when {
        "user selects 'partner' or 'both' from who is in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(partner) thenReturn Some(both)
          navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
          navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
        }
      }

      "Go to Parent work hours" when {
        "user selects 'Yes' from are you in paid work" in {
          val answers = spy(userAnswers())
          when(answers.areYouInPaidWork) thenReturn Some(true)
          navigator.nextPage(AreYouInPaidWorkId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
        }
        "user selects 'you' from who is in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(you)
          navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
        }

        "when user selects 'both' on paid employment and coming from partner work hours" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(both)
          when(answers.partnerWorkHours) thenReturn Some(BigDecimal(23))
          navigator.nextPage(PartnerWorkHoursId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
        }
      }

      "Go to Has you partners tax code been adjusted" when {
        "user selects 'Partner' on who is in paid employment and hit continue on Partner work hours" in {
          val answers = spy(userAnswers())
          when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
          when(answers.partnerWorkHours) thenReturn Some(BigDecimal(23))
          navigator.nextPage(PartnerWorkHoursId, NormalMode)(answers) mustBe routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        }
      }

      "Go to Has your tax code been adjusted" when {
        "user selects hit continue on Parent work hours" in {
          val answers = spy(userAnswers())
          when(answers.parentWorkHours) thenReturn Some(BigDecimal(23))
          navigator.nextPage(ParentWorkHoursId, NormalMode)(answers) mustBe routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        }
      }

      "WHO GETS VOUCHERS" when {
        "user select any option go to Get Benefits page" in {
          navigator.nextPage(WhoGetsVouchersId, NormalMode)(spy(userAnswers())) mustBe routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
        "DO EITHER OF YOU GET VOUCHERS" when {
          "go to who gets vouchers page from do either of you get vouchers page when user selects 'yes'" in {
            val answers = spy(userAnswers())
            when(answers.eitherGetsVouchers) thenReturn Some("yes")
            navigator.nextPage(EitherGetsVouchersId, NormalMode)(answers) mustBe routes.WhoGetsVouchersController.onPageLoad(NormalMode)
            navigator.nextPage(EitherGetsVouchersId, NormalMode)(answers) mustBe routes.WhoGetsVouchersController.onPageLoad(NormalMode)
          }

          "go to do you get benefits page from do either of you get vouchers page when user selects 'no' or 'not sure'" in {
            val answers = spy(userAnswers())
            when(answers.eitherGetsVouchers) thenReturn Some("no") thenReturn Some("notSure")
            navigator.nextPage(EitherGetsVouchersId, NormalMode)(answers) mustBe routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
            navigator.nextPage(EitherGetsVouchersId, NormalMode)(answers) mustBe routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
          }
        }
      }

      "Does Your Employer Offer Childcare Vouchers" when {
        "user with partner will be taken to Do you get any benefits screen from " +
          "DoesYourEmployerOfferChildcareVouchers screen when any selection is done" in {
          val answers = spy(userAnswers())
          when(answers.doesYourEmployerOfferChildcareVouchers) thenReturn
            Some(YesNoUnsureEnum.YES.toString) thenReturn
            Some(YesNoUnsureEnum.NO.toString) thenReturn
            Some(YesNoUnsureEnum.NOTSURE.toString)

          navigator.nextPage(DoesYourEmployerOfferChildcareVouchersId, NormalMode)(answers) mustBe
            routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
          navigator.nextPage(DoesYourEmployerOfferChildcareVouchersId, NormalMode)(answers) mustBe
            routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
          navigator.nextPage(DoesYourEmployerOfferChildcareVouchersId, NormalMode)(answers) mustBe
            routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      }

      "Go to Whats your age" when {
        "user selects 'No' from Do you get any benefits" in {
          val answers = spy(userAnswers())
          when(answers.doYouGetAnyBenefits) thenReturn Some(false)
          navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode)(answers) mustBe routes.WhatsYourAgeController.onPageLoad(NormalMode)
        }

        "user selects 'No' from Do you or your partner get any benefits" in {
          val answers = spy(userAnswers())
          when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)
          navigator.nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode)(answers) mustBe routes.WhatsYourAgeController.onPageLoad(NormalMode)
        }
      }

      "Go to Who Gets Benefit" when {
        "user selects 'Yes' from Do you or your partner get any benefits" in {
          val answers = spy(userAnswers())
          when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
          navigator.nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode)(answers) mustBe routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
        }
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        navigator.nextPage(UnknownIdentifier, CheckMode)(userAnswers()) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

    ".nextPage" must {

      def scheme(e: Eligibility): Scheme = new Scheme {
        override def eligibility(answers: UserAnswers): Eligibility = e
      }

      val eligible: Scheme = scheme(Eligible)
      val notEligible: Scheme = scheme(NotEligible)

      "return a redirect to the Results page" when {

        "all schemes are determined" in {
          val navigator = new Navigator(new Schemes(eligible, notEligible))
          navigator.nextPage(UnknownIdentifier, NormalMode)(spy(userAnswers())) mustEqual routes.WhatToTellTheCalculatorController.onPageLoad()
        }
      }
    }
  }
}
