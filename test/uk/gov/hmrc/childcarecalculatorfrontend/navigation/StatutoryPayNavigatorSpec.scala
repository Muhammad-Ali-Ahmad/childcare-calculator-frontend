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
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap

class StatutoryPayNavigatorSpec extends SpecBase with MockitoSugar {

  def eligibleScheme: TaxCredits = new TaxCredits {
    override def eligibility(answers: UserAnswers) = Eligible
  }

  def notEligibleScheme: TaxCredits = new TaxCredits {
    override def eligibility(answers: UserAnswers) = NotEligible
  }

  def notDeterminedScheme: TaxCredits = new TaxCredits {
    override def eligibility(answers: UserAnswers) = NotDetermined
  }


  def navigator(scheme: TaxCredits = eligibleScheme) = new StatutoryPayNavigator(new Utils(), scheme)

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  "Current Year Statutory Pay Route Navigation" when {

    "in Normal mode" must {
      "Parent Statutory Pay CY Route" must {
        "redirects to YouNoWeeksStatPayCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayCY) thenReturn Some(true)

          navigator().nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayCYController.onPageLoad(NormalMode)
        }

        "redirects to YourIncomeInfoPY page when user selects no, does not live with partner and eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourStatutoryPayCY) thenReturn Some(false)

          navigator(eligibleScheme).nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.YourIncomeInfoPYController.onPageLoad()
        }

        "redirects to PartnerIncomeInfoPY page when user selects no, live with partner and eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourStatutoryPayCY) thenReturn Some(false)

          navigator(eligibleScheme).nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.PartnerIncomeInfoPYController.onPageLoad()
        }

        "redirects to MaxFreeHoursResults page when user selects no, live with partner and not eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourStatutoryPayCY) thenReturn Some(false)

