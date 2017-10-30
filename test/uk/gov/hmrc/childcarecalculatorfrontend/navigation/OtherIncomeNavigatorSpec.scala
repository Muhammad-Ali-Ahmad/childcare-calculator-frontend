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

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, OtherIncomeAmountCY}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class OtherIncomeNavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new OtherIncomeNavigator(new Utils())

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Other Income Route Navigation" when {

    "in Normal mode" must {
      "Parent Other Income CY Route" must {
        "redirects to YourOtherIncomeAmountCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear) thenReturn Some(true)

          navigator.nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to YouAnyTheseBenefitsCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear) thenReturn Some(false)

          navigator.nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeThisYear) thenReturn None

          navigator.nextPage(YourOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

      }

      "Partner Other Income CY Route" must {
        "redirects to PartnerOtherIncomeAmountCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(true)

          navigator.nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerAnyTheseBenefitsCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeThisYear) thenReturn Some(false)

          navigator.nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerAnyOtherIncomeThisYear) thenReturn None

          navigator.nextPage(PartnerAnyOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Other Income CY Route" must {
        "redirects to WhoGetsOtherIncomeCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear) thenReturn Some(true)

          navigator.nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.WhoGetsOtherIncomeCYController.onPageLoad(NormalMode)
        }

        "redirects to BothAnyTheseBenefitsCY page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear) thenReturn Some(false)

          navigator.nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothOtherIncomeThisYear) thenReturn None

          navigator.nextPage(BothOtherIncomeThisYearId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Other Income CY Route" must {
        "redirects to YourOtherIncomeAmountCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("you")

          navigator.nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.YourOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerOtherIncomeAmountCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("partner")

          navigator.nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.PartnerOtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to OtherIncomeAmountCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn Some("both")

          navigator.nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.OtherIncomeAmountCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsOtherIncomeCY) thenReturn None

          navigator.nextPage(WhoGetsOtherIncomeCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Your Other Income CY Route" must {
        "redirects to YouAnyTheseBenefitsCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.YouAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourOtherIncomeAmountCY) thenReturn None

          navigator.nextPage(YourOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Partner Other Income CY Route" must {
        "redirects to PartnerAnyTheseBenefitsCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.partnerOtherIncomeAmountCY) thenReturn Some(BigDecimal(23))

          navigator.nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.PartnerAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerOtherIncomeAmountCY) thenReturn None

          navigator.nextPage(PartnerOtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "How Much Both Other Income CY Route" must {
        "redirects to BothAnyTheseBenefitsCY page when user provides valid input" in {
          val answers = spy(userAnswers())
          when(answers.otherIncomeAmountCY) thenReturn Some(OtherIncomeAmountCY("23", "23"))

          navigator.nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.BothAnyTheseBenefitsCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.howMuchBothPayPension) thenReturn None

          navigator.nextPage(OtherIncomeAmountCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }
  }
}
