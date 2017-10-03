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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.WhatToTellTheCalculatorController.onPageLoad()
      }

      "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("england") thenReturn Some("wales") thenReturn Some("scotland")

        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("northernIreland")
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Child Aged Two" in {
        navigator.nextPage(ChildAgedTwoId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
      }

      "go to expect approved childcare cost from childcare cost when you have childcare cost or not yet decided" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("yes") thenReturn Some("notYet")

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
      }

      "go to results page from childcare cost if you are not eligible for free hours and don't have the child care cost" in {//TODO - results page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")

        when(answers.isEligibleForFreeHours) thenReturn NotEligible
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
      }

      "go to results page from childcare cost if you are eligible for free hours, does not live in england & don't have the child care cost" in {//TODO results page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northern-ireland")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
      }

      "go to free hours info page if you are eligible for free hours and don't have childcare cost and lives in england" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.location) thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
      }

      "got to partner page if your eligibility for free hours yet to be determined and don't have childcare cost" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.isEligibleForFreeHours) thenReturn NotDetermined

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }


      "go to are you in paid work from do you live with partner when user selects No" in {
        val answers = mock[UserAnswers]
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        navigator.nextPage(DoYouLiveWithPartnerId, NormalMode)(answers) mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
      }

      "go to parent work hours from are you in paid work when user selects Yes" in {
        val answers = mock[UserAnswers]
        when(answers.areYouInPaidWork) thenReturn Some(true)
        navigator.nextPage(AreYouInPaidWorkId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
      }

      "go to eligibility results from are you in paid work when user selects No" in {
        val answers = mock[UserAnswers]
        when(answers.areYouInPaidWork) thenReturn Some(false)
        //TODO: Once Free hours results is ready redirect to eligibility
        //navigator.nextPage(AreYouInPaidWorkId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
      }

      "go to paid employment from do you live with partner when user selects yes" in {
        val answers = mock[UserAnswers]
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        navigator.nextPage(DoYouLiveWithPartnerId, NormalMode)(answers) mustBe routes.PaidEmploymentController.onPageLoad(NormalMode)
      }

      "go to who is in paid employment from paid employment when user answers yes" in {
        val answers = mock[UserAnswers]
        when(answers.paidEmployment) thenReturn Some(true)
        navigator.nextPage(PaidEmploymentId, NormalMode)(answers) mustBe routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
      }

      "go to eligibility results from paid employment when user answers no" in {
        val answers = mock[UserAnswers]
        when(answers.paidEmployment) thenReturn Some(false)
        //TODO: Once Free hours results is ready redirect to eligibility
        navigator.nextPage(PaidEmploymentId, NormalMode)(mock[UserAnswers]) mustBe routes.Re.onPageLoad(NormalMode)
      }

      "go to parent work hours from who is in paid employment when user selects you" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some("you")
        navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
      }

      "go to partner work hours from who is in paid employment when user selects partner" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
        navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      }

      "go to partner work hours from who is in paid employment when user selects both" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode)(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      }

      "go to parent work hours from partner work hours when user selects both on paid employment" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some("both")
        when(answers.partnerWorkHours) thenReturn Some(23)
        navigator.nextPage(PartnerWorkHoursId, NormalMode)(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }

  }
}