          navigator(notEligibleScheme).nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to MaxFreeHoursResults page when user selects no, does not live with partner and not eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourStatutoryPayCY) thenReturn Some(false)

          navigator(notEligibleScheme).nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is tax credit eligibility is not determined" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayCY) thenReturn None

          navigator(notDeterminedScheme).nextPage(YourStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Pay CY Route" must {
        "redirects to PartnerNoWeeksStatPayCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayCY) thenReturn Some(true)

          navigator().nextPage(PartnerStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerIncomeInfoPY page when user selects no and eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.partnerStatutoryPayCY) thenReturn Some(false)
          navigator(eligibleScheme).nextPage(PartnerStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.PartnerIncomeInfoPYController.onPageLoad()
        }

        "redirects to MaxFreeHoursResults page when user selects no and not eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.partnerStatutoryPayCY) thenReturn Some(false)
          navigator(notEligibleScheme).nextPage(PartnerStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is tax credit eligibility is not determined" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayCY) thenReturn None

          navigator(notDeterminedScheme).nextPage(PartnerStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Statutory Pay CY Route" must {
        "redirects to WhoGetsStatutoryCY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayCY) thenReturn Some(true)

          navigator().nextPage(BothStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.WhoGetsStatutoryCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerIncomeInfoPY page when user selects no and eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.bothStatutoryPayCY) thenReturn Some(false)
          navigator(eligibleScheme).nextPage(BothStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.PartnerIncomeInfoPYController.onPageLoad()
        }

        "redirects to MaxFreeHoursResult page when user selects no and not eligible for TC" in {
          val answers = spy(userAnswers())

          when(answers.bothStatutoryPayCY) thenReturn Some(false)
          navigator(notEligibleScheme).nextPage(BothStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is tax credit eligibility is not determined" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayCY) thenReturn None

          navigator(notDeterminedScheme).nextPage(BothStatutoryPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Statutory Pay CY Route" must {
        "redirects to YouNoWeeksStatPayCY page when user selects you option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryCY) thenReturn Some(You)

          navigator().nextPage(WhoGetsStatutoryCYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayCYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerNoWeeksStatPayCY page when user selects partner option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryCY) thenReturn Some(Partner)

          navigator().nextPage(WhoGetsStatutoryCYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayCYController.onPageLoad(NormalMode)
        }

        "redirects to BothGetsStatutoryCY page when user selects both option" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryCY) thenReturn Some(Both)

          navigator().nextPage(WhoGetsStatutoryCYId, NormalMode).value(answers) mustBe
            routes.BothNoWeeksStatPayCYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryCY) thenReturn None

          navigator().nextPage(WhoGetsStatutoryCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "You No Weeks Stat Pay CY Route" must {
        "redirects to StatutoryPayAWeek page when user enters valid input" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayCY) thenReturn Some(12)

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
          navigator().nextPage(YouNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when user enters invalid input" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayCY) thenReturn None

          navigator().nextPage(YouNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner No Weeks Stat Pay CY Route" must {
        "redirects to StatutoryPayAWeek page when user enters valid input" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayCY) thenReturn Some(12)

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
          navigator().nextPage(PartnerNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekController.onPageLoad(NormalMode)  
        }

        "redirects to sessionExpired page when user enters invalid input" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayCY) thenReturn None

          navigator().nextPage(PartnerNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both No Weeks Stat Pay CY Route" must {
        "redirects to StatutoryPayAWeek page when user enters valid input" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayCY) thenReturn Some(BothNoWeeksStatPayCY(12, 12))

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for current year, once clarification is got on the same
          navigator().nextPage(BothNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekController.onPageLoad(NormalMode)
        }
        "redirects to sessionExpired page when user enters invalid input" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayCY) thenReturn None

          navigator().nextPage(BothNoWeeksStatPayCYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }

        "Your Statutory Pay Amount CY Route" must {

          "redirects to YourIncomeInfoPY page when user does not lives with partner, provides a valid value and eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(false)
            when(answers.yourStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(eligibleScheme).nextPage(YourStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.YourIncomeInfoPYController.onPageLoad()
          }

          "redirects to MaxFreeHoursResult page when user does not lives with partner,provides a valid value and not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(false)
            when(answers.yourStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(notEligibleScheme).nextPage(YourStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.MaxFreeHoursResultController.onPageLoad()
          }

          "redirects to PartnerIncomeInfoPY page when user lives with partner, provides a valid value and eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.yourStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(eligibleScheme).nextPage(YourStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.PartnerIncomeInfoPYController.onPageLoad()
          }

          "redirects to MaxFreeHoursResult page when user lives with partner, provides a valid value and not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.yourStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(notEligibleScheme).nextPage(YourStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.MaxFreeHoursResultController.onPageLoad()
          }

          "redirects to sessionExpired page when there is no value for user selection" in {
            val answers = spy(userAnswers())
            when(answers.yourStatutoryPayAmountCY) thenReturn None

            navigator(notDeterminedScheme).nextPage(YourStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }

        "Partner Statutory Pay Amount CY Route" must {

          "redirects to PartnerIncomeInfoPY page when user provides a valid value and eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.partnerStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(eligibleScheme).nextPage(PartnerStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.PartnerIncomeInfoPYController.onPageLoad()
          }

          "redirects to MaxFreeHoursResult page when user provides a valid value and not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.partnerStatutoryPayAmountCY) thenReturn Some(BigDecimal(12))

            navigator(notEligibleScheme).nextPage(PartnerStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.MaxFreeHoursResultController.onPageLoad()
          }

          "redirects to sessionExpired page when there is no value for user selection" in {
            val answers = spy(userAnswers())
            when(answers.partnerStatutoryPayAmountCY) thenReturn None

            navigator(notDeterminedScheme).nextPage(PartnerStatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }

        "Both Statutory Pay Amount CY Route" must {

          "redirects to PartnerIncomeInfoPY page when user provides a valid value and eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.statutoryPayAmountCY) thenReturn Some(StatutoryPayAmountCY("12", "12"))

            navigator(eligibleScheme).nextPage(StatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.PartnerIncomeInfoPYController.onPageLoad()
          }

          "redirects to MaxFreeHoursResult page when user provides a valid value and not eligible for tax credits" in {
            val answers = spy(userAnswers())
            when(answers.statutoryPayAmountCY) thenReturn Some(StatutoryPayAmountCY("12", "12"))

            navigator(notEligibleScheme).nextPage(StatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.MaxFreeHoursResultController.onPageLoad()
          }

          "redirects to sessionExpired page when there is no value for user selection" in {
            val answers = spy(userAnswers())
            when(answers.statutoryPayAmountCY) thenReturn None

            navigator(notDeterminedScheme).nextPage(StatutoryPayAmountCYId, NormalMode).value(answers) mustBe
              routes.SessionExpiredController.onPageLoad()
          }
        }

      }
    }
  }

  "Previous Year Statutory Pay Route Navigation" when {

    "in Normal mode" must {
      "Parent Statutory Pay PY Route" must {
        "redirects to YouNoWeeksStatPayPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn Some(true)

          navigator().nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn Some(false)

          navigator().nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayPY) thenReturn None

          navigator().nextPage(YourStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Pay PY Route" must {
        "redirects to PartnerNoWeeksStatPayPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn Some(true)

          navigator().nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn Some(false)

          navigator().nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayPY) thenReturn None

          navigator().nextPage(PartnerStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Statutory Pay PY Route" must {
        "redirects to WhoGetsStatutoryPY page when user selects yes" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn Some(true)

          navigator().nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.WhoGetsStatutoryPYController.onPageLoad(NormalMode)
        }

        "redirects to MaxFreeHoursResult page when user selects no" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn Some(false)

          navigator().nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothStatutoryPayPY) thenReturn None

          navigator().nextPage(BothStatutoryPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Who Gets Statutory PY Route" must {
        "redirects to YouNoWeeksStatPayPY page when user selects you" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(You)

          navigator().nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.YouNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to PartnerNoWeeksStatPayPY page when user selects partner" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(Partner)

          navigator().nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.PartnerNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to BothNoWeeksStatPayPY page when user selects both" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn Some(Both)

          navigator().nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.BothNoWeeksStatPayPYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.whoGetsStatutoryPY) thenReturn None

          navigator().nextPage(WhoGetsStatutoryPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "You No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayPY) thenReturn Some(12)

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
          navigator().nextPage(YouNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.youNoWeeksStatPayPY) thenReturn None

          navigator().nextPage(YouNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayPY) thenReturn Some(12)

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
          navigator().nextPage(PartnerNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerNoWeeksStatPayPY) thenReturn None

          navigator().nextPage(PartnerNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both No Weeks Statutory Pay PY Route" must {
        "redirects to StatutoryPayAWeekLY page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayPY) thenReturn Some(BothNoWeeksStatPayPY(12, 12))

          //TODO: To be replaced with correct pages for StatutoryPayAWeek for last year, once clarification is got on the same
          navigator().nextPage(BothNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.StatutoryPayAWeekLYController.onPageLoad(NormalMode)
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.bothNoWeeksStatPayPY) thenReturn None

          navigator().nextPage(BothNoWeeksStatPayPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Your Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayAmountPY) thenReturn Some(BigDecimal(12))

          navigator().nextPage(YourStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.yourStatutoryPayAmountPY) thenReturn None

          navigator().nextPage(YourStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Partner Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayAmountPY) thenReturn Some(BigDecimal(12))

          navigator().nextPage(PartnerStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.partnerStatutoryPayAmountPY) thenReturn None

          navigator().nextPage(PartnerStatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }

      "Both Statutory Pay Amount PY Route" must {
        "redirects to MaxFreeHoursResult page when user provides a valid value" in {
          val answers = spy(userAnswers())
          when(answers.statutoryPayAmountPY) thenReturn Some(StatutoryPayAmountPY("12", "12"))

          navigator().nextPage(StatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.MaxFreeHoursResultController.onPageLoad()
        }

        "redirects to sessionExpired page when there is no value for user selection" in {
          val answers = spy(userAnswers())
          when(answers.statutoryPayAmountPY) thenReturn None

          navigator().nextPage(StatutoryPayAmountPYId, NormalMode).value(answers) mustBe
            routes.SessionExpiredController.onPageLoad()
        }
      }
    }

  }
}
